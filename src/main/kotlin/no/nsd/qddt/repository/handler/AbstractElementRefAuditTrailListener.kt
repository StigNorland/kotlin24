//package no.nsd.qddt.repository.handler
//
//import no.nsd.qddt.model.classes.ElementLoader
//import no.nsd.qddt.model.interfaces.IElementRef
//import no.nsd.qddt.model.interfaces.IWebMenuPreview
//import no.nsd.qddt.model.interfaces.RepLoaderService
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import javax.persistence.PostLoad
//import javax.persistence.PrePersist
//import javax.persistence.PreUpdate
//
///**
// * @author Stig Norland
// */
//abstract class AbstractElementRefAuditTrailListener<T : IWebMenuPreview> {
//
//    @Autowired
//    lateinit var loader1: RepLoaderService
//
//
//    @PrePersist
//    @PreUpdate
//    private fun beforeAnyUpdate(entity: IElementRef<T>) {
//    }
//
//    @PostLoad
//    private fun afterLoad(entity: IElementRef<T>) {
//        log.info("After load: {} {}" , entity.name, entity.elementKind)
//        ElementLoader<T>(loader1.getRepository<T>(entity.elementKind)).also {
//            it.fill(entity)
//        }
//    }
//
//    companion object {
//        private val log: Logger = LoggerFactory.getLogger(this::class.java)
//    }
//}
