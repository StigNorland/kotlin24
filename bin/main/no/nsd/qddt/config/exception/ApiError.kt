package  no.nsd.qddt.config.exception

import org.springframework.http.HttpStatus
import java.util.*

/**
 * @author Stig Norland
 */
class ApiError(val status: HttpStatus, val message: String, val errors: List<String>?= listOf())
