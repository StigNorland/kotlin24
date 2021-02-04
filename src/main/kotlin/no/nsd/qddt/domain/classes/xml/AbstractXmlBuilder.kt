package no.nsd.qddt.domain.classes.xml

import no.nsd.qddt.domain.AbstractEntity
import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.AbstractEntityAudit.getAgency
import no.nsd.qddt.domain.AbstractEntityAudit.getVersion
import no.nsd.qddt.domain.classes.elementref.*
import no.nsd.qddt.domain.classes.interfaces.*
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author Stig Norland
 */
abstract class AbstractXmlBuilder {
    protected val xmlURN = "<r:URN type=\"URN\" typeOfIdentifier=\"Canonical\">urn:ddi:%1\$s:%2\$s:%3\$s</r:URN>\n"
    protected val xmlHeader =
        """		<%1${"$"}s:%2${"$"}s isUniversallyUnique="true" versionDate="%3${"$"}s" isVersionable="true" %4${"$"}s >
%5${"$"}s"""
    protected val xmlFooter = "\t\t</%1\$s:%2\$s>\n"
    private val xmlLang = "xml:lang=\"%1\$s\""
    private val xmlUserId = "\t\t\t<r:UserID typeOfUserID=\"User.Id\">%1\$s</r:UserID>\n"
    private val xmlRationale = """			<r:VersionResponsibility>%1${"$"}s</r:VersionResponsibility>
			<r:VersionRationale>
				<r:RationaleDescription>
					<r:String xml:lang="en-GB">%2${"$"}s</r:String>
				</r:RationaleDescription>
				<r:RationaleCode>%3${"$"}s</r:RationaleCode>
			</r:VersionRationale>
"""
    private val xmlBasedOn = """			<r:BasedOnObject>
				<r:BasedOnReference isExternal="true" externalReferenceDefaultURI="%3${"$"}s">
					%1${"$"}s					<r:TypeOfObject>%2${"$"}s</r:TypeOfObject>
				</r:BasedOnReference>
				<r:BasedOnRationaleDescription><r:String/></r:BasedOnRationaleDescription>
				<r:BasedOnRationaleCode></r:BasedOnRationaleCode>
			</r:BasedOnObject>
"""

    abstract fun addXmlFragments(fragments: Map<ElementKind?, MutableMap<String?, String>>)
    abstract fun getXmlEntityRef(depth: Int): String?
    abstract val xmlFragment: String?
    protected fun <S : AbstractEntityAudit?> getXmlHeader(instance: S): String {
        val prefix: String = ElementKind.Companion.getEnum(instance.javaClass.getSimpleName()).getDdiPreFix()
        return String.format(
            xmlHeader, prefix,
            instance.javaClass.getSimpleName(),
            getInstanceDate(instance),
            "",
            "\t\t\t" + getXmlURN(instance) + getXmlUserId(instance) + getXmlRationale(instance) + getXmlBasedOn(instance)
        )
    }

    protected fun <S : AbstractEntityAudit?> getXmlFooter(instance: S): String {
        val prefix: String = ElementKind.Companion.getEnum(instance.javaClass.getSimpleName()).getDdiPreFix()
        return String.format(xmlFooter, prefix, instance.javaClass.getSimpleName())
    }

    protected fun <S : AbstractEntityAudit?> getXmlLang(instance: S): String {
        return if (instance!!.xmlLang == null) "" else String.format(xmlLang, instance.xmlLang)
    }

    fun <S : AbstractEntityAudit?> getXmlURN(instance: S): String {
        return java.lang.String.format(
            xmlURN,
            instance!!.getAgency()!!.name,
            instance.id,
            instance.getVersion().toDDIXml()
        )
    }

    protected fun <S : AbstractEntity?> getXmlUserId(instance: S): String {
        return String.format(xmlUserId, instance!!.modifiedBy.id)
    }

    protected fun <S : AbstractEntityAudit?> getXmlRationale(instance: S): String {
        return String.format(
            xmlRationale, instance!!.modifiedBy.getName().toString() + "@" + instance.getAgency()!!.name,
            "[" + instance.changeKind!!.description + "] ➫ " + instance.changeComment, instance.changeKind!!.name
        )
    }

    protected fun <S : AbstractEntityAudit?> getXmlBasedOn(instance: S): String {
        if (instance!!.basedOnObject == null) return ""
        val uri = "https://qddt.nsd.no/preview/" + instance.basedOnObject + "/" + instance.basedOnRevision
        val urn = java.lang.String.format(
            xmlURN,
            instance.getAgency()!!.name,
            instance.basedOnObject,
            Version(1, 0, instance.basedOnRevision, "").toDDIXml()
        )
        return String.format(xmlBasedOn, urn, instance.javaClass.getSimpleName(), uri)
    }

    protected fun <S : AbstractEntityAudit?> getInstanceDate(instance: S): String {
        return instance!!.modified.toLocalDateTime()
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_DATE_TIME)
    }

    protected fun getTabs(depth: Int): String {
        return java.lang.String.join("", Collections.nCopies(depth, "\t"))
    } // protected String html5toXML(String html5){
    //     // https://zenodo.org/record/259546
    //     StringBuffer sb = new StringBuffer();
    //     Pattern p = Pattern.compile("<[^>]*>",
    //         Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    //     MatchResult result = p.matcher(html5)
    //         .appendReplacement(sb,"").toMatchResult();
    //     return sb.toString();
    // }
}
