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
class ElementLoader<T : IWebMenuPreview>(protected var serviceAudit: BaseServiceAudit<T, UUID, Int>) {
    protected val LOG = LoggerFactory.getLogger(this.javaClass)

    fun fill(element: IElementRef<T>): IElementRef<T> {
        try {
            get(element.elementId, element.elementRevision).also {  
                element.setElement(it!!.entity)
                element.elementRevision = it.revisionNumber.get()
            }
            return element
        } catch (e: Exception) {
            LOG.error("ElementLoader setElement, reference has wrong signature", e)
            throw e
        }
    }

    // uses rev Object to facilitate by rev by reference
    private operator fun get(id: UUID, rev: Int?): Revision<Int, T>? {
        return try {
            rev?.let {  
                serviceAudit.findRevision(id, it) 
            }
            
            else 
             serviceAudit.findLastChange(id) 
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
