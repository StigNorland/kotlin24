package no.nsd.qddt.domain.controlconstruct.pojo

/**
 * @author Stig Norland
 */
enum class ConditionKind(override val name: String, val description: String) {
    COMPUTATION_ITEM("ComputationItem", "JavaScript"), IF_THEN_ELSE(
        "IfThenElse",
        "If Then Else"
    ),  //    LOOP("ForI","For i = X do X += STEP while i <= X "),
    LOOP("ForEach", "For each SOURCES do SEQUENCE"), REPEAT_UNTIL(
        "RepeatUntil",
        "Repeat SEQUENCE Until CONDITION"
    ),
    REPEAT_WHILE("RepeatWhile", "Repeat SEQUENCE While CONDITION");

    companion object {
        fun getEnum(name: String?): ConditionKind {
            requireNotNull(name)
            for (v in values()) if (name.equals(v.name, ignoreCase = true)) return v
            throw IllegalArgumentException()
        }
    }
}
