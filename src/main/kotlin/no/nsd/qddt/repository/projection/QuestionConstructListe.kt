package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.QuestionConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "questionConstructListe", types = [QuestionConstruct::class])
interface QuestionConstructListe: IAbstractEntityEditList {
    @Value(value = "#{target.questionItem?.question.toString() }")
    fun getQuestionText(): String?

    @Value(value = "#{target.questionItem?.name.toString() }")
    fun getQuestionName(): String?


}
