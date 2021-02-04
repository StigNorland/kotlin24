package no.nsd.qddt.domain.category

import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty

/**
 * @author Stig Norland
 * @see Category
 */
enum class CategoryType(override val name: String, val description: String, val ddiComment: String) {
    /**
     * single USER INPUT Category,
     */
    DATETIME(
        "DateTime",
        "Single USER INPUT Category, input date and time",
        "NOT_IMPLEMENTED:blankIsMissingValue |regExp"
    ),  /*
        single USER INPUT Category, input one line of text    ,NOT_IMPLEMENTED: blankIsMissingValue |maxLength |minLength |regExp
     */
    TEXT("Text", "Single USER INPUT Category, input one line of text", ""),  /*
        single USER INPUT Category, input is a number      ,NOT_IMPLEMENTED: blankIsMissingValue |format |scale |decimalPositions |interval
     */
    NUMERIC("Numeric", "Single USER INPUT Category, input is a number", ""),  /*
        True or false. Can be represented by 1 and 0 correspondingly.
     */
    BOOLEAN("Boolean", "True or false. Can be represented by 1 and 0 correspondingly", ""),  /*
        Code: single Category, input is CODE/VALUE                  ,
     */
    CATEGORY("Category", "Single Category, input is CODE/VALUE", "NOT_IMPLEMENTED: blankIsMissingValue"),  /*
        Missing values: CategoryList/CodeList that are used as missingvalues.
     */
    MISSING_GROUP("MissingValue", "CategoryList/CodeList that are used as missingvalues", ""),  /*
        List: CategoryList/CodeList                                 ,NOT_IMPLEMENTED: xml:lang |isMaintainable |isSystemMissingValue
     */
    LIST("CodeList", "CategoryList/CodeList", "NOT_IMPLEMENTED: isMaintainable |isSystemMissingValue"),  /*
        CategoryGroup/root -> ScaleDomain/ input is CODE/VALUE pairs,NOT_IMPLEMENTED: blankIsMissingValue
     */
    SCALE("Scale", "CategoryGroup/root -> ScaleDomain/ input is CODE/VALUE pairs", ""),  /*
        ONLY for CategoryRoot -> a collection of different responsedomains
     */
    MIXED("MixedManRep", "Mixed Mananged representation -> a collection of mananged representations", "");

    companion object {
        fun getEnum(name: String): CategoryType? {
            if (IsNullOrTrimEmpty(name)) return null
            for (v in values()) if (name.equals(v.toString(), ignoreCase = true) || name.equals(
                    v.name,
                    ignoreCase = true
                )
            ) return v
            throw IllegalArgumentException("Enum value not valid $name")
        }
    }
}
