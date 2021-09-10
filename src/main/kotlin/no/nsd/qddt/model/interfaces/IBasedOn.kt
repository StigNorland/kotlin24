package no.nsd.qddt.model.interfaces

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

/**
 * @author Stig Norland
 */
interface IBasedOn:IDomainObject {

    var basedOnObject: UUID?
    var basedOnRevision: Int?

    var changeKind: ChangeKind
    var changeComment: String

    @JsonIgnore
    fun isBasedOn() = changeKind == ChangeKind.BASED_ON || changeKind == ChangeKind.NEW_COPY || changeKind == ChangeKind.TRANSLATED || changeKind == ChangeKind.REFERENCED
    @JsonIgnore
    fun isNewCopy() = (changeKind == ChangeKind.NEW_COPY || version!!.rev == 0 && changeKind != ChangeKind.CREATED)


    /**
     * ChangeKinds are the different ways an entity can be modified by the system/user.
     * First entry will always be CREATED.
     * TYPO, can be used modify without breaking a release.
     * Every other version is a IN_DEVELOPMENT change.
     */
    @Suppress("UNUSED_PARAMETER")
    enum class ChangeKind(name: String, val description: String) {
        CREATED("Created", "New element status"),
        BASED_ON("Based on", "Based on copy"),
        NEW_COPY("New Copy","Copy new"),
        TRANSLATED("Translated", "Translation of source"),
        REFERENCED("Reference of","Concepts can be copied as a reference, to facilitate hierarchical revision trees"),
        UPDATED_PARENT("Parent Updated", "ChildSaved as part of parent save"),
        UPDATED_CHILD("Child Updated","ParentSaved as part of child save"),
        UPDATED_HIERARCHY_RELATION("Hierarchy Relation Updated",
            "Element added to a collection, no changes to element itself"),
        IN_DEVELOPMENT("In Development", "UnfinishedWork"),
        TYPO("NoMeaningChange","Typo or No Meaning Change"),

        CONCEPTUAL("ConceptualImprovement", "Conceptual Improvement"),
        EXTERNAL("RealLifeChange","Real Life Change"),

        OTHER("OtherPurpose", "Other Purpose"),
        ADDED_CONTENT("AddContentElement","Added content later on"),

        ARCHIVED("Archived", "READ ONLY"),
        TO_BE_DELETED("ToBeDeleted",
            "This has been marked for deletion, but we need to see it a tiny bit longer.");

        override fun toString(): String {
            return """{ "ChangeKind": ${"\"" + name + "\""}}"""
        }
    }
}
