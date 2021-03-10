package no.nsd.qddt.controller

import no.nsd.qddt.model.builder.xml.XmlDDIFragmentAssembler
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.BaseMixedRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


//@BasePathAwareController
abstract class AbstractRestController<T : AbstractEntityAudit>( val repository: BaseMixedRepository<T>) {

    protected val logger = LoggerFactory.getLogger(AbstractRestController::class.java)


    @ResponseBody
    open fun getById(@PathVariable uri: String): ResponseEntity<EntityModel<T>> {
        logger.debug("getById : {}" , uri)
        return ResponseEntity.ok(EntityModel.of(getByUri(uri)))
    }

    @ResponseBody
    open fun getRevisions(@PathVariable uri: String, pageable: Pageable): ResponseEntity<Page<Revision<Int, T>>> {
        logger.debug("getRevisions : {}" , uri)
        val page = repository.findRevisions(UriId.fromAny(uri).id, pageable)
        return ResponseEntity.ok(page)
    }



    open fun getPdf(@PathVariable uri: String): ByteArray {
        logger.debug("getPdf : {}", uri)
        return getByUri(uri).makePdf().toByteArray()
    }


    open fun getXml(@PathVariable uri: String): ResponseEntity<String> {
        logger.debug("compileToXml : {}" ,uri)
        return ResponseEntity.ok(XmlDDIFragmentAssembler(getByUri(uri)).compileToXml())
    }

    protected fun getByUri(uri: String): T {
        return getByUri(UriId.fromAny(uri))
    }


    private fun getByUri(uri: UriId): T {
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).get().entity
        else
            repository.findById(uri.id).get()
    }

}
