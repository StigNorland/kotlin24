package no.nsd.qddt.service
import no.nsd.qddt.model.OtherMaterial
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.sql.Timestamp
import java.util.*
import kotlin.io.path.deleteIfExists

/**
* @author Stig Norland
*/
@PropertySource(value = ["classpath:application.yml"])
@Service
internal class OtherMaterialServiceImpl : OtherMaterialService {
  @Value("\${qddt.api.fileroot}")
  private lateinit var fileRoot:String

  protected val logger = LoggerFactory.getLogger(this.javaClass)


  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  @Throws(IOException::class)
  override fun saveFile(multipartFile:MultipartFile, uuid:UUID): OtherMaterial {
    return OtherMaterial(multipartFile).apply {
      logger.info(uuid.toString())

      originalOwner = uuid

      val filePath = Paths.get(getFolder(uuid.toString()), fileName).also {
        if (Files.exists(it)){
          fileName = getNextFileName(it)
          Paths.get(getFolder(uuid.toString()), fileName)
        } else
          it
      }

      Files.copy(multipartFile.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

      fileDate = with(Files.readAttributes(filePath, BasicFileAttributes::class.java)) {
        Timestamp.from(creationTime().toInstant())
      }

    }
  }

  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR','ROLE_CONCEPT','ROLE_VIEW')")
  override fun getFile(root:UUID, fileName:String):File {
    val filePath = Paths.get(getFolder(root.toString()), fileName).toString()
    return File(filePath)
  }

  override fun deleteFile(root: UUID, fileName: String): Boolean {
    return Paths.get(getFolder(root.toString()), fileName).deleteIfExists()
  }

  /*
 return absolute path to save folder, creates folder if not exists
 */
  private fun getFolder(ownerId:String):String {
    val directory = File(fileRoot + ownerId.lowercase(Locale.getDefault()))
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
