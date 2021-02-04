package no.nsd.qddt.domain.category

import no.nsd.qddt.domain.classes.interfaces.BaseService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * @author Dag Østgulen Heradstveit
 */
interface CategoryService : BaseService<Category?, UUID?> {
    fun findBy(
        hierarchyLevel: String,
        categoryType: String,
        name: String?,
        description: String?,
        xmlLang: String?,
        pageable: Pageable?
    ): Page<Category?>?
}
