package no.nsd.qddt.service

import no.nsd.qddt.model.PublicationStatus
import no.nsd.qddt.model.interfaces.PublicationStatusService
import no.nsd.qddt.repository.PublicationStatusRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("publicationStatusService")
class PublicationStatusServiceImpl : PublicationStatusService {

    @Autowired
    private var publicationStatusRepository: PublicationStatusRepository? = null

    override fun getStatusList(): List<PublicationStatus> {
        return publicationStatusRepository?.findAll() ?: mutableListOf()
    }

    override fun getStatus(id: Int): PublicationStatus? {
        return publicationStatusRepository?.getById(id)
    }

}