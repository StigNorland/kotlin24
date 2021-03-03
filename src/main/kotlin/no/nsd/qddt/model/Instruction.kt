package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.model.builder.InstructionFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * @author Stig Norland
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Audited
@Table(
    name = "INSTRUCTION",
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["name", "description", "agencyId"],
        name = "UNQ_INSTRUCTION_NAME"
    )]
)
class Instruction : AbstractEntityAudit() {

    override lateinit var name: String

    @Column(length = 2000, nullable = false)
    var description: String? = null
        set(value) {
        field = value
        if (name.isBlank() && value.isNullOrBlank()) {
            val max25 = if (value!!.length > 25) 25 else value.length
            name = value.toUpperCase().replace(' ', '_').substring(0, max25)
        }
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }


    override fun xmlBuilder(): AbstractXmlBuilder {
        return InstructionFragmentBuilder(this)
    }
//        {
//            override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
//                super.addXmlFragments(fragments)
//                if (children.size == 0)
//                    addChildren()
//                children.stream()
//                    .forEach { it.addXmlFragments(fragments) }
//            }
//
//            override val xmlFragment: String
//                get() {
//                    if (children.size == 0) addChildren()
//                    return super.xmlFragment
//                }
//
//            private fun addChildren() {
//                children.addAll(
//                    sequence
//                        .filter { it.element != null }
//                        .map { it.element!!.xmlBuilder() }.toList()
//                )
//            }
//        }


//    override fun xmlBuilder(): AbstractXmlBuilder
//        get() = XmlDDIFragmentBuilder<Instruction>(this) {
//            val xmlFragment: String
//                get() = String.format(
//                    xmlInstruction,
//                    getXmlHeader(entity),
//                    entity.name,
//                    entity.description,
//                    entity.xmlLang
//                )
//
//            fun getXmlEntityRef(depth: Int): String {
//                return java.lang.String.format(
//                    xmlInsRef,
//                    "PRE",
//                    getXmlURN(entity),
//                    java.lang.String.join("", Collections.nCopies(depth, "\t"))
//                )
//            }
//        }


    @JsonIgnore
    @Transient
    protected val xmlInsRef = """
         %3${"$"}s<r:InterviewerInstructionReference>
         %3${"$"}s	%2${"$"}s%3${"$"}s	<r:TypeOfObject>Instruction</r:TypeOfObject>
         %3${"$"}s	<InstructionAttachmentLocation><r:Value xml:space="default">%1${"$"}s</r:Value></InstructionAttachmentLocation>
         %3${"$"}s</r:InterviewerInstructionReference>
         
         """.trimIndent()

    @JsonIgnore
    @Transient
    private val xmlInstruction = """%1${"$"}s			<c:InstructionName>
				<r:String xml:lang="%4${"$"}s">%2${"$"}s</r:String>
			</c:InstructionName>
			<r:Description>
				<r:Content xml:lang="%4${"$"}s" isPlainText="false"><![CDATA[%3${"$"}s]]></r:Content>
			</r:Description>
		</c:Instruction>
"""
}
