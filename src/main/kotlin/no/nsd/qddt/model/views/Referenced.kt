//package no.nsd.qddt.model
//
//import org.hibernate.annotations.Immutable
//import java.sql.Timestamp
//import java.util.*
//import javax.persistence.Entity
//import javax.persistence.Id
//import javax.persistence.Table
//
///**
// * @author Stig Norland
// */
//@Entity
//@Table(name = "referenced")
//@Immutable
//class Referenced {
//    // @Type(type = "no.nsd.qddt.utils.GenericArrayType")
//    // public UUID[] refs;
//    @Id
//    var entity: UUID? = null
//    var kind: String? = null
//    var modified: Timestamp? = null
//    var antall: Long? = null
//
//    // public UUID[] getRefs() {
//    //     return refs;
//    // }
//    // public void setRefs(UUID[] refs) {
//    //     this.refs = refs;
//    // }
//
//}