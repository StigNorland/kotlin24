package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.interfaces.IWebMenuPreview
import no.nsd.qddt.model.Category
import org.springframework.data.rest.core.config.Projection

@Projection(name = "categoryListe", types = [Category::class])
interface CategoryListe: IWebMenuPreview
