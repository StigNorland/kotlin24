package no.nsd.qddt.service

import no.nsd.qddt.model.enums.ElementKind
import org.springframework.data.repository.history.RevisionRepository
import java.util.*

interface RepositoryLoader {

    fun <T> getRepository(elementKind: ElementKind): RevisionRepository<T, UUID, Int>
}
