package no.nsd.qddt.domain.concept

import no.nsd.qddt.classes.IEntityFactory
import no.nsd.qddt.classes.elementref.ElementRefEmbedded
import java.util.stream.Collectors
import java.util.UUID
/**
* @author Stig Norland
**/

class ConceptFactory:IEntityFactory<Concept> {
    override fun create(): Concept {
    return Concept()
  }
  override fun copyBody(source:Concept, dest:Concept):Concept {
    return dest.apply {  
      description =source.description
      label = source.label
      name = source.name
      children = source.children.stream()
        .map{  copy(it, basedOnRevision) }
        .collect(Collectors.toList()))
      conceptQuestionItems = source.conceptQuestionItems.stream()
        .map{ it.clone()  }
        .collect(Collectors.toList())
      // dest.getChildren().forEach(action -> action.setParentC(dest));
    }
  }
}