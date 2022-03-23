package no.nsd.qddt.repository.handler

import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.controller.AbstractRestController.Companion.loadRevisionEntity
import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryKind
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.PublicationStatusService
import no.nsd.qddt.model.interfaces.RepLoaderService
import no.nsd.qddt.repository.projection.PublicationStatusItem
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.projection.ProjectionFactory
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import javax.persistence.*


/**
 * @author Stig Norland
 */
class EntityAuditTrailListener{


    @Autowired
    private val factory: ProjectionFactory? = null

    @Autowired
    private val applicationContext: ApplicationContext? = null

    private val repLoaderService get() = applicationContext?.getBean("repLoaderService") as RepLoaderService

    private val publicationStatusService get() = applicationContext?.getBean("publicationStatusService") as PublicationStatusService


    private fun capitalize(label:String): String {
        return label.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    @PreRemove
    private fun beforeAnyDelete(entity: AbstractEntityAudit) {
        entity.changeKind = ChangeKind.TO_BE_DELETED
        entity.changeComment = "Deleting..."
        when (entity) {
            is Study -> {
                beforeStudyRemove(entity)
            }
            is Category -> {
//                entity.categoryChildren.clear()
            }
            is ResponseDomain -> {
                entity.managedRepresentation.categoryChildren.clear()
            }
        }
        log.debug("About to delete entity: {}" , entity.id)
    }

    @PrePersist
    private fun prePersist(entity: AbstractEntityAudit) {
        try {
            log.debug("PrePersist [{}] {}", entity.classKind, entity.name)
            val user = SecurityContextHolder.getContext().authentication.principal as User
            entity.agency = user.agency
            entity.modifiedBy = user
            entity.version = Version()
            if (entity.xmlLang == "") user.agency.xmlLang.also { entity.xmlLang = it }

            checkBasedOn(entity)

            when (entity) {
                is Category -> {
                    beforeCategoryToDb(entity)
                }
                is ResponseDomain -> {
//                if (entity.changeKind.ordinal > 0 && entity.changeKind.ordinal < 4 && entity.managedRepresentation?.id != null ) {
//                    entity.managedRepresentation = entity.managedRepresentation?.clone()
//                }
//                persistManagedRep(entity)
                }
                is Instrument -> {
                    log.debug("{}:{}", entity.name, entity.root.children.joinToString { it.toString() })
                }
                is Study -> {
                    entity.parentIdx
                    beforeStudyInsert(entity)
                }
                else -> {
                    log.debug("PrePersist [{}] {} : (no pre processing)", entity.classKind, entity.name)
                }
            }
        } catch (ex: Exception) {
            log.error("AbstractEntityAudit::prePersist", ex)
        }
    }

    @PreUpdate
    private fun preUpdate(entity: AbstractEntityAudit) {
        log.debug("PreUpdate [{}] : {} - {}", entity.classKind, entity.name, entity.id)
        try {
            val user = SecurityContextHolder.getContext().authentication.principal as User
            user.getAuthority()
            val ver: Version = entity.version
            val change = entity.changeKind

                // it is illegal to update an entity with "Creator statuses" (CREATED...BASEDON)
//                if ((change.ordinal <= ChangeKind.REFERENCED.ordinal) and !ver!!.isModified) {
//                    change = ChangeKind.IN_DEVELOPMENT
//                    changeKind = change
//                }
                if (entity.changeComment.isEmpty()) // insert default comment if none was supplied, (can occur with auto touching (hierarchy updates etc))
                    entity.changeComment = change.description
                when (change) {
                    ChangeKind.CREATED -> {
                        if (entity.changeComment == "") entity.changeComment = change.description
                    }
                    ChangeKind.BASED_ON, ChangeKind.NEW_COPY, ChangeKind.TRANSLATED ,
                    ChangeKind.REFERENCED, ChangeKind.TO_BE_DELETED -> { }
                    ChangeKind.UPDATED_PARENT, ChangeKind.UPDATED_CHILD, ChangeKind.UPDATED_HIERARCHY_RELATION -> {
                        ver.versionLabel = ChangeKind.IN_DEVELOPMENT.label
                    }
                    ChangeKind.IN_DEVELOPMENT-> {
                        ver.versionLabel = ChangeKind.IN_DEVELOPMENT.label
                    }
                    ChangeKind.TYPO -> {
                        ver.minor++
                        ver.versionLabel = ""
                    }
                    ChangeKind.CONCEPTUAL, ChangeKind.EXTERNAL, ChangeKind.OTHER, ChangeKind.ADDED_CONTENT -> {
                        ver.minor=0
                        ver.major++
                        ver.versionLabel = ""
                    }
                    ChangeKind.ARCHIVED -> {
                        (this as IArchived).isArchived = true
                        ver.versionLabel = ChangeKind.ARCHIVED.label
                    }
                }

            entity.modifiedBy = user
            entity.agency = user.agency
            entity.version = ver
            entity.name = entity.name.trim().uppercase(Locale.getDefault())

            when (entity) {
                is Publication -> {
                    entity.publicationElements.forEach {
                        it.element
                        log.debug(it.uri.toString())
                    }
                }
                is Instrument ->{
                    entity.root.children.forEach {
                        log.debug(it.toString())
                    }
                }
                is Sequence -> {
                    entity.sequence.forEach {
                        log.debug(it.uri.toString())
                    }
                }
                is Study -> {
                    beforeStudyUpdate(entity)
                }
                is TopicGroup -> {
                    if (entity.label?.isBlank() == true)
                        entity.label = entity.name
                    entity.label = capitalize(entity.label!!)
                }
                is Concept -> {
                    if (entity.label?.isBlank() == true)
                        entity.label = entity.name
                    entity.label = capitalize(entity.label!!)
                }
                is Category -> {
                    if (entity.label.isBlank())
                        entity.label = capitalize(entity.name)
                }
//                is ResponseDomain -> {
//                }
//                is QuestionConstruct -> {
//
//                }
            }
        } catch (ex: Exception) {
            log.error("AbstractEntityAudit::onUpdate", ex)
        }
    }

    @PostRemove
    private fun afterREmove(entity: AbstractEntityAudit) {
        log.debug("PostRemove complete for entity: {}" , entity.id)
    }

    @PostPersist
    private fun afterAnyUpdate(entity: AbstractEntityAudit) {
        log.debug("PostPersist for entity: {}" , entity.id)
        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)
    }

