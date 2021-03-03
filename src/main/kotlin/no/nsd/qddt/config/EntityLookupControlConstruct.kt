package no.nsd.qddt.config

import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.ControlConstructRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.support.EntityLookupSupport
import org.springframework.stereotype.Component
import java.util.*


@Component
class EntityLookupControlConstruct: EntityLookupSupport<ControlConstruct>() {

    @Autowired
    private lateinit var repository: ControlConstructRepository<ControlConstruct>

    override fun getResourceIdentifier(entity: ControlConstruct): UriId {
        return UriId().apply {
            id = entity.id
            rev = entity.rev
        }
    }

    override fun getLookupProperty(): Optional<String> {
        return Optional.empty<String>()
    }

    override fun lookupEntity(id: Any?): Optional<ControlConstruct> {
        val uri = UriId.fromString(id as String)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map {
                it.entity
            } else
            repository.findById(uri.id)
    }
}
