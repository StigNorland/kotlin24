package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty
import org.hibernate.envers.Audited
import java.util.*
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
        columnNames = ["name", "description", "agency_id"],
        name = "UNQ_INSTRUCTION_NAME"
    )]
)
class Instruction : AbstractEntityAudit() {
    @get:Column(name = "description", length = 2000, nullable = false)
    var description: String? = null
        private set(value) {
        field = value
        if (IsNullOrTrimEmpty(name)) {
            val max25 = if (description.length > 25) 25 else description.length
            setName(description.toUpperCase().replace(' ', '_').substring(0, max25))
        }
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        TODO("Not yet implemented")
    }

    override fun beforeUpdate() {
        TODO("Not yet implemented")
    }

    override fun beforeInsert() {
        TODO("Not yet implemented")
    }


    override val xmlBuilder: AbstractXmlBuilder
        get() = XmlDDIFragmentBuilder<Instruction>(this) {
            val xmlFragment: String
                get() = java.lang.String.format(
                    xmlInstruction,
                    getXmlHeader(entity),
                    entity.name,
                    entity.description,
                    entity.xmlLang
                )

            fun getXmlEntityRef(depth: Int): String {
                return java.lang.String.format(
                    xmlInsRef,
                    "PRE",
                    getXmlURN(entity),
                    java.lang.String.join("", Collections.nCopies(depth, "\t"))
                )
            }
        }
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}

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
