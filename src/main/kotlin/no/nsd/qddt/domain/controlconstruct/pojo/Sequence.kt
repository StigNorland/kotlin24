package no.nsd.qddt.domain.controlconstruct.pojo

import no.nsd.qddt.domain.classes.elementref.*
import no.nsd.qddt.domain.classes.pdf.PdfReport
import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.controlconstruct.pojo.SequenceKind
import no.nsd.qddt.domain.instrument.pojo.Parameter
import no.nsd.qddt.domain.universe.Universe
import org.hibernate.envers.Audited
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("SEQUENCE_CONSTRUCT")
class Sequence : ControlConstruct() {
    @Column(length = 3000)
    var description: String? = null

    @OrderColumn(name = "sequence_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_SEQUENCE",
        joinColumns = [JoinColumn(name = "sequence_id", referencedColumnName = "id")]
    )
    private var sequence: List<ElementRefEmbedded<ControlConstruct?>>? = ArrayList(0)

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    @JoinTable(
        name = "CONTROL_CONSTRUCT_UNIVERSE",
        joinColumns = [JoinColumn(name = "question_construct_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "universe_id", referencedColumnName = "id")]
    )
    var universe: List<Universe?> = ArrayList(0)

    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    var sequenceKind: SequenceKind? = null

    @Embedded
    var condition: ElementRefCondition<*>? = null
    @PrePersist
    @PreUpdate
    private fun setDefaults() {
        if (sequenceKind == null) sequenceKind = SequenceKind.SECTION
    }

    fun getSequence(): List<ElementRefEmbedded<ControlConstruct?>>? {
        if (sequence == null) {
            LOG.info("sequnece is null")
        } else if (condition != null && !sequence!![0].equals(condition)) {
//            sequence.add( 0,getCondition() );
        }
        return sequence
    }

    fun setSequence(sequence: List<ElementRefEmbedded<ControlConstruct?>>?) {
        this.sequence = sequence
    }

    override var parameterIn: Set<Parameter?>
        get() {
            val tmp = getSequence()!!.stream()
                .filter { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null }
                .flatMap { s: ElementRefEmbedded<ControlConstruct?> -> s.getElement().getParameterIn().stream() }
                .collect(Collectors.toSet())
            tmp.add(Parameter(name, "IN"))
            return tmp
        }
        set(parameterIn) {
            super.parameterIn = parameterIn
        }
    override var parameterOut: Set<Parameter?>
        get() = getSequence()!!.stream()
            .filter { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null }
            .flatMap { s: ElementRefEmbedded<ControlConstruct?> -> s.getElement().getParameterOut().stream() }
            .collect(Collectors.toSet())
        set(parameterOut) {
            super.parameterOut = parameterOut
        }

    override fun fillDoc(pdfReport: PdfReport?, counter: String?) {
        pdfReport!!.addHeader(this, "Sequence $counter")
        pdfReport.addParagraph(description!!)
        if (universe.size > 0) pdfReport.addheader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni!!.description)
        }
        getSequence()!!.forEach(Consumer { entity: ElementRefEmbedded<ControlConstruct?> ->
            entity.getElement()!!
                .fillDoc(pdfReport, counter)
        })
        if (comments.size > 0) pdfReport.addheader2("Comments")
        pdfReport.addComments(comments)

        // pdfReport.addPadding();
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = object : ControlConstructFragmentBuilder<Sequence?>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String>>) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream().forEach { c: AbstractXmlBuilder -> c.addXmlFragments(fragments) }
            }

            override val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.getXmlFragment()
                }

            private fun addChildren() {
                children.addAll(
                    getSequence()!!.stream()
                        .map(Function<ElementRefEmbedded<ControlConstruct?>, AbstractXmlBuilder> { seq: ElementRefEmbedded<ControlConstruct?> ->
                            seq.getElement().getXmlBuilder()
                        }).collect(Collectors.toList())
                )
            }
        }

    init {
        name = "root"
    }
}
