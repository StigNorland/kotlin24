package no.nsd.qddt.model;

import org.hibernate.envers.Audited
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.UniverseFragmentBuilder
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty
/**
* @author Stig Norland
*/
@Audited
@Entity
@Table(
    name = "UNIVERSE",
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["name","description","agency_id"],
        name = "UNQ_universe_name"
    )]                                                      //https://github.com/DASISH/qddt-client/issues/606
)
class Universe(override var name: String = ""):AbstractEntityAudit() {

    @Column(length = 2000)
    var description: String = ""
      set(value) {
        field = value
        if (IsNullOrTrimEmpty(name)) {
          val max25 = minOf(description.length,25)
          name = description.substring(0,max25).toUpperCase().replace(' ','_')
        }
      }

  override val xmlBuilder:AbstractXmlBuilder
    get() = UniverseFragmentBuilder(this)
    
  override fun fillDoc(pdfReport: PdfReport, counter: String) {
    // do nothing.....
  }

  override fun beforeUpdate() {}
  override fun beforeInsert() {}
}
