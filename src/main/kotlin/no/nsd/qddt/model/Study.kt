package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonBackReference
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.AbstractEntityAudit
import no.nsd.qddt.model.classes.elementref.ParentRef
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IDomainObjectParentRef
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import java.lang.Exception
import java.util.*
import java.util.function.Predicate
import javax.persistence.*
import javax.sound.midi.Instrument
import kotlin.collections.HashSet

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
 *
 *
 *
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
@Audited
@Entity
@Table(name = "STUDY")
class Study(override var name: String) : AbstractEntityAudit(), IAuthorSet, IArchived, IDomainObjectParentRef {

    @ManyToOne
    @JsonBackReference(value = "surveyRef")
    @JoinColumn(name = "survey_id", updatable = false)
    var surveyProgram: SurveyProgram? = null

    @Column(name = "survey_id", insertable = false, updatable = false)
    protected var surveyId: UUID? = null

    @Column(name = "survey_idx", insertable = false, updatable = false)
    private var surveyIdx: Int? = null

    @Column(length = 20000)
    var description: String? = null

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "study", cascade = [CascadeType.MERGE, CascadeType.DETACH])
     var instruments: MutableSet<Instrument> = mutableSetOf()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "study", cascade = [CascadeType.REMOVE, CascadeType.PERSIST])
    @OrderColumn(name = "study_idx", nullable = false)
    @AuditMappedBy(mappedBy = "study", positionMappedBy = "studyIdx")
    private var topicGroups: MutableList<TopicGroup> = ArrayList<TopicGroup>()

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "STUDY_AUTHORS",
        joinColumns = [JoinColumn(name = "study_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    override var authors: MutableSet<Author> = mutableSetOf()

    @Column(name = "is_archived")
    var isArchived = false
        set(archived) {
            try {
                field = archived
                if (archived) {
                    setChangeKind(ChangeKind.ARCHIVED)
                    if (Hibernate.isInitialized(getTopicGroups())) LOG.debug("getTopicGroups isInitialized. ") else Hibernate.initialize(
                        getTopicGroups()
                    )
                    for (topicGroup in getTopicGroups()) {
                        if (!topicGroup.isArchived()) topicGroup.setArchived(archived)
                    }
                }
            } catch (ex: Exception) {
                LOG.error("setArchived", ex)
                StackTraceFilter.filter(ex.stackTrace).stream()
                    .map { a -> a.toString() }
                    .forEach(LOG::info)
            }
        }



    fun addTopicGroup(topicGroup: TopicGroup): TopicGroup {
        topicGroups.add(topicGroup)
        topicGroup.st (this)
        setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION)
        setChangeComment("TopicGroup [" + topicGroup.getName().toString() + "] added")
        return topicGroup
    }


    val xmlBuilder: AbstractXmlBuilder?
        get() = null

    fun fillDoc(pdfReport: PdfReport, counter: String) {
        var counter = counter
        pdfReport.addHeader(this, "Study $counter")
        pdfReport.addParagraph(description)
        if (getComments().size() > 0) pdfReport.addheader2("Comments")
        pdfReport.addComments(getComments())
        pdfReport.addPadding()
        if (counter.length > 0) counter = "$counter."
        var i = 0
        for (topic in getTopicGroups()) {
            topic.fillDoc(pdfReport, counter + ++i)
        }
    }

    @PreRemove
    fun remove() {
        LOG.debug(" Study pre remove")
        if (getSurveyProgram() != null) {
            LOG.debug(getSurveyProgram().getName())
            getSurveyProgram().getStudies().removeIf(Predicate { p: Study -> p.getId() === this.getId() })
        }
        getAuthors()!!.clear()
        getInstruments()!!.clear()
    }

    protected fun beforeUpdate() {
        LOG.info("Study beforeUpdate")
        if (surveyIdx == null) {
            LOG.info("Setting surveyIdx")
            surveyIdx = getSurveyProgram().getStudies().indexOf(this)
        }
    }

    protected fun beforeInsert() {
        LOG.info("Study beforeInsert")
        if (getSurveyProgram() != null && surveyIdx == null) {
            LOG.info("Setting surveyIdx")
            surveyIdx = getSurveyProgram().getStudies().indexOf(this)
        } else {
            LOG.debug("no survey reference, cannot add..")
        }
    }

    val parentRef: ParentRef?
        get() = null
}
