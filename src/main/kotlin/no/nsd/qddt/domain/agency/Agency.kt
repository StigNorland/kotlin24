package no.nsd.qddt.domain.agency

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Version

@Entity
class Agency (
    @Id  @GeneratedValue
    val id:UUID,
    var name: String,
    @Version
    val updated : Date,
    var xmlLang:String
    )
