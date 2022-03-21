package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.IInstructionImpl
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.enums.InstructionRank
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.util.stream.Collectors
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("QUESTION_CONSTRUCT")
data class QuestionConstruct(var description: String = ""): ControlConstruct() {

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "questionitem_id", nullable = true)),
        AttributeOverride(name = "rev", column = Column(name = "questionitem_revision", nullable = true)),
    )
    var questionId: UriId? = null

    @Column(name = "question_name")
    var questionName: String? = null

    @Column(name = "question_text", length = 1500)
    var questionText: String? = null


    @Transient
    @JsonIgnore
    var questionItem: QuestionItem? = null

    @OrderColumn(name = "instruction_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_INSTRUCTION",
        joinColumns = [JoinColumn(name = "control_construct_id", referencedColumnName = "id")]
    )
    var controlConstructInstructions: MutableList<ControlConstructInstruction> = mutableListOf()


//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.fk")
//    @MapKeyColumn(name = "aar")
//    val valgKommentar: Map<Short, FylkeKommentar>?,


//    @OneToMany(fetch = FetchType.EAGER,  mappedBy = "pk.fk")
//    @MapKeyColumn(name = "instructionRank")
//    val controlConstructInstructions2: Map<InstructionRank, MutableList<ControlConstructInstruction>> = mutableMapOf()

    @Transient
    @JsonIgnore
    var preInstructions: List<IInstructionImpl>? = null
        get() = controlConstructInstructions.stream()
            .filter { it.instructionRank == InstructionRank.PRE }
            .map { it.instruction?.let { it1 -> IInstructionImpl(it1) } }
            .collect(Collectors.toList())

    @Transient
    @JsonIgnore
    var postInstructions: List<IInstructionImpl>? = null
        get() = controlConstructInstructions.stream()
            .filter { it.instructionRank == InstructionRank.POST }
            .map { it.instruction?.let { it1 -> IInstructionImpl(it1) } }
            .collect(Collectors.toList())


    override fun xmlBuilder(): AbstractXmlBuilder {
        return object : ControlConstructFragmentBuilder<QuestionConstruct>(this) {
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
                questionItem?.let { children.add(it.xmlBuilder()) }
                children.addAll(
                    universe.stream()
                        .map { it.xmlBuilder() }
                        .collect(Collectors.toList())
                )

                children.addAll(
                    controlConstructInstructions.stream()
                        .filter { it.instruction != null }
                        .map { it.instruction!!.xmlBuilder() }
                        .collect(Collectors.toList())
                )
            }
        }
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "ControlConstruct $counter")
        description.let { pdfReport.addParagraph(it) }

        if (universe.size > 0)
            pdfReport.addHeader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }

        if (preInstructions?.isNotEmpty() == true) {
            pdfReport.addHeader2("Pre Instructions")

            for (pre in preInstructions!!) {
                pre.let { pdfReport.addParagraph(it.description) }
            }
        }

        pdfReport.addHeader2("Question Item")
        questionItem?.name?.let { pdfReport.addParagraph(it) }
        questionItem?.response?.fillDoc(pdfReport, "")

        if (postInstructions?.isNotEmpty() == true) {
            pdfReport.addHeader2("Post Instructions")

            for (post in postInstructions!!) {
                post.let { pdfReport.addParagraph(it.description) }
            }
        }
        if (comments.size > 0)
            pdfReport.addHeader2("Comments")

        pdfReport.addComments(comments)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as QuestionConstruct

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
