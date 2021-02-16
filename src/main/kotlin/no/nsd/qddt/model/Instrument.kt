package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.model.builder.InstrumentFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.InstrumentKind
import no.nsd.qddt.model.classes.elementref.ParentRef
import org.hibernate.envers.Audited
import javax.persistence.*
import javax.persistence.FetchType




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

    override lateinit var name: String

    var label: String = ""
        protected set(value) {
            field = value
            if (name.isBlank()) {
                name = value.toUpperCase()
            }
        }

    var description: String? = null

    var externalInstrumentLocation: String? = null

    @Column(name = "instrument_kind")
    @Enumerated(EnumType.STRING)
    var instrumentKind = InstrumentKind.QUESTIONNAIRE_SEMISTRUCTURED

    @JsonBackReference(value = "studyRef")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", updatable = false)
    var study: Study? = null


    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE, CascadeType.MERGE])
    var root: InstrumentNode<*>? = null

    val parentRef: ParentRef<Study>?
        get() = study?.let { ParentRef(it) }

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


    override val xmlBuilder: AbstractXmlBuilder
        get() = InstrumentFragmentBuilder(this)

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addParagraph("Instrument...")
    }

    override fun beforeUpdate() {}
    override fun beforeInsert() {}
}
