package no.nsd.qddt.classes.exception


import org.slf4j.LoggerFactory

/**
 * @author Stig Norland
 */
class ReferenceInUseException(name: String) : RuntimeException("Unable to remove ") {
    companion object {
        private val logger = LoggerFactory.getLogger(ReferenceInUseException::class.java)
    }

    init {
        logger.error("Unable to remove $name")
    }
}
