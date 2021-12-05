package no.nsd.qddt.model.embedded

import no.nsd.qddt.model.embedded.Version
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class RevisionId (
//    @Column(updatable = false)
    var id:UUID?=null,
//    @Column(updatable = false)
    var rev:Int?=null
    ) :Serializable
