package no.nsd.qddt.domain.classes.interfaces

/**
 * @author Stig Norland
 *
 * A ref is a simple interface which is intended to help
 * reporting backreferences without ending up with a circular reference loop.
 */
interface IDomainObjectParentRef : IDomainObject {
    var parentRef: IParentRef?
}
