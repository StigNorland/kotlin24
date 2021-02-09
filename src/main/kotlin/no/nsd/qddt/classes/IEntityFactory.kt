package no.nsd.qddt.classes

import no.nsd.qddt.classes.interfaces.IArchived
import java.util.UUID


/**
 * @author Stig Norland
 */
interface IEntityFactory<T : AbstractEntityAudit> {
    fun create(): T
    fun copyBody(source: T, dest: T): T
    fun copy(source: T, revision: Int?): T {
        val rev = when {
            source.isNewCopy -> null
            else -> revision
        }
        return copyBody(source,makeNewCopy(source, rev))
    }

    fun makeNewCopy(source: T, revision: Int?): T { 
        
        return if (revision != null) {
            create().apply {
            basedOnObject = source.id
            basedOnRevision = revision
            changeKind = AbstractEntityAudit.ChangeKind.BASED_ON
            changeComment = when (source.changeComment) {
                    "" -> "based on " + source.name
                    else -> source.changeComment
                }
            }
        } else {
            create().apply {
                changeKind = AbstractEntityAudit.ChangeKind.NEW_COPY
                changeComment = "copy of " + (source.name)
            }
        }.apply { 
            version.versionLabel = "COPY OF [" + (source.name) + "]"
            (this as IArchived).isArchived = false
            classKind = source.classKind
            name = source.name
            xmlLang = source.xmlLang
        }
    }
}
