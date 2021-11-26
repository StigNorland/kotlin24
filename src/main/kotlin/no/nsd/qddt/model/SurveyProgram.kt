package no.nsd.qddt.model

import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IBasedOn
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import org.springframework.hateoas.RepresentationModel
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.OrderColumn


/**
 *
 *  * A Survey is a root element of this model. Every Survey has atleast one Study and one Instrument.
 *
 *  * A Study will have of one or more Modules.
 *
 *  * A Module will have one or more Concepts.
 *
 *  * A Concept consist of one or more QuestionItems.
 *
 *  * Every QuestionItem will have a Question.
 *  * Every QuestionItem will have a ResponseDomain.
 *
 *
 *  * An Instrument will have a ordered list of Questions, all of which are contained in Concepts
 * belonging to Modules that belongs to the Studies that this Survey has.
 *
 *
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
@Audited
@Entity
@DiscriminatorValue("SURVEY_PROGRAM")
data class SurveyProgram(override var name: String = "") : ConceptHierarchy() {

    fun getModified() : Long {
        return super.modified!!.time
    }

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent")
    var children: MutableList<Study> = mutableListOf()

    override fun xmlBuilder(): AbstractXmlBuilder? {
        return null
    }

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "Survey")
        description.let { pdfReport.addParagraph(it) }
        if (comments.size > 0)
            pdfReport.addHeader2("Comments")
        pdfReport.addComments(comments)
        pdfReport.addPadding()
        for ((i, study) in children.withIndex()) {
            study.fillDoc(pdfReport, counter + (i + 1))
        }
    }
    override var isArchived = false
        set(value) {
            try {
                field = value
                if (value) {
                    changeKind = IBasedOn.ChangeKind.ARCHIVED

                    if (Hibernate.isInitialized(children))
                        logger.debug("Children isInitialized. ")
                    else
                        Hibernate.initialize(children)

                    children.forEach{  with (it as IArchived){ if (!it.isArchived) it.isArchived = true }}
                }
            } catch (ex: Exception) {
                logger.error("setArchived", ex)
                StackTraceFilter.filter(ex.stackTrace).stream()
                    .map { a -> a.toString() }
                    .forEach(logger::info)
            }
        }
}
