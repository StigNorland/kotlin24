package no.nsd.qddt.domain.author

/**
 * @author Stig Norland
 */
interface IAuthor {
    /**
     * Add a [Author] to a [Set] of authors.
     * @param user added author.
     */
    fun addAuthor(user: Author?)

    /**
     * Get all authors attached to this entity as a [Set]
     */
    fun getAuthors(): Set<Author?>?

    /**
     * Set the [Set] of [Author] for the entity.
     * @param authors populated set of authors.
     */
    fun setAuthors(authors: Set<Author?>?)
}
