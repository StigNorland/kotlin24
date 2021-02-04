package no.nsd.qddt.domain.category

import com.itextpdf.layout.Document
import no.nsd.qddt.domain.responsedomain.Code
import no.nsd.qddt.utils.StringTool
import org.joda.time.DateTime
import java.util.*
import java.util.function.Consumer
import javax.persistence.*

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
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["label", "name", "category_kind"],
        name = "UNQ_CATEGORY_NAME_KIND"
    )]
) //https://github.com/DASISH/qddt-client/issues/606
class Category() : AbstractEntityAudit(), Comparable<Category>, Cloneable {
    @Transient
    @JsonSerialize
    @JsonDeserialize
    var code: Code?

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "category_idx")
    private var children: List<Category>? = ArrayList()

    @Column(name = "label")
    @OrderBy
    private var label: String? = null

    @Column(name = "description", length = 2000)
    private var description: String? = null

    @Embedded
    private var inputLimit: ResponseCardinality? = null

    @Column(name = "classification_level")
    @Enumerated(EnumType.STRING)
    var classificationLevel: CategoryRelationCodeType? = null
        private set

    /**
     * format is used by datetime, and other kinds if needed.
     */
    var format: String? = null

    @Column(name = "Hierarchy_level", nullable = false)
    @Enumerated(EnumType.STRING)
    var hierarchyLevel: HierarchyLevel

    @Column(name = "category_kind", nullable = false)
    @Enumerated(EnumType.STRING)
    private var categoryType: CategoryType? = null

    /***
     *
     * @param name Category name
     * @param label Shorter version of name if applicable
     */
    constructor(name: String?, label: String?) : this() {
        name = name
        setLabel(label)
    }

    fun getCategoryType(): CategoryType? {
        if (categoryType == null) setCategoryType(CategoryType.CATEGORY)
        return categoryType
    }

    fun setCategoryType(categoryType: CategoryType?) {
        this.categoryType = categoryType
        if (classificationLevel == null) {
            when (categoryType) {
                CategoryType.MISSING_GROUP, CategoryType.LIST -> {
                    classificationLevel = CategoryRelationCodeType.Ordinal
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
    }

    /*
    A display label for the category.
    May be expressed in multiple languages.
    Repeat for labels with different content, for example,
    labels with differing length limitations or of different types or applications.
     */
    fun getLabel(): String? {
        return SafeString(label)
    }

    fun setLabel(label: String?) {
        this.label = label
    }

    /*
   A description of the content and purpose of the category.
   May be expressed in multiple languages and supports the use of structured content.
   Note that comparison of categories is done using the content of description.
    */
    fun getDescription(): String? {
        if (StringTool.IsNullOrEmpty(description)) description = getCategoryType().getName()
        return StringTool.CapString(description)
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    /**
     * This field is only used for categories that facilitates user input.
     * like numeric range / text length /
     */
    fun getInputLimit(): ResponseCardinality? {
        if (inputLimit == null) setInputLimit(0, 1, 1)
        return inputLimit
    }

    fun setInputLimit(inputLimit: ResponseCardinality?) {
        this.inputLimit = inputLimit
    }

    fun setInputLimit(minimum: Int?, maximum: Int?, step: Int?) {
        inputLimit = ResponseCardinality(minimum, maximum, step)
    }

    fun getChildren(): List<Category> {
        return if (categoryType == CategoryType.SCALE) {
            if (children == null || children!!.size == 0) LOG.error("getChildren() is 0/NULL")
            children!!.stream().filter { obj: Category? -> Objects.nonNull(obj) }
                .sorted(Comparator.comparing { obj: Category -> obj.code })
                .collect(Collectors.toList())
        } else children!!.stream().filter { obj: Category? -> Objects.nonNull(obj) }.collect(Collectors.toList())
    }

    fun setChildren(children: List<Category>?) {
        if (categoryType == CategoryType.SCALE) this.children =
            children!!.stream().sorted(Comparator.comparing { obj: Category -> obj.code }).collect(Collectors.toList())
        this.children = children
    }

    @get:Column(nullable = false)
    var name: String?
        get() {
            if (StringTool.IsNullOrTrimEmpty(super.name)) super.name = getLabel()!!.toUpperCase()
            return super.name
        }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Category) return false
        if (!super.equals(o)) return false
        val category = o
        if (if (getLabel() != null) getLabel() != category.getLabel() else category.getLabel() != null) return false
        return if (if (getDescription() != null) getDescription() != category.getDescription() else category.getDescription() != null) false else hierarchyLevel == category.hierarchyLevel
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (getLabel() != null) getLabel().hashCode() else 0
        result = 31 * result + if (getDescription() != null) getDescription().hashCode() else 0
        result = 31 * result + if (hierarchyLevel != null) hierarchyLevel.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return if (categoryType == CategoryType.CATEGORY) "{" +
                "\"label\":" + (if (label == null) "null" else "\"" + label + "\"") + ", " +
                "\"code\":" + (if (code == null) "null" else code!!.value) +
                "}" else "{" +
                "\"id\":" + (if (id == null) "null" else "\"" + id + "\"") + ", " +
                "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
                "\"label\":" + (if (label == null) "null" else "\"" + label + "\"") + ", " +
                "\"description\":" + (if (description == null) "null" else "\"" + description + "\"") + ", " +
                "\"classificationLevel\":" + (if (classificationLevel == null) "null" else classificationLevel) + ", " +
                "\"format\":" + (if (format == null) "null" else "\"" + format + "\"") + ", " +
                "\"categoryType\":" + (if (categoryType == null) "null" else categoryType) + ", " +
                "\"inputLimit\":" + (if (inputLimit == null) "null" else inputLimit) + ", " +
                "\"children\":" + (if (children == null) "null" else Arrays.toString(children!!.toTypedArray())) + ", " +
                "\"modified\":" + (if (modified == null) "null" else "\"" + modified + "\"") + " , " +
                "\"modifiedBy\":" + (if (modifiedBy == null) "null" else modifiedBy) +
                "}"
    }

    override fun fillDoc(pdfReport: PdfReport?, counter: String?) {
        val document: Document = pdfReport.theDocument
        when (getCategoryType()) {
            CategoryType.DATETIME, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.BOOLEAN, CategoryType.CATEGORY -> {
                document.add(Paragraph("Category " + getLabel()))
                document.add(Paragraph("Type " + getCategoryType()!!.name))
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
    @get:JsonIgnore
    val isValid: Boolean
        get() = if (hierarchyLevel == HierarchyLevel.ENTITY) when (categoryType) {
            CategoryType.DATETIME, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.BOOLEAN -> children!!.size == 0 && inputLimit.isValid()
            CategoryType.CATEGORY -> children!!.size == 0 && label != null && !label!!.trim { it <= ' ' }.isEmpty()
                    && name != null && !name!!.trim { it <= ' ' }.isEmpty()
            else -> false
        } else when (categoryType) {
            CategoryType.MISSING_GROUP, CategoryType.LIST -> children!!.size > 0 && inputLimit.isValid() && classificationLevel != null
            CategoryType.SCALE -> children!!.size >= 2 && inputLimit.isValid() && classificationLevel != null
            CategoryType.MIXED -> children!!.size >= 2 && classificationLevel != null
            else -> false
        }

    override fun compareTo(o: Category): Int {
        var i: Int
        i = this.agency.compareTo(o.agency)
        if (i != 0) return i
        i = hierarchyLevel.compareTo(o.hierarchyLevel)
        if (i != 0) return i
        i = getCategoryType()!!.compareTo(o.getCategoryType()!!)
        if (i != 0) return i
        i = name!!.compareTo(o.name!!)
        if (i != 0) return i
        i = getLabel()!!.compareTo(o.getLabel()!!)
        if (i != 0) return i
        i = getDescription()!!.compareTo(o.getDescription()!!)
        if (i != 0) return i
        i = this.id.compareTo(o.id)
        return if (i != 0) i else super.modified.compareTo(o.modified)
    }

    protected override fun beforeUpdate() {
        LOG.debug("Category beforeUpdate $name")
        if (inputLimit == null) setInputLimit(0, 1, 1)
        beforeInsert()
    }

    override fun beforeInsert() {
        LOG.debug("Category beforeInsert $name")
        if (getCategoryType() == null) setCategoryType(CategoryType.CATEGORY)
        if (hierarchyLevel == null) when (getCategoryType()) {
            CategoryType.DATETIME, CategoryType.BOOLEAN, CategoryType.TEXT, CategoryType.NUMERIC, CategoryType.CATEGORY -> hierarchyLevel =
                HierarchyLevel.ENTITY
            CategoryType.MISSING_GROUP, CategoryType.LIST, CategoryType.SCALE, CategoryType.MIXED -> hierarchyLevel =
                HierarchyLevel.GROUP_ENTITY
        }
        name = name!!.trim { it <= ' ' }
    }

    // /used to keep track of current item in the recursive call populateCatCodes
    @Transient
    private var _Index = 0

    //codes.clear();
    // this is useful for populating codes before saving to DB (used in the service)
    @get:JsonIgnore
    var codes: List<Code>
        get() = harvestCatCodes(this)
        set(codes) {
            _Index = 0
            populateCatCodes(this, codes)
            //codes.clear();
        }

    private fun harvestCatCodes(current: Category?): List<Code> {
        val tmplist: MutableList<Code> = ArrayList(0)
        if (current == null) return tmplist
        if (current.hierarchyLevel == HierarchyLevel.ENTITY && current.code != null) {
            tmplist.add((if (current.code == null) Code("") else current.code!!))
        }
        current.getChildren().forEach(Consumer { c: Category? -> tmplist.addAll(harvestCatCodes(c)) })
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
                LOG.error(
                    DateTime.now().toDateTimeISO().toString() +
                            " populateCatCodes (catch & continue) " + ex.message + " - " +
                            current
                )
                current.code = Code()
            }
        }
        current.getChildren().forEach(Consumer { c: Category? -> populateCatCodes(c, codes) })
    }

    public override fun clone(): Category {
        val clone = Category(name, label)
        clone.setCategoryType(categoryType)
        clone.classificationLevel = classificationLevel
        clone.setChildren(children)
        clone.code = code
        clone.setDescription(description)
        clone.format = format
        clone.hierarchyLevel = hierarchyLevel
        clone.setInputLimit(inputLimit)
        clone.basedOnObject = id
        clone.changeKind = ChangeKind.NEW_COPY
        clone.changeComment = "Copy of [$name]"
        return clone
    }

    val xmlBuilder: AbstractXmlBuilder
        get() = CategoryFragmentBuilder(this)

    init {
        code = Code()
        hierarchyLevel = HierarchyLevel.ENTITY
        setCategoryType(CategoryType.CATEGORY)
        setInputLimit(0, 1, 1)
    }
}
