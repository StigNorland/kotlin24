package no.nsd.qddt.domain

import no.nsd.qddt.domain.classes.xml.AbstractXmlBuilder
import no.nsd.qddt.domain.classes.xml.XmlDDICommentsBuilder
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
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "comment")
class Comment(
    @Column(length = 10000)
    var comment: String? = null,

    @Column(name = "owner_id", columnDefinition = "uuid", updatable = false, nullable = false)
    var ownerId: UUID? = null,

    @OrderColumn(name = "owner_idx")
    @OneToMany(mappedBy = "ownerId", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var comments: List<Comment>? = ArrayList(0),

    @Column(name = "is_public", columnDefinition = "boolean not null default true")
    var isPublic: Boolean = true
) : AbstractEntity() {

    fun size(): Int {
        return when {
            comments == null || comments!!.isEmpty() -> 0
            else -> comments!!.stream()
                .filter { obj: Comment? -> Objects.nonNull(obj) }
                .mapToInt { c: Comment -> c.size() + 1 }
                .sum()
        }
    }

    override val xmlBuilder: AbstractXmlBuilder
        get() = XmlDDICommentsBuilder(this)

}