    @PostLoad
    private fun afterLoad(entity: AbstractEntityAudit) {
        log.debug("PostLoad [{}] : {} - {}:{}", entity.classKind, entity.name, entity.id, entity.version.rev)

        Hibernate.initialize(entity.agency)
        Hibernate.initialize(entity.modifiedBy)

        when (entity) {
            is QuestionConstruct -> {
                entity.universe.size
                entity.controlConstructInstructions.size

                if (Thread.currentThread().stackTrace.find { it.methodName.contains("getById")  } != null) {
                    if (entity.questionItem == null && entity.questionId?.id != null) {
                        repLoaderService.getRepository<QuestionItem>(ElementKind.QUESTION_ITEM).let {
                            entity.questionItem = loadRevisionEntity(entity.questionId!!, it)
                            afterLoad(entity.questionItem!!)
                        }
                    }
                }
                entity.controlConstructInstructions.forEach { cci ->
                    repLoaderService.getRepository<Instruction>(ElementKind.INSTRUCTION).let {
                        cci.instruction = loadRevisionEntity(cci.uri, it)
                    }
                }
            }
            is QuestionItem -> {
                if (entity.response == null && entity.responseId?.id != null) {
                    if (Thread.currentThread().stackTrace.find { it.methodName.contains("getById")  } != null) {
                        repLoaderService.getRepository<ResponseDomain>(ElementKind.RESPONSEDOMAIN).let {
                            entity.response = loadRevisionEntity(entity.responseId!!, it)
                            entity.responseId!!.rev = entity.response!!.version.rev
                            afterLoad(entity.response!!)
                        }
                    }
                }
            }
            is ResponseDomain -> {
                var _index = 0
                populateCatCodes(entity.managedRepresentation,_index,entity.codes)
            }
            is Category -> {
                if (entity.hierarchyLevel == HierarchyLevel.GROUP_ENTITY) {
                    repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let {
                        entity.children = loadChildrenDefault(entity,it)
                    }
                }
            }
            is Concept ->{
                entity.questionItems.size
                entity.children.size
                entity.comments.size
            }
            is TopicGroup -> {
                if (StackTraceFilter.stackContains("getPdf"))
                    entity.children.size
                entity.questionItems.size
                entity.otherMaterials.size
                entity.comments.size
            }
            is Study -> {
                entity.instruments.size
                entity.comments.size
            }
            is Publication -> {
                entity.comments.size
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
//                entity.comments.size
//                log.debug("AfterLoad [{}] {} : (no post loading)", entity.classKind , entity.name)
            }
        }
    }


