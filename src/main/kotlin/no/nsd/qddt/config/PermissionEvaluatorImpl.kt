package no.nsd.qddt.config

import no.nsd.qddt.domain.AbstractEntity
import no.nsd.qddt.domain.agency.Agency
import no.nsd.qddt.domain.classes.interfaces.IDomainObject
import no.nsd.qddt.domain.user.User
import org.slf4j.LoggerFactory
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable

/**
 * @author Stig Norland
 */
@Component
class PermissionEvaluatorImpl : PermissionEvaluator {
    private enum class PermissionType {
        OWNER, USER, AGENCY
    }

    protected val LOG = LoggerFactory.getLogger(PermissionEvaluatorImpl::class.java)
    override fun hasPermission(auth: Authentication, targetDomainObject: Any, permission: Any): Boolean {
        if (auth == null || targetDomainObject == null ||
            permission !is String ||
            targetDomainObject !is AbstractEntity
        ) {
            LOG.info("Prereq for hasPermission not fulfilled")
            return false
        }
        LOG.debug(auth.details.toString())
        return hasPrivilege(
            auth.details as UserDetails,
            targetDomainObject,
            permission.toUpperCase()
        )
    }

    override fun hasPermission(
        auth: Authentication,
        targetId: Serializable,
        targetType: String,
        permission: Any
    ): Boolean {
        LOG.error("hasPermission (4 args) not implemented")
        return false
    }

    private fun hasPrivilege(details: UserDetails, entity: AbstractEntity, permission: String): Boolean {
//        LOG.info( details.getUsername() + ": " + permission + ": " + toJson(entity)  );
        return when (PermissionType.valueOf(permission)) {
            PermissionType.OWNER -> isOwner(details as User, entity)
            PermissionType.USER -> isUser(details as User, entity)
            PermissionType.AGENCY -> isMemberOfAgency((details as User).agency, entity as IDomainObject)
            else -> {
                LOG.info("hasPrivilege default: fail: $permission")
                false
            }
        }
    }

    private fun isOwner(user: User, entity: AbstractEntity): Boolean {
        return user.id == entity.modifiedBy!!.id
    }

    // entity is a User entity
    private fun isUser(user: User, entity: AbstractEntity): Boolean {
        return user.id == entity.id
    }

    private fun isMemberOfAgency(agency: Agency, entity: IDomainObject): Boolean {
        if (agency.id == entity.agency!!.id) return true
        LOG.info(agency.name + " != " + entity.agency!!.name)
        return false
    }
}
