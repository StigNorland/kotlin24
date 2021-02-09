package no.nsd.qddt.domain.concept

import no.nsd.qddt.classes.interfaces.BaseArchivedRepository
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
@RepositoryRestResource(path = "concepts", collectionResourceRel = "concept", itemResourceRel = "Concept", excerptProjection = TopicGroupListe::class)
interface ConceptRepository:BaseArchivedRepository<Concept, UUID> {

    fun findByTopicGroupIdAndNameIsNotNull(id:UUID, pageable:Pageable):Page<Concept>

    fun findByConceptQuestionItemsElementId(id:UUID):List<Concept>
    
    @Query(value = ("SELECT c.* FROM concept c " +
                  "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                  + "ORDER BY ?#{#pageable}"),
        countQuery = ("SELECT count(c.*) FROM concept c " +
                    "WHERE ( c.change_kind !='BASED_ON' and (c.name ILIKE :name or c.description ILIKE :description) ) "
                    + "ORDER BY ?#{#pageable}"), nativeQuery = true)
    fun findByQuery(@Param("name") name:String, @Param("description") description:String, pageable:Pageable):Page<Concept>

}