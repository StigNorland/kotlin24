package no.nsd.qddt.domain.topicgroup

import no.nsd.qddt.classes.interfaces.BaseArchivedRepository
import no.nsd.qddt.domain.topicgroup.TopicGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.UUID

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "topicgroups", collectionResourceRel = "topicgroup", itemResourceRel = "TopicGroup", excerptProjection = TopicGroupListe::class)
interface TopicGroupRepository : BaseArchivedRepository<TopicGroup, UUID> {

    fun findByStudyId(id: UUID): List<TopicGroup>?
    override fun findAll(pageable: Pageable): Page<TopicGroup>

    @Query(
        value = "SELECT tg.* FROM topic_group tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and (tg.name ILIKE :name or tg.description ILIKE :description) ) "
                + "ORDER BY ?#{#pageable}", countQuery = "SELECT count(tg.*) FROM topic_group tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and (tg.name ILIKE :name or tg.description ILIKE :description) ) "
                + "ORDER BY ?#{#pageable}", nativeQuery = true
    )
    fun findByQuery(
        @Param("name") name: String,
        @Param("description") description: String?,
        pageable: Pageable?
    ): Page<TopicGroup>?

    fun findByTopicQuestionItemsElementId(id: UUID): List<TopicGroup>?
}
