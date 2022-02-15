package no.nsd.qddt.repository.criteria

class ControlConstructCriteria {
    var constructKind: String = ""
//    set(value) { field = "'$value'" }

    var superKind: String? = null

    var xmlLang: String="none"
//        set(value) { field = "'$value'" }

    var label: String = "*"
//        set(value) { field = "'$value'" }

    var name: String = "*"
//        set(value) { field = "'$value'" }

    var description: String = "*"
//        set(value) { field = "'$value'" }

    var questionName: String ="*"
//        set(value) { field = "'$value'" }

    var questionText: String ="*"
//        set(value) { field = "'$value'" }

    override fun toString(): String {
        return "CCCriteria(kind=$constructKind, superKind=$superKind, label=$label, name=$name, desc=$description, qName=$questionName, qText=$questionText, xmlLang=$xmlLang)"
    }


}