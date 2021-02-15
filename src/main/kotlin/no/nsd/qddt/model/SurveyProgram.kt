package no.nsd.qddt.model

import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import org.hibernate.envers.AuditMappedBy
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
@Table(name = "SURVEY_PROGRAM")
class SurveyProgram(override var name: String) : AbstractEntityAudit(), IAuthorSet, IArchived {

    @Column(length = 20000)
    var description: String? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "surveyProgram", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @OrderColumn(name = "survey_idx")
    @AuditMappedBy(mappedBy = "surveyProgram", positionMappedBy = "surveyIdx")
    var studies: MutableList<Study> = mutableListOf()

//    @OrderBy(value = "name ASC,email DESC")
//    @JoinTable(
//        name = "SURVEY_PROGRAM_AUTHORS",
//        joinColumns = [JoinColumn(name = "survey_id")],
//        inverseJoinColumns = [JoinColumn(name = "author_id")]
//    )

    fun addStudy(study: Study): Study {
        studies.add(study)
        study.s(this)
        changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
        changeComment = "Study [" + study.name + "] added"
        return study
    }


    override val xmlBuilder: AbstractXmlBuilder?
        get() = null

    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        pdfReport.addHeader(this, "Survey")
        pdfReport.addParagraph(description)
        if (comments.size > 0) pdfReport.addHeader2("Comments")
        pdfReport.addComments(comments)
        pdfReport.addPadding()
        var i = 0
        for (study in studies) {
            study.fillDoc(pdfReport, counter + ++i)
        }
    }

    override fun beforeUpdate() {
        TODO("Not yet implemented")
    }

    override fun beforeInsert() {
        TODO("Not yet implemented")
    }

    override var isArchived: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    override var authors: MutableSet<Author> = mutableSetOf()



}
