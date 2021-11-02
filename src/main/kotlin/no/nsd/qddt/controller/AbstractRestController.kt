package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.BaseMixedRepository
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


//@BasePathAwareController
abstract class AbstractRestController<T : AbstractEntityAudit>( val repository: BaseMixedRepository<T>) {

//    @Autowired
//    private val applicationContext: ApplicationContext? = null

    @ResponseBody
    open fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<T>> {
        logger.debug("getById : {}" , uri)
        val model = EntityModel.of(getByUri(uri))
//            .addIf()
        return ResponseEntity.ok(model)
    }
//    @Transactional
    @ResponseBody
    open fun getRevisions(@PathVariable uri: String, pageable: Pageable): Page<EntityModel<T>> {

        val qPage: Pageable = if (pageable.sort.isUnsorted) {
             PageRequest.of(pageable.pageNumber, pageable.pageSize,Sort.Direction.DESC,"modified")
        } else {
            pageable
        }
        logger.debug("getRevisions 1: {}" , qPage)


        val result = repository.findRevisions(UriId.fromAny(uri).id, qPage )
        logger.debug("getRevisions 2: {}" , result.totalElements)
        val entities = result.content.map {
//            Hibernate.initialize(it.entity.agency)
//            Hibernate.initialize(it.entity.modifiedBy)
            it.entity.rev = it.revisionNumber.get()
            EntityModel.of(it.entity)
        }
        logger.debug("getRevisions 3: {}" , entities.size)
        val page: Page<EntityModel<T>> = PageImpl(entities, result.pageable, result.totalElements )
        result.let { page ->
            page.map {
                it.entity.rev = it.revisionNumber.get()
                EntityModel.of(it.entity)
            }
        }
        return page
    }



    open fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("getPdf : {}", uri)
        return getByUri(uri).makePdf().toByteArray()
    }


    open fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        logger.debug("compileToXml : {}" ,uri)
        return ResponseEntity.ok(XmlDDIFragmentAssembler(getByUri(uri)).compileToXml())
    }

    private fun getByUri(uri: String): T {
        return getByUri(UriId.fromAny(uri))
    }


    private fun getByUri(uri: UriId): T {
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map { it.entity.rev = it.revisionNumber.get(); it.entity }.get()
        else
            repository.findById(uri.id).get()
    }

    companion object {
//        protected val logger = LoggerFactory.getLogger(AbstractRestController::class.java)

        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

}
