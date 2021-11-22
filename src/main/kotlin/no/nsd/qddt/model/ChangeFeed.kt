package no.nsd.qddt.model

import org.hibernate.annotations.Immutable
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

/**
 * @author Stig Norland
 */
@Entity
@Table(name = "change_log")
@Immutable
data class ChangeFeed(@EmbeddedId private val changeFeedKey: ChangeFeedKey? = null) {


    @Column(name = "ref_rev", insertable = false, updatable = false)
    var refRev: Int? = null
        protected set

    @Column(name = "ref_kind")
    var refKind: String? = null

    @Column(name = "ref_change_kind")
    var refChangeKind: String? = null

    @Column(name = "ref_modified")
    var modified: Timestamp? = null

    @ManyToOne
    @JoinColumn(name = "ref_modified_by", updatable = false)
    private var modifiedBy: User? = null

    @Column(name = "element_id")
    private var elementId: UUID? = null

    @Column(name = "element_revision")
    var elementRevision: Int? = null

    @Column(name = "element_kind")
    var elementKind: String? = null

    @Column(name = "name")
    var name: String? = null

//        get() = changeFeedKey.refId
//    var refAction: ActionKind
//        get() = changeFeedKey.refAction
//        set(refAction) {
//            changeFeedKey.refAction = refAction
//        }


}