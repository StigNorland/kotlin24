package no.nsd.qddt.model.classes
import java.util.*

class SurveyOrders {
    private var content: List<SurveyOrder>? = null
    fun getContent(): List<SurveyOrder>? {
        return content
    }

    fun setContent(content: List<SurveyOrder>?) {
        this.content = content
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val surveyOrders = o as SurveyOrders
        return if (content != null) content == surveyOrders.content else surveyOrders.content == null
    }

    override fun hashCode(): Int {
        return if (content != null) content.hashCode() else 0
    }

    override fun toString(): String {
        return "{\"_class\":\"SurveyOrders\", " +
                "\"content\":" + (if (content == null) "null" else Arrays.toString(content.toTypedArray())) +
                "}"
    }
}
