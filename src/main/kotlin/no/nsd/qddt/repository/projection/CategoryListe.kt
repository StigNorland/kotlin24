package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "categoryListe", types = [Category::class])
interface CategoryListe: IAbstractEntityViewList {

    val label: String?

    val description: String?

    @Value(value = "#{target.categoryKind?.toString() }")
    fun getCategoryKind(): String?

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.children }")
    fun  getChildren() : MutableList<Category>


}
