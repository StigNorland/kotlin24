package no.nsd.qddt.controller

import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.OtherMaterial
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.embedded.UriId
import no.nsd.qddt.model.enums.ElementKind
import no.nsd.qddt.model.interfaces.RepLoaderService
import no.nsd.qddt.repository.BaseEntityAuditRepository
import no.nsd.qddt.repository.ChangeFeedRepository
import no.nsd.qddt.service.OtherMaterialService
import org.hibernate.Hibernate
import org.hibernate.envers.AuditReaderFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.io.InputStreamResource
import org.springframework.http.*
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


/**
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */

@RestController
@RequestMapping("/othermaterial")
class OtherMaterialController {

    @Autowired
    private lateinit var service: OtherMaterialService

    @Autowired
    lateinit var changeRepository: ChangeFeedRepository


    @PersistenceContext
    protected val entityManager: EntityManager? = null

    @Autowired
    protected val applicationContext: ApplicationContext? = null

    val repLoaderService get() = applicationContext?.getBean("repLoaderService") as RepLoaderService

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



    @Transactional(propagation = Propagation.REQUIRED)
    @GetMapping("/pdf/{uri}" )
    fun getPdf(@PathVariable uri: String): ResponseEntity<ByteArray> {
        val uriId = UriId.fromAny(uri)
        val headers = HttpHeaders().also {
            it.contentType = MediaType.APPLICATION_OCTET_STREAM
            it.cacheControl = CacheControl.noCache().headerValue
        }
        val result = if (uriId.id != null && uriId.rev == null)
            changeRepository.findFirstByRefIdOrderByRefRevDesc(uriId.id!!)
        else
            changeRepository.findByRefIdAndRefRev(uriId.id!!, uriId.rev!!)
        Hibernate.initialize(result)
        val instance = Class.forName("no.nsd.qddt.model."+ ElementKind.valueOf(result.refKind!!).className)
        instance.kotlin.java.let {
            clazz ->
                AuditReaderFactory.get(entityManager).let {
                    auditReader ->
                    val entityAudit = auditReader.find(clazz, result.refId, result.refRev)
                    val media = (entityAudit as AbstractEntityAudit).makePdf().toByteArray()
                    return ResponseEntity<ByteArray>(media, headers, HttpStatus.OK)
                }
        }
    }

//    private fun getRepository(uri: UriId) {
//
//
//
//        repLoaderService.getRepository<BaseEntityAuditRepository<*>>( result.elementKind!!)
//    }

    private fun <T : AbstractEntityAudit>getByUri(repository: BaseEntityAuditRepository<T>, uri: UriId): T {
        return if (uri.rev != null)
            repository.findRevision(uri.id!!, uri.rev!!).map {
                it.entity.version.rev = it.revisionNumber.get()
                it.entity
            }.orElseThrow()
        else
            repository.findById(uri.id!!).orElseThrow()
    }
}
