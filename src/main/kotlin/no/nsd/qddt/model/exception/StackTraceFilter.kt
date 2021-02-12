package no.nsd.qddt.model.exception

import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * @author Stig Norland
 */
object StackTraceFilter {
    fun filter(stacktrace: Array<StackTraceElement?>): List<StackTraceElement?> {
        val retval = Arrays.stream(stacktrace)
            .filter { stackTraceElement: StackTraceElement? -> stackTraceElement.toString().contains("no.nsd") }
            .collect(Collectors.toList())
        retval.add(0, stacktrace[0])
        return retval
    }

    fun println(stacktrace: Array<StackTraceElement?>) {
        filter(stacktrace)
            .forEach(Consumer { x: StackTraceElement? -> println(x) })
    }

    fun nsdStack(): List<StackTraceElement?> {
        return filter(Thread.currentThread().stackTrace)
    }

    fun stackContains(vararg words: String): Boolean {
        val ps = Predicate { e: StackTraceElement? ->
            Arrays.stream(words).anyMatch { w: String? ->
                e!!.methodName.contains(
                    w!!
                )
            }
        }
        return nsdStack().stream().anyMatch(ps)
    }
}
