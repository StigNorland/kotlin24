package no.nsd.qddt.repository.criteria

import org.springframework.data.repository.query.Param
import java.security.Principal

class PublicationCriteria(
    var name: String?,
    var purpose: String?,
    var publicationStatus: String?,
    var publishedKind: String,
    var principal: Principal
) { }