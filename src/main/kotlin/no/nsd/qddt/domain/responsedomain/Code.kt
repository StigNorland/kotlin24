package no.nsd.qddt.domain.responsedomain
import com.fasterxml.jackson.annotation.JsonIgnore
import no.nsd.qddt.utils.StringTool
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
  var value:String
):Comparable<Code> {
  
  public override fun compareTo(other:Code):Int {
    try
    {
      val a = Integer.parseInt(value)
      val b = Integer.parseInt(other.value)
      return a.compareTo(b)
    }
    catch (nfe:NumberFormatException) {
      return value.compareTo(other.value)
    }
  }
}