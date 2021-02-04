package no.nsd.qddt.domain.classes.exception

import no.nsd.qddt.domain.classes.exception.DescendantsArchivedException
import org.slf4j.LoggerFactory

/**
 * @author Stig Norland
 */
class DescendantsArchivedException(name: String) :
    RuntimeException("Element has Archived Descendants, unable to remove.", null, true, false) {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 5534742135508502155L
        private val logger = LoggerFactory.getLogger(DescendantsArchivedException::class.java)
    }

    init {
        logger.error("Element has Archived Descendants, unable to remove $name")
    }
}
