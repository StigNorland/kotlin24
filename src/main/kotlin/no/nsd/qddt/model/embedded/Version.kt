package no.nsd.qddt.model.embedded

 import com.fasterxml.jackson.annotation.JsonIgnore
 import com.fasterxml.jackson.databind.annotation.JsonSerialize
 import org.hibernate.envers.Audited
 import org.slf4j.Logger
 import org.slf4j.LoggerFactory
 import java.io.Serializable
 import java.util.*
 import javax.persistence.Embeddable
 import javax.persistence.Transient

/**
 * @author Stig Norland
 */

@Embeddable
@Audited
data class Version(@Transient private var _isModified: Boolean = false) : Comparable<Version>, Serializable {
    var major = 1
        set(value) {
            field = value
            _isModified = true
        }

    var minor: Int = 0
        set(value) {
            field = value
            _isModified = true
        }

    var versionLabel: String = ""
    set(value) {
        field = value
    }


    @Transient
    @JsonSerialize
    var rev: Int = 0

    @JsonIgnore
    @Transient
    fun isModified(): Boolean {
        return _isModified
    }

    @Transient
    @JsonIgnore
    private val VERSION_FORMAT = "%1\$s.%2\$s%3\$s"


    constructor(major: Int, minor: Int, revision: Int?=null, versionLabel: String?=null) : this() {
        this.major = major
        this.minor = minor
        this.rev = revision?:0
        this.versionLabel = versionLabel?:""
//        _isModified = false
    }


    override fun compareTo(other: Version): Int {
        return major.compareTo(other.major) + minor.compareTo(other.minor)
    }

    fun toDDIXml(): String {
        return String.format(VERSION_FORMAT, major, minor, if (rev != 0) ".$rev" else "")
            .trim { it <= ' ' }
    }

    override fun toString(): String {
        return String.format(VERSION_FORMAT, major, minor, " $versionLabel")
    }


    fun toJson(): String {
        return String.format(
            "{\"Version\":{\"major\":\"%d\", \"minor\":\"%d\", \"versionLabel\":\"%s\"%s}}",
            major, minor, versionLabel, if (rev != 0) ", \"revision\":\"$rev\"" else ""
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Version

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (rev != other.rev) return false

        return true
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + (rev?:0)
        return result
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}

