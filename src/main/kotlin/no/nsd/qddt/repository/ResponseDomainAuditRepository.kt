 package no.nsd.qddt.repository

 import no.nsd.qddt.model.ResponseDomain
 import no.nsd.qddt.repository.projection.ResponseDomainListe
 import org.springframework.data.repository.history.RevisionRepository
 import org.springframework.data.rest.core.annotation.RepositoryRestResource
 import java.util.*

 /**
  * @author Dag Østgulen Heradstveit
  */
 @RepositoryRestResource(path = "responsedomain", collectionResourceRel = "responseDomain", itemResourceRel = "ResponseDomain", excerptProjection = ResponseDomainListe::class)
 interface ResponseDomainAuditRepository : RevisionRepository<ResponseDomain, UUID, Int>
