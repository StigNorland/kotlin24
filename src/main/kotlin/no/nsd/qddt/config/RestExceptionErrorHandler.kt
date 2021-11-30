package  no.nsd.qddt.config

import no.nsd.qddt.config.exception.ApiError
import no.nsd.qddt.config.exception.StackTraceFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


/**
 * @author Stig Norland
 */
@ControllerAdvice
class RestExceptionErrorHandler: ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger("nsd.no.qddt")

    @Value(value = "\${spring.profiles.active}")
    lateinit var profile: String

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest?): ResponseEntity<Any> {

        log.info("CUSTOM ERRORHANDLER -> $profile")
        request?.let {
            log.info(it.toString())
        }
        log.error(ex.message, ex.cause)

        var qddtStack = if (profile.isBlank() || profile=="local") StackTraceFilter.filter(ex.stackTrace).let {
            if (it.isNotEmpty()) {
                log.debug(it.joinToString(separator = "\n", prefix = "\t"))
                it
            } else
                mutableListOf<StackTraceElement>()
            }
            .map { it.toString() }
        else
            mutableListOf<String>()

        val httpStatus = if (ex is NoSuchElementException)
            HttpStatus.NO_CONTENT
        else
            HttpStatus.INTERNAL_SERVER_ERROR

        val apiError = ApiError(httpStatus, ex.message?:ex.localizedMessage?:ex.toString(), qddtStack)

        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }
}

