package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.ControlConstruct
import org.springframework.data.rest.core.config.Projection

@Projection(name = "controlconstructListe", types = [ControlConstruct::class])
interface ControlConstructListe: IAbstractEntityViewList

