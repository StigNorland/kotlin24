package no.nsd.qddt.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Authority (
    @Id  @GeneratedValue
    val id: UUID,
    var authority: String,
    var name: String)
