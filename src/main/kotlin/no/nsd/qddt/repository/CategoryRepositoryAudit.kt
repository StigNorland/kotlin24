package no.nsd.qddt.repository

import no.nsd.qddt.model.Category
import no.nsd.qddt.repository.projection.CategoryListe
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "categories", collectionResourceRel = "category", itemResourceRel = "Category", excerptProjection = CategoryListe::class)
interface CategoryRepositoryAudit  : RevisionRepository<Category, UUID, Int>
