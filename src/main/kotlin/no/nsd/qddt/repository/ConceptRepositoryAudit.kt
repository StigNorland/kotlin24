package no.nsd.qddt.repository

import no.nsd.qddt.model.Concept
import no.nsd.qddt.repository.projection.CategoryListe
import no.nsd.qddt.repository.projection.ConceptListe
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "soncepts", collectionResourceRel = "concept", itemResourceRel = "Concept", excerptProjection = ConceptListe::class)
interface ConceptRepositoryAudit  : RevisionRepository<Concept, UUID, Int>
