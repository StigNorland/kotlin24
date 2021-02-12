package no.nsd.qddt.model.exception

import org.slf4j.LoggerFactory

/**
 * @author Dag Østgulen Heradstveit.
 */
class FileUploadException(path: String) : RuntimeException("Unable to upload file") {
    companion object {
        /**
         *
         */
        private const val serialVersionUID = 7324952042963154126L
        private val logger = LoggerFactory.getLogger(FileUploadException::class.java)
    }

    init {
        logger.error("Unable to upload file to location $path")
    }
}
