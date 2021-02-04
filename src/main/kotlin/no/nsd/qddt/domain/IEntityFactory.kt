package no.nsd.qddt.domain

import no.nsd.qddt.domain.classes.interfaces.IArchived


/**
 * @author Stig Norland
 */
interface IEntityFactory<T : AbstractEntityAudit?> {
    fun create(): T
    fun copyBody(source: T, dest: T): T
    fun copy(source: T, revision: Int?): T {
        var revision = revision
        if (source!!.isNewCopy) revision = null
        return copyBody(
            source,
            makeNewCopy(source, revision)
        )
    }

    fun makeNewCopy(source: T, revision: Int?): T {
        return create()!!.apply {
            if (revision != null) {
                this!!.basedOnObject = source!!.id
                basedOnRevision = revision
                changeKind = AbstractEntityAudit.ChangeKind.BASED_ON
                when (source.changeComment) {
                    null -> changeComment = "based on " + source.name
                    else -> changeComment = source.changeComment
                }
            } else {
                this!!.changeKind = AbstractEntityAudit.ChangeKind.NEW_COPY
                changeComment = "copy of " + (source?.name ?: '?')
            }
            version!!.versionLabel = "COPY OF [" + (source?.name ?: '?') + "]"
            (this as IArchived).isArchived = false
            classKind = source!!.classKind
            name = source.name
            xmlLang = source.xmlLang
        }
    }
}
