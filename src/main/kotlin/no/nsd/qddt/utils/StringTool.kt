package no.nsd.qddt.utils

import java.util.*

/**
 * @author Stig Norland
 */
object StringTool {
    fun capString(input: String?): String {
        return if (input != null && input.length > 1) input.substring(0, 1)
            .uppercase(Locale.getDefault()) + input.substring(1) else input!!
    }

    fun isNullOrEmpty(input: String?): Boolean {
        return input == null || input.isEmpty()
    }

    fun isNullOrTrimEmpty(input: String?): Boolean {
        return input == null || input.trim().isEmpty()
    }

    fun safeString(input: String?): String {
        return if (isNullOrTrimEmpty(input)) "" else input!!
    }

    fun likeify(value: String): String {
        if (isNullOrTrimEmpty(value)) return ""

        return value
            .let { it.replace("*", "%") }
            .let { "%$it"}
            .let { "$it%"}
            .let { it.replace("%%", "%")} 
    }

}
