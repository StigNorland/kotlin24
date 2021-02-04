package utils

import java.util.*

/**
 * @author Stig Norland
 */
class HtmlTool {
    fun toHtml(source: String): String {
        return source
    }

    fun StripScript(source: String): String {
        return source
    }

    private fun parseTxt(source: String): List<String> {
        return Arrays.asList(*source.split("/n").toTypedArray())
    }
}
