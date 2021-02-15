package no.nsd.qddt.model
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.model.classes.InstrumentKind
import java.lang.Exception
import javax.persistence.*
import kotlin.jvm.Transient

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
    @JsonBackReference(value = "studyRef")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", updatable = false)
    private var study: Study? = null

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE, CascadeType.MERGE])
    var root: InstrumentNode<*>? = null
    var description: String? = null
    var label: String? = null
        private set
    var externalInstrumentLocation: String? = null

    @Column(name = "instrument_kind")
    @Enumerated(EnumType.STRING)
    private var instrumentKind: InstrumentKind? = null
    fun setLabel(label: String) {
        if (getName() == null) setName(label.toUpperCase())
        this.label = label
    }

    fun getInstrumentKind(): InstrumentKind? {
        return instrumentKind
    }

    fun setInstrumentKind(instrumentKind: InstrumentKind?) {
        this.instrumentKind = instrumentKind
    }

    fun getStudy(): Study? {
        return study
    }

    fun setStudy(study: Study?) {
        this.study = study
    }

    @get:Transient
    val parentRef: ParentRef<Study>?
        get() = try {
            ParentRef(getStudy())
        } catch (ex: Exception) {
            null
        }

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
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Instrument) return false
        if (!super.equals(o)) return false
        val that = o
        if (if (study != null) !study.equals(that.study) else that.study != null) return false
        //        if (sequence != null ? !sequence.equals( that.sequence ) : that.sequence != null) return false;
        if (if (description != null) description != that.description else that.description != null) return false
        if (if (label != null) label != that.label else that.label != null) return false
        return if (if (externalInstrumentLocation != null) externalInstrumentLocation != that.externalInstrumentLocation else that.externalInstrumentLocation != null) false else instrumentKind == that.instrumentKind
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + if (description != null) description.hashCode() else 0
        result = 31 * result + if (label != null) label.hashCode() else 0
        result = 31 * result + if (externalInstrumentLocation != null) externalInstrumentLocation.hashCode() else 0
        result = 31 * result + if (instrumentKind != null) instrumentKind.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return ("{\"Instrument\":"
                + super.toString()
                ) + ", \"study\":" + study
            .toString() + ", \"description\":\"" + description.toString() + "\"" + ", \"label\":\"" + label.toString() + "\"" + ", \"externalInstrumentLocation\":\"" + externalInstrumentLocation.toString() + "\"" + ", \"instrumentKind\":\"" + instrumentKind.toString() + "\"" + "}"
    }

    val xmlBuilder: AbstractXmlBuilder
        get() = InstrumentFragmentBuilder(this)

    fun fillDoc(pdfReport: PdfReport, counter: String?) {
        pdfReport.addParagraph("Instrument...")
    }

    protected fun beforeUpdate() {}
    protected fun beforeInsert() {}
}
