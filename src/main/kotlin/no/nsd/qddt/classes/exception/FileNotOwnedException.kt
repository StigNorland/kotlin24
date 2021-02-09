package no.nsd.qddt.classes.exception

import no.nsd.qddt.domain.user.User
import org.slf4j.LoggerFactory
import java.util.*

class FileNotOwnedException(activeUser: User, fileId: UUID) :
    RuntimeException("User:" + activeUser.username + " does now own data set with id " + fileId) {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 297954013228247353L
        private val logger = LoggerFactory.getLogger(FileNotOwnedException::class.java)
    }

    init {
        logger.error("User:" + activeUser.username + " does now own data set with id " + fileId)
    }
}
