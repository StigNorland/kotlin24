package no.nsd.qddt.model

import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.builder.xml.XmlDDICommentsBuilder
import no.nsd.qddt.model.classes.AbstractEntity
import no.nsd.qddt.repository.handler.CommentTrailListener
import org.hibernate.Hibernate
import org.hibernate.envers.NotAudited
import java.util.*
import javax.persistence.*

/**
 * Current demand for comments is just for Survey. If we want to extend this to other entities we must change
 * the relationship and hold the entry comment at each entity that wants a comment tree. Today we hold this in comments themselves.
 * Today this relationship is like this ( Survey &#8592;  comment &#8592;  comment child )
 *
 * If we need to change this, we'll have to add a empty root comment for every survey and replace survey_id with this root element,
 * and add a reference for this root element to the corresponding survey , the relationship will be like this ( entity(survey) &#8594;  comment root &#8592;  comments)
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
@Cacheable
@Entity
@Table(name = "comment")
@EntityListeners(value = [CommentTrailListener::class])
data class Comment(
    @Column(length = 10000)
    var comment: String? = null,

    @Column(updatable = false, nullable = false)
    var ownerIdx: Int? = 0,

    @Column(updatable = false, nullable = false)
    var ownerId: UUID? = null,

    @NotAudited
    @OrderColumn(name = "ownerIdx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.REMOVE], fetch = FetchType.EAGER, orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    @Column(name = "is_public", columnDefinition = "boolean not null default true")
    var isPublic: Boolean = true
) : AbstractEntity() {

    fun size(): Int {
        logger.debug("comment size")
        return when {
            comments.isEmpty() -> 0
            else -> comments.stream()
                .filter { obj: Comment? -> Objects.nonNull(obj) }
                .mapToInt { c: Comment -> c.size() + 1 }
                .sum()
        }
    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        return XmlDDICommentsBuilder(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Comment

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , modified = $modified )"
    }

}
