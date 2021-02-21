package no.nsd.qddt.model.builder


import no.nsd.qddt.model.Instruction
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import java.util.*

/**
 * @author Stig Norland
 */
open class InstructionFragmentBuilder(entity: Instruction) : XmlDDIFragmentBuilder<Instruction>(entity) {
    override val xmlRef = """
         %3${"$"}s<r:InterviewerInstructionReference>
         %3${"$"}s	%2${"$"}s%3${"$"}s	<r:TypeOfObject>Instruction</r:TypeOfObject>
         %3${"$"}s	<InstructionAttachmentLocation><r:Value xml:space="default">%1${"$"}s</r:Value></InstructionAttachmentLocation>
         %3${"$"}s</r:InterviewerInstructionReference>
         
         """.trimIndent()
    private val xmlInstruction = """%1${"$"}s			<c:InstructionName>
				<r:String xml:lang="%4${"$"}s">%2${"$"}s</r:String>
			</c:InstructionName>
			<r:Description>
				<r:Content xml:lang="%4${"$"}s" isPlainText="false"><![CDATA[%3${"$"}s]]></r:Content>
			</r:Description>
		</c:Instruction>
"""
    override val xmlFragment: String
        get() = String.format(
            xmlInstruction,
            getXmlHeader(entity),
            entity.name,
            entity.description,
            entity.xmlLang
        )

    override fun getXmlEntityRef(depth: Int): String {
        return String.format(
            xmlRef,
            "PRE",
            getXmlURN(entity),
            Collections.nCopies(depth, "\t").joinToString { "" }
        )
    }
}
