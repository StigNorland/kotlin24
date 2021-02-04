package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.classes.interfaces.Version
import no.nsd.qddt.domain.user.json.UserJson
import java.sql.Timestamp

/**
 * @author Stig Norland
 */
open class ConstructJsonView(construct: ControlConstruct?) {
    private val id: UUID

    /**
     * @return the name
     */
    val name: String
    var label: String?

    /**
     * @return the version
     */
    val version: Version

    /**
     * @return the classKind
     */
    val classKind: String

    /**
     * @return the modified
     */
    val modified: Timestamp
    private val modifiedBy: UserJson

    /**
     * @return the id
     */
    fun getId(): UUID {
        return id
    }

    /**
     * @return the modifiedBy
     */
    fun getModifiedBy(): UserJson {
        return modifiedBy
    }

    // /**
    //  * @return the agency
    //  */
    // public AgencyJsonView getAgency() {
    //     return agency;
    // }
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ConstructJsonView) return false
        val that = o
        return if (label != null) label == that.label else that.label == null
    }

    override fun hashCode(): Int {
        return if (label != null) label.hashCode() else 0
    }

    override fun toString(): String {
        return ("{\"ConstructJson\":"
                + super.toString()
                + ", \"label\":\"" + label + "\""
                + "}")
    }

    companion object {
        private const val serialVersionUID = 15049624309583L
    }

    init {
        id = construct.id
        name = construct.name
        label = construct.getLabel()
        modified = construct.modified
        version = construct.version
        classKind = construct.classKind
        modifiedBy = construct.modifiedBy
        // agency = new AgencyJsonView(construct.getAgency());
    }
}
