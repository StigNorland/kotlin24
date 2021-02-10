package no.nsd.qddt.domain.responsedomain
import com.fasterxml.jackson.annotation.JsonIgnore
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.borders.DottedBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import no.nsd.qddt.domain.ResponseCardinality
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.CategoryType
import no.nsd.qddt.domain.category.HierarchyLevel
import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.classes.interfaces.IWebMenuPreview
import no.nsd.qddt.classes.pdf.PdfReport
import no.nsd.qddt.classes.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.utils.StringTool
import org.hibernate.envers.Audited
import javax.persistence.*
import java.util.ArrayList
import java.util.stream.Collectors
import no.nsd.qddt.utils.StringTool.CapString
import no.nsd.qddt.utils.StringTool.IsNullOrEmpty
/**
*
* CodeList A special form of maintainable that allows a single codelist to be maintained outside of a CodeListScheme.
*
*<dl>
* <dt>QuestionGrid</dt><dd>Structures the QuestionGrid as an NCube-like structure providing dimension information, labeling options, and response domains attached to one or more cells within the grid.</dd>
* <dt>QuestionItem</dt><dd>Structure a single Question which may contain one or more response domains (i.e., a list of valid category responses where if "Other" is indicated a text response can be used to specify the intent of "Other").</dd>
* <dt>ResponseInMixed</dt><dd>A structure that provides both the response domain and information on how it should be attached, or related, to other specified response domains in the question.</dd>
*
* <dt>Category</dt><dd>A category (without an attached category) response for a question item.</dd>
* <dd>Implemented as: Code.Category = Code.codeValue;</dd>
*
* <dt>Code</dt><dd>A coded response (where both codes and their related category value are displayed) for a question item.</dd>
* <dd>Implemented as: Code.Category = "A_NAME", Code.CodeValue = "A_VALUE"</dd>
*
* <dt>Numeric</dt><dd>A numeric response (the intent is to analyze the response as a number) for a question item.</dd>
* <dd>Implemented as: Code = NULL; (no category is needed)</dd>
*
* <dt>Scale</dt><dd>A scale response which describes a 1..n dimensional scale of various display types for a question.</dd>
* <dd>Implemented as: Code.CodeValue = valid values 1..n + control codes (N/A, Refuses, can't answer, don't know etc)</dd>
*
* <dt>Text</dt><dd>A textual response.</dd>
* <dd>Implemented as: Code = NULL; (no category is needed)</dd>
*
* <dt>These to be implemented later </dt>
* <dd>-DateTime; A date or time response for a question item.</dd>
* <dd>-Distribution;A distribution response for a question, may only be included in-line.</dd>
* <dd>-Geographic; A geographic coordinate reading as a response for a question item.</dd>
* <dd>-GeographicLocationCode; A response domain capturing the name or category of a Geographic Location as a response for a question item, may only be included in-line.</dd>
* <dd>-GeographicStructureCode;A geographic structure category as a response for a question item, may only be included in-line.</dd>
* <dd>-Location; A location response (mark on an image, recording, or object) for a question, may only be included in-line.</dd>
* <dd>-Nominal; A nominal (check off) response for a question grid response, may only be included in-line.</dd>
* <dd>-Ranking; A ranking response which supports a "ranking" of categories. Generally used within a QuestionGrid, may only be included in-line.</dd>
*</dl>
*
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
@Audited
@Entity
@Table(name = "RESPONSEDOMAIN", uniqueConstraints =
        [UniqueConstraint(name = "UNQ_RESPONSEDOMAIN_NAME",
          columnNames = ["name","category_id","based_on_object"])]
)
class ResponseDomain:AbstractEntityAudit(), IWebMenuPreview {
  /**
 * Can't have two responsedomain with the same template and the same name, unless they are based on
 */
  @JsonIgnore
  @OrderColumn(name = "responsedomain_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CODE", joinColumns = JoinColumn(name = "responsedomain_id", referencedColumnName = "id"))
  val codes = ArrayList<Code>()

  @Column(length = 2000, nullable = false)
  var description:String?
  get() = field?:""

  /**
   * a link to a category root/group (template)
   * the managed representation is never reused (as was intended),
   * so we want to remove it when the responseDomain is removed. -> CascadeType.REMOVE
  **/
  @ManyToOne(fetch = [FetchType.EAGER], cascade = [CascadeType.REMOVE])
  @JoinColumn(name = "category_id")
  var managedRepresentation:Category?=null

  /**
   * Vocabulary for Display layout would suffice with 'Horizontal' (default) vs. Vertical'.
   * @return DisplayLayout
   */
  var displayLayout:String=""

  @Enumerated(EnumType.STRING)
  var responseKind:ResponseKind = ResponseKind.SCALE

  /**
  * Allows the designation of the minimum and maximum number of responses allowed for this response domain.
  **/
  @Embedded
  var responseCardinality:ResponseCardinality = ResponseCardinality()
  protected set
  
  
  vav name:String
  get() =  CapString(field)
  
  @JsonIgnore
  var managedRepresentationFlatten:List<Category>
  get() {
    return getFlatManagedRepresentation(getManagedRepresentation())
  }

  val xmlBuilder:XmlDDIFragmentBuilder<ResponseDomain>
  get() {
    return ResponseDomainFragmentBuilder(this)
  }
  init{
    this.description = ""
  }
  /**
 * this is useful for populating codes before saving to DB
 */
  fun populateCodes() {
    this.codes = managedRepresentation.getCodes()
  }
  fun getManagedRepresentation():Category {
    assert(managedRepresentation != null)
    if (getCodes().size > 0)
    managedRepresentation.setCodes(getCodes())
    return managedRepresentation
  }
  protected fun getFlatManagedRepresentation(current:Category):List<Category> {
    val retval = ArrayList<Category>()
    if (current == null) return retval
    retval.add(current)
    current.getChildren().forEach({ c-> retval.addAll(getFlatManagedRepresentation(c)) })
    return retval
  }
  fun setManagedRepresentation(managedRepresentation:Category) {
    LOG.debug("setManagedRepresentation")
    setCodes(managedRepresentation.getCodes())
    this.managedRepresentation = managedRepresentation
  }
  protected fun beforeInsert() {}
  protected fun beforeUpdate() {
    if (field == null)
    responseCardinality = managedRepresentation.inputLimit
    if (managedRepresentation.categoryType === CategoryType.MIXED)
    {
      setName(String.format("Mixed [%s]", managedRepresentation.getChildren().stream().map(???({ Category.label })).collect(Collectors.joining(" + "))))
    }
    if (StringTool.IsNullOrTrimEmpty(managedRepresentation.label))
    managedRepresentation.setLabel(name)
    managedRepresentation.setName(managedRepresentation.categoryType.name + "[" + (if ((getId() != null)) getId().toString() else name) + "]")
    if (managedRepresentation.getHierarchyLevel() === HierarchyLevel.GROUP_ENTITY)
    managedRepresentation.setDescription(managedRepresentation.categoryType.description)
    else
    managedRepresentation.setDescription(description)
    managedRepresentation.setChangeComment(getChangeComment())
    managedRepresentation.setChangeKind(getChangeKind())
    managedRepresentation.setXmlLang(xmlLang)
    if (!version.isModified())
    {
      LOG.debug("onUpdate not run yet ♣♣♣ ")
    }
    managedRepresentation.setVersion(version)
    LOG.debug("ResponseDomain PrePersist " + name + " - " + version)
  }
  fun getCodes():List<Code> {
    if (codes == null)
    codes = ArrayList<Code>()
    return codes.stream().filter({ c-> !c.isEmpty() }).collect(Collectors.toList<Any>())
  }
  fun setCodes(codes:List<Code>) {
    if (codes.stream().filter({ c-> !c.isEmpty() }).count() > 0)
    this.codes = codes
  }
  fun fillDoc(pdfReport:PdfReport, counter:String) {
    val table = com.itextpdf.layout.element.Table(UnitValue.createPercentArray(floatArrayOf(15.0f, 70.0f, 15.0f)))
    .setKeepTogether(true)
    .setKeepTogether(true)
    .setWidth(pdfReport.width100 * 0.8f)
    .setBorder(DottedBorder(ColorConstants.GRAY, 1))
    .setFontSize(10)
    table.addCell(Cell(1, 2)
                  .add(Paragraph(this.name))
                  .setBorder(DottedBorder(ColorConstants.GRAY, 1)))
    .addCell(Cell()
             .setTextAlignment(TextAlignment.RIGHT)
             .add(Paragraph(String.format("Version %s", version))))
    for (cat in getFlatManagedRepresentation(getManagedRepresentation()))
    if (cat.categoryType === CategoryType.CATEGORY)
    {
      table.addCell(Cell()
                    .setBorder(DottedBorder(ColorConstants.GRAY, 1)))
      table.addCell(Cell().add(Paragraph(cat.label))
                    .setBorder(DottedBorder(ColorConstants.GRAY, 1)))
      table.addCell(Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(Paragraph(if (cat.getCode() != null) cat.getCode().getValue() else cat.categoryType.name()))
                    .setBorder(DottedBorder(ColorConstants.GRAY, 1)))
    }
    else
    {
      table.addCell(Cell().add(Paragraph(cat.categoryType.name()))
                    .setBorder(DottedBorder(ColorConstants.GRAY, 1))
                   )
      table.addCell(Cell(1, 2).add(Paragraph(cat.label))
                    .setBorder(DottedBorder(ColorConstants.GRAY, 1)))
    }
    pdfReport.getTheDocument().add(table)
  }


  fun toString():String {
    return ("{" +
            "\"id\":" + (if (getId() == null) "null" else "\"" + getId() + "\"") + ", " +
            "\"name\":" + (if (name == null) "null" else "\"" + name + "\"") + ", " +
            "\"description\":" + (if (field == null) "null" else "\"" + field + "\"") + ", " +
            "\"displayLayout\":" + (if (displayLayout == null) "null" else "\"" + displayLayout + "\"") + ", " +
            "\"responseKind\":" + (if (responseKind == null) "null" else responseKind) + ", " +
            "\"responseCardinality\":" + (if (field == null) "null" else field) + ", " +
            "\"managedRepresentation\":" + (if (managedRepresentation == null) "null" else managedRepresentation) + ", " +
            "\"modified\":" + (if (getModified() == null) "null" else "\"" + getModified() + "\"") + " , " +
            "\"modifiedBy\":" + (if (getModifiedBy() == null) "null" else getModifiedBy()) +
            "}")
  }
}
