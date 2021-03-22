package no.nsd.qddt.repository

import no.nsd.qddt.model.Concept
import no.nsd.qddt.repository.projection.ConceptListe
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
@RepositoryRestResource(path = "concept",  itemResourceRel = "Concept", excerptProjection = ConceptListe::class)
interface ConceptRepository: BaseArchivedRepository<Concept> {


    @RestResource(rel = "revision", path = "/rev")
    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, Concept>>

    @RestResource(rel = "all", path = "/list")
    override fun findAll(pageable: Pageable): Page<Concept>

    fun findByParentIdAndNameIsNotNull(id:UUID, pageable:Pageable):Page<Concept>

    fun findByQuestionItemsElementId(id:UUID):List<Concept>
    
    @Query( nativeQuery = true,
        value = "SELECT c.* FROM concept_hierarchy c " +
                " WHERE c.change_kind !='BASED_ON' " +
                " AND class_kind='CONCEPT' " +
                " AND ( " +
                " (:name is null OR c.name ILIKE cast(:name AS text)) " +
                " OR  (:description is null OR c.description ILIKE cast(:description AS text)) " +
                " ) "
        ,
        countQuery = "SELECT count(c.*) FROM concept_hierarchy c " +
                " WHERE c.change_kind !='BASED_ON' " +
                " AND class_kind='CONCEPT' " +
                " AND ( " +
                " (:name is null OR c.name ILIKE cast(:name AS text)) " +
                " OR  (:description is null OR c.description ILIKE cast(:description AS text)) " +
                " ) "
    )
    fun findByQuery(@Param("name") name:String, @Param("description") description:String, pageable:Pageable):Page<Concept>


//    /**
//     * Find the latest changed revision.
//     * @param id of the entity
//     * @return [Revision]
//     */
//    override fun findLastChangeRevision(id: UUID): Optional<Revision<Int, Concept>>
//
//    /**
//     * Find the latest changed revision.
//     * @param id of the entity
//     * @param revisionNumber of the entity
//     * @return [Revision]
//     */
//    override fun findRevision(id: UUID, revisionNumber: Int): Optional<Revision<Int, Concept>>
//
//    /**
//     * Find the entity based on a revision number.
//     * @param id of the entity
//     * @param revision number of the entity
//     * @return [Revision] at the given revision
//     */
//    override fun findRevisions(id: UUID): Revisions<Int, Concept>
//
//    /**
//     * Find all revisions and return in a pageable view
//     * @param id of the entity
//     * @param pageable from controller method
//     * @return [Page] of the entity
//     */
//    override fun findRevisions(id: UUID, pageable: Pageable): Page<Revision<Int, Concept>>
}
