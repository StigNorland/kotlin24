package no.nsd.qddt.model.exception

import org.slf4j.LoggerFactory

/**
 * @author Dag Østgulen Heradstveit
 */
class UserAlreadyExistsException(email: String) : RuntimeException("User already exsists '$email'.") {
    companion object {
        private val logger = LoggerFactory.getLogger(UserAlreadyExistsException::class.java)
    }

    init {
        logger.error("User already exsists $email.")
    }
}
