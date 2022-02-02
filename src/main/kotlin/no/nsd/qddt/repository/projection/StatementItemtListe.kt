package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.StatementItem
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "statementItemtListe", types = [StatementItem::class])
interface StatementItemtListe: IAbstractEntityViewList {
    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
