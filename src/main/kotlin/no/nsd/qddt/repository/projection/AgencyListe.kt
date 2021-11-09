package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.classes.UriId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "agencyListe", types = [Agency::class])
    interface AgencyListe{
        val id: UUID
        var name: String
    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long
        var xmlLang: String
    }
