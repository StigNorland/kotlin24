package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.embedded.ElementRefResponseDomain
import no.nsd.qddt.model.interfaces.IDomainObject
import no.nsd.qddt.model.QuestionItem
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "questionItemListe", types = [QuestionItem::class])
interface QuestionItemListe: IDomainObject {
    var question: String
    var intent: String
    var responseDomainRef: ElementRefResponseDomain
}

