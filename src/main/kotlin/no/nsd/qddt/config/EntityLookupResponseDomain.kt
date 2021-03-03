package no.nsd.qddt.config

import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.ResponseDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.support.EntityLookupSupport
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author Stig Norland
 */

@Component
class EntityLookupResponseDomain: EntityLookupSupport<ResponseDomain>() {

    @Autowired
    private lateinit var repository: ResponseDomainRepository

    override fun getResourceIdentifier(entity: ResponseDomain): UriId {
        return UriId().apply {
            id = entity.id
            rev = entity.rev
        }
    }

    override fun getLookupProperty(): Optional<String> {
        return Optional.empty<String>()
    }

    override fun lookupEntity(id: Any?): Optional<ResponseDomain> {
        val uri = UriId.fromString(id as String)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map {
                it.entity.rev = uri.rev
                it.entity
            } else
            repository.findById(uri.id)
    }
}
