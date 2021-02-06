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
class Code:Comparable<Code> {
  @Column(name = "code_value")
  var value:String
  val isEmpty:Boolean
  @JsonIgnore
  get() {
    return StringTool.IsNullOrTrimEmpty(value)
  }
  constructor() {
    value = ""
  }
  constructor(value:String) {
    this.value = value
  }
  public override fun equals(o:Any):Boolean {
    if (this === o) return true
    if (o == null || javaClass != o.javaClass) return false
    val code = o as Code
    return if (value != null) value == code.value else code.value == null
  }
  public override fun hashCode():Int {
    return if (value != null) value.hashCode() else 0
  }
  public override fun toString():String {
    return ("{\"_class\":\"Code\", " +
            "\"value\":" + (if (value == null) "null" else "\"" + value + "\"") + ", " +
            "}")
  }
  public override fun compareTo(o:Code):Int {
    try
    {
      val a = Integer.parseInt(value)
      val b = Integer.parseInt(o.value)
      return a.compareTo(b)
    }
    catch (nfe:NumberFormatException) {
      return value.compareTo(o.value)
    }
  }
}