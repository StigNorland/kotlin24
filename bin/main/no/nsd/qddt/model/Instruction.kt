package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nsd.qddt.model.builder.InstructionFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IInstruction
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Audited
@Cacheable
@Table(
    name = "INSTRUCTION",
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["name", "description", "agency_id"],
        name = "UNQ_INSTRUCTION_NAME"
    )]
)
data class Instruction(override var name: String = "") : AbstractEntityAudit() {

    @Column(length = 2000, nullable = false)
    var description: String = ""
        set(value) {
        field = value
        if (name.isBlank() && value.isNotBlank()) {
            val max25 = if (value.length > 25) 25 else value.length
            name = value.uppercase(Locale.getDefault()).replace(' ', '_').substring(0, max25)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Instruction

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }
}
