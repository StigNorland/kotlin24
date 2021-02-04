package no.nsd.qddt.domain.category.json

import no.nsd.qddt.domain.ResponseCardinality
import no.nsd.qddt.domain.category.*
import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.responsedomain.Code
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Stig Norland
 */
class CategoryJsonView {
    var id: UUID? = null
        private set
    var label: String? = null
        private set
    var name: String? = null
        private set
    var version: Version? = null

    @Embedded
    var inputLimit: ResponseCardinality? = null
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
    var children: List<CategoryJsonView> = ArrayList()
        private set

    constructor() {}
    constructor(category: Category) {
        setId(category.id)
        label = category.label
        name = category.getName()
        version = category.version
        inputLimit = category.inputLimit
        hierarchyLevel = category.hierarchyLevel
        categoryType = category.categoryType
        code = category.code
        format = category.format
        children = category.children.stream().map { category: Category -> CategoryJsonView(category) }
            .collect(Collectors.toList())
    }

    private fun setId(id: UUID) {
        this.id = id
    }
}
