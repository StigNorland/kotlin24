package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.controlconstruct.pojo.*

/**
 * @author Stig Norland
 */
object Converter {
    fun <S : ConstructJson?> mapConstruct(construct: ControlConstruct?): S {
        return when (construct!!.classKind) {
            "QUESTION_CONSTRUCT" -> ConstructQuestionJson(construct as QuestionConstruct?) as S
            "STATEMENT_CONSTRUCT" -> ConstructStatementJson(construct as StatementItem?) as S
            "CONDITION_CONSTRUCT" -> ConstructConditionJson(construct as ConditionConstruct?) as S
            "SEQUENCE_CONSTRUCT" -> ConstructSequenceJson(construct as Sequence?) as S
            else -> ConstructJson(construct) as S
        }
    }

    fun <S : ConstructJsonView?> mapConstructView(construct: ControlConstruct?): S {
        return when (construct!!.classKind) {
            "QUESTION_CONSTRUCT" -> ConstructQuestionJsonView(construct as QuestionConstruct?) as S
            "STATEMENT_CONSTRUCT" -> ConstructStatementJsonView(construct as StatementItem?) as S
            "CONDITION_CONSTRUCT" -> ConstructConditionJsonView(construct as ConditionConstruct?) as S
            "SEQUENCE_CONSTRUCT" -> ConstructSequenceJsonView(construct as Sequence?) as S
            else -> ConstructJsonView(construct) as S
        }
    }
}
