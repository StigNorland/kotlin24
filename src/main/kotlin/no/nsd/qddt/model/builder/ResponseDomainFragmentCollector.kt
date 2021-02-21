package no.nsd.qddt.model.builder

import no.nsd.qddt.model.enums.ResponseKind
import no.nsd.qddt.model.enums.ResponseKind.*
/**
* @author Stig Norland
*/
class ResponseDomainFragmentCollector {
  fun get(responseKind: ResponseKind):MutableMap<String, String> {
    return myMap.getOrElse(responseKind, defaultValue = { mutableMapOf()})
  }
  companion object {
    private val myMap:Map<ResponseKind, MutableMap<String, String>> =  mapOf(
      DATETIME to mutableMapOf(),
      TEXT to mutableMapOf(),
      NUMERIC  to mutableMapOf(),
      LIST to mutableMapOf(),
      SCALE to mutableMapOf(),
      MIXED to mutableMapOf()
    )
  }
}
