package no.nsd.qddt.model

import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefCondition
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.SequenceKind
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("SEQUENCE_CONSTRUCT")
data class Sequence(
    @Column(length = 3000)
    var description: String? = null
) : ControlConstruct() {


    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    var sequenceKind: SequenceKind = SequenceKind.SECTION

    @AttributeOverrides(
        AttributeOverride(name = "elementId",column = Column(name = "questionitem_id")),
        AttributeOverride(name = "elementRevision",column = Column(name = "questionitem_revision")),
        AttributeOverride(name = "name",column = Column(name = "question_name", length = 25)),
        AttributeOverride(name = "condition",column = Column(name = "question_text", length = 1500)),
        AttributeOverride(name = "version.revision",column = Column(name = "questionitem_revision"))
    )
    @Embedded
    var condition: ElementRefCondition? = null


    @OrderColumn(name = "sequence_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_SEQUENCE",
        joinColumns = [JoinColumn(name = "sequence_id", referencedColumnName = "id")]
    )
    var sequence: MutableList<ElementRefEmbedded<ControlConstruct>> = mutableListOf()

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "universe_idx")
    @JoinTable(
        name = "control_construct_universe",
        joinColumns = [JoinColumn(name = "question_construct_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "universe_id", referencedColumnName = "id")]
    )
    var universe: MutableList<Universe> = mutableListOf()




//
//    override fun getParameterIn(): Set<Parameter<*>> {
//        val tmp: MutableSet<Parameter<*>> = getSequence()!!.stream()
//            .filter(Predicate<ElementRefEmbedded<ControlConstruct?>> { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null })
//            .flatMap(Function<ElementRefEmbedded<ControlConstruct?>, Stream<*>> { s: ElementRefEmbedded<ControlConstruct?> ->
//                s.getElement().getParameterIn().stream()
//            }).collect(Collectors.toSet<Any>())
//        tmp.add(Parameter<Any?>(this.name, "IN"))
//        return tmp
//    }
//
//    override fun getParameterOut(): Set<Parameter<*>> {
//        return getSequence()!!.stream()
//            .filter(Predicate<ElementRefEmbedded<ControlConstruct?>> { p: ElementRefEmbedded<ControlConstruct?> -> p.getElement() != null })
//            .flatMap(Function<ElementRefEmbedded<ControlConstruct?>, Stream<*>> { s: ElementRefEmbedded<ControlConstruct?> ->
//                s.getElement().getParameterOut().stream()
//            })
//            .collect(Collectors.toSet<Any>())
//    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {

        pdfReport.addHeader(this, "Sequence $counter")

        description?.let { pdfReport.addParagraph(it) }

        if (universe.isNotEmpty())
            pdfReport.addHeader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }

        sequence.forEach { it.element?.fillDoc(pdfReport, counter) }

        if (comments.size > 0)
            pdfReport.addHeader2("Comments")

        pdfReport.addComments(comments)

    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        return object : ControlConstructFragmentBuilder<Sequence>(this) {
            override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
                super.addXmlFragments(fragments)
                if (children.size == 0) addChildren()
                children.stream()
                    .forEach { it.addXmlFragments(fragments) }
            }

            override val xmlFragment: String
                get() {
                    if (children.size == 0) addChildren()
                    return super.xmlFragment
                }

            private fun addChildren() {
                children.addAll(
                    sequence
                        .filter { it.element != null }
                        .map { it.element!!.xmlBuilder() }.toList()
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Sequence

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
