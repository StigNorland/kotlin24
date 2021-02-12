package no.nsd.qddt.config

import no.nsd.qddt.model.User
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import java.util.*

/**
 * @author Stig Norland
 */
class AuditAwareImpl : AuditorAware<User> {
    override fun getCurrentAuditor(): Optional<User> {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map { obj: SecurityContext -> obj.authentication }
            .filter { obj: Authentication -> obj.isAuthenticated }
            .map { obj: Authentication -> obj.principal }
            .map { obj: Any? -> User::class.java.cast(obj) }
    }
}
