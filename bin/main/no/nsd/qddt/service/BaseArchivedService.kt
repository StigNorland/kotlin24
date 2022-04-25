package no.nsd.qddt.service

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import java.util.*

/**
 * @author Stig Norland
 */
interface BaseArchivedService<T : AbstractEntityAudit> : BaseService<T, UUID> {
    fun doArchive(instance: T): T {
//        var instance: T = instance
        return try {
            if (instance.changeKind === ChangeKind.ARCHIVED) {
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
