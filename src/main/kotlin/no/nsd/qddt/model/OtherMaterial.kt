package no.nsd.qddt.model

import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.elementref.ElementKind
import org.hibernate.envers.Audited
import org.springframework.web.multipart.MultipartFile
import javax.persistence.Embeddable
import java.util.UUID

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
class OtherMaterial(
  originalName1: String = ""
):Cloneable {

  var size:Long = 0
  lateinit var originalOwner: UUID
  lateinit var fileName:String
  lateinit var fileType:String
  lateinit var description:String

  var originalName: String = originalName1
    set(value) {
      field = value
      this.fileName = value.toUpperCase().replace(' ', '_').replace('.', '_') + "00"
    }


  constructor(file:MultipartFile) : this() {
    this.originalName = file.originalFilename.toString()
    this.fileType = file.contentType.toString()
    this.size = file.size
  }
  constructor(originalName:String, fileType:String, size:Long, description:String) : this() {
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
    return String.format("%1\$s:%2\$s:%3\$s", entity.agency.name, entity.id, this.fileName)
  }
  
  companion object {
    private val OM_REF_FORMAT = (
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
