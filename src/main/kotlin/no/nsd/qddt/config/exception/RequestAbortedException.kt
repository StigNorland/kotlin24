package no.nsd.qddt.config.exception

import org.slf4j.LoggerFactory

/**
 * @author Stig Norland
 */
class RequestAbortedException : RuntimeException {
    constructor(ex: Exception) : super(
        "Request " + stackTraceElements[2].className + "." + stackTraceElements[2].methodName + " could not finish." + ex.message,
        if (ex.cause != null) ex.cause else Throwable(ex.message)
    ) {
        logger.error(stackTraceElements[2].className + "." + stackTraceElements[2].methodName + "failed, message: " + ex.message)
    }

    constructor(message: String) : super(message) {
        logger.error(stackTraceElements[2].className + "." + stackTraceElements[2].methodName + "failed, message: " + message)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(InvalidObjectException::class.java)
        private val stackTraceElements = Thread.currentThread().stackTrace
    }
}
