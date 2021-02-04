package no.nsd.qddt.domain.controlconstruct.json

import no.nsd.qddt.domain.AbstractJsonEdit

/**
 * @author Stig Norland
 */
open class ConstructJson(construct: ControlConstruct?) : AbstractJsonEdit(construct) {
    var label: String?
    private var otherMaterials: List<OtherMaterial>?
    fun getOtherMaterials(): List<OtherMaterial>? {
        return otherMaterials
    }

    fun setOtherMaterials(otherMaterials: List<OtherMaterial>?) {
        this.otherMaterials = otherMaterials
    }

    override fun equals(o: Any): Boolean {
        if (this === o) return true
        if (o !is ConstructJson) return false
        val that = o
        if (if (label != null) label != that.label else that.label != null) return false
        return if (otherMaterials != null) otherMaterials == that.otherMaterials else that.otherMaterials == null
    }

    override fun hashCode(): Int {
        var result = if (label != null) label.hashCode() else 0
        result = 31 * result + if (otherMaterials != null) otherMaterials.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return ("{\"ConstructJson\":"
                + super.toString()
                ) + ", \"label\":\"" + label.toString() + "\"" + ", \"otherMaterials\":" + otherMaterials
            .toString() + "}"
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = 6589197591383740935L
    }

    init {
        label = construct.getLabel()
        otherMaterials = construct.getOtherMaterials()
    }
}
