package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.Author

/**
 * @author Stig Norland
 */
interface IAuthorSet {
    var authors: MutableSet<Author>
    /**
     * Add a [Author] to a [Set] of authors.
     * @param user added author.
     */
    fun addAuthor(author: Author): Author {
        this.authors.add(author)
        if (this.authors.stream().noneMatch { it.equals(authors) })
        {
           authors.add(author)
        }        
        return author
    }

}
