package no.nsd.qddt.model.classes

import org.springframework.data.domain.Page
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.EmbeddedWrapper

class ModelRevisionResults(val content:List<EmbeddedWrapper>) : RepresentationModel<ModelRevisionResults>()
class ModelRevisionPaged(val content: Page<EmbeddedWrapper>) : RepresentationModel<ModelRevisionPaged>()


//EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
//List<Object> elements = new ArrayList<>();
//
//elements.add(wrappers.wrap(new Product("Product1a"), LinkRelation.of("all")));
//elements.add(wrappers.wrap(new Product("Product2a"), LinkRelation.of("purchased")));
//elements.add(wrappers.wrap(new Product("Product1b"), LinkRelation.of("all")));
//
//return new Result(elements);