package no.nsd.qddt.domain.classes.elementref

import no.nsd.qddt.domain.classes.interfaces.BaseServiceAudit
import no.nsd.qddt.domain.classes.interfaces.IElementRef
import no.nsd.qddt.domain.classes.interfaces.IWebMenuPreview
import org.hibernate.envers.exception.RevisionDoesNotExistException
import org.slf4j.LoggerFactory
import org.springframework.data.history.Revision
import java.util.*

/**
 * @author Stig Norland
 */
class ElementLoader<T : IWebMenuPreview?>(protected var serviceAudit: BaseServiceAudit<*, *, *>) {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)
    fun fill(element: IElementRef<T>): IElementRef<T> {
        val revision: Revision<Int, T> = get(element.elementId, element.elementRevision)
        try {
            element.setElement(revision.entity)
            element.elementRevision = revision.revisionNumber.get()
        } catch (e: Exception) {
            LOG.error("ElementLoader setElement, reference has wrong signature")
        }
        return element
    }

    // uses rev Object to facilitate by rev by reference
    private operator fun get(id: UUID?, rev: Number?): Revision<*, *> {
        return try {
            if (rev == null || rev.toInt() == 0) serviceAudit.findLastChange(id) else serviceAudit.findRevision(id, rev)
        } catch (e: RevisionDoesNotExistException) {
            if (rev == null) throw e // if we get an RevisionDoesNotExistException with rev == null, we have already tried to get last change, exiting function
            LOG.warn("ElementLoader - RevisionDoesNotExist fallback, fetching latest -> $id")
            get(id, null)
        } catch (ex: Exception) {
            LOG.error("ElementLoader - fill", ex)
            throw ex
        }
    }
}
