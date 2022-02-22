package no.nsd.qddt.model.classes

import no.nsd.qddt.model.interfaces.IElementRef
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.hibernate.envers.exception.RevisionDoesNotExistException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.history.Revision
import org.springframework.data.repository.history.RevisionRepository
import java.util.*

/**
 * @author Stig Norland
 */
class ElementLoader<T : IWebMenuPreview>(protected var repository: RevisionRepository<T, UUID, Int>) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun fill(element: IElementRef<T>): IElementRef<T> {
        try {
            get(element.uri.id, element.uri.rev).also {
                element.element = it.entity
                element.uri.rev = it.revisionNumber.get()
            }
            return element
        } catch (e: Exception) {
            logger.error("ElementLoader setElement, reference has wrong signature", e)
            throw e
        }
    }

    // uses rev Object to facilitate by rev by reference
    private operator fun get(id: UUID?, rev: Int?): Revision<Int, out T> {
        return try {
            rev?.let {
                return repository.findRevision(id!!, it).get()
            }
            return repository.findLastChangeRevision(id!!).orElseThrow()
        } catch (e: RevisionDoesNotExistException) {
            if (rev == null) throw e // if we get an RevisionDoesNotExistException with rev == null, we have already tried to get last change, exiting function
            logger.warn("ElementLoader - RevisionDoesNotExist fallback, fetching latest -> $id")
            get(id, null)
        } catch (ex: Exception) {
            logger.error("ElementLoader - fill", ex)
            throw ex
        }
    }
}
