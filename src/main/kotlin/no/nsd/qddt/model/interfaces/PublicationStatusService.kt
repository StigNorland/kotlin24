package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.PublicationStatus

interface PublicationStatusService {

    fun getStatusList() : List<PublicationStatus>

    fun getStatus(id:Int) : PublicationStatus?

}
