package no.nsd.qddt.domain.author

/**
 * @author Stig Norland
 */
interface IAuthorSet {
    var authors: MutableSet<Author>
    /**
     * Add a [Author] to a [Set] of authors.
     * @param user added author.
     */
    fun addAuthor(author: Author):Author {
        this.authors.add(author)
        if (this.authors.stream().noneMatch { it.equals(authors) })
        {
           authors.add(author)
        }        
        return author
    }

}
