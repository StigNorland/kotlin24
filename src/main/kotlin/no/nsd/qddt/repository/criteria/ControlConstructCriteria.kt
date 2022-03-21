package no.nsd.qddt.repository.criteria

class ControlConstructCriteria {
    var constructKind: String = ""
    var superKind: String? = null
    var xmlLang: String="none"
    var label: String = "*"
    var name: String = "*"
    var description: String = "*"
    var questionName: String ="*"
    var questionText: String ="*"
    override fun toString(): String {
        return "CCCriteria(kind=$constructKind, superKind=$superKind, label=$label, name=$name, desc=$description, qName=$questionName, qText=$questionText, xmlLang=$xmlLang)"
    }


}