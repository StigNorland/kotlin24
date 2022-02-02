package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Publication
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "publicationListe", types = [Publication::class])
interface PublicationListe: IAbstractEntityViewList {
    var label: String
    var statusId: Long

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String

}