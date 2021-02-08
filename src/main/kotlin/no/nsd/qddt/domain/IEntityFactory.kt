package no.nsd.qddt.domain

import no.nsd.qddt.domain.classes.interfaces.IArchived


/**
 * @author Stig Norland
 */
interface IEntityFactory<T : AbstractEntityAudit?> {
    fun create(): T
    fun copyBody(source: T, dest: T): T
    fun copy(source: T, revision: Int?): T {
        val rev = when {
            source!!.isNewCopy -> null
            else -> revision
        }
        return copyBody(source,makeNewCopy(source, rev)
        )
    }

    fun makeNewCopy(source: T, revision: Int?): T {
        return create()!!.apply {
            if (revision != null) {
                this.basedOnObject = source!!.id
                basedOnRevision = revision
                changeKind = AbstractEntityAudit.ChangeKind.BASED_ON
                changeComment = when (source.changeComment) {
                    null -> "based on " + source.name
                    else -> source.changeComment
                }
            } else {
                changeKind = AbstractEntityAudit.ChangeKind.NEW_COPY
                changeComment = "copy of " + (source!!.name)
            }
            version.versionLabel = "COPY OF [" + (source.name) + "]"
            (this as IArchived).isArchived = false
            classKind = source.classKind
            name = source.name
            xmlLang = source.xmlLang
        }
    }
}
