package no.nsd.qddt.repository

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.repository.projection.TopicGroupListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "topicgroup",  itemResourceRel = "TopicGroup", excerptProjection = TopicGroupListe::class)
interface TopicGroupRepository : BaseArchivedRepository<TopicGroup> {


    @RestResource(rel = "revision", path = "rev")
    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, TopicGroup>>

    @RestResource(rel = "all", path = "list")
    override fun findAll(pageable: Pageable): Page<TopicGroup>


    fun findByQuestionItemsElementId(id: UUID): List<TopicGroup>?


    @Query( nativeQuery = true,
        value = "SELECT tg.* FROM concept_hierarchy tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and class_kind='TOPIC_GROUP' and (tg.name ILIKE :name or tg.description ILIKE :description) ) ",
        countQuery = "SELECT count(tg.*) FROM concept_hierarchy tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and class_kind='TOPIC_GROUP' and (tg.name ILIKE :name or tg.description ILIKE :description) ) ",
    )
    fun findByQuery(@Param("name") name: String,@Param("description") description: String?,pageable: Pageable): Page<TopicGroup>
}
