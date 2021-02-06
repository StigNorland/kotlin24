package no.nsd.qddt.domain.othermaterial

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.UUID
/**
* @author Stig Norland
*/
interface OtherMaterialService {
  @Throws(IOException::class)
  fun saveFile(multipartFile:MultipartFile, uuid:UUID):OtherMaterial
  fun getFile(root:UUID, fileName:String):File
}