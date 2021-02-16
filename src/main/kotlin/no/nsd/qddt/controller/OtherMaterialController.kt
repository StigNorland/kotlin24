package no.nsd.qddt.controller

import no.nsd.qddt.model.exception.FileUploadException
import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.service.OtherMaterialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.UUID
/**
* @author Stig Norland
* @author Dag Østgulen Heradstveit
*/
@RestController
@RequestMapping("/othermaterial")
class OtherMaterialController {

  @Autowired
  private lateinit var service: OtherMaterialService

  @ResponseStatus(value = HttpStatus.CREATED)
  @RequestMapping(value = ["/upload/{ownerid}"], method = [RequestMethod.POST], headers = ["content-type=multipart/form-data"])
  @ResponseBody
  @Throws(IOException::class, FileUploadException::class)
  fun handleFileUpload(
    @PathVariable("ownerid") ownerId:UUID,
    @RequestParam("file") file:MultipartFile): OtherMaterial {
    if (file.isEmpty) throw FileUploadException("File is empty")
    return service.saveFile(file, ownerId)
  }

  @RequestMapping(value = ["/files/{root}/{filename}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
  @ResponseBody
  @Throws(IOException::class)
  fun getFile(@PathVariable("root") root:UUID, @PathVariable("filename") fileName:String):FileSystemResource {
    return FileSystemResource(service.getFile(root, fileName))
  }
}
