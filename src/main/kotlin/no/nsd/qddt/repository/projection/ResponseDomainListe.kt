package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.ResponseDomain
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "responseDomainListe", types = [ResponseDomain::class])
interface ResponseDomainListe: IAbstractEntityViewList {
    var displayLayout: String
    var responseKind: String
    var description: String

    @Value(value = "#{target.getAnchors() }")
    fun getAnchor(): Collection<Pair<String, String>>

}



