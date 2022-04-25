package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.model.classes.AbstractEntity

/**
 * @author Stig Norland
 */
interface IOtherMaterialList {
    var changeComment: String
    var changeKind: IBasedOn.ChangeKind
    var otherMaterials: MutableList<OtherMaterial>
    /**
     * Add a [Author] to a [Set] of authors.
     * @param user added author.
     */
    fun addOtherMaterial(otherMaterial: OtherMaterial): OtherMaterial {
        if (this.otherMaterials.stream().noneMatch { it.equals(otherMaterial) })
        {
            otherMaterials.add(otherMaterial)
            this.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
            this.changeComment =  String.format("Added FILE [${otherMaterial.fileName}]")
        } else
            AbstractEntity.logger.debug("FILE not inserted, match found")
        return otherMaterial
    }

    fun removeOtherMaterial(fileName:String) {

        if (this.otherMaterials.removeIf { it.fileName == fileName }){
            this.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
            this.changeComment =  String.format("Removed FILE [${fileName}]")
        } else
            AbstractEntity.logger.debug("FILE not found, nothing to do")
    }


    fun removeOtherMaterial(otherMaterial: OtherMaterial) {
        if (this.otherMaterials.remove(otherMaterial)){
            this.changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
            this.changeComment =  String.format("Removed FILE [${otherMaterial.fileName}]")
        } else
            AbstractEntity.logger.debug("FILE not found, nothing to do")
    }

}
