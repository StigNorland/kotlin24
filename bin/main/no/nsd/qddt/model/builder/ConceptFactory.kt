package no.nsd.qddt.model.builder

import no.nsd.qddt.model.Concept
import no.nsd.qddt.model.interfaces.IEntityFactory
import java.util.stream.Collectors

/**
* @author Stig Norland
**/

class ConceptFactory: IEntityFactory<Concept> {
    override fun create(): Concept {
    return Concept()
  }
  override fun copyBody(source: Concept, dest: Concept): Concept {
    return dest.apply {  
      description =source.description
      label = source.label
      name = source.name
      children = source.children.stream()
        .map{  copy(it as Concept, basedOn?.rev) }
        .collect(Collectors.toList())
      questionItems = source.questionItems.stream()
        .map{ it.clone()  }
        .collect(Collectors.toList())
      // dest.getChildren().forEach(action -> action.setParentC(dest));
    }
  }
}
