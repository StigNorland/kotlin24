package no.nsd.qddt.model
import no.nsd.qddt.model.enums.ActionKind
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 * @author Stig Norland
 */
@Embeddable
class ChangeFeedKey(
    @Column(name = "ref_id")
    protected var refId: UUID? = null,

    @Column(name = "ref_rev")
    protected var refRev: Int? = null,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ref_action", columnDefinition = "int2")
    var refAction: ActionKind? = null


) :Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChangeFeedKey

        if (refId != other.refId) return false
        if (refRev != other.refRev) return false
        if (refAction != other.refAction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = refId?.hashCode() ?: 0
        result = 31 * result + (refRev ?: 0)
        result = 31 * result + (refAction?.hashCode() ?: 0)
        return result
    }
}
