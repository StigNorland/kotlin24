package no.nsd.qddt.model

import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 *
 *  * A Study will have of one or more TopicGroups.
 *
 *  * A TopicGroup will have one or more Concepts.
 *
 *  * A Concept consist of one or more QuestionItems.
 *
 *  * Every QuestionItem will have a Question.
 *
 *
 *  * Every QuestionItem will have a ResponseDomain.
 *
 *
 * <br></br>
 * A publication structure for a specific study. Structures identification information, full
 * bibliographic and discovery information, administrative information, all of the reusable
 * delineations used for response domains and variable representations, and TopicGroups covering
 * different points in the lifecycle of the study (DataCollection, LogicalProduct,
 * PhysicalDataProduct, PhysicalInstance, Archive, and DDIProfile).
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */
// @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
@Audited
@Entity
@DiscriminatorValue("STUDY")
class Study : ConceptHierarchy(), IAuthorSet, IArchived {

    override var name: String = ""


    @OneToMany( mappedBy="studyId", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var instruments: MutableSet<Instrument> = mutableSetOf()

    
    @ManyToMany(cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "CONCEPT_HIERARCHY_AUTHORS",
        joinColumns = [JoinColumn(name = "parent_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")])
    override var authors: MutableSet<Author> = mutableSetOf()

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="parentId", insertable = false, updatable = false )
//    override var parent: SurveyProgram? = null
//
//    @OrderColumn(name = "parentIdx",  updatable = false, insertable = false)
//    @AuditMappedBy(mappedBy = "parentId", positionMappedBy = "parentIdx")
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentId",cascade = [CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE])
//    override lateinit var children: MutableList<TopicGroup>


    override fun fillDoc(pdfReport: PdfReport, counter: String) {

        pdfReport.addHeader(this, "Study $counter")
        description.let { pdfReport.addParagraph(it) }

        if (comments.size > 0)
            pdfReport.addHeader2("Comments")

        pdfReport.addComments(comments)

        pdfReport.addPadding()
         var teller = if (counter.isNotEmpty()) "$counter." else counter
         for ((i, topic) in children.withIndex()) {
             topic.fillDoc(pdfReport, teller + (i + 1))
         }
    }


    override fun xmlBuilder(): AbstractXmlBuilder? {
        return null
    }

}
