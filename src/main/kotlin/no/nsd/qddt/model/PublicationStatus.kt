package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Cacheable
@Table(name = "PUBLICATION_STATUS")
data class PublicationStatus(

    @Id @Column(updatable = false, nullable = false)
    var id: Int? = null,

    var label: String?=null

) : Comparable<PublicationStatus> {
    enum class Published {
        NOT_PUBLISHED, INTERNAL_PUBLICATION, EXTERNAL_PUBLICATION
    }

    @Enumerated(EnumType.STRING)
    var published: Published = Published.NOT_PUBLISHED

    var description: String? = null

    @JsonIgnore
    @Column(updatable = false, insertable = false)
    val parentIdx: Int? = null

    @JsonIgnore
    @Column(updatable = false, insertable = false)
    val parentId: Int? = null

    @OrderColumn(name = "parentIdx")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "parentId")
    var children: MutableList<PublicationStatus> = mutableListOf()

    override fun compareTo(other: PublicationStatus): Int {
        return id?.compareTo(other.id!!)?:0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as PublicationStatus

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , parentId = $parentId )"
    }

}
