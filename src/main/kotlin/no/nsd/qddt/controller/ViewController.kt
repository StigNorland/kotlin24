package no.nsd.qddt.controller

import no.nsd.qddt.repository.ChangeFeedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.HalModelBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@BasePathAwareController
class ViewController(@Autowired val changeFeedRepository: ChangeFeedRepository) {

    class ChangelogCriteria {
        var name: String? = null
            get() = field?.replace("*", "%") ?: "%"
        var kind: String? = null
            get() = field?.replace("*", "%") ?: "%"
        var changeKind: String? = null
            get() = field?.replace("*", "%") ?: "%"

        override fun toString(): String {
            return "ChangelogCriteria(name=$name, changeKind=$changeKind, kind=$kind)"
        }

    }

    @ResponseBody
    @GetMapping("/changelog/search/findByQuery", produces = ["application/hal+json"])
    fun getByQuery(changelogCriteria: ChangelogCriteria, pageable: Pageable?): RepresentationModel<*> {

        AbstractRestController.logger.debug(changelogCriteria.toString())
        val entities =
            changeFeedRepository.findByNameLikeIgnoreCaseAndRefChangeKindLikeIgnoreCaseAndRefKindLikeIgnoreCase(
                name = changelogCriteria.name,
                changeKind = changelogCriteria.changeKind,
                kind = changelogCriteria.kind,
                pageable
            ).map {
                val user = it.modifiedBy!!
                HalModelBuilder.halModel()
                    .entity(it)
                    .embed(user, LinkRelation.of("modifiedBy"))
                    .build()
            }

        return PagedModel.of(entities.content, pageMetadataBuilder(entities), Link.of("changelogs"))
    }

    protected fun pageMetadataBuilder(revisions: Page<RepresentationModel<out RepresentationModel<*>>>): PagedModel.PageMetadata {
        return PagedModel.PageMetadata(
            revisions.size.toLong(),
            revisions.pageable.pageNumber.toLong(),
            revisions.totalElements,
            revisions.totalPages.toLong()
        )
    }
}