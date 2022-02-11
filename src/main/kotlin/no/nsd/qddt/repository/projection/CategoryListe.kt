package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Category
import no.nsd.qddt.model.embedded.Code
import no.nsd.qddt.model.embedded.ResponseCardinality
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "categoryListe", types = [Category::class])
interface CategoryListe: IAbstractEntityViewList {

    var label: String

    @Value(value = "#{target.categoryKind?.toString() }")
    fun getCategoryKind(): String?

    @Value(value = "#{target.hierarchyLevel?.toString() }")
    fun  getHierarchyLevel(): String?

    @Value(value = "#{target.inputLimit}")
    fun getInputLimit(): ResponseCardinality


    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String


}
