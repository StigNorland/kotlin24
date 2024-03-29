package no.nsd.qddt.model

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.enums.ElementKind
import org.hibernate.envers.Audited
import org.springframework.web.multipart.MultipartFile
import java.io.Serializable
import java.sql.Timestamp
import java.util.*
import javax.persistence.Cacheable
import javax.persistence.Embeddable
import javax.persistence.Table
import javax.persistence.UniqueConstraint

/**
* This class is just a placeholder for functionality not implemented.
* Storing of arbitrary data is best suited for none relational datastores.
* A simple but not very recommended solution would be to use file system and
* rename files to guid and store the original filename in the attachment repository.
*
* @author Stig Norland
*/
@Audited
@Embeddable
@Cacheable
@Table( uniqueConstraints = [UniqueConstraint(columnNames = ["originalOwner", "fileName", "size"],name = "UNQ_OTHERMATERIAL_IDENT")])
class OtherMaterial():Cloneable, Serializable {

  lateinit var originalOwner: UUID

  lateinit var fileName:String

  lateinit var fileType:String

  var fileDate: Timestamp? = null

  var description:String? = ""

  var originalName: String =""
  set(value) {
  field = value
  this.fileName = value.uppercase(Locale.getDefault()).replace(' ', '_').replace('.', '_') + "00"
  }

  var size:Long = 0


  constructor(file:MultipartFile) : this() {
    this.originalName = file.originalFilename.toString()
    this.fileType = file.contentType.toString()
    this.size = file.size
//    this.fileDate = Timestamp.from(Instant.ofEpochMilli(file.resource.lastModified()))
  }
  constructor(originalName:String, fileType:String, size:Long, description:String?) : this() {
    this@OtherMaterial.originalName = originalName
    this@OtherMaterial.fileType = fileType
    this@OtherMaterial.size = size
    this@OtherMaterial.description = description
  }

  
  public override fun clone(): OtherMaterial {
    return OtherMaterial(this.originalName, fileType, size, description).apply { originalOwner = this.originalOwner}
  }

  fun toDDIXml(entity: AbstractEntityAudit, tabs:String):String {
    return String.format(
        OM_REF_FORMAT, tabs,
      "",
      "<r:URN type=\"URN\" typeOfIdentifier=\"Canonical\">urn:ddi:" + getUrnId(entity) + "</r:URN>",
      ElementKind.getEnum(entity.classKind).className,
      entity.id.toString() + ":" + entity.version.toDDIXml(),
      description,
      entity.id.toString() + '/'.toString() + this.fileName,
      fileType
    )
  }

  fun getUrnId(entity:AbstractEntityAudit):String {
    return String.format("%1\$s:%2\$s:%3\$s", entity.agency!!.name, entity.id, this.fileName)
  }
  
  companion object {
    private const val OM_REF_FORMAT = (
      "%1\$s<r:ExternalAid scopeOfUniqueness = \"Maintainable\" isUniversallyUnique = \"true\">\n" +
      "%1\$s\t%3\$s\n" +
      "%1\$s\t<MaintainableObject>\n" +
      "%1\$s\t\t<TypeOfObject>%4\$s</TypeOfObject>\n" +
      "%1\$s\t\t<MaintainableID type=\"ID\">%5\$s</MaintainableID>\n" +
      "%1\$s\t</MaintainableObject>\n" +
      "%1\$s\t<Description>%6\$s</Description>\n" +
      "%1\$s\t<ExternalURLReference>https://qddt.nsd.no/preview/%7\$s</ExternalURLReference>\n" +
      "%1\$s\t<MIMEType>%8\$s</MIMEType>\n" +
      "%1\$s</r:ExternalAid>\n")
  }
}
