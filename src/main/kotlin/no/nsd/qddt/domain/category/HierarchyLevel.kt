package no.nsd.qddt.domain.category

import no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty

/**
 * @author Stig Norland
 */
enum class HierarchyLevel {
    ENTITY, GROUP_ENTITY;

    companion object {
        fun getEnum(name: String): HierarchyLevel? {
            if (IsNullOrTrimEmpty(name)) return null
            //            throw new IllegalArgumentException("Enum cannot be null");
            for (v in values()) if (name.equals(v.name, ignoreCase = true)) return v
            throw IllegalArgumentException("Enum value not valid $name")
        }
    }
}
