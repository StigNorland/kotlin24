package no.nsd.qddt.service

import org.springframework.dao.DataAccessException

/**
 * @author Stig Norland
 */
interface BaseService<T, ID> {


    /**
     * Return a entity based on its ID.
     * @param id ID of entity
     * @return Entity
     */
    fun <S : T?> findOne(id: ID): S

    /**
     * Store object T to backstore
     * @param instance object T
     * @return saved instance T (may have fields updated by backstore)
     */
    //    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EDITOR')")
    fun <S : T?> save(instance: S): S

    /**
     * Deletes object with id ID from backstore, exception raised by failure.
     * @param id ID of entity
     */
    //    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Throws(DataAccessException::class)
    fun delete(id: ID)
    /**
     * Deletes object with these IDs from backstore, exception raised by failure.
     * @param instances list of entity IDs
     */
    //    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    //    void delete(List<T> instances) throws DataAccessException;
}
