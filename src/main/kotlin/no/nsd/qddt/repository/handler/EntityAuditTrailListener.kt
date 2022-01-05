package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryType
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.RepLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import javax.persistence.*


/**
 * @author Stig Norland
 */
class EntityAuditTrailListener{

    @Autowired
    private val applicationContext: ApplicationContext? = null

    @PreRemove
    private fun beforeAnyUpdate(entity: AbstractEntityAudit) {
        when (entity) {
            is Study -> {
                beforeStudyRemove(entity)
            }
        }
        log.debug("About to delete entity: {}" , entity.id)
    }

    @PrePersist
    private fun onInsert(entity: AbstractEntityAudit) {
        log.debug("About to insert entity: {}" , entity.name)
        val user = SecurityContextHolder.getContext().authentication.principal as User
        entity.agency = user.agency
        entity.modifiedBy = user
        if (entity.xmlLang == "") user.agency.xmlLang.also { entity.xmlLang = it }
        when (entity) {
            is Category -> {
                beforeCategoryInsert(entity)
            }
            is ResponseDomain -> {
                entity.codes = harvestCatCodes(entity.managedRepresentation)
            }
            is Study -> {
                beforeStudyInsert(entity)
            }
        }
    }

    @PreUpdate
    private fun onUpdate(entity: AbstractEntityAudit) {
        log.debug("About to update entity: {}" , entity.id)
        try {
            with(entity) {
            entity.modifiedBy = SecurityContextHolder.getContext().authentication.principal as User
            var ver: Version? = version
            var change = changeKind

            // it is illegal to update an entity with "Creator statuses" (CREATED...BASEDON)
            if ( (change.ordinal <= ChangeKind.REFERENCED.ordinal)  and !ver!!.isModified) {
                change = ChangeKind.IN_DEVELOPMENT
                changeKind = change
            }
            if (changeComment.isEmpty()) // insert default comment if none was supplied, (can occur with auto touching (hierarchy updates etc))
                changeComment = change.description
            when (change) {
                ChangeKind.CREATED
                -> if (changeComment == "") changeComment = change.description
                ChangeKind.BASED_ON, ChangeKind.NEW_COPY, ChangeKind.TRANSLATED
                -> {
                    ver = Version()
                    entity.basedOn =  UriId.fromAny("${entity.id}:${entity.version.rev}")
                }
                ChangeKind.REFERENCED, ChangeKind.TO_BE_DELETED
                -> {}
                ChangeKind.UPDATED_PARENT, ChangeKind.UPDATED_CHILD, ChangeKind.UPDATED_HIERARCHY_RELATION
                -> ver.versionLabel = ""
                ChangeKind.IN_DEVELOPMENT -> ver.versionLabel = ChangeKind.IN_DEVELOPMENT.name
                ChangeKind.TYPO -> {
                    ver.minor++
                    ver.versionLabel = ""
                }
                ChangeKind.CONCEPTUAL, ChangeKind.EXTERNAL, ChangeKind.OTHER, ChangeKind.ADDED_CONTENT -> {
                    ver.major++
                    ver.versionLabel =""
                }
                ChangeKind.ARCHIVED -> {
                    (this as IArchived).isArchived =true
                    ver.versionLabel =""
                }
            }
            version = ver
            }
            when (entity) {
                is Study -> {
                    beforeStudyUpdate(entity)
                }
                is ResponseDomain -> {
                    entity.codes = harvestCatCodes(entity.managedRepresentation)
                }
            }
        } catch (ex: Exception) {
            log.error("AbstractEntityAudit::onUpdate", ex)
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private fun afterAnyUpdate(entity: AbstractEntityAudit) {
        log.debug("Add/update/delete complete for entity: {}" , entity.id)
    }

    @PostLoad
    private fun afterLoad(entity: AbstractEntityAudit) {
        log.debug("After load of entity: {}" , entity.id)

        val bean =  applicationContext?.getBean("repLoaderService") as RepLoaderService
        when (entity) {
            is QuestionConstruct -> {
                if (entity.questionItem == null && entity.questionId?.id != null) {

                    val repository =  bean.getRepository<QuestionItem>(ElementKind.QUESTION_ITEM)
                    entity.questionItem = loadRevisionEntity(entity.questionId!!,repository)

                }
            }
            is QuestionItem -> {
                log.debug("After load of Qi: {}" , entity.name)
                if (entity.responseDomain == null && entity.responseId?.id != null) {
                    log.debug("After load of Qi -> loading RD")

                    val repository =  bean.getRepository<ResponseDomain>(ElementKind.RESPONSEDOMAIN)
                    entity.responseDomain = loadRevisionEntity(entity.responseId!!,repository)

                    var _index = 0
                    populateCatCodes(entity.responseDomain!!.managedRepresentation,_index, entity.responseDomain!!.codes)

                }
            }
            is ResponseDomain -> {
                log.debug("POPULATE_CODES - {} : {} : {}", entity.classKind.padEnd(15) , entity.id, entity.name)

                var _index = 0
                populateCatCodes(entity.managedRepresentation,_index,entity.codes)

            }
            else -> {
                log.debug("UNTOUCHED - {} : {} : {}", entity.classKind.padEnd(15) , entity.id, entity.name)
                log.debug(entity.modified.toString())
            }
        }
    }


    private fun <T: AbstractEntityAudit>loadRevisionEntity(uri: UriId, repository: RevisionRepository<T, UUID, Int>): T {
        return with(uri) {
            if (rev != null)
                repository.findRevision(id,rev!!).map {
                    it.entity.version.rev = it.revisionNumber.get()
                    it.entity
                    }.get()
            else
                repository.findLastChangeRevision(id).map {
                    it.entity.version.rev = it.revisionNumber.get()
                    it.entity
                }.get()
        }
    }

    private fun beforeCategoryInsert(entity: Category) {
        with(entity) {
            log.info("Category beforeInsert $name")
            hierarchyLevel = when (categoryKind) {
                CategoryType.DATETIME, CategoryType.BOOLEAN, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.CATEGORY ->
                    HierarchyLevel.ENTITY
                CategoryType.MISSING_GROUP, CategoryType.LIST, CategoryType.SCALE, CategoryType.MIXED ->
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

    private fun harvestCatCodes(current: Category?): MutableList<Code> {
        val tmpList: MutableList<Code> = mutableListOf()
        if (current == null) return tmpList
        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            tmpList.add((current.code?:Code("")))
        }
        current.children.forEach {  tmpList.addAll(harvestCatCodes(it)) }
        return tmpList
    }

    private fun populateCatCodes(current: Category?, _index: Int,  codes: List<Code>): Int {
        if (current == null) return _index

        var index = _index

        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            try {
                log.debug(codes[index].toString())
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

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)
    }

}
