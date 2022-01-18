package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "PUBLICATION_STATUS")
data class PublicationStatus(
    var label: String?=null

) : Comparable<PublicationStatus> {
    enum class Published {
        NOT_PUBLISHED, INTERNAL_PUBLICATION, EXTERNAL_PUBLICATION
    }
    @Id
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var published: Published = Published.NOT_PUBLISHED

    var description: String? = null


    @Column(updatable = false, insertable = false)
    @JsonIgnore
    val parentIdx: Int? = null

    @Column(updatable = false, insertable = false)
//    @JsonIgnore
    val parentId: Int? = null

    @OneToMany(fetch = FetchType.EAGER)
    @OrderColumn(name = "parentIdx")
    @JoinColumn(name = "parentId")
    var children: MutableList<PublicationStatus> = mutableListOf()

    override fun compareTo(other: PublicationStatus): Int {
        return id?.compareTo(other.id!!)?:0
    }


}
