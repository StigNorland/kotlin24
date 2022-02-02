package no.nsd.qddt.repository

import no.nsd.qddt.model.StatementItem
import no.nsd.qddt.repository.projection.StatementItemtListe
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(path = "statementitem",  itemResourceRel = "StatementItem", excerptProjection = StatementItemtListe::class)
interface StatementConstructRepository: ControlConstructRepository<StatementItem>