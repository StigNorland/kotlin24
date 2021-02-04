package utils

import kotlin.reflect.KProperty

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
        return input == null || input.trim { it <= ' ' }.isEmpty()
    }

    fun SafeString(input: String?): String {
        return if (IsNullOrTrimEmpty(input)) "" else input!!
    }

    fun likeify(value: String): String {
        var value = value
        if (IsNullOrTrimEmpty(value)) return ""
        value = value.replace("*", "%")
        if (!value.startsWith("%")) value = "%$value"
        if (!value.endsWith("%")) value = "$value%"
        value = value.replace("%%", "%")
        return value
    }

}
