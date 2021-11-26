package no.nsd.qddt.model.classes

import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class RevisionId (
    @Column(updatable = false)
    var id:UUID?=null,
    @Column(updatable = false)
    var rev:Long?=null
    ) :Serializable
