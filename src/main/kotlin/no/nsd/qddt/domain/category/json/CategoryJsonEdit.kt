package no.nsd.qddt.domain.category.json

import no.nsd.qddt.domain.AbstractJsonEdit
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.responsedomain.Code
import java.util.*
import javax.persistence.Embedded
import javax.persistence.EnumType

/**
 * @author Stig Norland
 */
class CategoryJsonEdit : AbstractJsonEdit {
    var label: String? = null
        private set
    var description: String? = null
        private set

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
    var code: Code? = null
        private set
    var format: String? = null
    var children: List<CategoryJsonEdit> = ArrayList()
        private set

    constructor() {}
    constructor(category: Category?) : super(category) {
        children = category!!.children.stream().map { category: Category? -> CategoryJsonEdit(category) }
            .collect(Collectors.toList())
        label = category.label
        description = category.description
        inputLimit = category.inputLimit
        classificationLevel = category.classificationLevel
        hierarchyLevel = category.hierarchyLevel
        categoryType = category.categoryType
        code = category.code
        format = category.format
    }

    companion object {
        private const val serialVersionUID = -2195696472853764486L
    }
}
