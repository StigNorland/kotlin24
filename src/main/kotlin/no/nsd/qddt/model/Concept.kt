package no.nsd.qddt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.model.builder.ConceptFragmentBuilder
import no.nsd.qddt.model.builder.pdf.PdfReport
import no.nsd.qddt.model.builder.xml.AbstractXmlBuilder
import no.nsd.qddt.model.classes.ParentRef
import no.nsd.qddt.model.embedded.ElementRefQuestionItem
import no.nsd.qddt.model.interfaces.IArchived
import no.nsd.qddt.model.interfaces.IAuthorSet
import no.nsd.qddt.model.interfaces.IParentRef
import no.nsd.qddt.model.interfaces.IQuestionItemRef
import org.hibernate.Hibernate
import org.hibernate.envers.AuditMappedBy
import org.hibernate.envers.Audited
import javax.persistence.*

/**
 * <ul>
 *     <li>A Concept consist of one or more QuestionItems.</li>
 *         <li>Every QuestionItem will have a Question.</li>
 *         <li>Every QuestionItem will have a ResponseDomain.</li>
 * </ul>
 * <br>
 * ConceptScheme: Concepts express ideas associated with objects and means of representing the concept
 *
 * @author Stig Norland
 * @author Dag Østgulen Heradstveit
 */

@Audited
@Entity
@DiscriminatorValue("CONCEPT")
@Cacheable
data class Concept(override var name: String ="?") : ConceptHierarchy(), IAuthorSet, IArchived, IQuestionItemRef {

    @Column(insertable = false, updatable = false)
    var parentIdx: Int? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    override lateinit var parent: ConceptHierarchy

    @OrderColumn(name = "parentIdx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "parentIdx")
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REMOVE], targetEntity = Concept::class)
    override var children: MutableList<ConceptHierarchy> = mutableListOf()

    @OrderColumn(name="parentIdx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "CONCEPT_HIERARCHY_QUESTION_ITEM",
        joinColumns = [JoinColumn(name = "parent_id", referencedColumnName = "id")])
    override var questionItems:MutableList<ElementRefQuestionItem> = mutableListOf()
    get() = field.filterNotNull().toMutableList()

    @JsonIgnore
    @Transient
    override var parentRef: IParentRef? = null
        get() = ParentRef(parent)


    override fun fillDoc(pdfReport: PdfReport, counter: String) {
        try {
            pdfReport.addHeader(this, "Concept $counter")
            pdfReport.addParagraph(this.description)

            if (comments.size > 0) {
                pdfReport.addHeader2("Comments")
                pdfReport.addComments(comments)
            }

            if (questionItems.size > 0) {
                pdfReport.addHeader2("QuestionItem(s)")
                questionItems.stream().map {
                    it.element
                }
                    .forEach {
                        if (it != null) {
                            pdfReport.addHeader2(it.name, String.format("Version %s", it.version))
                            pdfReport.addParagraph(it.question)
                            it.response?.fillDoc(pdfReport, "")
                        }
                    }
            }
            pdfReport.addPadding()

//            var i = 0
//            children.forEach {
//                it.fillDoc(pdfReport, counter + "." + ++i)
//            }
//
//            if (children.size == 0)
//                pdfReport.addPadding()

        } catch (ex:Exception) {
            logger.error(ex.message)
            throw ex
        }
    }

    override fun xmlBuilder(): AbstractXmlBuilder {
        this.children.size
        return ConceptFragmentBuilder(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Concept

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , modified = $modified , classKind = $classKind )"
    }

}
