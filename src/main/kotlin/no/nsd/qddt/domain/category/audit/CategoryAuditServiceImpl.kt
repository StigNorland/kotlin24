package no.nsd.qddt.domain.category.audit

import no.nsd.qddt.domain.category.Category
import no.nsd.qddt.utils.FilterTool.defaultOrModifiedSort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author Dag Østgulen Heradstveit
 */
@Service("categoryAuditService")
internal class CategoryAuditServiceImpl @Autowired constructor(private val categoryAuditRepository: CategoryAuditRepository) :
    AbstractAuditFilter<Int?, Category?>(), CategoryAuditService {
    @Transactional(readOnly = true)
    override fun findLastChange(uuid: UUID): Revision<Int, Category>? {
        return categoryAuditRepository.findLastChangeRevision(uuid).get()
    }

    @Transactional(readOnly = true)
    override fun findRevision(uuid: UUID, revision: Int): Revision<Int, Category>? {
        return categoryAuditRepository.findRevision(uuid, revision).get()
    }

    @Transactional(readOnly = true)
    override fun findRevisions(uuid: UUID, pageable: Pageable?): Page<Revision<Int, Category>?>? {
        return try {
            categoryAuditRepository.findRevisions(uuid, defaultOrModifiedSort(pageable))
            //modified?
        } catch (e: Exception) {
            StackTraceFilter.println(e.stackTrace)
            println(e.message)
            null
        }
    }

    @Transactional(readOnly = true)
    override fun findFirstChange(uuid: UUID): Revision<Int, Category>? {
        return categoryAuditRepository.findRevisions(uuid)
            .reverse().content[0]
    }

    @Transactional(readOnly = true)
    override fun findRevisionByIdAndChangeKindNotIn(
        id: UUID,
        changeKinds: Collection<ChangeKind?>?,
        pageable: Pageable?
    ): Page<Revision<Int?, Category?>?> {
        return getPage(categoryAuditRepository.findRevisions(id), changeKinds, pageable)
    }

    // Categories most likely don't have discussions about them... and you are not often interested in old versions of a category,
    // hence we don't need to fetch comments that never are there...
    override fun setShowPrivateComment(showPrivate: Boolean) {
        // no implementation
    }

    protected override fun postLoadProcessing(instance: Revision<Int, Category>?): Revision<Int, Category>? {
        return instance
    }
}
