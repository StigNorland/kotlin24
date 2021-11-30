package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.RevisionId
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind



/**
 * @author Stig Norland
 */
interface IEntityFactory<T : AbstractEntityAudit> {
    fun create(): T
    fun copyBody(source: T, dest: T): T
    fun copy(source: T, revision: Int?): T {
        val rev = when {
            source.isNewCopy() -> null
            else -> revision
        }
        return copyBody(source,makeNewCopy(source, rev))
    }

    fun makeNewCopy(source: T, revision: Int?): T {
        
        return if (revision != null) {
            create().apply {
            basedOn = RevisionId(source.id, revision)
            changeKind = ChangeKind.BASED_ON
            changeComment = when (source.changeComment) {
                    "" -> "based on " + source.name
                    else -> source.changeComment
                }
            }
        } else {
            create().apply {
                changeKind = ChangeKind.NEW_COPY
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
