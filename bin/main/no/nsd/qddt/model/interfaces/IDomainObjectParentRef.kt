package no.nsd.qddt.model.interfaces

/**
 * @author Stig Norland
 *
 * A ref is a simple interface which is intended to help
 * to report backreferences without ending up with a circular reference loop.
 */
interface IDomainObjectParentRef : IDomainObject {
    var parentRef: IParentRef?
}
