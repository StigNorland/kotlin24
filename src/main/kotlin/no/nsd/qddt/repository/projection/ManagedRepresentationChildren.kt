package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.enums.CategoryKind
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "managedRepresentationChildren", types = [Category::class])
interface ManagedRepresentationChildren {
    var id: UUID

    var label: String

    var categoryKind: CategoryKind

    var xmlLang: String

    var classKind: String

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality

    @Value(value = "#{target.code }")
    fun  getCode() : Code


    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<ManagedRepresentationChildren>


}