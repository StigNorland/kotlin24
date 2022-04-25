package no.nsd.qddt.repository.projection

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.CategoryChildren
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryKind
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "managedRepresentation", types = [Category::class])
@JsonPropertyOrder(alphabetic = true,
    value = *["id","label",  "categoryKind", "classKind", "hierarchyLevel", "inputLimit", "basedOn", "format", "modified", "version", "xmlLang","categoryChildren","children" ]
)

interface ManagedRepresentation {
    var id: UUID

    var label: String

    var categoryKind: CategoryKind

    var version: Version

    var xmlLang: String

    var classKind: String

    var format: String

    @Value(value = "#{target.basedOn }")
    fun getBasedOn(): UriId?


    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality



//    @Value(value = "#{target.categoryChildren }")
//    fun  getCategoryChildren() : MutableList<CategoryChildren>

    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<ManagedRepresentationChildren>



}
