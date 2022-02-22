package no.nsd.qddt.service

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.RepLoaderService
import org.hibernate.Hibernate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.history.Revision
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author Stig Norland
 */
@Service("revisionService")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
class RevisionService(val repLoaderService: RepLoaderService) {
    internal val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @ResponseBody
    fun <T: AbstractEntityAudit> getRevisions(uri: UriId, elementKind: ElementKind, pageable: Pageable): Page<Revision<Int, T>> {

        val repository = repLoaderService.getRepository<T>(elementKind)

        val qPage: Pageable = if (pageable.sort.isUnsorted) {
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.Direction.DESC,"modified")
        } else {
            pageable
        }
        return repository.findRevisions(uri.id!!, qPage ).map {
            Hibernate.initialize(it.entity.agency)
            Hibernate.initialize(it.entity.modifiedBy)
            it.entity.version.rev = it.revisionNumber.get()
            it
        }

    }
    @ResponseBody
    fun <T:AbstractEntityAudit> getRevision(uri: UriId, elementKind: ElementKind): Revision<Int, T>? {

        val repository = repLoaderService.getRepository<T>(elementKind)

        val result = uri.rev?.let { repository.findRevision(uri.id!!, it) }

        return if (result?.isPresent == true) {
            logger.debug(result.get().entity.agency.toString())
            logger.debug(result.get().entity.modifiedBy.toString())
//            logger.debug(result.get().entity)
            result.get()
        } else
            repository.findLastChangeRevision(uri.id!!).get()
    }


}
