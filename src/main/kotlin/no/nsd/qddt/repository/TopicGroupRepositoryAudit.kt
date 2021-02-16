package no.nsd.qddt.repository

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.repository.projection.TopicGroupListe
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "topicgroups", collectionResourceRel = "TopicGroup", itemResourceRel = "TopicGroup", excerptProjection = TopicGroupListe::class)
interface TopicGroupRepositoryAudit  : RevisionRepository<TopicGroup, UUID, Int>
