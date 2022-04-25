package  no.nsd.qddt.config

import no.nsd.qddt.config.exception.ApiError
import no.nsd.qddt.config.exception.StackTraceFilter
import org.hibernate.exception.ConstraintViolationException
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.dao.DataIntegrityViolationException
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

        val qddtStack = if (profile.isBlank() || profile=="development") StackTraceFilter.filter(ex.stackTrace).let {
            if (it.isNotEmpty()) {
                log.debug(it.joinToString(separator = "\n", prefix = "\t"))
                it
            } else
                mutableListOf()
            }
            .map { it.toString() }
        else
            mutableListOf()

        val httpStatus = when (ex) {
            is NoSuchElementException -> HttpStatus.NO_CONTENT
            is ConstraintViolationException -> HttpStatus.CONFLICT
            is DataAccessResourceFailureException -> HttpStatus.CONFLICT
            is DataIntegrityViolationException -> HttpStatus.CONFLICT
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        val message = when (ex) {
            is DataAccessResourceFailureException ->
                ex.mostSpecificCause.localizedMessage
            is DataIntegrityViolationException ->
                (ex.mostSpecificCause as PSQLException).serverErrorMessage?.message!!
            else ->
                ex.localizedMessage?:ex.toString()
        }

        val apiError = ApiError(httpStatus, message, qddtStack)

        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }
}

