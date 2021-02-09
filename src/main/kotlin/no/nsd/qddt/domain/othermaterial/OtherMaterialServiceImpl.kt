package no.nsd.qddt.domain.othermaterial
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
@PropertySource(value = {"classpath:application.properties"})
@Service("otherMaterialService")
internal class OtherMaterialServiceImpl @Autowired
constructor():OtherMaterialService {
  @Value("\${api.fileroot}")
  private lateinit var fileRoot:String

  protected val LOG = LoggerFactory.getLogger(this.javaClass)

  @Transactional
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  @Throws(IOException::class)
  override fun saveFile(multipartFile:MultipartFile, ownerId:UUID):OtherMaterial {
    LOG.info(ownerId.toString())
    val om = OtherMaterial(multipartFile).setOriginalOwner(ownerId)
    var filePath = Paths.get(getFolder(ownerId.toString()), om.fileName)
    if (Files.exists(filePath))
    {
      om.fileName = getNextFileName(filePath)
      filePath = Paths.get(getFolder(ownerId.toString()), om.fileName)
    }
    Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING)
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
    return directory.getAbsolutePath()
  }
 
  private fun getNextFileName(filePath:Path):String {
    val matcher = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().length - 2)
    val matchingFiles = filePath.getParent().toFile().listFiles({ dir, name-> name.startsWith(matcher) })
    val fileIndex = if ((matchingFiles != null)) (matchingFiles.size).toString() else ""
    return matcher + ("00" + fileIndex).substring(fileIndex.length)
  }
}