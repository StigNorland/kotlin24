package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Comment
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "commentListe", types = [Comment::class])
interface CommentListe {
    val id: UUID
    var comment: String
    var isPublic: Boolean
    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long
    @Value(value = "#{target.modifiedBy.username  + '@' + target.modifiedBy.agency.name  }")
    fun getModifiedBy(): String?
    @Value(value = "#{target.modifiedBy.email }")
    fun getModifiedEmail(): String?

    var comments: List<CommentListe>
}
