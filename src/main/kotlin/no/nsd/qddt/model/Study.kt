package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.config.exception.StackTraceFilter
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IBasedOn
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
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
data class Study(override var name: String = "") : ConceptHierarchy(), IAuthorSet, IArchived {

    @Column(insertable = false, updatable = false)
    var parentIdx: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    override lateinit var parent: ConceptHierarchy

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE], targetEntity = TopicGroup::class)
    override var children: MutableList<ConceptHierarchy> = mutableListOf()

    fun addChildren(entity: TopicGroup): TopicGroup {
        children.add(entity)
        changeKind = IBasedOn.ChangeKind.UPDATED_HIERARCHY_RELATION
        changeComment = String.format("{} [ {} ] added", entity.classKind, entity.name)
        return entity
    }

    @OneToMany( mappedBy="studyId")
    var instruments: MutableSet<Instrument> = mutableSetOf()


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
