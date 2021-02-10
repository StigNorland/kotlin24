package no.nsd.qddt.classes.exception

import no.nsd.qddt.classes.AbstractEntity
import org.slf4j.LoggerFactory

/**
 * @author Stig Norland
 */
class InvalidObjectException(`object`: AbstractEntity) : RuntimeException("Object was badly formed ->$`object`") {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 7509899931915065094L
        private val logger = LoggerFactory.getLogger(InvalidObjectException::class.java)
    }

    init {
        logger.error("Object was badly formed " + `object`.javaClass.name + "'.")
    }
}
