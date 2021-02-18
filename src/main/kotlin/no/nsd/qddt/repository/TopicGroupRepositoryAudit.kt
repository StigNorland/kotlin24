package no.nsd.qddt.repository

import no.nsd.qddt.model.TopicGroup
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author Stig Norland
 */
@Repository
interface TopicGroupRepositoryAudit  : RevisionRepository<TopicGroup, UUID, Int>
