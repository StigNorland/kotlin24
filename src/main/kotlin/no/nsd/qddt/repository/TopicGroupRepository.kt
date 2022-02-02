package no.nsd.qddt.repository

import no.nsd.qddt.model.TopicGroup
import no.nsd.qddt.repository.projection.TopicGroupListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "topicgroup",  itemResourceRel = "TopicGroup", excerptProjection = TopicGroupListe::class)
interface TopicGroupRepository : BaseArchivedRepository<TopicGroup> {

    fun findByQuestionItemsElementId(id: UUID): List<TopicGroup>?


    @Query( nativeQuery = true,
        value = "SELECT tg.* FROM concept_hierarchy tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and class_kind='TOPIC_GROUP' and (tg.name ILIKE searchStr(cast(:name AS text))  or tg.description ILIKE searchStr(cast(:description AS text)) ) ) ",
        countQuery = "SELECT count(tg.*) FROM concept_hierarchy tg " +
                "WHERE (  tg.change_kind !='BASED_ON' and class_kind='TOPIC_GROUP' and (tg.name ILIKE searchStr(cast(:name AS text))  or tg.description ILIKE searchStr(cast(:description AS text)) ) ) "
    )
    fun findByQuery(@Param("name") name: String,@Param("description") description: String?,pageable: Pageable): Page<TopicGroup>
}
