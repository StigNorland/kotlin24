package no.nsd.qddt.model.exception

import org.slf4j.LoggerFactory

/**
 * @author Dag Østgulen Heradstveit.
 */
class FileDownloadException(path: String) : RuntimeException("Attempted to download a file at an invalid location.") {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 2602852740974424455L
        private val logger = LoggerFactory.getLogger(FileDownloadException::class.java)
    }

    init {
        logger.error("Attempted to download a file at an invalid location $path")
    }
}
