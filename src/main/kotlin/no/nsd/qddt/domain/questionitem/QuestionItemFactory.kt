package no.nsd.qddt.domain.questionitem
import no.nsd.qddt.domain.IEntityFactory
/**
* @author Stig Norland
**/
class QuestionItemFactory:IEntityFactory<QuestionItem> {
  fun create():QuestionItem {
    return QuestionItem()
  }
  fun copyBody(source:QuestionItem, dest:QuestionItem):QuestionItem {
    dest.setName(source.name)
    dest.setResponseDomainRef(source.getResponseDomainRef())
    dest.setQuestion(source.getQuestion())
    dest.setIntent(source.getIntent())
    return dest
  }
}
