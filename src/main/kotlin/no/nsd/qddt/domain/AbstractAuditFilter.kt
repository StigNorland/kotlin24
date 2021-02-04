package no.nsd.qddt.domain

import no.nsd.qddt.domain.AbstractEntityAudit.ChangeKind
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.history.Revision
import org.springframework.data.history.Revisions
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Stig Norland
 */
abstract class AbstractAuditFilter<N, T : AbstractEntityAudit?> where N : Number?, N : Comparable<N>? {
    protected abstract fun postLoadProcessing(instance: Revision<N, T>?): Revision<N, T>?
    protected fun getPageIncLatest(
        revisions: Revisions<N, T>,
        filter: Collection<ChangeKind?>,
        pageable: Pageable
    ): Page<Revision<N, T>> {
        val skip = pageable.offset
        val limit = pageable.pageSize
        val totalsize =
            revisions.content.stream().filter { c: Revision<N, T> -> !filter.contains(c.entity!!.changeKind) }
                .count()
        return PageImpl(
            Stream.concat(
                Stream.of(revisions.latestRevision)
                    .map { c: Revision<N, T> ->
                        c.entity!!.version!!.versionLabel = "Latest version"
                        c
                    },
                revisions.reverse().content.stream()
                    .filter { f: Revision<N, T> -> !filter.contains(f.entity!!.changeKind) }
            )
                .skip(skip)
                .distinct()
                .limit(limit.toLong())
                .map { instance: Revision<N, T>? -> postLoadProcessing(instance) }
                .collect(Collectors.toList()), pageable, totalsize + 1)
    }

    protected fun getPage(
        revisions: Revisions<N, T>,
        filter: Collection<ChangeKind?>,
        pageable: Pageable
    ): Page<Revision<N, T>> {
        val totalsize =
            revisions.content.stream().filter { f: Revision<N, T> -> !filter.contains(f.entity!!.changeKind) }
                .count()
        return PageImpl(
            revisions.reverse().content.stream()
                .filter { c: Revision<N, T> -> !filter.contains(c.entity!!.changeKind) }
                .skip(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .map { instance: Revision<N, T>? -> postLoadProcessing(instance) }
                .collect(Collectors.toList()), pageable, totalsize)
    }
}
