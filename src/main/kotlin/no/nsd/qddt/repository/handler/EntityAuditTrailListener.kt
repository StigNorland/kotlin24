package no.nsd.qddt.repository.handler

import no.nsd.qddt.controller.AbstractRestController.Companion.loadRevisionEntity
import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.PublicationStatusService
import no.nsd.qddt.repository.QuestionItemRepository
import no.nsd.qddt.repository.ResponseDomainRepository
import no.nsd.qddt.repository.projection.PublicationStatusItem
import no.nsd.qddt.repository.projection.ResponseDomainListe
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import org.springframework.data.projection.ProjectionFactory
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import javax.persistence.*


/**
 * @author Stig Norland
 */
class EntityAuditTrailListener(
    @Autowired
    private var applicationContext: ApplicationContext? = null,

    @Autowired
    private var factory: ProjectionFactory? = null,

    @Autowired
    private var entityManager: EntityManager? = null

){
    private val jpaFactory get() = JpaRepositoryFactory(entityManager!!)

    private val publicationStatusService get() = applicationContext?.getBean("publicationStatusService") as PublicationStatusService

    @PreRemove
    private fun beforeAnyUpdate(entity: AbstractEntityAudit) {
        entity.changeKind = ChangeKind.TO_BE_DELETED
        entity.changeComment = "Deleting..."
        when (entity) {
            is Study -> {
                beforeStudyRemove(entity)
            }
        }
        log.debug("About to delete entity: {}" , entity.id)
    }

    @PrePersist
    private fun prePersist(entity: AbstractEntityAudit) {
        log.debug("PrePersist [{}] {}", entity.classKind, entity.name)
        val user = SecurityContextHolder.getContext().authentication.principal as User
        entity.agency = user.agency
        entity.modifiedBy = user
        if (entity.xmlLang == "") user.agency.xmlLang.also { entity.xmlLang = it }
        when (entity) {
            is Category -> {
                beforeCategoryInsert(entity)
            }
//            is ResponseDomain -> {
//                persistManagedRep(entity)
//            }
            is Study -> {
                entity.parentIdx
                beforeStudyInsert(entity)
            }
            else -> {
                log.debug("PrePersist [{}] {} : (no pre processing)", entity.classKind , entity.name)
            }
        }
    }

//    private fun persistManagedRep(entity: ResponseDomain) {
//        entity.codes = harvestCatCodes(entity.managedRepresentation)
//        entity.managedRepresentation.let{ manRep ->
//            manRep.name = entity.name
//            manRep.changeComment = entity.changeComment
//            manRep.changeKind = entity.changeKind
//            manRep.xmlLang = entity.xmlLang
//            manRep.version = entity.version
//            manRep.description = entity.getAnchorLabels()
//            jpaFactory.getRepository(CategoryRepository::class.java).let {
//                var result = it.save(manRep)
//                var _index = 0
//                populateCatCodes(result, _index,entity.codes)
//            }
//            entity.responseCardinality = manRep.inputLimit
//
//            log.debug("PrePersist - harvestCode : {} : {}", entity.name, entity.codes.joinToString { it.value })
//        }
//    }


    @PreUpdate
    private fun preUpdate(entity: AbstractEntityAudit) {
        log.debug("PreUpdate [{}] {}", entity.name, entity.id)
        try {
            val user = SecurityContextHolder.getContext().authentication.principal as User
            with(entity) {
                agency = user.agency
                modifiedBy = user
                var ver: Version? = version
                var change = changeKind

                // it is illegal to update an entity with "Creator statuses" (CREATED...BASEDON)
                if ((change.ordinal <= ChangeKind.REFERENCED.ordinal) and !ver!!.isModified) {
                    change = ChangeKind.IN_DEVELOPMENT
                    changeKind = change
                }
                if (changeComment.isEmpty()) // insert default comment if none was supplied, (can occur with auto touching (hierarchy updates etc))
                    changeComment = change.description
                when (change) {
                    ChangeKind.CREATED -> {
                        if (changeComment == "") changeComment = change.description
                    }
                    ChangeKind.BASED_ON, ChangeKind.NEW_COPY, ChangeKind.TRANSLATED -> {
                        ver = Version()
                        entity.basedOn = UriId.fromAny("${entity.id}:${entity.version.rev}")
                    }
                    ChangeKind.REFERENCED, ChangeKind.TO_BE_DELETED -> { }
                    ChangeKind.UPDATED_PARENT, ChangeKind.UPDATED_CHILD, ChangeKind.UPDATED_HIERARCHY_RELATION -> {
                        ver.versionLabel = ""
                    }
                    ChangeKind.IN_DEVELOPMENT-> {
                        ver.versionLabel = ChangeKind.IN_DEVELOPMENT.label
                    }
                    ChangeKind.TYPO -> {
                        ver.minor++
                        ver.versionLabel = ""
                    }
                    ChangeKind.CONCEPTUAL, ChangeKind.EXTERNAL, ChangeKind.OTHER, ChangeKind.ADDED_CONTENT -> {
                        ver.major++
                        ver.versionLabel = ""
                    }
                    ChangeKind.ARCHIVED -> {
                        (this as IArchived).isArchived = true
                        ver.versionLabel = ""
                    }
                }
                version = ver
            }
            when (entity) {
                is Publication -> {
                    entity.publicationElements.forEach {
                        log.debug(it.elementRevision.toString())
                    }
                }
                is Study -> {
                    beforeStudyUpdate(entity)
                }
                is Category -> {
                    log.debug("PreUpdate: {}, value = {}", entity.name, entity.code?.value ?: "NIL")
                }
                is ResponseDomain -> {
                    entity.managedRepresentation.version = entity.version
//                    persistManagedRep(entity)
                }
            }
            log.debug("PreUpdate [{}] {} : (done)", entity.name, entity.id)
        } catch (ex: Exception) {
            log.error("AbstractEntityAudit::onUpdate", ex)
        }
    }

    @PostPersist
    @PostRemove
    private fun afterAnyUpdate(entity: AbstractEntityAudit) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: AbstractEntityAudit) {
        log.debug("AfterLoad [{}] {} : ({})" , entity.classKind, entity.name, entity.modified)
        entity.comments.size
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)

        when (entity) {
            is QuestionConstruct -> {
                if (entity.questionItem == null && entity.questionId?.id != null) {
                    log.debug("AfterLoad of QC -> loading QI")
                    jpaFactory.getRepository(QuestionItemRepository::class.java).let {
                        entity.questionItem = loadRevisionEntity(entity.questionId!!,it)
                        afterLoad(entity.questionItem!!)
                    }
                }
                entity.universe.size
                entity.controlConstructInstructions.size
            }
            is QuestionItem -> {
                if (entity.response == null && entity.responseId?.id != null) {
                    log.debug("AfterLoad of Qi -> loading RD")

                    jpaFactory.getRepository(ResponseDomainRepository::class.java).let {
                        entity.response = loadRevisionEntity(entity.responseId!!,it)
                        afterLoad(entity.response!!)
                        entity.responseDomain = this.factory!!.createProjection(ResponseDomainListe::class.java,
                            entity.response!!
                        )
                    }
                }
            }
            is ResponseDomain -> {
                var _index = 0
                populateCatCodes(entity.managedRepresentation,_index,entity.codes)
                log.debug("AfterLoad [...] {} : {}", entity.name, entity.codes.joinToString { it.value })

            }
            is Category -> {
                entity.children.size
            }
            is Concept ->{
                entity.questionItems.size
                entity.children.size
            }
            is TopicGroup -> {
                entity.questionItems.size
                entity.otherMaterials.size
            }
            is Study -> {
                entity.instruments.size
            }
            is Publication -> {
                entity.status = publicationStatusService.getStatus(entity.statusId)
                entity.status?.let {
                    log.debug(
                    this.factory!!.createProjection(PublicationStatusItem::class.java, it).toString()
                    )
                }

                entity.publicationElements.forEach {
                    log.debug(it.toString())
                }

            }
            else -> {
                log.debug("AfterLoad [{}] {} : (no post loading)", entity.classKind , entity.name)
            }
        }
    }


    private fun beforeCategoryInsert(entity: Category) {
        with(entity) {
            log.debug("beforeCategoryInsert [{}] {}", entity.name, entity.modified.toString())
            when {
                this.categoryKind === CategoryKind.MIXED -> {
                    this.name = (String.format("Mixed [%s]",this.children.joinToString { it.label }))
                }
                this.categoryKind === CategoryKind.SCALE -> {
                    log.debug(this.toString())
                }
            }
            if (label.isBlank())
                label = entity.name

            name = entity.name.uppercase(Locale.getDefault())

            description = when {
                this.hierarchyLevel === HierarchyLevel.GROUP_ENTITY && description.isNullOrBlank()
                    -> this.categoryKind.description
                else
                    -> entity.description
            }

            if (!version.isModified) {
                log.debug("onUpdate not run yet ♣♣♣ ")
            }

            hierarchyLevel = when (categoryKind) {
                CategoryKind.DATETIME, CategoryKind.BOOLEAN, CategoryKind.TEXT, CategoryKind.NUMERIC, CategoryKind.CATEGORY ->
                    HierarchyLevel.ENTITY
                CategoryKind.MISSING_GROUP, CategoryKind.LIST, CategoryKind.SCALE, CategoryKind.MIXED ->
                    HierarchyLevel.GROUP_ENTITY
            }
            name = name.trim()
        }
    }

    private fun beforeStudyRemove(entity: Study) {
        with(entity) {
//            log.debug("Study pre remove " + parent?.name)
//            parent?.?.removeIf { it.id == this.id }
            authors.clear()
            instruments.clear()
        }
    }

    private fun beforeStudyUpdate(entity: Study) {
        with(entity) {
            log.info("Study beforeUpdate {} - {}",entity.name, entity.id)
        }
    }

    private fun beforeStudyInsert(entity: Study) {
         with(entity) {
             log.info("Study beforeInsert {}", entity.name)
         }
    }



    companion object {
        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)

        fun harvestCatCodes(current: Category?): MutableList<Code> {
            val tmpList: MutableList<Code> = mutableListOf()
            if (current == null) return tmpList
            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
                tmpList.add((current.code?:Code("")))
            }
            current.children.forEach {  tmpList.addAll(harvestCatCodes(it)) }
            return tmpList
        }

        fun populateCatCodes(current: Category?, _index: Int,  codes: List<Code>): Int {
            if (current == null) return _index

            var index = _index

            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
                try {
//                log.debug(codes[index].toString())
                    current.code = codes[index++]
                } catch (iob: IndexOutOfBoundsException) {
                    current.code = Code()
                } catch (ex: Exception) {
                    log.error(ex.localizedMessage)
                    current.code = Code()
                }
            }
            current.children.forEach {
                index = populateCatCodes(it, index, codes)
            }
            return index
        }
    }

}
