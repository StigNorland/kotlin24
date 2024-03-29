package no.nsd.qddt.repository


import no.nsd.qddt.model.Universe
import no.nsd.qddt.model.User
import no.nsd.qddt.repository.projection.UserListe
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import java.util.*


/**
 * @author Stig Norland
 */
@RepositoryRestResource(path = "user",  itemResourceRel = "User", excerptProjection = UserListe::class)
interface UserRepository : JpaRepository<User, UUID> {


    @Cacheable(cacheNames = ["USERS"])
    override fun findById(id: UUID): Optional<User>

    @Cacheable(cacheNames = ["USERS"])
    override fun findAll(): MutableList<User>

    @Cacheable(cacheNames = ["USERS"])
    override fun findAll(pageable: Pageable): Page<User>

    @RestResource(rel = "email", path = "by-mail")
//    @Cacheable(cacheNames = ["USERS"])
    fun findByEmailIgnoreCase(email: String): User?

    @Query(
        value = "SELECT ca.* FROM user_account ca WHERE " +
                "ca.username ILIKE searchStr(:name)" ,
        countQuery = "SELECT count(ca.*) FROM user_account ca WHERE " +
                "ca.username ILIKE searchStr(:name)" ,
        nativeQuery = true
    )
    fun findByQuery(
        @Param("name") name: String?,
        pageable: Pageable?
    ): Page<User>

    @Modifying
    @RestResource(exported = false)
    @Query(value = "update user_account set password = :passwordEncrypted where id = :uuid", nativeQuery = true)
    fun setPassword(uuid: UUID, passwordEncrypted: String)
}

