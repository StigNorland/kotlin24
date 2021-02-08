//package no.nsd.qddt.domain.classes.builders
//
//import no.nsd.qddt.domain.category.Category
//import no.nsd.qddt.domain.category.CategoryType
//import no.nsd.qddt.domain.category.HierarchyLevel
//
///**
// * @author Stig Norland
// */
//class CategoryBuilder {
//    private val name: String? = null
//    private var label: String? = null
//    private var hierarchyLevel: HierarchyLevel = HierarchyLevel.ENTITY
//    private var categoryType: CategoryType = CategoryType.CATEGORY
//
//    //    public CategoryBuilder setName(String name) {
//    //        this.name = name;
//    //        return this;
//    //    }
//    fun setLabel(label: String?): CategoryBuilder {
//        this.label = label
//        return this
//    }
//
//    fun setCode(code: String?): CategoryBuilder {
//        return this
//    }
//
//    fun setHierarchy(hierarchyLevel: HierarchyLevel): CategoryBuilder {
//        this.hierarchyLevel = hierarchyLevel
//        return this
//    }
//
//    fun setType(categoryType: CategoryType): CategoryBuilder {
//        this.categoryType = categoryType
//        return this
//    }
//
//    fun createCategory(): Category {
//        return Category().apply {
//            name = name
//        }
//        category.setName(name)
//        category.setLabel(label)
//        //        Code  aCode = new Code();
////        aCode.setCodeValue(this.code);
////        aCode.setResponseDomain();
////        category.setCode(aCode);
//        category.setHierarchyLevel(hierarchyLevel)
//        category.setCategoryType(categoryType)
//        return category
//    }
//}
