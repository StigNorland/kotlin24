package no.nsd.qddt.domain.concept;

import no.nsd.qddt.domain.classes.*
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import javax.persistence.*
import org.hibernate.envers.Audited

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
@Table(name = "CONCEPT")
class Concept(

    private var label: String,

    @Column(length = 20000)
    private var description: String,

    private var isArchived: Boolean,


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "topicGroupRef")
    @JoinColumn(name="topicgroup_id", nullable = false,updatable = false)
    val topicGroup: TopicGroup,

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name="concept_id")
    @JsonBackReference(value = "conceptParentRef")
    var parent: Concept,

    // in the @OrderColumn annotation on the referencing entity.
    @Column( name = "concept_idx", insertable = false, updatable = false)
    private var conceptIdx: Long,


    @OrderColumn(name="concept_idx")
    @AuditMappedBy(mappedBy = "parent", positionMappedBy = "conceptIdx")
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER,
        orphanRemoval = true, cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE})
    var  children: List<Concept>,


    @OrderColumn(name="concept_idx")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CONCEPT_QUESTION_ITEM",
        joinColumns = @JoinColumn(name="concept_id", referencedColumnName = "id"))
    var conceptQuestionItems: List<ElementRefEmbedded<QuestionItem>>,


    @Transient
    private var parentRef ParentRef<TopicGroup>

): AbstractEntityAudit , IArchived, IDomainObjectParentRef {

    fun removeQuestionItem(UUID id, Integer rev) {
        if (conceptQuestionItems.removeIf( q -> q.getElementId().equals( id) && q.getElementRevision().equals( rev ))) {
            this.setChangeKind( ChangeKind.UPDATED_HIERARCHY_RELATION );
            this.setChangeComment( "QuestionItem assosiation removed" );
            this.getParents().forEach( p -> {
                p.setChangeKind( ChangeKind.UPDATED_CHILD );
                p.setChangeComment( "QuestionItem assosiation removed from child" );
            } );
        }
    }

    fun addQuestionItem(UUID id, Integer rev) {
        addQuestionItem( new ElementRefEmbedded<>( ElementKind.QUESTION_ITEM, id,rev ) );
    }

    fun addQuestionItem(ElementRefEmbedded<QuestionItem> qef) {
        if (this.conceptQuestionItems.stream().noneMatch(cqi->cqi.equals( qef ))) {

            conceptQuestionItems.add(qef);
            this.setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION);
            this.setChangeComment("QuestionItem assosiation added");
            this.getParents().forEach(p->p.setChangeKind(ChangeKind.UPDATED_CHILD));
        }
        else
            LOG.debug("ConceptQuestionItem not inserted, match found" );
    }

    fun addChildren(Concept concept): Concept {
        if(concept == null) return null;
        this.children.add( concept );
        concept.setParent( this);
        setChangeKind(ChangeKind.UPDATED_HIERARCHY_RELATION);
        setChangeComment("SubConcept added");
        getParents().forEach(p->p.setChangeKind(ChangeKind.UPDATED_CHILD));
        return concept;
    }


    fun hasTopicGroup(): Boolean {
        return (topicGroup != null);
    }

    private fun getParents(): List<AbstractEntityAudit> {
        List<AbstractEntityAudit> retvals = new ArrayList<>( 1 );
        Concept current = this;
        while(current.getParent() !=  null){
            current = current.getParent();
            retvals.add( current );
        }
        if (current.getTopicGroup()!= null)
            retvals.add( current.getTopicGroup() );         //this will fail for Concepts that return from clients.
        return retvals; // .stream().filter( f -> f != null ).collect( Collectors.toList());
    }

    @Override
    public String toString() {
        return "{" +
            "\"id\":" + (getId() == null ? "null" : "\"" + getId() +"\"" ) + ", " +
            "\"name\":" + (getName() == null ? "null" : "\"" + getName() + "\"") + ", " +
            "\"label\":" + (label == null ? "null" : "\"" + label + "\"") + ", " +
            "\"description\":" + (description == null ? "null" : "\"" + description + "\"") + ", " +
            "\"topicGroupId\":" + (topicGroupId == null ? "null" : topicGroupId) + ", " +
            "\"conceptQuestionItems\":" + (conceptQuestionItems == null ? "null" : Arrays.toString( conceptQuestionItems.toArray() )) + ", " +
            "\"children\":" + (children == null ? "null" : Arrays.toString( children.toArray() )) + ", " +
            "\"modified\":" + (getModified() == null ? "null" : "\"" + getModified()+ "\"" ) + " , " +
            "\"modifiedBy\":" + (getModifiedBy() == null ? "null" : getModifiedBy()) +
            "}";
    }


    @Override
    fun getXmlBuilder():AbstractXmlBuilder {
        return new ConceptFragmentBuilder(this);
    }

    @Override
    fun fillDoc(PdfReport pdfReport,String counter ) {
        try {
            pdfReport.addHeader(this, "Concept " + counter )
            pdfReport.addParagraph( this.description )

            if (getComments().size() > 0) {
                pdfReport.addheader2("Comments")
                pdfReport.addComments(getComments())
            }

            if (getConceptQuestionItems().size() > 0) {
                pdfReport.addheader2("QuestionItem(s)")
                getConceptQuestionItems().stream()
                    .map( cqi -> {
                        if (cqi.getElement() == null) {
                            LOG.info( cqi.toString() )
                            return null;
                        }
                        return cqi.getElement()
                    } )
                    .filter( Objects::nonNull )
                    .forEach( item -> {
                        pdfReport.addheader2( item.getName(), String.format( "Version %s", item.getVersion() ) )
                        pdfReport.addParagraph( item.getQuestion() )
                        if (item.getResponseDomainRef().getElement() != null)
                            item.getResponseDomainRef().getElement().fillDoc( pdfReport, "" )
                })
            }

            pdfReport.addPadding()

            if (counter.length()>0)
                counter = counter+"."
                int i = 0;
            for (Concept concept : getChildren()) {
                concept.fillDoc(pdfReport, counter + ++i )
            }

            if (getChildren().size() == 0)
                 pdfReport.addPadding()

        } catch (Exception ex) {
            LOG.error(ex.getMessage())
            throw ex
        }
    }


}
