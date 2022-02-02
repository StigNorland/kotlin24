package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.enums.CategoryKind
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "managedRepresentation", types = [Category::class])
interface ManagedRepresentation: IAbstractEntityViewList {

    var label: String

    var categoryKind: CategoryKind

//    @Value(value = "#{target.categoryKind?.toString() }")
//    fun getCategoryKind(): String?

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality

    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<ManagedRepresentation>

    @Value(value = "#{target.code }")
    fun  getCode() : Code


}