package no.nsd.qddt.repository

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.interfaces.BaseArchivedRepository
import no.nsd.qddt.repository.projection.ConceptListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*
/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "concepts", collectionResourceRel = "Concepts", itemResourceRel = "Concept", excerptProjection = ConceptListe::class)
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
