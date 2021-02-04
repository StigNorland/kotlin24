package no.nsd.qddt.domain.author

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author Stig Norland
 */
@Service("authorService")
class AuthorServiceImpl @Autowired constructor(private val authorRepository: AuthorRepository) : AuthorService {
    override fun count(): Long {
        return authorRepository.count()
    }

    override fun exists(uuid: UUID): Boolean {
        return authorRepository.existsById(uuid)
    }

    override fun findOne(uuid: UUID): Author {
        return authorRepository.findById(uuid).orElse(null)!!
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    override fun save(instance: Author): Author {
        return authorRepository.save(instance)
    }

    override fun delete(uuid: UUID) {
        authorRepository.deleteById(uuid)
    }

    protected fun prePersistProcessing(instance: Author): Author {
        return instance
    }

    protected fun postLoadProcessing(instance: Author): Author {
        return instance
    }

    override fun findAllPageable(pageable: Pageable?): Page<Author?> {
        return authorRepository.findAll(pageable!!)
    }

    override fun findbyPageable(name: String?, about: String?, email: String?, pageable: Pageable?): Page<Author?>? {
        return authorRepository.findAuthorsByAboutContainingOrNameContainingOrEmailContaining(
            about,
            name,
            email,
            pageable
        )
    }
}
