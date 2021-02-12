package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.OtherMaterial

/**
 * @author Stig Norland
 */
interface IOtherMaterialList {
    var otherMaterials: MutableList<OtherMaterial>
    /**
     * Add a [Author] to a [Set] of authors.
     * @param user added author.
     */
    fun addOtherMaterial(otherMaterial: OtherMaterial): OtherMaterial {
        if (this.otherMaterials.stream().noneMatch { it.equals(otherMaterial) })
        {
            otherMaterials.add(otherMaterial)
        }        
        return otherMaterial
    }

}
