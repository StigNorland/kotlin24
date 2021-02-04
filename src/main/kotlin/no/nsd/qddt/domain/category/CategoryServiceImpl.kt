package no.nsd.qddt.domain.category

import no.nsd.qddt.domain.classes.exception.InvalidObjectException
import no.nsd.qddt.domain.classes.exception.ResourceNotFoundException
import no.nsd.qddt.domain.responsedomain.Code
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * http://www.ddialliance.org/Specification/DDI-Lifecycle/3.2/XMLSchema/FieldLevelDocumentation/schemas/logicalproduct_xsd/elements/Category.html
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Service("categoryService")
internal class CategoryServiceImpl @Autowired constructor(private val repository: CategoryRepository) :
    CategoryService {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
    override fun findBy(
        level: String,
        type: String,
        name: String?,
        description: String?,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<Category?>? {
        var name = name
        if (IsNullOrTrimEmpty(name) && IsNullOrTrimEmpty(description)) {
            name = "%"
        }
        val categoryType: CategoryType = CategoryType.Companion.getEnum(type)
        val hierarchyLevel: HierarchyLevel = HierarchyLevel.Companion.getEnum(level)
        require(!(categoryType == null && hierarchyLevel == null)) { "categoryType OR hierarchyLevel has to be specified." }
        val sort: PageRequest = defaultOrModifiedSort(pageable, "name ASC", "updated DESC")
        return repository.findByQuery(type, level, likeify(name), likeify(description), likeify(xmlLang), sort)
    }

    @Transactional(readOnly = true)
    override fun count(): Long {
        return repository.count()
    }

    @Transactional(readOnly = true)
    override fun exists(uuid: UUID): Boolean {
        return repository.existsById(uuid)
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun findOne(uuid: UUID): Category {
        return postLoadProcessing(
            repository.findById(uuid).orElseThrow(
                Supplier<ResourceNotFoundException> { ResourceNotFoundException(uuid, Category::class.java) })
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR') and hasPermission(#instance,'AGENCY')")
    override fun save(instance: Category): Category {
        return postLoadProcessing(
            repository.save(
                prePersistProcessing(instance)
            )
        )
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    override fun delete(uuid: UUID) {
        repository.deleteById(uuid)
    }

    private val _codes: MutableList<Code> = ArrayList(0)
    private fun prePersistProcessing(instance: Category): Category {
        // Category Save fails when there is a mix of new and existing children attached to a new element.
        var instance = instance
        return try {
            if (instance.id == null) instance.beforeInsert()
            if (!instance.isValid) throw InvalidObjectException(instance)
            if (_codes.size == 0) _codes.addAll(instance.codes)
            instance.children.forEach(Consumer { c: Category -> prePersistProcessing(c) })
            if (instance.id == null) {
                val c = instance.code
                instance = repository.save(instance)
                instance.code = c
            }
            instance
        } catch (e: Exception) {
            LOG.error(e.javaClass.name, e)
            throw e
        }
    }

    private fun postLoadProcessing(instance: Category): Category {
        instance.codes = _codes
        _codes.clear()
        return instance
    }
}
