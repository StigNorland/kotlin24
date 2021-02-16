package no.nsd.qddt.model

import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.SequenceKind
import no.nsd.qddt.model.classes.elementref.ElementRefEmbedded
import org.hibernate.envers.Audited
import java.util.ArrayList
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream
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
    private var sequence: List<ElementRefEmbedded<ControlConstruct>>? =
        ArrayList<ElementRefEmbedded<ControlConstruct>>(0)

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    @JoinTable(
        name = "CONTROL_CONSTRUCT_UNIVERSE",
        joinColumns = [JoinColumn(name = "question_construct_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "universe_id", referencedColumnName = "id")]
    )
    var universe: List<Universe> = ArrayList<Universe>(0)

    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    private var sequenceKind: SequenceKind? = null

    @Embedded
    var condition: ElementRefCondition? = null
    @PrePersist
    @PreUpdate
    private fun setDefaults() {
        if (sequenceKind == null) sequenceKind = SequenceKind.SECTION
    }

    fun getSequence(): List<ElementRefEmbedded<ControlConstruct>>? {
        if (sequence == null) {
            LOG.info("sequnece is null")
        } else if (condition != null && !sequence!![0].equals(condition)) {
//            sequence.add( 0,getCondition() );
        }
        return sequence
    }

    fun setSequence(sequence: List<ElementRefEmbedded<ControlConstruct?>?>) {
        this.sequence = sequence
    }

    fun getSequenceKind(): SequenceKind? {
        return sequenceKind
    }

    fun setSequenceKind(sequenceKind: SequenceKind?) {
        this.sequenceKind = sequenceKind
    }

    override fun getParameterIn(): Set<Parameter<*>> {
        val tmp: MutableSet<Parameter<*>> = getSequence()!!.stream()
            .filter(Predicate<ElementRefEmbedded<ControlConstruct?>> { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null })
            .flatMap(Function<ElementRefEmbedded<ControlConstruct?>, Stream<*>> { s: ElementRefEmbedded<ControlConstruct?> ->
                s.getElement().getParameterIn().stream()
            }).collect(Collectors.toSet<Any>())
        tmp.add(Parameter<Any?>(this.name, "IN"))
        return tmp
    }

    override fun getParameterOut(): Set<Parameter<*>> {
        return getSequence()!!.stream()
            .filter(Predicate<ElementRefEmbedded<ControlConstruct?>> { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null })
            .flatMap(Function<ElementRefEmbedded<ControlConstruct?>, Stream<*>> { s: ElementRefEmbedded<ControlConstruct?> ->
                s.getElement().getParameterOut().stream()
            })
            .collect(Collectors.toSet<Any>())
    }

    fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "Sequence $counter")
        pdfReport.addParagraph(description)
        if (universe.size > 0) pdfReport.addheader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }
        getSequence().forEach(Consumer<ElementRefEmbedded<ControlConstruct?>> { entity: ElementRefEmbedded<ControlConstruct?> ->
            entity.getElement().fillDoc(pdfReport, counter)
        })
        if (getComments().size() > 0) pdfReport.addheader2("Comments")
        pdfReport.addComments(getComments())

        // pdfReport.addPadding();
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = object : ControlConstructFragmentBuilder<Sequence?>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind?, Map<String?, String?>?>?) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream()
                    .forEach(Consumer<AbstractXmlBuilder> { c: AbstractXmlBuilder -> c.addXmlFragments(fragments) })
            }

            val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.getXmlFragment()
                }

            private fun addChildren() {
                children.addAll(
                    getSequence()!!.stream()
                        .map(Function<ElementRefEmbedded<ControlConstruct?>, Any> { seq: ElementRefEmbedded<ControlConstruct?> ->
                            seq.getElement().getXmlBuilder()
                        }).collect(Collectors.toList<Any>())
                )
            }
        }

    init {
        setName("root")
    }
}
