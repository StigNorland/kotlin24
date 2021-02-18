package no.nsd.qddt.repository

import no.nsd.qddt.model.Concept
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface ConceptRepositoryAudit  : RevisionRepository<Concept, UUID, Int>
