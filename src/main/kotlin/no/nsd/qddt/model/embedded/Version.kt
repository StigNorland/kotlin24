package no.nsd.qddt.model.embedded

 import com.fasterxml.jackson.annotation.JsonIgnore
 import com.fasterxml.jackson.databind.annotation.JsonDeserialize
 import com.fasterxml.jackson.databind.annotation.JsonSerialize
 import java.io.Serializable
 import javax.persistence.Column
 import javax.persistence.Embeddable
 import javax.persistence.Transient

/**
 * @author Stig Norland
 */
@Embeddable
//@JsonDeserialize(converter = KotlinVersionConverter::class)
data class Version( var versionLabel: String = "") : Comparable<Version>, Serializable {
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
    @JsonSerialize
    @Column(name="rev", insertable = false, updatable = false)
    var rev: Int = 0

    @Transient
    @JsonIgnore
    final var isModified: Boolean = false
        get() = field
        private set(value) { field = value}


    @Transient
    private val VERSION_FORMAT = "%1\$s.%2\$s%3\$s"



    constructor(major: Int, minor: Int, revision: Int?=null, versionLabel: String?=null) : this() {
        this.major = major
        this.minor = minor
        this.rev = revision?:0
        this.versionLabel = versionLabel?:""
        isModified = false
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
}

