package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.borders.DottedBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import no.nsd.qddt.model.builder.ResponseDomainFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.enums.CategoryKind
import no.nsd.qddt.model.enums.HierarchyLevel
import no.nsd.qddt.model.enums.ResponseKind
import no.nsd.qddt.utils.StringTool.CapString
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import javax.persistence.*

@Cacheable
@Audited
@Entity
@Table(name = "RESPONSEDOMAIN",
       uniqueConstraints =[UniqueConstraint(
         name = "UNQ_RESPONSEDOMAIN_NAME",
         columnNames = ["name","category_id","based_on_object"])])
@JsonPropertyOrder(alphabetic = true, value = ["id","name","description"])
data class ResponseDomain(
  @Column(length = 2000, nullable = false)
  var description:String = ""
):AbstractEntityAudit() {
  /**
 * Can't have two responsedomain with the same template and the same name, unless they are based on
 */


  override var name: String = "?"
    get() =  CapString(field)


  /**
   * Vocabulary for Display layout would suffice with 'Horizontal' (default) vs. Vertical'.
   * @return DisplayLayout
   */
  var displayLayout:String=""

  @Enumerated(EnumType.STRING)
  var responseKind: ResponseKind = ResponseKind.SCALE

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
  @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE] )
  @JoinColumn(name = "category_id", nullable = false)
  var managedRepresentation: Category = Category()


  fun getAnchorLabels(): String {
    return try {
      this.getFlatManagedRepresentation(this.managedRepresentation)
        .filter { it.code != null }
        .joinToString(" + ") { it.label }
    } catch (ex:Exception) {
      logger.error(ex.localizedMessage)
      return "?"
    }
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
      if (cat.categoryKind === CategoryKind.CATEGORY)
      {
        table.addCell(Cell()
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
        table.addCell(Cell().add(Paragraph(cat.label))
          .setBorder(DottedBorder(ColorConstants.GRAY, 1F)))
        table.addCell(Cell()
          .setTextAlignment(TextAlignment.CENTER)
          .add(Paragraph(cat.code?.value))
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
    pdfReport.getTheDocument().add(table)
  }

  override fun xmlBuilder():XmlDDIFragmentBuilder<ResponseDomain> {
    return ResponseDomainFragmentBuilder(this)
  }
  

  protected fun getFlatManagedRepresentation(current: Category?):List<Category> {
    val retval = mutableListOf<Category>()
    return when (current) {
        null -> retval
        else -> {
          retval.add(current)
          if (current.hierarchyLevel==HierarchyLevel.GROUP_ENTITY)
            current.children.forEach { retval.addAll(getFlatManagedRepresentation(it)) }
          retval
        }
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    other as ResponseDomain

    return id != null && id == other.id
  }

  override fun hashCode(): Int = javaClass.hashCode()

  @Override
  override fun toString(): String {
    return this::class.simpleName + "(id = $id , name = $name , classKind = $classKind , modified = $modified )"
  }

}
