package no.nsd.qddt.repository.projection

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import no.nsd.qddt.model.ResponseDomain
import no.nsd.qddt.model.embedded.ResponseCardinality
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "responseDomainListe", types = [ResponseDomain::class])
@JsonPropertyOrder(alphabetic = true,
    value = *["id","name", "description", "anchorLabels", "responseKind", "classKind", "displayLayout","responseCardinality", "managedRepresentation" ]
)

interface ResponseDomainListe: IAbstractEntityViewList {
    var displayLayout: String
    var responseKind: String
    var description: String
    var responseCardinality: ResponseCardinality

    @Value(value = "#{target.getAnchorLabels() }")
    fun getAnchorLabels(): String

    @Value(value = "#{target.managedRepresentation }")
    fun getManagedRepresentation(): ManagedRepresentation

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}



