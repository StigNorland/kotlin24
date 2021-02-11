package no.nsd.qddt.domain.responsedomain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.borders.DottedBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.domain.category.CategoryType
import no.nsd.qddt.classes.AbstractEntityAudit
import no.nsd.qddt.classes.interfaces.IWebMenuPreview
import no.nsd.qddt.classes.pdf.PdfReport
import no.nsd.qddt.classes.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.domain.category.HierarchyLevel
import no.nsd.qddt.utils.StringTool.CapString
import org.hibernate.envers.Audited
import javax.persistence.*
import java.util.ArrayList

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

  override var name: String = ""
    get() =  CapString(field)

  @Column(length = 2000, nullable = false)
  var description:String = ""

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
  var responseCardinality: ResponseCardinality = ResponseCardinality()

  @JsonIgnore
  @OrderColumn(name = "responsedomain_idx")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CODE", joinColumns =[JoinColumn(name = "responsedomain_id", referencedColumnName = "id")]  )
  var codes : MutableList<Code> = mutableListOf()


  /**
   * a link to a category root/group (template)
   * the managed representation is never reused (as was intended),
   * so we want to remove it when the responseDomain is removed. -> CascadeType.REMOVE
  **/
  @ManyToOne(fetch = FetchType.EAGER , cascade = [CascadeType.REMOVE])
  @JoinColumn(name = "category_id")
  var managedRepresentation:Category = Category(hierarchyLevel = HierarchyLevel.GROUP_ENTITY)
    get() {
      if (codes.size > 0)
        field.codes =codes
      return field
    }
  set(value) {
    field = value
    codes = value.codes

  }

  override fun fillDoc(pdfReport: PdfReport, counter: String) {
    val table = com.itextpdf.layout.element.Table(UnitValue.createPercentArray(floatArrayOf(15.0f, 70.0f, 15.0f)))
      .setKeepTogether(true)
      .setKeepTogether(true)
      .setWidth(pdfReport.width100 * 0.8f)
      .setBorder(DottedBorder(ColorConstants.GRAY, 1F))
      .setFontSize(10F)
    table.addCell(Cell(1, 2)
      .add(Paragraph(this.name))
      .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
      .addCell(Cell()
        .setTextAlignment(TextAlignment.RIGHT)
        .add(Paragraph(String.format("Version %s", version))))
    for (cat in getFlatManagedRepresentation(managedRepresentation))
      if (cat.categoryKind === CategoryType.CATEGORY)
      {
        table.addCell(Cell()
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
        table.addCell(Cell().add(Paragraph(cat.label))
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
        table.addCell(Cell()
          .setTextAlignment(TextAlignment.CENTER)
          .add(Paragraph(cat.code.value))
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
      }
      else
      {
        table.addCell(Cell().add(Paragraph(cat.categoryKind.name))
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F))
        )
        table.addCell(Cell(1, 2).add(Paragraph(cat.label))
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
      }
    pdfReport.theDocument?.add(table)
  }

  public override fun beforeUpdate() {
    TODO("Not yet implemented")
  }

  override fun beforeInsert() {
    TODO("Not yet implemented")
  }

  val managedRepresentationFlatten get() = getFlatManagedRepresentation(managedRepresentation)

  override val xmlBuilder:XmlDDIFragmentBuilder<ResponseDomain>
  get() = ResponseDomainFragmentBuilder(this)
  

  /**
 * this is useful for populating codes before saving to DB
 */
  fun populateCodes() {
    this.codes = managedRepresentation.codes
  }
  

  protected fun getFlatManagedRepresentation(current:Category?):List<Category> {
    var retval = mutableListOf<Category>()
    return when (current) {
        null -> retval
        else -> {
          retval.add(current)
          current.children?.forEach { retval.addAll(getFlatManagedRepresentation(it)) }
          retval
        }
    }
  }

}
//    if (field == null)
//    responseCardinality = managedRepresentation.inputLimit
//    if (managedRepresentation.categoryType === CategoryType.MIXED)
//    {
//      setName(String.format("Mixed [%s]", managedRepresentation.getChildren().stream().map(???({ Category.label })).collect(Collectors.joining(" + "))))
//    }
//    if (StringTool.IsNullOrTrimEmpty(managedRepresentation.label))
//    managedRepresentation.setLabel(name)
//    managedRepresentation.setName(managedRepresentation.categoryType.name + "[" + (if ((getId() != null)) getId().toString() else name) + "]")
//    if (managedRepresentation.getHierarchyLevel() === HierarchyLevel.GROUP_ENTITY)
//    managedRepresentation.setDescription(managedRepresentation.categoryType.description)
//    else
//    managedRepresentation.setDescription(description)
//    managedRepresentation.setChangeComment(getChangeComment())
//    managedRepresentation.setChangeKind(getChangeKind())
//    managedRepresentation.setXmlLang(xmlLang)
//    if (!version.isModified())
//    {
//      LOG.debug("onUpdate not run yet ♣♣♣ ")
//    }
//    managedRepresentation.setVersion(version)
//    LOG.debug("ResponseDomain PrePersist " + name + " - " + version)
//  }

