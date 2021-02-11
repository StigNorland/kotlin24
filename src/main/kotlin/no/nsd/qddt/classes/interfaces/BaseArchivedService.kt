package no.nsd.qddt.classes.interfaces

import no.nsd.qddt.classes.AbstractEntityAudit
import java.util.*

/**
 * @author Stig Norland
 */
interface BaseArchivedService<T : AbstractEntityAudit> : BaseService<T, UUID> {
    fun doArchive(instance: T): T {
//        var instance: T = instance
        return try {
            if (instance.changeKind === AbstractEntityAudit.ChangeKind.ARCHIVED) {
                findOne<T>(instance.id!!).apply {
                    (this as IArchived).isArchived = true
                    changeComment = instance.changeComment
                }
            } else instance
        } catch (ex: Exception) {
//            println(ex.message)
            ex.printStackTrace()
            instance
        }
    }
}
