package no.nsd.qddt.model.builder

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.builder.xml.XmlDDIFragmentBuilder
import no.nsd.qddt.model.enums.ElementKind
import java.util.stream.Collectors
/**
* @author Stig Norland
*/
class QuestionItemFragmentBuilder(questionItem: QuestionItem):XmlDDIFragmentBuilder<QuestionItem>(questionItem) {
    private val xmlQuestionItem = (
    "%1\$s" +
    "\t\t\t<d:QuestionItemName>\n" +
    "\t\t\t\t<r:String>%2\$s</r:String>\n" +
    "\t\t\t</d:QuestionItemName>\n" +
    "\t\t\t<d:QuestionText isStructureRequired=\"false\">\n" +
    "\t\t\t\t<d:LiteralText>\n" +
    "\t\t\t\t\t<d:Text xml:lang=\"nb-NO\" isPlainText=\"false\" xml:space=\"default\">%5\$s</d:Text>\n" +
    "\t\t\t\t</d:LiteralText>\n" +
    "\t\t\t</d:QuestionText>\n" +
    "\t\t\t<d:QuestionIntent>\n" +
    "\t\t\t\t<r:Content %3\$s isPlainText=\"false\">%4\$s</r:Content>\n" +
    "\t\t\t</d:QuestionIntent>\n" +
    "%6\$s" +
    "%7\$s")
      // r:ConceptReference/r:URN"/>
      // r:ConceptReference/r:TypeOfObject" defaultValue="Concept" fixedValue="true"/>
    private val responseBuilder = questionItem.response?.xmlBuilder()

    override val xmlFragment:String
        get() {
            return String.format(
                xmlQuestionItem,
                 getXmlHeader(entity),
                 entity.name,
                 getXmlLang(entity),
                 entity.intent,
                 entity.question,
                 (responseBuilder?.getXmlEntityRef(3) ?: "<empty>") + conceptRef,
                 getXmlFooter(entity))
        }

    protected val conceptRef:String
        get() {
            return entity.parentRefs.stream()
            .map { cr ->
                val urn = String.format(xmlURN, cr.entity?.agency?.name, cr.id, cr.version.toDDIXml())
                String.format(xmlRef, "Concept", urn, "\t\t\t")
            }.collect(Collectors.joining())
        }

    override fun addXmlFragments(fragments: Map<ElementKind, MutableMap<String, String>>) {
        super.addXmlFragments(fragments)
        entity.parentRefs.stream().forEach{ it.entity?.xmlBuilder()?.addXmlFragments(fragments) }
        responseBuilder?.addXmlFragments(fragments)
    }

}
