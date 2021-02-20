package no.nsd.qddt.repository


import no.nsd.qddt.model.User
import no.nsd.qddt.repository.projection.UserListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*


/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "user", collectionResourceRel = "Users", itemResourceRel = "User", excerptProjection = UserListe::class)
interface UserRepository : CrudRepository<User, UUID> {

    @RestResource(rel = "email", path = "by-mail")
    fun findByEmailIgnoreCase(email: String): User?

    @RestResource(rel = "name-like", path = "by-name")
    fun findUsersByUsernameIsLikeOrEmailIsLike(username: String?, email: String?, pageable: Pageable): Page<User>?

    @RestResource(rel = "password", path = "set")
    @Modifying
    @Query(value = "update user_account set password = :passwordEncrypted where id = :uuid", nativeQuery = true)
    fun setPassword(uuid: UUID, passwordEncrypted: String)
}

