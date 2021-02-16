package no.nsd.qddt.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.persistence.*
import org.hibernate.envers.Audited
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.CategoryFragmentBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.CategoryRelationCodeType
import no.nsd.qddt.model.enums.CategoryType
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.utils.StringTool
import java.util.stream.Collectors
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
    uniqueConstraints = [UniqueConstraint(columnNames = ["label", "name", "category_kind"],name = "UNQ_CATEGORY_NAME_KIND")]                                                      //https://github.com/DASISH/qddt-client/issues/606
) 
class Category(
    /**
     *   A display label for the category. May be expressed in multiple languages.
     *   Repeat for labels with different content, for example,
     *   labels with differing length limitations or of different types or applications.
     */
    var label: String? = null,


    /*
     *   A description of the content and purpose of the category.
     *   May be expressed in multiple languages and supports the use of structured content.
     *   Note that comparison of categories is done using the content of description.
    */
    @Column(length = 2000)
    var description: String? = null,
//        set(value) {
//            field =  StringTool.CapString(value)
//        }
//        get {
//            if StringTool.IsNullOrEmpty(field)
//                field = categoryType.name
//            return field
//        },

    /**
     *  This field is only used for categories that facilitates user input.
     *  like numeric range / text length /
     */
    @Embedded
    var inputLimit: ResponseCardinality = ResponseCardinality(),

    @Column(name = "classification_level")
    @Enumerated(EnumType.STRING)
    var classificationLevel: CategoryRelationCodeType? = null,

    /**
     *  format is used by datetime, and other kinds if needed.
     */
    var format: String = "",

    @Column(name = "Hierarchy_level", nullable = false)
    @Enumerated(EnumType.STRING)
    var hierarchyLevel: HierarchyLevel


) : AbstractEntityAudit(), Comparable<Category>, Cloneable {

    override var name: String = ""
        get() {
            if (StringTool.IsNullOrTrimEmpty(field))
                field = label!!.toUpperCase()
            return field
        }

    @Column(name = "category_kind")
    @Enumerated(EnumType.STRING)
    var categoryKind: CategoryType = CategoryType.CATEGORY
        set(value) {
            field = value
            when (value) {
                CategoryType.MISSING_GROUP,
                CategoryType.LIST -> {
                    classificationLevel = CategoryRelationCodeType.Nominal
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                CategoryType.SCALE -> {
                    classificationLevel = CategoryRelationCodeType.Interval
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                CategoryType.MIXED -> {
                    classificationLevel = CategoryRelationCodeType.Continuous
                    hierarchyLevel = HierarchyLevel.GROUP_ENTITY
                }
                else -> hierarchyLevel = HierarchyLevel.ENTITY
            }
        }


    @Transient
    @JsonSerialize
    @JsonDeserialize
    var code: Code = Code("")

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "category_idx")
    var children: MutableList<Category> =  mutableListOf()
    get() {
        return if (categoryKind == CategoryType.SCALE) {
            if (field.isEmpty()) logger.error("getChildren() is 0/NULL")
            field.stream().filter { obj: Category? -> Objects.nonNull(obj) }
                .sorted(Comparator.comparing { obj: Category -> obj.code })
                .toList() as MutableList<Category>
        } else
            field.stream()
                .filter { obj: Category? -> Objects.nonNull(obj) }
                .toList() as MutableList<Category>
        }
    set(value) {
        field =
        when (categoryKind) {
            CategoryType.SCALE -> value.stream().sorted(Comparator.comparing { obj: Category -> obj.code }).collect(Collectors.toList())
            else -> value
        }
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        val document: Document = pdfReport.theDocument!!
        when (categoryKind) {
            CategoryType.DATETIME, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.BOOLEAN, CategoryType.CATEGORY -> {
                document.add(Paragraph("Category $label"))
                document.add(Paragraph("Type ${categoryKind.name}"))
            }
            CategoryType.MISSING_GROUP -> {
            }
            CategoryType.LIST -> {
            }
            CategoryType.SCALE -> {
            }
            CategoryType.MIXED -> {
            }
        }
        document.add(Paragraph(" "))
    }

    /*
    preRec for valid Categories
     */
    val isValid: Boolean
        get() = if (hierarchyLevel == HierarchyLevel.ENTITY) when (categoryKind) {
            CategoryType.DATETIME, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.BOOLEAN -> children.size == 0 && inputLimit.valid()
            CategoryType.CATEGORY -> children.size == 0 && label != null && label!!.trim { it <= ' ' }.isNotEmpty() && name.trim { it <= ' ' }
                .isNotEmpty()
            else -> false
        } else when (categoryKind) {
            CategoryType.MISSING_GROUP, CategoryType.LIST -> children.size > 0 && inputLimit.valid() && classificationLevel != null
            CategoryType.SCALE -> children.size >= 2 && inputLimit.valid() && classificationLevel != null
            CategoryType.MIXED -> children.size >= 2 && classificationLevel != null
            else -> false
        }

    override fun compareTo(other: Category): Int {
        var i = this.agency.compareTo(other.agency)
        if (i != 0) return i
        i = hierarchyLevel.compareTo(other.hierarchyLevel)
        if (i != 0) return i
        i = categoryKind.compareTo(other.categoryKind)
        if (i != 0) return i
        i = name.compareTo(other.name)
        if (i != 0) return i
        i = label!!.compareTo(other.label!!)
        if (i != 0) return i
        i = description!!.compareTo(other.description!!)
        if (i != 0) return i
        i = this.id.compareTo(other.id)
        return if (i != 0) i else modified.compareTo(other.modified)
    }

    override fun beforeUpdate() {
        TODO("Not yet implemented")
    }

    override fun beforeInsert() {
        logger.debug("Category beforeInsert $name")
        hierarchyLevel = when (categoryKind) {
            CategoryType.DATETIME, CategoryType.BOOLEAN, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.CATEGORY ->
                HierarchyLevel.ENTITY
            CategoryType.MISSING_GROUP, CategoryType.LIST, CategoryType.SCALE, CategoryType.MIXED ->
                HierarchyLevel.GROUP_ENTITY
        }
        name = name.trim()
    }

    // /used to keep track of current item in the recursive call populateCatCodes
    @Transient
    private var _Index = 0

    //codes.clear();
    // this is useful for populating codes before saving to DB (used in the service)
    var codes: MutableList<Code>
        get() = harvestCatCodes(this)
        set(codes) {
            _Index = 0
            populateCatCodes(this, codes)
            //codes.clear();
        }

    private fun harvestCatCodes(current: Category?): MutableList<Code> {
        val tmplist: MutableList<Code> = mutableListOf()
        if (current == null) return tmplist
        if (current.hierarchyLevel == HierarchyLevel.ENTITY) {
            tmplist.add((current.code))
        }
        current.children.forEach {  tmplist.addAll(harvestCatCodes(it)) }
        return tmplist
    }

    private fun populateCatCodes(current: Category?, codes: List<Code>) {
        assert(current != null)
        if (current!!.hierarchyLevel == HierarchyLevel.ENTITY) {
            try {
                current.code = codes[_Index]
                _Index++
            } catch (iob: IndexOutOfBoundsException) {
                current.code = Code()
            } catch (ex: Exception) {
                logger.error(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString() +
                            " populateCatCodes (catch & continue) " + ex.message + " - " +
                            current
                )
                current.code = Code()
            }
        }
        current.children.forEach { populateCatCodes(it, codes) }
    }

    public override fun clone(): Category {
        return Category(name, label,inputLimit,classificationLevel,format,hierarchyLevel).apply {
            categoryKind = categoryKind
            children = children
            code = code
            description = description
            basedOnObject = id
            changeKind = IBasedOn.ChangeKind.NEW_COPY
            changeComment = "Copy of [$name]"
        }
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = CategoryFragmentBuilder(this)

}
