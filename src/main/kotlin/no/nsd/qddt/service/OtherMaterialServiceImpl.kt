package no.nsd.qddt.service
import no.nsd.qddt.model.OtherMaterial
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.UUID
/**
* @author Stig Norland
*/
@PropertySource(value = ["classpath:application.properties"])
@Service
internal class OtherMaterialServiceImpl : OtherMaterialService {
  @Value("\${qddt.api.fileroot}")
  private lateinit var fileRoot:String

  protected val LOG = LoggerFactory.getLogger(this.javaClass)

  @Transactional
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  @Throws(IOException::class)
  override fun saveFile(multipartFile:MultipartFile, uuid:UUID): OtherMaterial {
    LOG.info(uuid.toString())
    val om = OtherMaterial(multipartFile).apply {
      originalOwner = uuid
    }
    var filePath = Paths.get(getFolder(uuid.toString()), om.fileName)
    if (Files.exists(filePath))
    {
      om.fileName = getNextFileName(filePath)
      filePath = Paths.get(getFolder(uuid.toString()), om.fileName)
    }
    Files.copy(multipartFile.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
    return om
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  override fun getFile(root:UUID, fileName:String):File {
    val filePath = Paths.get(getFolder(root.toString()), fileName).toString()
    return File(filePath)
  }
  /*
 return absolute path to save folder, creates folder if not exists
 */
  private fun getFolder(ownerId:String):String {
    val directory = File(fileRoot + ownerId.toLowerCase())
    if (!directory.exists())
    {
      directory.mkdirs()
    }
    return directory.absolutePath
  }
 
  private fun getNextFileName(filePath:Path):String {
    val matcher = filePath.fileName.toString().substring(0, filePath.fileName.toString().length - 2)
    val matchingFiles = filePath.parent.toFile().listFiles { _, name -> name.startsWith(matcher) }
    val fileIndex = if ((matchingFiles != null)) (matchingFiles.size).toString() else ""
    return matcher + ("00$fileIndex").substring(fileIndex.length)
  }
}
