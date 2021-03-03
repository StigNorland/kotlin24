package no.nsd.qddt.model

// import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.model.builder.InstrumentFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
// import no.nsd.qddt.model.classes.ParentRef
import no.nsd.qddt.model.enums.InstrumentKind
import org.hibernate.envers.Audited
import javax.persistence.*
import java.util.*



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
class Instrument : AbstractEntityAudit() {

    @Column(insertable = false, updatable = false)
    var studyId: UUID? = null

    @ManyToOne
    @JoinColumn(name="studyId")
    var study: Study? = null

    override var name: String = ""


    var label: String = ""
        protected set(value) {
            field = value
            if (name.isBlank()) {
                name = value.toUpperCase()
            }
        }


    var description: String? = null

    var externalInstrumentLocation: String? = null

    @Enumerated(EnumType.STRING)
    var instrumentKind = InstrumentKind.QUESTIONNAIRE_SEMISTRUCTURED

    // @JsonBackReference(value = "studyRef")
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "study_id", updatable = false)


    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE, CascadeType.MERGE])
    var root: InstrumentNode<*>? = null


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

}
