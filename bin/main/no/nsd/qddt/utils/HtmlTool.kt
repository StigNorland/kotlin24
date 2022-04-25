package no.nsd.qddt.utils

import java.util.*

/**
 * @author Stig Norland
 */
class HtmlTool {
    fun toHtml(source: String): String {
        return source
    }

    fun stripScript(source: String): String {
        return source
    }

    private fun parseTxt(source: String): List<String> {
        return listOf(*source.split("/n").toTypedArray())
    }
}
