package no.nsd.qddt.domain.author

import no.nsd.qddt.domain.classes.interfaces.BaseService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * @author Stig Norland
 */
interface AuthorService : BaseService<Author?, UUID?> {
    fun findAllPageable(pageable: Pageable?): Page<Author?>
    fun findbyPageable(name: String?, about: String?, email: String?, pageable: Pageable?): Page<Author?>?
}
