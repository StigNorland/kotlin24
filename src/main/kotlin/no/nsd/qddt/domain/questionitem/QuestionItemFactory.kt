package no.nsd.qddt.domain.questionitem

import no.nsd.qddt.classes.IEntityFactory
/**
* @author Stig Norland
**/
class QuestionItemFactory:IEntityFactory<QuestionItem> {
  override fun create():QuestionItem {
    return QuestionItem()
  }
  override fun copyBody(source:QuestionItem, dest:QuestionItem):QuestionItem {
    with(dest) {
      name = source.name
      responseDomainRef = source.responseDomainRef
      question = source.question
      intent = source.intent
    }
    return dest
  }
}
