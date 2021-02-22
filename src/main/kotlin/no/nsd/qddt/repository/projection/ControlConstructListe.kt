package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.*
import org.springframework.data.rest.core.config.Projection

@Projection(name = "controlconstructListe", types = [ControlConstruct::class])
interface ControlConstructListe: IAbstractEntityEditList

@Projection(name = "conditionConstructListe", types = [ConditionConstruct::class])
interface ConditionConstructListe: IAbstractEntityEditList

@Projection(name = "questionConstructListe", types = [QuestionConstruct::class])
interface QuestionConstructListe: IAbstractEntityEditList

@Projection(name = "sequenceListe", types = [Sequence::class])
interface SequenceListe: IAbstractEntityEditList

@Projection(name = "statementItemtListe", types = [StatementItem::class])
interface StatementItemtListe: IAbstractEntityEditList