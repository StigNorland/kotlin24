package no.nsd.qddt.model.embedded

import no.nsd.qddt.model.Category
import org.hibernate.envers.Audited
import java.io.Serializable
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Cacheable
@Audited
@Embeddable
class CategoryChildren : Serializable {

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "id", column = Column(name = "children_id", nullable = false,  insertable = false, updatable = false)),
        AttributeOverride(name = "rev", column = Column(name = "children_rev", nullable = false)),
    )
    lateinit var uri: UriId

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinColumn(name = "children_id")
    var children: Category?=null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryChildren

        if (uri != other.uri) return false

        return true
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun toString(): String {
        return "CategoryChildren(uri=$uri)"
    }


//    /**
//     * This field will be populated with the correct version of a Category,
//     * but should never be persisted.
//     */
//    @Transient
//    @JsonSerialize
//    var category: Category? = null
//        set(value) {
//            field = value
//            value?.let { item ->
//                uri = UriId().also {
//                    it.id = item.id!!
//                    it.rev = item.version.rev
//                }
//            }
//        }

}
