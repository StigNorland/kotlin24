package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import no.nsd.qddt.model.embedded.Version
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(name = "categoryListe", types = [Category::class])
interface CategoryListe: IAbstractEntityViewList {

    var label: String
    val description: String?

    @Value(value = "#{target.categoryKind?.toString() }")
    fun getCategoryKind(): String?

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality

    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<CategoryListe>

    @Value(value = "#{target.code }")
    fun  getCode() : Code


}
