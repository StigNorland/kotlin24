//package no.nsd.qddt.classes.elementref
//
//import com.fasterxml.jackson.databind.annotation.JsonSerialize
//import no.nsd.qddt.classes.interfaces.Version
//import no.nsd.qddt.classes.interfaces.IElementRef
//import java.util.*
//import javax.persistence.*
////import no.nsd.qddt.domain.controlconstruct.pojo.ControlConstruct
//
///**
// * @author Stig Norland
// */
//@Embeddable
//data class ElementRefCondition<T : ControlConstruct?> : IElementRef<T> {
//    /**
//     * This field will be populated with the correct version of a QI,
//     * but should never be persisted.
//     */
//
//    @Column(name = "questionitem_id")
//    override var elementId: UUID? = null
//
//    @Column(name = "questionitem_revision")
//    override var elementRevision: Int? = null
//
//    @Transient
//    @JsonSerialize
//    @Enumerated(EnumType.STRING)
//    override var elementKind = ElementKind.CONDITION_CONSTRUCT
//
//    @Column(name = "question_name", length = 25)
//    override var name: String? = null
//
//    override var version: Version? = null
//        get() =  element?.version ?: field
//
//
//    @Column(name = "question_text", length = 500)
//    var condition: String? = null
//            set(value){
//                field = value? value.subSequence(0, minOf(value.length,499)).toString()
//            }
//
//
//    @Transient
//    @JsonSerialize
//    var element: T? = null
//        set(value) {
//            field = value.also {
//                elementId = it!!.id
//                name = it.name
//                version.revision = elementRevision
//
//                }
//
//            if (element != null) {
//            } else {
//                name = null
//                setCondition(null)
//                elementRevision = null
//                setElementId(null)
//            }}
//
//    fun setCondition(condition: String?) {
//        if (condition != null) {
//            val min = Integer.min(condition.length, 500)
//            this.condition = condition.substring(0, min)
//        } else {
//            this.condition = null
//        }
//    }
//
//
//
//    override fun setElement(element: T?) {
//        this.element = element
//        if (element != null) {
//            setElementId(element.getId())
//            name = element.name
//            //            setCondition( element.getCondition() );
//            version.setRevision(elementRevision)
//        } else {
//            name = null
//            setCondition(null)
//            elementRevision = null
//            setElementId(null)
//        }
//    }
//
//
//}
