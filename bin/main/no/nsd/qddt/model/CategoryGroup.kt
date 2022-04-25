//package no.nsd.qddt.model
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize
//import com.fasterxml.jackson.databind.annotation.JsonSerialize
//import com.itextpdf.layout.element.Paragraph
//import no.nsd.qddt.model.builder.CategoryFragmentBuilder
//import no.nsd.qddt.model.builder.pdf.PdfReport
//import no.nsd.qddt.model.classes.AbstractCategory
//import no.nsd.qddt.model.classes.AbstractEntityAudit
//import no.nsd.qddt.model.embedded.UriId
//import no.nsd.qddt.model.embedded.Code
//import no.nsd.qddt.model.embedded.ResponseCardinality
//import no.nsd.qddt.model.enums.CategoryRelationCodeType
//import no.nsd.qddt.model.enums.CategoryKind
//import no.nsd.qddt.model.enums.HierarchyLevel
//import no.nsd.qddt.model.interfaces.IBasedOn
//import no.nsd.qddt.utils.StringTool
//import org.hibernate.Hibernate
//import org.hibernate.envers.Audited
//import java.util.*
//import javax.persistence.*
//
///**
// *
// *
// * CategoryScheme : Categories provide enumerated representations for
// * concepts and are used by questions, category lists, and variables
// *
// *
// * CodeListScheme : Code lists link a specific value with a category and
// * are used by questions and variables
// *
// *
// * ManagedRepresentationScheme : Reusable representations of numeric,
// * textual datetime, scale or missing values types.
// *
// *
// * CodeType (aka Code) A structure that links a unique value of a category to a
// * specified category and provides information as to the location of the category
// * within a hierarchy, whether it is discrete, represents a total for the CodeList contents,
// * and if its sub-elements represent a comprehensive coverage of the category.
// * The Code is identifiable, but the value within the category must also be unique within the CodeList.
// *
// *
// * @author Stig Norland
// * @author Dag Østgulen Heradstveit
// */
//
//@Audited
//@Entity
//@DiscriminatorValue("GROUP_ENTITY")
//data class CategoryGroup(override var label: String = "") : AbstractCategory() {
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "managedRepresentation",  fetch = FetchType.LAZY)
//    private var responseDomains: MutableSet<ResponseDomain> = mutableSetOf()
//
//    @OrderColumn(name = "category_idx")
//    @ManyToMany(fetch = FetchType.LAZY)
//    var children: MutableList<AbstractCategory> =  mutableListOf()
//        get() = when(hierarchyLevel){
//            HierarchyLevel.GROUP_ENTITY -> field
//            else -> mutableListOf()
//        }
//
//
//    fun addChildren(entity: AbstractCategory): AbstractCategory {
//        this.children.add(entity)
//        this.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
//        this.changeComment =  String.format("Added [${entity.name}]")
//        return entity
//    }
//
//    override fun fillDoc(pdfReport: PdfReport, counter: String) {
//        val document = pdfReport.getTheDocument()
//        when (categoryKind) {
//            CategoryKind.DATETIME, CategoryKind.TEXT, CategoryKind.NUMERIC, CategoryKind.BOOLEAN, CategoryKind.CATEGORY -> {
//                document.add(Paragraph("Category $label"))
//                document.add(Paragraph("Type ${categoryKind.name}"))
//            }
//            CategoryKind.MISSING_GROUP -> {
//            }
//            CategoryKind.LIST -> {
//            }
//            CategoryKind.SCALE -> {
//            }
//            CategoryKind.MIXED -> {
//            }
//        }
//        document.add(Paragraph(" "))
//    }
//
//    override fun isValid(): Boolean {
//        return when (categoryKind) {
//            CategoryKind.MISSING_GROUP,
//            CategoryKind.LIST ->
//                children.size > 0 && inputLimit.valid() && classificationLevel != null
//            CategoryKind.SCALE ->
//                children.size >= 2 && inputLimit.valid() && classificationLevel != null
//            CategoryKind.MIXED ->
//                children.size >= 2 && classificationLevel != null
//            else -> false
//        }
//    }
//    override fun xmlBuilder() = CategoryFragmentBuilder(this)
//
//    override fun clone(): CategoryGroup {
//        return CategoryGroup().also {
//            it.name = name
//            it.label = label
//            it.inputLimit = inputLimit
//            it.classificationLevel = classificationLevel
//            it.format = format
//            it.hierarchyLevel = hierarchyLevel
//            it.categoryKind = categoryKind
//            it.children = children
//            it.description = description
//            if (changeKind.ordinal >= IBasedOn.ChangeKind.BASED_ON.ordinal && changeKind.ordinal <= IBasedOn.ChangeKind.REFERENCED.ordinal) {
//                it.changeKind = changeKind
//                it.changeComment = changeComment
//                it.basedOn = basedOn
//            } else {
//                it.changeKind = IBasedOn.ChangeKind.NEW_COPY
//                it.changeComment = "Clone of [$name]"
//                it.basedOn = UriId.fromAny("$id:0")
//            }
//        }
//    }
//
//}
