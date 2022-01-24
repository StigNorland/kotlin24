package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Agency
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "userAgency", types = [Agency::class])
interface UserAgency {
    val id: UUID
    val name: String
}