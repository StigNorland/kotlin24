package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.BaseMixedRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

//import org.springframework.hateoas.Link;
//import org.springframework.hateoas.Resource;
//import org.springframework.hateoas.Resources;
//import org.springframework.hateoas.mvc.ControllerLinkBuilder;


//@BasePathAwareController
abstract class AbstractRestController<T : AbstractEntityAudit>( val repository: BaseMixedRepository<T>) {

    protected val logger = LoggerFactory.getLogger(AbstractRestController::class.java)


    @ResponseBody
    open fun getById(@PathVariable uri: String): ResponseEntity<T> {
        return ResponseEntity.ok(getByUri(uri))
    }

    @ResponseBody
    open fun getRevisions(@PathVariable uri: String, pageable: Pageable): ResponseEntity<Page<Revision<Int, T>>> {
            val page = repository.findRevisions(UriId.fromAny(uri).id, pageable)
            return ResponseEntity.ok(page);
    }


    @ResponseBody
    open fun getPdf(@PathVariable uri: String): ByteArray? {
        logger.debug("makePdf")
        return getByUri(uri).makePdf().
            also {
                logger.debug(it.size().toString())
            }.toByteArray()
    }

    @ResponseBody
    open fun getXml(@PathVariable uri: String): String {
        logger.debug("compileToXml")
        return XmlDDIFragmentAssembler<T>(getByUri(uri)).compileToXml()
    }

    protected fun getByUri(uri: String): T {
        logger.debug("getByUri-01: {}", uri)
        return getByUri(UriId.fromAny(uri))
    }


    private fun getByUri(uri: UriId): T {
        logger.debug("getByUri-02: {}", uri)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).get().entity
        else
            repository.findById(uri.id).get()
    }

}