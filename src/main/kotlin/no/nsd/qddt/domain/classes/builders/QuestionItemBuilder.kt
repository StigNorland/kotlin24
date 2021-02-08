//package no.nsd.qddt.domain.classes.builders
//
//import no.nsd.qddt.domain.questionitem.QuestionItem
//
///**
// * @author Dag Østgulen Heradstveit
// */
//class QuestionItemBuilder {
//    private var question: String? = null
//    private var responseDomain: ResponseDomain? = null
//    fun setQuestion(question: String?): QuestionItemBuilder {
//        this.question = question
//        return this
//    }
//
//    fun setName(name: String?): QuestionItemBuilder {
//        return this
//    }
//
//    fun setResponseDomain(responseDomain: ResponseDomain?): QuestionItemBuilder {
//        this.responseDomain = responseDomain
//        return this
//    }
//
//    fun createQuestionItem(): QuestionItem {
//        val questionItem = QuestionItem()
//        questionItem.setQuestion(question)
//        questionItem.getResponseDomainRef().setElement(responseDomain)
//        return questionItem
//    }
//}
