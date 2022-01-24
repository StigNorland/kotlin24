package no.nsd.qddt.repository.criteria

import no.nsd.qddt.model.User
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class PublicationCriteria {
    var name: String? = null
        get() = field?.replace("*","%")
    var purpose: String? = null
        get() = field?.replace("*","%")
    var publicationStatus: String? = null
        get() = field?.replace("*","%")
    var publishedKind: String? = null
    var xmlLang: String? = null

    fun getAngencyId(): UUID {
        return (SecurityContextHolder.getContext().authentication.principal as User).agencyId!!
    }

    override fun toString(): String {
        return "PublicationCriteria(name=$name, purpose=$purpose, publicationStatus=$publicationStatus, publishedKind=$publishedKind, xmlLang=$xmlLang)"
    }


}