package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.interfaces.IParentRef
import org.hibernate.Hibernate
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
 *  * An Instrument will have an ordered list of Questions, all of which are contained in Concepts
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

    var parentIdx: Int? = 0

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    override lateinit var parent: ConceptHierarchy

    @JsonIgnore
    @Transient
    override var parentRef: IParentRef? = null

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE], targetEntity = Study::class)
    override var children: MutableList<ConceptHierarchy> = mutableListOf()


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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SurveyProgram

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
