package no.nsd.qddt.model.classes
import java.lang.IllegalArgumentException

/**
 * @author Stig Norland
 */
enum class SequenceKind(override val name: String, val description: String) {
    NA(
        "N/A",
        "Not Applicable"
    ),  //    QUESTIONNAIRE("Questionnare Sequence","Covers the content of a full questionnaire"),
    SECTION(
        "Section Sequence",
        "Covers the content of a section of a questionnaire section"
    ),
    BATTERY("Battery Sequence", "Covers content of a questionnaire battery"), UNIVERSE(
        "Universe Sequence",
        "Covers content for a specific universe or population"
    ),
    LOOP("ForEach", "For each Response do Sequence");

    companion object {
        fun getEnum(name: String?): SequenceKind {
            requireNotNull(name)
            for (v in values()) if (name.equals(v.name, ignoreCase = true)) return v
            throw IllegalArgumentException()
        }
    }
}
