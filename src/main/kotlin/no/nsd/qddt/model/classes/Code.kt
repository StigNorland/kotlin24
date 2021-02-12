package no.nsd.qddt.model.classes
import org.hibernate.envers.Audited
import javax.persistence.Column
import javax.persistence.Embeddable
/**
*
* @author Dag Østgulen Heradstveit
* @author Stig Norland
*/
@Audited
@Embeddable
class Code(
  @Column(name = "code_value")
  var value:String=""
):Comparable<Code> {
  
  override fun compareTo(other: Code):Int {
    return try
    {
      val a = Integer.parseInt(value)
      val b = Integer.parseInt(other.value)
      a.compareTo(b)
    }
    catch (nfe:NumberFormatException) {
      value.compareTo(other.value)
    }
  }
}
