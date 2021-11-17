package no.nsd.qddt.service

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.RepLoaderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.*
import org.springframework.data.history.Revision
import org.springframework.hateoas.EntityModel
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author Stig Norland
 */
@Service("revisionService")
class RevisionService(val repLoaderService: RepLoaderService) {
    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun <T: AbstractEntityAudit> getRevisions(uri: UriId, elementKind: ElementKind, pageable: Pageable): Page<EntityModel<T>> {

        val repository = repLoaderService.getRepository<T>(elementKind)

        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC,"modified")
        } else {
            pageable
        }
        val result = repository.findRevisions(uri.id, qPage )

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

    fun <T:AbstractEntityAudit> getRevision(uri: UriId, elementKind: ElementKind): EntityModel<Revision<Int, T>>? {

        val repository = repLoaderService.getRepository<T>(elementKind)

        val result = uri.rev?.let { repository.findRevision(uri.id, it) }

        return if (result?.isPresent == true) {
            EntityModel.of(result.get())
        } else
            EntityModel.of(repository.findLastChangeRevision(uri.id).get())
    }


}