    private fun checkBasedOn(entity: AbstractEntityAudit) {
        when (entity.changeKind) {
            ChangeKind.BASED_ON, ChangeKind.NEW_COPY, ChangeKind.TRANSLATED, ChangeKind.REFERENCED -> {
                log.debug("checkBasedOn {}", entity.name)
//                entity.version = Version()
                if (entity.version.rev == null || entity.version.rev == 0 ) {
                    repLoaderService.getRepository<AbstractEntityAudit>(ElementKind.getEnum(entity.classKind)).let {
                            repository -> loadRevisionEntity(entity.basedOn!!, repository).let {
                            result -> entity.basedOn = UriId.fromAny("${result.id}:${result.version.rev}")
                        }
                    }
                } else
                    entity.basedOn = UriId.fromAny("${entity.id}:${entity.version.rev}")
            }
            else -> {
                log.debug("ikke based on {}", entity.name)
            }
        }

    }

    private fun beforeCategoryToDb(category: Category) {
        log.debug("beforeCategoryToDb [{}] {}", category.name, category.modified.toString())
        when {
            category.categoryKind === CategoryKind.MIXED -> {
                category.label = (String.format("Mixed [%s]", category.children!!.joinToString { it.label  }))
            }
            category.categoryKind === CategoryKind.SCALE -> {
                log.debug(category.toString())
            }
        }
        category.name = category.name.trim().uppercase(Locale.getDefault())

        if (category.label.isBlank())
            category.label = capitalize(category.name)

        category.description = when {
            category.hierarchyLevel === HierarchyLevel.GROUP_ENTITY && category.description.isBlank()
            -> category.categoryKind.description
            else
            -> category.description
        }

        category.hierarchyLevel = when (category.categoryKind) {
            CategoryKind.DATETIME, CategoryKind.BOOLEAN, CategoryKind.TEXT, CategoryKind.NUMERIC, CategoryKind.CATEGORY ->
                HierarchyLevel.ENTITY
            CategoryKind.MISSING_GROUP, CategoryKind.LIST, CategoryKind.SCALE, CategoryKind.MIXED ->
                HierarchyLevel.GROUP_ENTITY
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
        log.info("Study beforeUpdate {} - {}",entity.name, entity.id)
    }

    private fun beforeStudyInsert(entity: Study) {
        log.info("Study beforeInsert {}", entity.name)
    }

//    private fun persistManagedRep(entity: ResponseDomain) {
//        entity.codes = harvestCatCodes(entity.managedRepresentation)
//        entity.managedRepresentation?.let{ manRep ->
//            manRep.name = entity.name
//            manRep.changeComment = entity.changeComment
//            manRep.changeKind = entity.changeKind
//            manRep.xmlLang = entity.xmlLang
//            manRep.version = entity.version
//            manRep.description = entity.getAnchorLabels()
////            entity.responseCardinality = manRep.inputLimit
//            log.debug("PrePersist - harvestCode : {} : {}", entity.name, entity.codes.joinToString { it.value })
//            manRep
//        }
//    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)

        fun harvestCatCodes(current: Category?): MutableList<Code> {
            val tmpList: MutableList<Code> = mutableListOf()
            if (current == null) return tmpList
            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
                tmpList.add((current.code?:Code("")))
            }
            if (current.hierarchyLevel == HierarchyLevel.GROUP_ENTITY) {
                current.children?.forEach { tmpList.addAll(harvestCatCodes(it)) }
            }
            return tmpList
        }

        fun populateCatCodes(current: Category?, _index: Int,  codes: List<Code>): Int {
            if (current == null) return _index

            var index = _index

            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
                try {
                    current.code = codes[index++]
                } catch (iob: IndexOutOfBoundsException) {
                    current.code = Code()
                } catch (ex: Exception) {
                    log.error(ex.localizedMessage)
                    current.code = Code()
                }
                log.debug("popCoding {}", current.code)
            }
            if (current.hierarchyLevel == HierarchyLevel.GROUP_ENTITY) {
                current.children?.forEach {
                    index = populateCatCodes(it, index, codes)
                }
            }
            return index
        }

        fun loadChildrenDefault(entity: Category, repository: RevisionRepository<Category, UUID, Int>): MutableList<Category> {
            return if (entity.hierarchyLevel == HierarchyLevel.GROUP_ENTITY) {
                log.debug("loadChildrenDefault -> {}", entity.name)
                entity.categoryChildren.filterNotNull().mapNotNull { cc ->
                    if (cc.uri.rev!! > 0) {
                        loadRevisionEntity(cc.uri, repository).also {
                            it.children = loadChildrenDefault(it, repository)
                        }
                    } else {
                        cc.children?.also {
                            it.children = loadChildrenDefault(it, repository)
                        }
                    }
                }.toMutableList()
            } else {
                mutableListOf()
            }
        }
    }

}
