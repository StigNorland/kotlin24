package no.nsd.qddt.repository

import no.nsd.qddt.repository.projection.SequenceListe
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(path = "sequence",  itemResourceRel = "Sequence", excerptProjection = SequenceListe::class)
interface SequenceConstructRepository: ControlConstructRepository<no.nsd.qddt.model.Sequence>