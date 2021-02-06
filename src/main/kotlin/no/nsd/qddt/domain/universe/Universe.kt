package no.nsd.qddt.domain.universe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import org.hibernate.envers.Audited
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty
/**
* @author Stig Norland
*/
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Audited
@Table(name = "UNIVERSE", uniqueConstraints = {@UniqueConstraint(columnNames = {"name","description","agency_id"},name = "UNQ_universe_name")})
class Universe(
    @Column(name = "description", length = 2000,nullable = false)
    var description: String
        set(value) {
            if (IsNullOrTrimEmpty(name) {
            var max25 = description.length()>25?25:description.length
            name = description.toUpperCase().replace(' ','_').substring(0,max25)
        }
):AbstractEntityAudit() {
  
  val xmlBuilder:AbstractXmlBuilder
  get() {
    return UniverseFragmentBuilder(this)
  }
  
  fun fillDoc(pdfReport:PdfReport, counter:String) {}

}