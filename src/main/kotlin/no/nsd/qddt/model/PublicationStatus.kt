package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "PUBLICATION_STATUS")
class PublicationStatus {
    enum class Published {
        NOT_PUBLISHED, INTERNAL_PUBLICATION, EXTERNAL_PUBLICATION
    }

    @Id
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    var published: Published = Published.NOT_PUBLISHED

    lateinit var label: String

    var description: String? = null

    @JsonBackReference(value = "parentRef")
    @ManyToOne
    @JoinColumn(name = "publication_status_id", updatable = false, insertable = false)
    val parent: PublicationStatus? = null

    @Column(name = "publication_status_idx", updatable = false, insertable = false)
    @JsonIgnore
    val childrenIdx: Int? = null

    @OneToMany(fetch = FetchType.LAZY)
    @OrderColumn(name = "publication_status_idx")
    @JoinColumn(name = "publication_status_id")
    var children: MutableList<PublicationStatus> = mutableListOf()

}
