package no.nsd.qddt.domain.category.json

import no.nsd.qddt.domain.ResponseCardinality
import no.nsd.qddt.domain.category.*
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Stig Norland
 */
class ManagedRepresentationJsonView {
    var id: UUID? = null
        private set
    var label: String? = null

    @Embedded
    var inputLimit: ResponseCardinality? = null
        private set

    @Enumerated(EnumType.STRING)
    var classificationLevel: CategoryRelationCodeType? = null
        private set

    @Enumerated(EnumType.STRING)
    var hierarchyLevel: HierarchyLevel? = null
        private set

    @Enumerated(EnumType.STRING)
    var categoryType: CategoryType? = null
        private set
    var format: String? = null
    var children: List<CategoryJsonView> = ArrayList()
        private set

    constructor() {}
    constructor(category: Category) {
        id = category.id
        label = category.label
        children = category.children.stream().map { category: Category -> CategoryJsonView(category) }
            .collect(Collectors.toList())
        inputLimit = category.inputLimit
        classificationLevel = category.classificationLevel
        hierarchyLevel = category.hierarchyLevel
        categoryType = category.categoryType
        format = category.format
    }
}
