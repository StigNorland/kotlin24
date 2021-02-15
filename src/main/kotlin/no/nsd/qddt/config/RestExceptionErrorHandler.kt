package  no.nsd.qddt.config

import no.nsd.qddt.model.exception.ApiError
import no.nsd.qddt.model.exception.StackTraceFilter
import org.slf4j.LoggerFactory
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


    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest?): ResponseEntity<Any?>? {
        log.error(ex.message, ex.cause)
        val qddtStack =  StackTraceFilter.filter(ex.stackTrace)
        if (qddtStack.isNotEmpty())
            qddtStack.forEach {
                log.debug(it.toString())
            }
        val apiError = ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.localizedMessage, qddtStack.map { it.toString() })
        return ResponseEntity(apiError, HttpHeaders(), apiError.status)
    }
}

