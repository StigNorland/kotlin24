package no.nsd.qddt.repository

import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.repository.projection.QuestionConstructListe
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(path = "questionconstruct",  itemResourceRel = "QuestionConstruct", excerptProjection = QuestionConstructListe::class)
interface QuestionConstructRepository: ControlConstructRepository<QuestionConstruct>