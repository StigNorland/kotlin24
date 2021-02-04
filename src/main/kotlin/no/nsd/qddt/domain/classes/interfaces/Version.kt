package no.nsd.qddt.domain.classes.interfaces

import javax.persistence.Embeddable
import javax.persistence.Transient

/**
 * @author Stig Norland
 */
@Embeddable
class Version : Comparable<Version> {


    @Transient
    final var isModified = false
        get() = field
        private set

    var major = 1
        set(value) {
            field = value
            isModified = true
        }

    var minor: Int = 0
        set(value) {
            field = value
            isModified = true
        }

    @Transient
    var revision: Int = 0

    var versionLabel: String = ""



    constructor(major: Int, minor: Int, revision: Int, versionLabel: String) {
        this.major = major
        this.minor = minor
        this.revision = revision
        this.versionLabel = versionLabel
        isModified = false
    }

    constructor()


    override fun compareTo(other: Version): Int {
        return major.compareTo(other.major) + minor.compareTo(other.minor)
    }

    fun toDDIXml(): String {
        return String.format(VERSION_FORMAT, major, minor, if (revision != 0) ".$revision" else "")
            .trim { it <= ' ' }
    }

    override fun toString(): String {
        return String.format(VERSION_FORMAT, major, minor, " $versionLabel")
    }

    fun toJson(): String {
        return String.format(
            "{\"Version\":{\"major\":\"%d\", \"minor\":\"%d\", \"versionLabel\":\"%s\"%s}}",
            major, minor, versionLabel, if (revision != 0) ", \"revision\":\"$revision\"" else ""
        )
    }

    companion object {
        private const val VERSION_FORMAT = "%1\$s.%2\$s%3\$s"
    }
}

