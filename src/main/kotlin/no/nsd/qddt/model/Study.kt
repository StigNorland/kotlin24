package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.exception.StackTraceFilter
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IBasedOn.ChangeKind
import no.nsd.qddt.model.interfaces.IDomainObjectParentRef
import no.nsd.qddt.model.interfaces.IParentRef
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import javax.persistence.*
import java.util.*

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
@Table(name = "STUDY")
class Study() : AbstractEntityAudit(), IAuthorSet, IArchived {

    @Column(insertable = false, updatable = false)
    var surveyIdx: Int? = null

    @Column(insertable = false, updatable = false)
    var surveyId: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="surveyId")
    var surveyProgram: SurveyProgram? = null

    override var name: String = ""

    @Column(length = 20000)
    var description: String = ""

    // @OrderColumn(name = "studyIdx",  updatable = false, insertable = false)
    // @AuditMappedBy(mappedBy = "studyId", positionMappedBy = "studyIdx")
    @OneToMany(mappedBy = "studyId" , fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE, CascadeType.PERSIST] )
    var topicGroups: MutableList<TopicGroup> = mutableListOf()

    @OneToMany( mappedBy="studyId", cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var instruments: MutableSet<Instrument> = mutableSetOf()

    
    @ManyToMany(cascade = [CascadeType.DETACH])
    override var authors: MutableSet<Author> = mutableSetOf()


    // @Column(name = "is_archived")
    override var isArchived = false
        set(value) {
            try {
                field = value
                if (value) {
                    changeKind = ChangeKind.ARCHIVED
                //     if (Hibernate.isInitialized(topicGroups))
                //         logger.debug("getTopicGroups isInitialized. ")
                //     else
                //         Hibernate.initialize(topicGroups)

                //     topicGroups.forEach{
                //         if (!it.isArchived) 
                //             it.isArchived = true
                //     }
                }
            } catch (ex: Exception) {
                logger.error("setArchived", ex)
                StackTraceFilter.filter(ex.stackTrace).stream()
                    .map { a -> a.toString() }
                    .forEach(logger::info)
            }
        }


    // fun addTopicGroup(topicGroup: TopicGroup): TopicGroup {
    //     topicGroups.add(topicGroup)
    //     topicGroup.study = this
    //     changeKind = ChangeKind.UPDATED_HIERARCHY_RELATION
    //     changeComment = "TopicGroup [" + topicGroup.name + "] added"
    //     return topicGroup
    // }


    override fun fillDoc(pdfReport: PdfReport, counter: String) {

        pdfReport.addHeader(this, "Study $counter")
        description?.let { pdfReport.addParagraph(it) }

        if (comments.size > 0)
            pdfReport.addHeader2("Comments")

        pdfReport.addComments(comments)

        pdfReport.addPadding()
        // var teller = if (counter.isNotEmpty()) "$counter." else counter
        // for ((i, topic) in topicGroups.withIndex()) {
        //     topic.fillDoc(pdfReport, teller + (i + 1))
        // }
    }


    override fun xmlBuilder(): AbstractXmlBuilder? {
        return null
    }

}
