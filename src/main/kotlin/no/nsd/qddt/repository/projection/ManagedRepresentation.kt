package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.embedded.Version
import no.nsd.qddt.model.enums.CategoryKind
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "managedRepresentation", types = [Category::class])
interface ManagedRepresentation {
    var id: UUID

    var label: String

    var categoryKind: CategoryKind

    var version: Version

    var xmlLang: String

    var classKind: String


    fun getTest():String  = "managedRepresentation"

    @Value(value = "#{target.modified.getTime() }")
    fun getModified(): Long

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality

    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<ManagedRepresentationChildren>



}