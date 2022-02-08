//package no.nsd.qddt.repository.handler
//
//import no.nsd.qddt.controller.AbstractRestController
//import no.nsd.qddt.model.*
//import no.nsd.qddt.model.classes.AbstractEntityAudit
//import no.nsd.qddt.model.embedded.Code
//import no.nsd.qddt.model.enums.HierarchyLevel
//import no.nsd.qddt.model.interfaces.PublicationStatusService
//import no.nsd.qddt.repository.QuestionItemRepository
//import no.nsd.qddt.repository.ResponseDomainRepository
//import no.nsd.qddt.repository.projection.PublicationStatusItem
//import no.nsd.qddt.repository.projection.ResponseDomainListe
//import org.hibernate.Hibernate
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.ApplicationContext
//import org.springframework.data.history.Revision
//import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
//import org.springframework.data.projection.ProjectionFactory
//import javax.persistence.EntityManager
//import javax.persistence.PostLoad
//
//class RevisionEntityListener (
//    @Autowired
//    private var applicationContext: ApplicationContext? = null,
//
//    @Autowired
//    private var factory: ProjectionFactory? = null,
//
//    @Autowired
//    private var entityManager: EntityManager? = null
//
//    ) {
//    private val jpaFactory get() = JpaRepositoryFactory(entityManager!!)
//
//    private val publicationStatusService get() = applicationContext?.getBean("publicationStatusService") as PublicationStatusService
//
//    @PostLoad
//    private fun afterLoad(revision: Revision<Int, AbstractEntityAudit>) {
//        log.debug("AfterLoad [{}] {} : ({})", revision.entity.classKind, revision.entity.name, revision.entity.modified)
//        revision.entity.comments.size
//        Hibernate.initialize(revision.entity.agency)
//        Hibernate.initialize(revision.entity.modifiedBy)
//
//        val entity = revision.entity
//
//        when (entity) {
//            is QuestionConstruct -> {
//                if (entity.questionItem == null && entity.questionId?.id != null) {
//                    log.debug("AfterLoad of QC -> loading QI")
//                    jpaFactory.getRepository(QuestionItemRepository::class.java).let {
//                        entity.questionItem = AbstractRestController.loadRevisionEntity(entity.questionId!!, it)
//                        afterLoad(entity.questionItem!!)
//                    }
//                }
//                entity.universe.size
//                entity.controlConstructInstructions.size
//            }
//            is QuestionItem -> {
//                if (entity.response == null && entity.responseId?.id != null) {
//                    log.debug("AfterLoad of Qi -> loading RD")
//
//                    jpaFactory.getRepository(ResponseDomainRepository::class.java).let {
//                        entity.response = AbstractRestController.loadRevisionEntity(entity.responseId!!, it)
//                        afterLoad(entity.response!!)
//                        entity.responseDomain = this.factory!!.createProjection(
//                            ResponseDomainListe::class.java,
//                            entity.response!!
//                        )
//                    }
//                }
//            }
//            is ResponseDomain -> {
//                var _index = 0
//                populateCatCodes(entity.managedRepresentation, _index, entity.codes)
//                log.debug(
//                    "AfterLoad [...] {} : {}",
//                    entity.name,
//                    entity.codes.joinToString { it.value })
//
//            }
//            is Category -> {
//                entity.children.size
//            }
//            is Concept -> {
//                entity.questionItems.size
//                entity.children.size
//            }
//            is TopicGroup -> {
//                entity.questionItems.size
//                entity.otherMaterials.size
//            }
//            is Study -> {
//                entity.instruments.size
//            }
//            is Publication -> {
//                entity.status = publicationStatusService.getStatus(entity.statusId)
//                entity.status?.let {
//                    log.debug(
//                        this.factory!!.createProjection(PublicationStatusItem::class.java, it).toString()
//                    )
//                }
//
//                entity.publicationElements.forEach {
//                    log.debug(it.toString())
//                }
//
//            }
//            else -> {
//                log.debug(
//                    "AfterLoad [{}] {} : (no post loading)",
//                    entity.classKind,
//                    entity.name
//                )
//            }
//        }
//    }
//
//    companion object {
//        private val log: Logger = LoggerFactory.getLogger(EntityAuditTrailListener::class.java)
//
//        fun harvestCatCodes(current: Category?): MutableList<Code> {
//            val tmpList: MutableList<Code> = mutableListOf()
//            if (current == null) return tmpList
//            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
//                tmpList.add((current.code ?: Code("")))
//            }
//            current.children.forEach { tmpList.addAll(harvestCatCodes(it)) }
//            return tmpList
//        }
//
//        fun populateCatCodes(current: Category?, _index: Int, codes: List<Code>): Int {
//            if (current == null) return _index
//
//            var index = _index
//
//            if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
//                try {
////                log.debug(codes[index].toString())
//                    current.code = codes[index++]
//                } catch (iob: IndexOutOfBoundsException) {
//                    current.code = Code()
//                } catch (ex: Exception) {
//                    log.error(ex.localizedMessage)
//                    current.code = Code()
//                }
//            }
//            current.children.forEach {
//                index = populateCatCodes(it, index, codes)
//            }
//            return index
//        }
//    }
//}