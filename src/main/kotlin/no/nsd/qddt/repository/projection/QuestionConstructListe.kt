package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.QuestionConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection

@Projection(name = "questionConstructListe", types = [QuestionConstruct::class])
interface QuestionConstructListe: IAbstractEntityViewList {
    @Value(value = "#{ target.questionText?: target.questionItem?.question }")
    fun getQuestionText(): String?

    @Value(value = "#{ target.questionName?: target.questionItem?.name }")
    fun getQuestionName(): String?

    @Value(value = "#{target.modifiedBy.username  + '@' + target.agency.name }")
    fun getUserAgencyName(): String
}
