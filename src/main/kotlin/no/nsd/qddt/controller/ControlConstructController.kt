package no.nsd.qddt.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import no.nsd.qddt.config.exception.FileUploadException
import no.nsd.qddt.model.*
import no.nsd.qddt.model.interfaces.IBasedOn
import no.nsd.qddt.repository.ControlConstructRepository
import no.nsd.qddt.service.OtherMaterialService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.BasePathAwareController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*


@BasePathAwareController
class ControlConstructController(@Autowired repository: ControlConstructRepository<ControlConstruct>) :
    AbstractRestController<ControlConstruct>(repository) {

    @Autowired
    lateinit var omService: OtherMaterialService


    @ResponseBody
    @PostMapping(value = ["/controlconstruct/condition"])
    fun update(@RequestBody instance: ConditionConstruct): ConditionConstruct {
        return repository.save(instance)
    }

    @ResponseBody
    @PostMapping(value = ["/controlconstruct/question"])
    fun update(@RequestBody instance: QuestionConstruct): QuestionConstruct {
        return repository.save(instance)
    }

    @ResponseBody
    @PostMapping(value = ["/controlconstruct/sequence"])
    fun update(@RequestBody instance: Sequence): Sequence {
        return repository.save(instance)
    }

    @ResponseBody
    @PostMapping(value = ["/controlconstruct/statement"])
    fun update(@RequestBody instance: StatementItem): StatementItem {
        return repository.save(instance)
    }

    @PostMapping(value = ["/controlconstruct/createfile"], headers = ["content-type=multipart/form-data"])
    @Throws(FileUploadException::class, IOException::class)
    fun createWithFile(
        @RequestParam("files") files: Array<MultipartFile>?,
        @RequestParam("controlconstruct") jsonString: String
    ): ControlConstruct? {
        val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val index = jsonString.indexOf("\"classKind\":\"QUESTION_CONSTRUCT\"")

        val instance: ControlConstruct = if (index > 0) {
            mapper.readValue(jsonString, QuestionConstruct::class.java)
        } else {
            mapper.readValue(jsonString, Sequence::class.java)
        }

        if (files != null && files.isNotEmpty()) {
            logger.info("got new files!!!")

            if (null == instance.id) instance.id = UUID.randomUUID()

            for (multipartFile in files) {
                instance.otherMaterials.add(omService.saveFile(multipartFile, instance.id!!))
            }
            if (IBasedOn.ChangeKind.CREATED == instance.changeKind) instance.changeKind =
                IBasedOn.ChangeKind.TO_BE_DELETED
        }
        return repository.save(instance)
    }

}