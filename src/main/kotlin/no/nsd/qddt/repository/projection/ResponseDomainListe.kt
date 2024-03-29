package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.ResponseDomain
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "responseDomainListe", types = [ResponseDomain::class])
interface ResponseDomainListe: IAbstractEntityViewList {
    var displayLayout: String
    var responseKind: String
//    var managedRepresentation: Category
}



