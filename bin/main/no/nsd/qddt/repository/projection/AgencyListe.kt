package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Agency
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "agencyListe", types = [Agency::class])
interface AgencyListe{
    val id: UUID
    var name: String
    var xmlLang: String
    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long
}
