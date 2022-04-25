package no.nsd.qddt.model.interfaces

import no.nsd.qddt.model.enums.ElementKind
import org.springframework.data.repository.history.RevisionRepository
import java.util.*

interface RepLoaderService {

    fun <T> getRepository(elementKind: ElementKind): RevisionRepository<T, UUID, Int>
}
