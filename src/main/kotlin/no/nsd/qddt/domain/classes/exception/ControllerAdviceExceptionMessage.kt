package no.nsd.qddt.domain.classes.exception

/**
 * Default error message object. Can be used anywhere in the application where
 * exceptions or errors are being returned to the client over API requests.
 * For consistency, this is the only API error object the application should use.
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
class ControllerAdviceExceptionMessage(url: String, exceptionMessage: String?) {
    var id: String?
    var url: String
    val exceptionMessage: String?
    var userfriendlyMessage: String? = null
    override fun toString(): String {
        return ("{\"ControllerAdviceExceptionMessage\":{"
                + "\"id\":\"" + id + "\""
                + ", \"url\":\"" + url + "\""
                + ", \"exceptionMessage\":\"" + exceptionMessage + "\""
                + ", \"userfriendlyMessage\":\"" + userfriendlyMessage + "\""
                + "}}")
    }

    init {
        id = ExtractFromException.extractUUID(exceptionMessage)
        this.url = url
        this.exceptionMessage = ExtractFromException.extractMessage(exceptionMessage)
    }
}
