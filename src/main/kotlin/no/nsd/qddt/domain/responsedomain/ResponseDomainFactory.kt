package no.nsd.qddt.domain.responsedomain

import no.nsd.qddt.classes.IEntityFactory

class ResponseDomainFactory: IEntityFactory<ResponseDomain> {
  override fun create():ResponseDomain {
    return ResponseDomain()
  }
  override fun copyBody(source:ResponseDomain, dest:ResponseDomain):ResponseDomain {
    with(dest) {
      name = source.name
      description = source.description
      displayLayout = source.displayLayout
      // List<Code> codes = source.getCodes();
      codes = source.codes.toCollection(codes)
      managedRepresentation = source.managedRepresentation.clone()
      responseKind = source.responseKind
      responseCardinality = source.responseCardinality
    }
    return dest
  }
}
