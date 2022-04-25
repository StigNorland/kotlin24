package no.nsd.qddt.repository

import no.nsd.qddt.model.ConditionConstruct
import no.nsd.qddt.repository.projection.ConditionConstructListe
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(path = "conditionconstruct",  itemResourceRel = "ConditionConstruct", excerptProjection = ConditionConstructListe::class)
interface ConditionConstructRepository: ControlConstructRepository<ConditionConstruct>