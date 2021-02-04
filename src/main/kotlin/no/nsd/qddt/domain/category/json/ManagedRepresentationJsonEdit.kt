package no.nsd.qddt.domain.category.json

import no.nsd.qddt.domain.AbstractJsonEdit
import no.nsd.qddt.domain.category.Category
import java.util.*
import javax.persistence.Embedded
import javax.persistence.EnumType

/**
 * @author Stig Norland
 */
class ManagedRepresentationJsonEdit : AbstractJsonEdit {
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

    //    public Code getCode() {
    //        return code;
    //    }
    //
    //    private void setCode(Code code) {
    //        this.code = code;
    //    }
    var format: String? = null

    //    private Code code;
    var children: List<CategoryJsonEdit> = ArrayList()
        private set

    constructor() {}
    constructor(category: Category) : super(category) {
        children = category.children.stream().map { category: Category? -> CategoryJsonEdit(category) }
            .collect(Collectors.toList())
        label = category.label
        description = category.description
        inputLimit = category.inputLimit
        classificationLevel = category.classificationLevel
        hierarchyLevel = category.hierarchyLevel
        categoryType = category.categoryType
        format = category.format
        //        setCode(category.getCode());
    }

    companion object {
        private const val serialVersionUID = -2417561008434743742L
    }
}
