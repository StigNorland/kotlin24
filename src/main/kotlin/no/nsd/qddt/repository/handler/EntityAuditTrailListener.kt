package no.nsd.qddt.repository.handler

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.Study
import no.nsd.qddt.model.User
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryType
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.repository.ResponseDomainRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import javax.persistence.*

/**
 * @author Stig Norland
 */
class EntityAuditTrailListener {


    @Autowired
    var responseDomainRepository: ResponseDomainRepository? = null


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
        val user = SecurityContextHolder.getContext().authentication.details as User
        entity.agency = user.agency
        if (entity.xmlLang == "") user.agency.xmlLang.also { entity.xmlLang = it }
        when (entity) {
            is Category -> {
                beforeCategoryInsert(entity)
            }
            is Study -> {
                beforeStudyInsert(entity)
            }
        }
    }

    @PreUpdate
    private fun onUpdate(entity: AbstractEntityAudit) {
        try {
            with(entity) {
            var ver: Version? = version
            var change = changeKind

            // it is illegal to update an entity with "Creator statuses" (CREATED...BASEDON)
            if ( (change.ordinal <= IBasedOn.ChangeKind.REFERENCED.ordinal)  and !ver!!.isModified) {
                change = IBasedOn.ChangeKind.IN_DEVELOPMENT
                changeKind = change
            }
            if (changeComment.isEmpty()) // insert default comment if none was supplied, (can occur with auto touching (hierarchy updates etc))
                changeComment = change.description
            when (change) {
                IBasedOn.ChangeKind.CREATED
                -> if (changeComment == "") changeComment = change.description
                IBasedOn.ChangeKind.BASED_ON, IBasedOn.ChangeKind.NEW_COPY, IBasedOn.ChangeKind.TRANSLATED
                -> ver = Version()
                IBasedOn.ChangeKind.REFERENCED, IBasedOn.ChangeKind.TO_BE_DELETED
                -> {}
                IBasedOn.ChangeKind.UPDATED_PARENT, IBasedOn.ChangeKind.UPDATED_CHILD, IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
                -> ver.versionLabel = ""
                IBasedOn.ChangeKind.IN_DEVELOPMENT -> ver.versionLabel = IBasedOn.ChangeKind.IN_DEVELOPMENT.name
                IBasedOn.ChangeKind.TYPO -> {
                    ver.minor++
                    ver.versionLabel = ""
                }
                IBasedOn.ChangeKind.CONCEPTUAL, IBasedOn.ChangeKind.EXTERNAL, IBasedOn.ChangeKind.OTHER, IBasedOn.ChangeKind.ADDED_CONTENT -> {
                    ver.major++
                    ver.versionLabel =""
                }
                IBasedOn.ChangeKind.ARCHIVED -> {
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
        when (entity) {
            is QuestionItem -> {
//                val repository =  elementRepositoryLoader.getRepository(entity.responseDomainRef.elementKind)
//                val loader = ElementLoader<ResponseDomain>(responseDomainRepository)
//                loader.fill(entity.responseDomainRef)
            }

//            is Study -> {
//                log.debug("Study loaded from database: {}" , entity.id)
//            }
            else -> {
                log.debug("{}:{}:{} loaded from database", entity.classKind.padEnd(14) , entity.id, entity.name)
            }
        }

    }

    fun beforeCategoryInsert(entity: Category) {
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

    fun beforeStudyRemove(entity: Study) {
        with(entity) {
            log.debug(" Study pre remove " + surveyProgram?.name)
            surveyProgram?.studies?.removeIf { it.id == this.id }
            authors.clear()
            instruments.clear()
        }
    }

    fun beforeStudyUpdate(entity: Study) {
        with(entity) {
            log.info("Study beforeUpdate")
//            if (surveyIdx == null) {
//                log.info("Setting surveyIdx")
//                surveyIdx = surveyProgram?.studies?.indexOf(this)
//            }
        }
    }

    fun beforeStudyInsert(entity: Study) {
         with(entity) {
             log.info("Study beforeInsert")
//             if (surveyProgram != null && surveyIdx == null) {
//                 log.info("Setting surveyIdx")
//                 surveyIdx = surveyProgram!!.studies.indexOf(this)
//             } else {
//                 log.debug("no survey reference, cannot add..")
//             }
         }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)
    }


}
