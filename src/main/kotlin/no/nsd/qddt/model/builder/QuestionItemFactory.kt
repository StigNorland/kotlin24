package no.nsd.qddt.model.builder

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.interfaces.IEntityFactory

/**
* @author Stig Norland
**/
class QuestionItemFactory: IEntityFactory<QuestionItem> {
  override fun create(): QuestionItem {
    return QuestionItem()
  }
  override fun copyBody(source: QuestionItem, dest: QuestionItem): QuestionItem {
    with(dest) {
      name = source.name
      responseId = source.responseId
      question = source.question
      intent = source.intent
    }
    return dest
  }
}
