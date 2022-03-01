package no.nsd.qddt.controller

import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.service.OtherMaterialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
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

    @PostMapping(value = ["/upload/{ownerid}"])
    @Throws(IOException::class, FileUploadException::class)
    fun handleFileUpload(
        @PathVariable("ownerid") ownerId: UUID,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<OtherMaterial> {
        if (file.isEmpty) throw FileUploadException("File is empty")
        return  ResponseEntity.ok().body(service.saveFile(file, ownerId))
    }


    @GetMapping(value = ["/files/{uuid}/{filename}"])
    @Throws(IOException::class)
    fun downloadFile(@PathVariable("uuid") uuid: UUID, @PathVariable("filename") fileName: String): ResponseEntity<InputStreamResource> {
        val file = service.getFile(uuid, fileName)
        val resource = InputStreamResource(FileInputStream(file))
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.name)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(file.length())
            .body(resource)
    }


}
