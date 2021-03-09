package no.nsd.qddt.config.exception

import org.slf4j.LoggerFactory
import java.util.*

/**
 * General exception to catch all resources not found by id.
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
class ResourceNotFoundException : RuntimeException {
    constructor(id: Number, clazz: Class<*>) : super("Could not find " + clazz.simpleName + " with id " + id) {
        logger.error("Could not find " + clazz.simpleName + " with id " + id)
    }

    constructor(id: UUID, clazz: Class<*>) : super("Could not find " + clazz.simpleName + " with id " + id.toString()) {
        logger.error("Could not find " + clazz.simpleName + " with id " + id.toString())
    }

    constructor(name: String, clazz: Class<*>) : super("Could not find " + clazz.simpleName + " with name " + name) {
        logger.error("Could not find " + clazz.simpleName + " with name " + name)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ResourceNotFoundException::class.java)
    }
}
