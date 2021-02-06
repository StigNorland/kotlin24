package no.nsd.qddt.domain.category

import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "categoryList", types = [Category::class])
    interface CategoryList {
        var id: UUID
        var label: String
        var name: String
        var version: Version
        var inputLimit: ResponseCardinality
        var hierarchyLevel: HierarchyLevel
        var categoryType: CategoryType
        var code: Code?
        var format: String?
        var children: List<CategoryList> = ArrayList()
    }

@Projection(name = "managedRepresentation", types = [Category::class])
    interface ManagedRepresentation {

        var label: String
        var description: String
        var inputLimit: ResponseCardinality
        var classificationLevel: CategoryRelationCodeType
        var hierarchyLevel: HierarchyLevel
        var categoryType: CategoryType
        var format: String?
        var children: List<ManagedRepresentation> = ArrayList()
    }