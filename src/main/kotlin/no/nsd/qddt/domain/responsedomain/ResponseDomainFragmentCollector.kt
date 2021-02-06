package no.nsd.qddt.domain.responsedomain
import java.util.Collections
import java.util.HashMap
import no.nsd.qddt.domain.responsedomain.ResponseKind.*
/**
* @author Stig Norland
*/
class ResponseDomainFragmentCollector {
  fun get(responseKind:ResponseKind):Map<String, String> {
    return myMap.get(responseKind)
  }
  companion object {
    private val myMap:Map<ResponseKind, Map<String, String>>
    init{
      val aMap = HashMap<ResponseKind, Map<String, String>>(6)
      aMap.put(DATETIME, HashMap<String, String>())
      aMap.put(TEXT, HashMap<String, String>())
      aMap.put(NUMERIC, HashMap<String, String>())
      aMap.put(LIST, HashMap<String, String>())
      aMap.put(SCALE, HashMap<String, String>())
      aMap.put(MIXED, HashMap<String, String>())
      myMap = Collections.unmodifiableMap(aMap)
    }
  }
}