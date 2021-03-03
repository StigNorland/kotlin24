package no.nsd.qddt.config

import no.nsd.qddt.model.*
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.core.support.EntityLookupSupport
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import kotlin.sequences.Sequence
import no.nsd.qddt.model.QuestionItem


// @Component
// class QuestionItemEntityLookup: EntityLookupSupport<QuestionItem>() {

//     @Autowired private var questionItemRepository: QuestionItemRepository? = null

//     override fun getResourceIdentifier(entity: QuestionItem) {
//         return UriId(entity.id, entity.rev)
//     }

    
//     override fun lookupEntity(id: Any!) {
//         var uri = UriId::fromString(id)
//         return questionItemRepository.findRevision(uri.id,uri.rev)
//     }
// }