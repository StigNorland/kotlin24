package no.nsd.qddt.config

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.QuestionItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.support.EntityLookupSupport
import org.springframework.stereotype.Component
import java.util.*


@Component
class EntityLookupQuestionItem: EntityLookupSupport<QuestionItem>() {

    @Autowired
    private lateinit var repository: QuestionItemRepository

    override fun getResourceIdentifier(entity: QuestionItem): UriId {
        return UriId().apply {
            id = entity.id
            rev = entity.rev
        }
    }

    override fun getLookupProperty(): Optional<String> {
        return Optional.empty<String>()
    }

    override fun lookupEntity(id: Any?): Optional<QuestionItem> {
        val uri = UriId.fromString(id as String)
        return if (uri.rev != null)
            repository.findRevision(uri.id, uri.rev!!).map {
                it.entity
            } else
            repository.findById(uri.id)
    }
}
