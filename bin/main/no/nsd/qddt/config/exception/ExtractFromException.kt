package no.nsd.qddt.config.exception

import java.util.regex.Pattern

/**
 * Simple util to extract the resource id from an exception message.
 *
 * @author Dag Østgulen Heradstveit
 * @author Stig Norland
 */
object ExtractFromException {
    /**
     * Get the resource ID which for now is always the last "word" in the exception.
     * It's no need to parse this as a [Number] as its REST.
     * @param exceptionMessage from the exception caster
     * @return a formatted version.
     */
    fun extractUUID(exceptionMessage: String?): String {
        if (exceptionMessage != null) {
            val patternMatcher =
                Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}")
                    .matcher(exceptionMessage)
            if (patternMatcher.find()) {
                return patternMatcher.group()
            }
        }
        return "NA"
    }

    /**
     * Get the resource ID which for now is always the last "word" in the exception.
     * It's no need to parse this as a [Number] as its REST.
     * @param exceptionMessage from the exception caster
     * @return a formatted version.
     */
    fun extractMessage(exceptionMessage: String?): String {
        if (exceptionMessage != null) {
            val patternMatcher = Pattern.compile("[^:]+").matcher(exceptionMessage)
            return if (patternMatcher.find()) {
                patternMatcher.group()
            } else exceptionMessage.substring(exceptionMessage.lastIndexOf(":") + 1)
        }
        return "NA"
    }
}
