package no.nsd.qddt.repository.projection

import no.nsd.qddt.model.QuestionItem
import no.nsd.qddt.model.interfaces.IWebMenuPreview
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.rest.core.config.Projection


/**
 * @author Stig Norland
 */
@Projection(name = "questionItemListe", types = [QuestionItem::class])
interface QuestionItemListe: IAbstractEntityViewList {
    var question: String
    var intent: String
}

