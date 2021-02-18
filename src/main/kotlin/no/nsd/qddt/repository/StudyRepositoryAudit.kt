package no.nsd.qddt.repository

import no.nsd.qddt.model.Study
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface StudyRepositoryAudit  : RevisionRepository<Study, UUID, Int>
