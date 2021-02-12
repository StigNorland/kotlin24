package no.nsd.qddt.model.exception


import org.slf4j.LoggerFactory

/**
 * @author Dag Østgulen Heradstveit
 */
class InvalidPasswordException(email: String) : RuntimeException("Invalid password for '$email'.") {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = -7026178953815161624L
        private val logger = LoggerFactory.getLogger(InvalidPasswordException::class.java)
    }

    init {
        logger.error("[logger] Invalid password for $email.")
    }
}
