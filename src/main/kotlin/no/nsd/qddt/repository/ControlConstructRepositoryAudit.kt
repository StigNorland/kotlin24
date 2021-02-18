package no.nsd.qddt.repository
import no.nsd.qddt.model.ControlConstruct
import org.springframework.data.repository.history.RevisionRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
* @author Stig Norland
*/
@Repository
interface ControlConstructRepositoryAudit: RevisionRepository<ControlConstruct, UUID, Int>
