package no.nsd.qddt.classes.exception

import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
class UserNotFoundException : RuntimeException {
    constructor(email: String) : super("Could not find User by email '$email'.") {
        logger.error("[logger] Could not find user by email $email.")
        logger.debug(StackTraceFilter.nsdStack().toString())
    }

    constructor(id: UUID) : super("Could not find User by ID '$id'.") {
        logger.error("[logger] Could not find user by ID $id.")
        logger.debug(StackTraceFilter.nsdStack().toString())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserNotFoundException::class.java)
    }
}
