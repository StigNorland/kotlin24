package no.nsd.qddt.domain.agency

import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "agencyListe", types = [Agency::class])
    interface AgencyListe {
        val id: UUID
        var name: String
    }
