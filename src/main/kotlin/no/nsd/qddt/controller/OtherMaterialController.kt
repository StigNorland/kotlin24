package no.nsd.qddt.controller

import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.service.OtherMaterialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
/**
* @author Stig Norland
* @author Dag Østgulen Heradstveit
*/
@RestController
@RequestMapping("/othermaterial")
class OtherMaterialController {

  @Autowired
  private lateinit var service: OtherMaterialService

  @PostMapping(value = ["/upload/{ownerid}"], headers = ["content-type=multipart/form-data"])
  @ResponseBody
  @Throws(IOException::class, FileUploadException::class)
  fun handleFileUpload(@PathVariable("ownerid") ownerId:UUID,  @RequestParam("file") file:MultipartFile): OtherMaterial {
    if (file.isEmpty) throw FileUploadException("File is empty")
    return service.saveFile(file, ownerId)
  }

  @GetMapping(value = ["/files/{root}/{filename}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  @ResponseBody
  @Throws(IOException::class)
  fun getFile(@PathVariable("root") root:UUID, @PathVariable("filename") fileName:String): FileSystemResource {
    return FileSystemResource(service.getFile(root, fileName))
  }
}
