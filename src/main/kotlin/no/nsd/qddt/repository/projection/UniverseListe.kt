package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.model.Universe
import org.springframework.data.rest.core.config.Projection

/**
 * @author Stig Norland
 */
@Projection(name = "universeListe", types = [Universe::class])
interface UniverseListe:IWebMenuPreview {
    var description:String
}

