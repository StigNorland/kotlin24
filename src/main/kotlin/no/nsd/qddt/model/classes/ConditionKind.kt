package no.nsd.qddt.model.classes

import java.lang.IllegalArgumentException

/**
 * @author Stig Norland
 */
enum class ConditionKind( name: String, val description: String) {
    COMPUTATION_ITEM("ComputationItem","JavaScript"),
    IF_THEN_ELSE("IfThenElse","If Then Else" ),
    LOOP("ForEach", "For each SOURCES do SEQUENCE"),
    REPEAT_UNTIL("RepeatUntil","Repeat SEQUENCE Until CONDITION"),
    REPEAT_WHILE("RepeatWhile", "Repeat SEQUENCE While CONDITION");

    companion object {
        fun getEnum(name: String?): ConditionKind {
            requireNotNull(name)
            for (v in values()) if (name.equals(v.name, ignoreCase = true)) return v
            throw IllegalArgumentException()
        }
    }
}
