package no.nsd.qddt.model

import no.nsd.qddt.model.builder.UniverseFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.utils.StringTool.isNullOrTrimEmpty
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
* @author Stig Norland
*/
@Cacheable
@Audited
@Entity
@Table(
    name = "UNIVERSE",
    uniqueConstraints = [UniqueConstraint(name = "UNQ_universe_name", columnNames = ["name","description","agency_id"])]                                                      //https://github.com/DASISH/qddt-client/issues/606
)
data class Universe(override var name: String = ""):AbstractEntityAudit() {
//
//    @ManyToMany(mappedBy = "universe")
//    var controlconstruct: MutableList<ControlConstruct> = mutableListOf()


    @Column(length = 2000)
    var description: String = ""
      set(value) {
        field = value
        if (isNullOrTrimEmpty(name)) {
            val max25 = minOf(description.length, 25)
            name = description.substring(0, max25).uppercase(Locale.getDefault()).replace(' ', '_')
        }
      }

  override fun xmlBuilder():AbstractXmlBuilder {
      return UniverseFragmentBuilder(this)
  }
    
  override fun fillDoc(pdfReport: PdfReport, counter: String) {
    // do nothing.....
  }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Universe

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified )"
    }

}
