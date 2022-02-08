package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.itextpdf.layout.element.Paragraph
import no.nsd.qddt.model.builder.CategoryFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.UriId
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.enums.CategoryRelationCodeType
import no.nsd.qddt.model.enums.CategoryKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.utils.StringTool
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.util.*
import java.util.stream.Collectors
import javax.persistence.*
import kotlin.streams.toList

/**
 *
 *
 * CategoryScheme : Categories provide enumerated representations for
 * concepts and are used by questions, category lists, and variables
 *
 *
 * CodeListScheme : Code lists link a specific value with a category and
 * are used by questions and variables
 *
 *
 * ManagedRepresentationScheme : Reusable representations of numeric,
 * textual datetime, scale or missing values types.
 *
 *
 * CodeType (aka Code) A structure that links a unique value of a category to a
 * specified category and provides information as to the location of the category
 * within a hierarchy, whether it is discrete, represents a total for the CodeList contents,
 * and if its sub-elements represent a comprehensive coverage of the category.
 * The Code is identifiable, but the value within the category must also be unique within the CodeList.
 *
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */


@Audited
@Entity
@Table(
    name = "CATEGORY",
    uniqueConstraints = [UniqueConstraint(columnNames = ["label", "name", "categoryKind","agencyId"],name = "UNQ_CATEGORY_NAME_KIND")]                                                      //https://github.com/DASISH/qddt-client/issues/606
) 
data class Category(var label: String = "") : AbstractEntityAudit(), Comparable<Category>, Cloneable {


    /**
     *   A display label for the category. May be expressed in multiple languages.
     *   Repeat for labels with different content, for example,
     *   labels with differing length limitations or of different types or applications.
     */


    override var name: String = ""
        get() {
            if (field.isBlank())
                field = label.uppercase(Locale.getDefault())
            return field
        }


    /**
     *   A description of the content and purpose of the category.
     *   May be expressed in multiple languages and supports the use of structured content.
     *   Note that comparison of categories is done using the content of description.
    */
    @Column(length = 2000)
    var description: String =""
        set(value) {
            field =  StringTool.CapString(value)
        }
        get() {
            if (field.isEmpty())
                field = categoryKind.description
            return field
        }

    /**
     *  This field is only used for categories that facilitates user input.
     *  like numeric range / text length /
     */
    @Embedded
    var inputLimit: ResponseCardinality = ResponseCardinality()

    @Enumerated(EnumType.STRING)
    var classificationLevel: CategoryRelationCodeType?=null

    /**
     *  format is used by datetime, and other kinds if needed.
     */
    var format: String = ""

    @Column( nullable = false)
    @Enumerated(EnumType.STRING)
    var hierarchyLevel: HierarchyLevel = HierarchyLevel.ENTITY


    @Enumerated(EnumType.STRING)
    var categoryKind: CategoryKind = CategoryKind.CATEGORY
        set(value) {
            field = value
            when (value) {
                CategoryKind.MISSING_GROUP,
                CategoryKind.LIST -> {
                    classificationLevel = CategoryRelationCodeType.Nominal
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                CategoryKind.SCALE -> {
                    classificationLevel = CategoryRelationCodeType.Interval
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                CategoryKind.MIXED -> {
                    classificationLevel = CategoryRelationCodeType.Continuous
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                else -> hierarchyLevel = HierarchyLevel.ENTITY
            }
        }


    @Transient
    @JsonSerialize
    @JsonDeserialize
    var code: Code? = null

    @JsonIgnore
    @OneToMany(mappedBy = "managedRepresentation",  fetch = FetchType.LAZY)
    private var responseDomains: MutableSet<ResponseDomain> = mutableSetOf()

    @OrderColumn(name = "category_idx")
    @ManyToMany(fetch = FetchType.EAGER,cascade = [CascadeType.MERGE])
    var children: MutableList<Category> =  mutableListOf()
//    get() {
//        return if (categoryKind == CategoryKind.SCALE) {
//            if (field.isEmpty()) {
//                logger.error("getChildren() is 0/NULL")
//                return field
//            }
//            field.stream().filter { obj: Category? -> Objects.nonNull(obj) }
//                .sorted(Comparator.comparing { obj: Category -> obj.code?:Code("") })
//                .toList() as MutableList<Category>
//        } else
//            field.stream()
//                .filter { obj: Category? -> Objects.nonNull(obj) }
//                .toList() as MutableList<Category>
//        }
//    set(value) {
//        field = when (categoryKind) {
//            CategoryKind.SCALE ->
//                value.stream().sorted(Comparator.comparing { obj: Category -> obj.code?:Code("") }).collect(Collectors.toList())
//            else ->
//                value
//        }
//    }


    fun addChildren(entity: Category): Category {
        children.add(entity)
        changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
        changeComment =  String.format("Added [${entity.name}]")
        return entity
    }


    /**
    *  preRec for valid Categories
     */
    @JsonIgnore
    fun isValid(): Boolean
    {
        return if (hierarchyLevel == HierarchyLevel.ENTITY) when (categoryKind) {
            CategoryKind.DATETIME, CategoryKind.TEXT, CategoryKind.NUMERIC, CategoryKind.BOOLEAN -> children.size == 0 && inputLimit.valid()
            CategoryKind.CATEGORY -> children.size == 0 && label.trim { it <= ' ' }
                .isNotEmpty() && name.trim { it <= ' ' }
                .isNotEmpty()
            else -> false
        } else when (categoryKind) {
            CategoryKind.MISSING_GROUP, CategoryKind.LIST -> children.size > 0 && inputLimit.valid() && classificationLevel != null
            CategoryKind.SCALE -> children.size >= 2 && inputLimit.valid() && classificationLevel != null
            CategoryKind.MIXED -> children.size >= 2 && classificationLevel != null
            else -> false
        }
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        val document = pdfReport.getTheDocument()
        when (categoryKind) {
            CategoryKind.DATETIME, CategoryKind.TEXT, CategoryKind.NUMERIC, CategoryKind.BOOLEAN, CategoryKind.CATEGORY -> {
                document.add(Paragraph("Category $label"))
                document.add(Paragraph("Type ${categoryKind.name}"))
            }
            CategoryKind.MISSING_GROUP -> {
            }
            CategoryKind.LIST -> {
            }
            CategoryKind.SCALE -> {
            }
            CategoryKind.MIXED -> {
            }
        }
        document.add(Paragraph(" "))
    }

    override fun xmlBuilder() = CategoryFragmentBuilder(this)

    override fun compareTo(other: Category): Int {
        var i = other.agency.let { this.agency.compareTo(it) }
        if (i != 0) return i
        i = hierarchyLevel.compareTo(other.hierarchyLevel)
        if (i != 0) return i
        i = categoryKind.compareTo(other.categoryKind)
        if (i != 0) return i
        i = name.compareTo(other.name)
        if (i != 0) return i
        i = label.compareTo(other.label)
        if (i != 0) return i
        i = description.compareTo(other.description)
        if (i != 0) return i
        i = this.id!!.compareTo(other.id)
        return if (i != 0) i else modified?.compareTo(other.modified)?:0
    }

    public override fun clone(): Category {
        return Category().apply {
            name = name
            label = label
            inputLimit = inputLimit
            classificationLevel = classificationLevel
            format = format
            hierarchyLevel = hierarchyLevel
            categoryKind = categoryKind
            children = children
            code = code
            description = description
            basedOn = UriId.fromAny("$id")
            changeKind = IBasedOn.ChangeKind.NEW_COPY
            changeComment = "Copy of [$name]"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Category

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , classKind = $classKind , modified = $modified)"
    }

}
