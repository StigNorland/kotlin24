package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.StatementItem
import org.springframework.data.rest.core.config.Projection

@Projection(name = "statementItemtListe", types = [StatementItem::class])
interface StatementItemtListe: IAbstractEntityViewList
