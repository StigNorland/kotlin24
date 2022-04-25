package no.nsd.qddt.model

import org.hibernate.Hibernate
import java.util.*
import javax.persistence.Cacheable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Cacheable
@Entity
data class Authority (
    @Id  @GeneratedValue
    val id: UUID,
    var authority: String,
    var name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Authority

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
