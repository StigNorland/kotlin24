package no.nsd.qddt.model.interfaces

import javax.lang.model.element.ElementKind

/**
 * @author Stig Norland
 */
interface IChildren {

//    @OneToMany(fetch = FetchType.EAGER,  mappedBy = "fk")
//    @MapKeyColumn(name = "elementKind")
    var children: MutableMap<ElementKind, MutableList<IElementRef<*>>>

}
