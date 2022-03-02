package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.InstrumentFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.InstrumentKind
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * You change your meaning by emphasizing different words in your sentence. ex:
 * "I never said she stole my money" has 7 meanings.
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@Entity
@Table(name = "INSTRUMENT")
@Cacheable
data class Instrument(
    override var name: String = "",
    @Column(length = 1000)
    var description: String? = null

) : AbstractEntityAudit() {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var study: Study

    var label: String = ""
        protected set(value) {
            field = value
            if (name.isBlank()) {
                name = value.uppercase(Locale.getDefault())
            }
        }

    var externalInstrumentLocation: String? = null

    @Enumerated(EnumType.STRING)
    var instrumentKind = InstrumentKind.QUESTIONNAIRE_SEMISTRUCTURED

    // @JsonBackReference(value = "studyRef")
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "study_id", updatable = false)


    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    var root: InstrumentElement? = InstrumentElement()


    ////    TODO implement outparams....
    //    @Transient
    //    @JsonSerialize
    //    public Map<String,OutParameter> getOutParameter() {
    //        this.sequence.stream()
    //            .flatMap( p -> p.getOutParameters().stream() )
    //            .collect( Collectors.toMap(OutParameter::getId, op -> op ) )
    //            .values()
    //            .collect( TreeMap::new, TreeMap::putAll, (map1, map2) -> { map1.putAll(map2); return map1; });
    //    }


    override fun xmlBuilder(): AbstractXmlBuilder {
        return InstrumentFragmentBuilder(this)
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addParagraph("Instrument...")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Instrument

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
