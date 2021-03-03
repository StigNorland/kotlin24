package no.nsd.qddt.utils

/**
 * @author Stig Norland
 */
object StringTool {
    fun CapString(input: String?): String {
        return if (input != null && input.length > 1) input.substring(0, 1)
            .toUpperCase() + input.substring(1) else input!!
    }

    fun IsNullOrEmpty(input: String?): Boolean {
        return input == null || input.isEmpty()
    }

    fun IsNullOrTrimEmpty(input: String?): Boolean {
        return input == null || input.trim().isEmpty()
    }

    fun SafeString(input: String?): String {
        return if (IsNullOrTrimEmpty(input)) "" else input!!
    }

    fun likeify(value: String): String {
        if (IsNullOrTrimEmpty(value)) return ""

        return value
            .let { it.replace("*", "%") }
            .let { "%$it"}
            .let { "$it%"}
            .let { it.replace("%%", "%")} 
    }

}
