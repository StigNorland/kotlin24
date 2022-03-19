package no.nsd.qddt.config.exception

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

//import static net.logstash.logback.encoder.org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage;
/**
 * Controller-advice to handle exception cast by any requests coming from
 * controllers. This will not interfere with the service layer, but it shares
 * the exceptions that can be cast from the service layer.
 *
 * @author Dag Østgulen Heradstveit
 */
@ControllerAdvice
@RequestMapping(value = ["/error"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ControllerExceptionAdvice {
    /**
     * Handle all exceptions to type [ResourceNotFoundException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        ResourceNotFoundException::class
    )
    @ResponseBody
    fun handleResourceNotFound(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        logger.error("EntityModeL not found: $message")
        return message
    }

    /**
     * Handle all exceptions to type [UserNotFoundException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        UserNotFoundException::class
    )
    @ResponseBody
    fun handleUserByEmailNotFound(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        logger.error("User not found: $message")
        return message
    }
    //
    //    /**
    //     * Handle all exceptions to type {@link OAuth2AuthenticationException}
    //     * when they occur from methods executed from the controller.
    //     * @param req servlet request
    //     * @param e general exception
    //     * @return a {@link no.nsd.qddt.config.exception.ControllerAdviceExceptionMessage} object
    //     */
    //    @ResponseStatus(HttpStatus.NOT_MODIFIED)
    //    @ExceptionHandler(OAuth2AuthenticationException.class)
    //    @ResponseBody public ControllerAdviceExceptionMessage handleDeniedAuthorization(HttpServletRequest req, Exception e) {
    //        ControllerAdviceExceptionMessage message = new ControllerAdviceExceptionMessage(
    //                req.getRequestURL().toString(),
    //                e.getLocalizedMessage()
    //        );
    //
    //        logger.error("Password missmatch: " + message.toString());
    //
    //        return message;
    //    }
    /**
     * Handle all exceptions to type [org.springframework.dao.OptimisticLockingFailureException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(
        ObjectOptimisticLockingFailureException::class
    )
    @ResponseBody
    fun handleConcurrencyCheckedFailed(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            (e as ObjectOptimisticLockingFailureException).mostSpecificCause.message
        )
        logger.error("ConcurencyCheckedFailed: $e")
        return message
    }

    /**
     * Handle all exceptions to type [org.springframework.dao.OptimisticLockingFailureException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(
        JpaObjectRetrievalFailureException::class
    )
    @ResponseBody
    fun handleRetrievalFailure(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            (e as JpaObjectRetrievalFailureException).mostSpecificCause.message
        )
        if (message.exceptionMessage!!.contains("Category") && message.url.contains("responsedomain")) {
            message.userFriendlyMessage =
                "Saving ResponseDomain failed.</BR>Can't add a MissingGroup to a deleted ResponseDomain.</br>Remove old ResponseDomain, add an active ResponseDomain, and then add MissingGroup..."
        } else message.userFriendlyMessage = """
     An Item required to complete the action, is no longer available.
     (remove old reference, add a new one, and try again...)
     """.trimIndent()
        logger.error("RetrievalFailure: $e")
        return message
    }

    /**
     * Handle all exceptions to type [org.springframework.dao.OptimisticLockingFailureException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(
        ReferenceInUseException::class
    )
    @ResponseBody
    fun handleRefInUseFailure(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.message
        )
        message.userFriendlyMessage =
            "User are author of active items and cannot be deleted, try to disable user instead."
        return message
    }

    /**
     * Handle all exceptions to type [org.springframework.security.access.AccessDeniedException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [org.springframework.security.access.AccessDeniedException] object
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = [AccessDeniedException::class])
    @ResponseBody
    fun handleAccessDeniedException(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        message.userFriendlyMessage = ExceptionUtils.getRootCauseMessage(e.cause)
        logger.error(e.javaClass.simpleName, e)
        return message
    }

    /**
     * Handle all exceptions to type [InvalidPasswordException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [InvalidPasswordException] object
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [InvalidPasswordException::class])
    @ResponseBody
    fun handleInvalidPasswordException(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        message.userFriendlyMessage = ExceptionUtils.getRootCauseMessage(e.cause)
        logger.error(e.javaClass.simpleName, e)
        return message
    }

    /**
     * Handle all exceptions to type [DescendantsArchivedException]
     * when they occur from methods executed from the controller.
     * @param req servlet request
     * @param e general exception
     * @return a [DescendantsArchivedException] object
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(value = [DescendantsArchivedException::class])
    @ResponseBody
    fun handleDescendantsArchivedException(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        message.userFriendlyMessage = ExceptionUtils.getRootCauseMessage(e.cause)
        return message
    }

    /**
     * Default exception handler.
     * Will catch all bad requests, but will not provide further details of the error.
     * @param req servlet request
     * @param e general exception
     * @return a [ControllerAdviceExceptionMessage] object
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun defaultErrorHandler(req: HttpServletRequest, e: Exception): ControllerAdviceExceptionMessage {
        val message = ControllerAdviceExceptionMessage(
            req.requestURL.toString(),
            e.localizedMessage
        )
        message.userFriendlyMessage = ExceptionUtils.getRootCauseMessage(e.cause)
        logger.error(e.javaClass.simpleName, e)
        logger.error(req.requestURI)
        logger.error("stacktrace", e.fillInStackTrace())
        return message
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ControllerExceptionAdvice::class.java)
    }
}
