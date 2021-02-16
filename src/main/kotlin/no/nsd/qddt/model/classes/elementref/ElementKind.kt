package no.nsd.qddt.model.classes.elementref

/**
 * @author Stig Norland
 */
enum class ElementKind( name: String, val className: String, val ddiPreFix: String) {
    SURVEY_PROGRAM("Survey", "SurveyProgram", ""),
    STUDY("Study", "Study", ""),
    TOPIC_GROUP("Module","TopicGroup","c"),
    CONCEPT("Concept", "Concept", "c"), 
    QUESTION_ITEM("Question Item","QuestionItem","d"),
    RESPONSEDOMAIN("Response Domain", "ResponseDomain", "r"),
    CATEGORY("Category","Category","l"),
    INSTRUMENT("Instrument", "Instrument", "c"), 
    PUBLICATION("Publication","Publication","c"),
    CONTROL_CONSTRUCT("Control Construct", "ControlConstruct", "d"),
    QUESTION_CONSTRUCT("Question Construct","QuestionConstruct","d"),
    STATEMENT_CONSTRUCT("Statement", "StatementItem", "d"), 
    CONDITION_CONSTRUCT("Condition","ConditionConstruct","d"),
    SEQUENCE_CONSTRUCT("Sequence", "Sequence", "c"), 
    INSTRUCTION("Instruction","Instruction","d"),
    UNIVERSE("Universe", "Universe", "d"), 
    COMMENT("Comment", "Comment", "c");

    companion object {
        fun getEnum(className: String?): ElementKind {
            requireNotNull(className) { "className not specified." }
            for (v in values()) if (className.equals(v.className, ignoreCase = true)) return v
            return valueOf(className)

//        throw new IllegalArgumentException("className not found. [" + className + "]");
        }
    }
}
