package no.nsd.qddt.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import no.nsd.qddt.model.builder.ControlConstructFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.embedded.ElementRefEmbedded
import no.nsd.qddt.model.enums.ConditionKind
import no.nsd.qddt.model.interfaces.IConditionNode
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import org.springframework.http.converter.json.GsonBuilderUtils
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Audited
@DiscriminatorValue("CONDITION_CONSTRUCT")
data class ConditionConstruct(
    @Column(name = "question_text", length = 1500)
    override var condition: String? = null
) : ControlConstruct(), IConditionNode {


    @Enumerated(EnumType.STRING)
    @Column(name = "CONTROL_CONSTRUCT_SUPER_KIND")
    override lateinit var conditionKind: ConditionKind

    @OrderColumn(name = "sequence_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONTROL_CONSTRUCT_SEQUENCE",
        joinColumns = [JoinColumn(name = "sequence_id", referencedColumnName = "id")]
    )
    var sequence: MutableList<ElementRefEmbedded<ControlConstruct>> = mutableListOf()


    override fun xmlBuilder(): AbstractXmlBuilder {
        return ControlConstructFragmentBuilder(this)
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "ConditionConstruct $counter")

        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val codeParser = mapper.readTree(condition!!)


        mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(codeParser).let {
                pdfReport.addParagraph("<code>$it</code>")
            }

        if (universe.size > 0)
            pdfReport.addHeader2("Universe")
        for (uni in universe) {
            pdfReport.addParagraph(uni.description)
        }

        if(sequence.size > 0) {
            pdfReport.addHeader2("Sequence")
            sequence.forEach {
                it.element?.fillDoc(pdfReport, "")
            }
        }


        if (comments.size > 0) {
            pdfReport.addHeader2("Comments")
            pdfReport.addComments(comments)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConditionConstruct

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }
}
