package no.nsd.qddt.repository

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.model.interfaces.BaseArchivedRepository
import no.nsd.qddt.repository.projection.TopicGroupListe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "topicgroup", collectionResourceRel = "TopicGroups", itemResourceRel = "TopicGroup", excerptProjection = TopicGroupListe::class)
interface TopicGroupRepository : BaseArchivedRepository<TopicGroup, UUID> {

    private val logger: Logger
        get() = LoggerFactory.getLogger(this.javaClass)

    @RestResource(rel = "revision", path = "rev")
    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, TopicGroup>>


    fun findByStudyId(id: UUID): List<TopicGroup>?

    override fun findAll(pageable: Pageable): Page<TopicGroup>

    @Query(
        value = "SELECT tg.* FROM topic_group tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and (tg.name ILIKE :name or tg.description ILIKE :description) ) "
                + "ORDER BY ?#{#pageable}", countQuery = "SELECT count(tg.*) FROM topic_group tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and (tg.name ILIKE :name or tg.description ILIKE :description) ) "
                + "ORDER BY ?#{#pageable}", nativeQuery = true
    )
    fun findByQuery(@Param("name") name: String,@Param("description") description: String?,pageable: Pageable): Page<TopicGroup>

    fun findByTopicQuestionItemsElementId(id: UUID): List<TopicGroup>?
}
