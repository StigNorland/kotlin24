package no.nsd.qddt.model.classes

import javax.persistence.Embeddable

/**
 * Indicates the minimum and maximum number of occurrences of a response within the given parameters.
 *
 * @author Stig Norland
 */
@Embeddable
class ResponseCardinality(
    var minimum: Int = 0,
    var maximum: Int = 1,
    var stepUnit: Int = 1
) {
    fun valid(): Boolean {
        return  minimum <= maximum && stepUnit >= -10 && stepUnit <= 10
    }
}
