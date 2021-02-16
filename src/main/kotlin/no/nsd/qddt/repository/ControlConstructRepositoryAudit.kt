package no.nsd.qddt.repository
import no.nsd.qddt.model.ControlConstruct
import no.nsd.qddt.model.QuestionConstruct
import no.nsd.qddt.model.interfaces.BaseRepository
import no.nsd.qddt.repository.projection.ControlConstructListe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

/**
* @author Stig Norland
*/
@RepositoryRestResource(path = "controlconstructs", collectionResourceRel = "controlconstruct", itemResourceRel = "ControlConstruct", excerptProjection = ControlConstructListe::class)
interface ControlConstructRepositoryAudit: RevisionRepository<ControlConstruct, UUID, Int>
