package no.nsd.qddt.domain.responsedomain
import no.nsd.qddt.classes.interfaces.BaseService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID
/**
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
interface ResponseDomainService:BaseService<ResponseDomain, UUID> {
  fun findBy(responseKind:ResponseKind, name:String, description:String, question:String, anchor:String, xmlLang:String, pageable:Pageable):Page<ResponseDomain>


}
