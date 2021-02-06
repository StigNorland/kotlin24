package no.nsd.qddt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author Stig Norland
 */
@SpringBootApplication
//@EnableConfigurationProperties(BlogProperties::class)
class QddtApplication

fun main(args: Array<String>) {
    runApplication<QddtApplication>(*args)
}
