package no.nsd.qddt.domain.author.web

import no.nsd.qddt.domain.author.Author
import no.nsd.qddt.domain.author.AuthorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author Stig Norland
 */
@RestController
@RequestMapping(value = ["/author"])
class AuthorController @Autowired constructor(private val authorService: AuthorService) {
    @GetMapping(value = ["{id}"])
    operator fun get(@PathVariable("id") id: UUID): Author {
        return authorService.findOne(id)
    }

    @PostMapping(value = [""])
    fun update(@RequestBody author: Author): Author {
        return authorService.save(author)
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = ["/create"])
    fun create(@RequestBody author: Author): Author {
        return authorService.save(author)
    }

    @DeleteMapping(value = ["/delete/{id}"])
    fun delete(@PathVariable("id") id: UUID) {
        authorService.delete(id)
    }

    @GetMapping(value = ["/page/search"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBy(
        @RequestParam(value = "name", defaultValue = "") name: String?,
        @RequestParam(value = "about", defaultValue = "") about: String?,
        @RequestParam(value = "email", defaultValue = "") email: String?,
        pageable: Pageable?, assembler: PagedResourcesAssembler<Author?>
    ): PagedModel<EntityModel<Author?>> {
        return assembler.toModel(
            authorService.findbyPageable(name, about, email, pageable)!!
        )
    }
}
