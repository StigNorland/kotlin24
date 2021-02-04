package no.nsd.qddt.domain.user


import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*


/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "users", collectionResourceRel = "user", itemResourceRel = "user", excerptProjection = UserListe::class)
open interface UserRepository : JpaRepository<User?, UUID?> {

    @RestResource(rel = "email", path = "by-mail")
    fun findByEmailIgnoreCase(email: String?): User?

    @RestResource(rel = "name-like", path = "by-name")
    fun findUsersByUsernameIsLikeOrEmailIsLike(username: String?, email: String?, pageable: Pageable?): Page<User?>?

    @RestResource(rel = "password", path = "set")
    @Modifying
    @Query(value = "update user_account set password = :passwordEncrypted where id = :uuid", nativeQuery = true)
    fun setPassword(uuid: UUID?, passwordEncrypted: String?)
}

