package no.nsd.qddt.model.enums

import no.nsd.qddt.utils.StringTool.isNullOrTrimEmpty

/**
 * @author Stig Norland
 */
enum class HierarchyLevel {
    ENTITY, GROUP_ENTITY;

    companion object {
        fun getEnum(name: String): HierarchyLevel? {
            if (isNullOrTrimEmpty(name)) return null
            //            throw new IllegalArgumentException("Enum cannot be null");
            for (v in values()) if (name.equals(v.name, ignoreCase = true)) return v
            throw IllegalArgumentException("Enum value not valid $name")
        }
    }
}
