package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.domain.IEntityFactory
/**
* @author Stig Norland
*/
class ResponseDomainFactory:IEntityFactory<ResponseDomain> {
  fun create():ResponseDomain {
    return ResponseDomain()
  }
  fun copyBody(source:ResponseDomain, dest:ResponseDomain):ResponseDomain {
    dest.setName(source.getName())
    dest.setDescription(source.getDescription())
    dest.setDisplayLayout(source.getDisplayLayout())
    // List<Code> codes = source.getCodes();
    dest.setCodes(source.getCodes())
    dest.setManagedRepresentation(source.getManagedRepresentation().clone())
    dest.setResponseKind(source.getResponseKind())
    dest.setResponseCardinality(source.getResponseCardinality())
    return dest
  }
}