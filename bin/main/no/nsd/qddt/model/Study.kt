package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.ParentRef
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IParentRef
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
 * bibliographic and discovery information, administrative information, all the reusable
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


    @JsonIgnore
    @Transient
    override var parentRef: IParentRef? = null
        get() = ParentRef(parent)

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE], targetEntity = TopicGroup::class)
    override var children: MutableList<ConceptHierarchy> = mutableListOf()

//
//    @JsonIgnore
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "CONCEPT_HIERARCHY_INSTRUMENT" )
//    @AttributeOverrides(AttributeOverride(name = "rev",column = Column(name = "revision", nullable =true)))
//    var instrumentUriIds : MutableSet<UriId> = mutableSetOf()

    @AuditMappedBy(mappedBy = "study")
    @OneToMany(mappedBy = "study", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Study

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name  , modified = $modified , classKind = $classKind )"
    }

}
