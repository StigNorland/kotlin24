package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.embedded.ResponseCardinality
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
    var responseCardinality: ResponseCardinality

//    @Value(value = "#{target.getAnchorLabels() }")
//    fun getAnchorLabels(): String

    @Value(value = "#{target.managedRepresentation }")
    fun getManagedRepresentation(): CategoryListe

}



