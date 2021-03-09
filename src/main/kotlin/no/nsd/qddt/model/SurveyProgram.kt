package no.nsd.qddt.model

import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.interfaces.IAuthorSet
import org.hibernate.envers.Audited
import javax.persistence.*


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
class SurveyProgram() : ConceptHierarchy(), IAuthorSet {

    override var name: String = ""



//    @OrderColumn(name = "surveyIdx",  updatable = false, insertable = false)
//    // @AuditMappedBy(mappedBy = "surveyId", positionMappedBy = "surveyIdx")
//    @OneToMany(mappedBy = "surveyId", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
//    var studies: MutableList<Study> = mutableListOf()
    

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "CONCEPT_HIERARCHY_AUTHORS",
        joinColumns = [JoinColumn(name = "parent_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")])
    override var authors: MutableSet<Author> = mutableSetOf()


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



}
