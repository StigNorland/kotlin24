package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.Study
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.springframework.data.rest.core.config.Projection

@Projection(name = "studyListe", types = [Study::class])
interface StudyListe: IWebMenuPreview
