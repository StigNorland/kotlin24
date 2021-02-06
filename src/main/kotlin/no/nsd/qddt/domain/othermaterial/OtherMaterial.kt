package no.nsd.qddt.domain.othermaterial

import no.nsd.qddt.domain.AbstractEntityAudit
import no.nsd.qddt.domain.classes.elementref.ElementKind
import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import org.springframework.web.multipart.MultipartFile
import javax.persistence.Column
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
class OtherMaterial:Cloneable {
  @Type(type = "pg-uuid")
  @Column(name = "original_owner")
  val originalOwner:UUID

  @Column(name = "original_name", updatable = false, nullable = false)
  var originalName:String
  set(originalName) {
    field = originalName
    this.fileName = originalName.toUpperCase().replace(' ', '_').replace('.', '_') + "00"
  }

  var fileName:String
  var fileType:String
  var size:Long = 0
  val description:String

  constructor(file:MultipartFile) {
    originalName = file.getOriginalFilename()
    fileType = file.getContentType()
    size = file.getSize()
    setDescription(null!!)
  }
  constructor(originalName:String, fileType:String, size:Long, description:String) {
    originalName = originalName
    fileType = fileType
    size = size
    setDescription(description)
  }
  private fun setDescription(description:String):OtherMaterial {
    this.description = description
    return this
  }
  /**
 * This function is safe to activate, nothing will be overwritten.
 *
 **/
  fun setOriginalOwner(originalOwner:UUID):OtherMaterial {
    // we want to keep reference to the first path for all descendants of the root...
    if (this.originalOwner == null)
    this.originalOwner = originalOwner
    return this
  }
  
  
  public override fun toString():String {
    return ("OtherMaterial{" +
            ", description='" + description + '\''.toString() +
            ", fileType='" + fileType + '\''.toString() +
            ", originalName='" + this.originalName + '\''.toString() +
            ", size=" + size +
            "} ")
  }
  
  
  public override fun clone():OtherMaterial {
    return OtherMaterial(this.originalName, fileType, size, description).setOriginalOwner(this.originalOwner)
  }

  fun toDDIXml(entity:AbstractEntityAudit, tabs:String):String {
    return String.format(OM_REF_FORMAT, tabs,
                         "",
                         "<r:URN type=\"URN\" typeOfIdentifier=\"Canonical\">urn:ddi:" + getUrnId(entity) + "</r:URN>",
                         ElementKind.getEnum(entity.getClassKind()).getClassName(),
                         entity.getId() + ":" + entity.getVersion().toDDIXml(),
                         description,
                         entity.getId().toString() + '/'.toString() + this.fileName,
                         fileType
                        )
  }

  fun getUrnId(entity:AbstractEntityAudit):String {
    return String.format("%1\$s:%2\$s:%3\$s", entity.getAgency().getName(), entity.getId(), this.fileName)
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