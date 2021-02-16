package no.nsd.qddt.repository

import no.nsd.qddt.model.Study
import no.nsd.qddt.repository.projection.StudyListe
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "studies", collectionResourceRel = "study", itemResourceRel = "Study", excerptProjection = StudyListe::class)
interface StudyRepositoryAudit  : RevisionRepository<Study, UUID, Int>
