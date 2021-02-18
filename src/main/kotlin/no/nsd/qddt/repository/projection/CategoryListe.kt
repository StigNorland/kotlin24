package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "categoryListe", types = [Category::class])
interface CategoryListe: IAbstractEntityEditList {


    @Value(value = "#{target.classificationLevel.toString() }")
    fun getClassificationLevel(): String

    @Value(value = "#{target.hierarchyLevel.toString() }")
    fun  getHierarchyLevel(): String

}
