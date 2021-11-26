package no.nsd.qddt.model
import javax.persistence.Embeddable
import javax.persistence.Enumerated
import no.nsd.qddt.model.enums.ActionKind
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.EnumType

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
    @Column(columnDefinition = "int2")
    protected var refAction: ActionKind? = null
) :Serializable
