package no.nsd.qddt.domain.universe

import no.nsd.qddt.classes.interfaces.IWebMenuPreview
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp
import java.util.*

/**
 * @author Stig Norland
 */
@Projection(name = "universeListe", types = [Universe::class])
interface UniverseListe:IWebMenuPreview {
    var description:String
}

