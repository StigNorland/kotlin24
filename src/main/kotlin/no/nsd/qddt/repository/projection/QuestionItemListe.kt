package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.ResponseDomain
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "questionItemListe", types = [QuestionItem::class])
interface QuestionItemListe: IAbstractEntityEditList {
    var question: String
    var intent: String
    var responseDomain: ResponseDomain?
    // var responseDomainRef: ElementRefResponseDomain
}

