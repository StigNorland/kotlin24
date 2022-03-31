package no.nsd.qddt.config

import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.controller.AbstractRestController
import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.RepLoaderService
import no.nsd.qddt.repository.handler.EntityAuditTrailListener
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.auditing.AuditingHandler
import org.springframework.stereotype.Component
import javax.persistence.PostLoad

/**
 * @author Stig Norland
 */
@Component
class CustomAuditingEntityListener(@Autowired private var handler: ObjectFactory<AuditingHandler>? = null) {


    @Autowired
    private val applicationContext: ApplicationContext? = null
    private val repLoaderService get() = applicationContext?.getBean("repLoaderService") as RepLoaderService




    @PostLoad
    fun afterLoad(entity: AbstractEntityAudit) {
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
                            entity.questionItem = AbstractRestController.loadRevisionEntity(entity.questionId!!, it)
                            afterLoad(entity.questionItem!!)
                        }
                    }
                }
                entity.controlConstructInstructions.forEach { cci ->
                    repLoaderService.getRepository<Instruction>(ElementKind.INSTRUCTION).let {
                        cci.instruction = AbstractRestController.loadRevisionEntity(cci.uri, it)
                    }
                }
            }
            is QuestionItem -> {
                if (entity.response == null && entity.responseId?.id != null) {
                    if (Thread.currentThread().stackTrace.find { it.methodName.contains("getById")  } != null) {
                        repLoaderService.getRepository<ResponseDomain>(ElementKind.RESPONSEDOMAIN).let {
                            entity.response = AbstractRestController.loadRevisionEntity(entity.responseId!!, it)
                            entity.responseId!!.rev = entity.response!!.version.rev
                            afterLoad(entity.response!!)
                        }
                    }
                }
            }
            is ResponseDomain -> {
                var _index = 0
                EntityAuditTrailListener.populateCatCodes(entity.managedRepresentation, _index, entity.codes)
            }
            is Category -> {
                if (entity.hierarchyLevel == HierarchyLevel.GROUP_ENTITY) {
                    repLoaderService.getRepository<Category>(ElementKind.CATEGORY).let {
                        entity.children = EntityAuditTrailListener.loadChildrenDefault(entity, it)
                    }
                }
            }
            is Concept ->{
                entity.questionItems.size
                if (Thread.currentThread().stackTrace.find { it.methodName.contains("getPdf")  } != null) {
                    repLoaderService.getRepository<QuestionItem>(ElementKind.QUESTION_ITEM).let {
                        entity.questionItems.forEach { qref ->
                            qref.element= AbstractRestController.loadRevisionEntity(qref.uri!!, it)
                            afterLoad(qref.element!!)
                        }
                    }
                }
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
//                entity.status = publicationStatusService.getStatus(entity.statusId)
//                entity.status?.let {
//                    EntityAuditTrailListener.log.debug(
//                        this.factory!!.createProjection(PublicationStatusItem::class.java, it).toString()
//                    )
//                }

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

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CustomAuditingEntityListener::class.java)
    }

}
