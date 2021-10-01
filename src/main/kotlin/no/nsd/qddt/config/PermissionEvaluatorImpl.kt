package no.nsd.qddt.config

import no.nsd.qddt.model.Agency
import no.nsd.qddt.model.User
import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.model.interfaces.IDomainObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable


/**
 * @author Stig Norland
 */
@Configuration
class PermissionEvaluatorImpl : PermissionEvaluator {

    private enum class PermissionType {
        OWNER, USER, AGENCY
    }

    protected val logger: Logger = LoggerFactory.getLogger(PermissionEvaluatorImpl::class.java)



    override fun hasPermission(auth: Authentication, targetDomainObject: Any, permission: Any): Boolean {
        if (permission !is String || targetDomainObject !is AbstractEntity
        ) {
            logger.info("Requirement for hasPermission not fulfilled")
            return false
        }
        logger.debug(auth.details.toString())
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
        logger.error("hasPermission (4 args) not implemented")
        return false
    }

    private fun hasPrivilege(details: UserDetails, entity: AbstractEntity, permission: String): Boolean {
//        LOG.info( details.getUsername() + ": " + permission + ": " + toJson(entity)  );
        return when (PermissionType.valueOf(permission)) {
            PermissionType.OWNER -> isOwner(details as User, entity)
            PermissionType.USER -> isUser(details as User, entity)
            PermissionType.AGENCY -> isMemberOfAgency((details as User).agency, entity as IDomainObject)
        }
    }

    private fun isOwner(user: User, entity: AbstractEntity): Boolean {
        return user.id == entity.modifiedBy.id
    }

    // entity is a User entity
    private fun isUser(user: User, entity: AbstractEntity): Boolean {
        return user.id == entity.id
    }

    private fun isMemberOfAgency(agency: Agency, entity: IDomainObject): Boolean {
        if (agency.id == entity.agencyId) return true
        logger.info(agency.id.toString() + " != " + entity.agencyId)
        return false
    }
}
