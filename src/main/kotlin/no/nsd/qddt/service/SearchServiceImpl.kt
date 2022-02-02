package no.nsd.qddt.service

import no.nsd.qddt.model.QddtUrl
import no.nsd.qddt.repository.SearchRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author Stig Norland
 */
@Service("SearchService")
class SearchServiceImpl(@Autowired val repository: SearchRepository) : SearchService {

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
    override fun findPath(id: UUID): QddtUrl {
        return repository.findById(id).get()
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT')")
    override fun findByName(name: String): List<QddtUrl> {
        return repository.findByName(name)
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    override fun findByUserId(userId: UUID): List<QddtUrl> {
        return repository.findByUserId(userId)
    }

}