package no.nsd.qddt.service

import no.nsd.qddt.model.views.QddtUrl
import java.util.*

/**
 * @author Stig Norland
 */
interface SearchService {
    /**
     * Return a path based on its ID.
     * @param id ID
     * @return Entity
     */
    fun findPath(id: UUID): QddtUrl?
    fun findByName(name: String): List<QddtUrl>?
    fun findByUserId(userId: UUID): List<QddtUrl>?
}