// package no.nsd.qddt.domain.instruction;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import no.nsd.qddt.domain.AbstractEntityAudit;
// import no.nsd.qddt.classes.pdf.PdfReport;
// import no.nsd.qddt.classes.xml.AbstractXmlBuilder;
// import no.nsd.qddt.classes.xml.XmlDDIFragmentBuilder;
// import org.hibernate.envers.Audited;

// import javax.persistence.*;
// import java.util.Collections;

// import static no.nsd.qddt.utils.StringTool.IsNullOrTrimEmpty;

// /**
//  * @author Stig Norland
//  */
// @JsonIgnoreProperties(ignoreUnknown = true)
// @Entity
// @Audited
// @Table(name = "INSTRUCTION", uniqueConstraints = {@UniqueConstraint(columnNames = {"name","description","agency_id"},name = "UNQ_INSTRUCTION_NAME")})
// class Instruction(
//     @Column(name = "description", length = 2000,nullable = false)
//     var description: String = ""
//         set(value) {
//             field = value
//             if (IsNullOrTrimEmpty(super.name)) {
//                 var max25 = minOf(value.length,25)
//                 super.name = value.substring(0,max25).toUpperCase().replace(' ','_')
//             }
//         }

// ): AbstractEntityAudit() {

//     @Override
//     public void fillDoc(PdfReport pdfReport,String counter) {
//     }


//     @Override
//     public AbstractXmlBuilder getXmlBuilder() {
//         return new XmlDDIFragmentBuilder<Instruction>( this ) {
//             public String getXmlFragment() {
//                 return String.format( xmlInstruction,
//                     getXmlHeader(entity),
//                     entity.name,
//                     entity.description,
//                     entity.xmlLang);
//             }

//             @Override
//             public String getXmlEntityRef(int depth) {
//                 return String.format( xmlInsRef, "PRE" , getXmlURN(entity)  , String.join("", Collections.nCopies(depth, "\t")) );
//             }
//         };
//     }

//     @JsonIgnore
//     @Transient
//     protected final String xmlInsRef =
//         "%3$s<r:InterviewerInstructionReference>\n" +
//             "%3$s\t%2$s" +
//             "%3$s\t<r:TypeOfObject>Instruction</r:TypeOfObject>\n" +
//             "%3$s\t<InstructionAttachmentLocation><r:Value xml:space=\"default\">%1$s</r:Value></InstructionAttachmentLocation>\n"+
//             "%3$s</r:InterviewerInstructionReference>\n";

//     @JsonIgnore
//     @Transient
//     private final String xmlInstruction =
//         "%1$s"+
//             "\t\t\t<c:InstructionName>\n" +
//             "\t\t\t\t<r:String xml:lang=\"%4$s\">%2$s</r:String>\n" +
//             "\t\t\t</c:InstructionName>\n"+
//             "\t\t\t<r:Description>\n" +
//             "\t\t\t\t<r:Content xml:lang=\"%4$s\" isPlainText=\"false\"><![CDATA[%3$s]]></r:Content>\n" +
//             "\t\t\t</r:Description>\n" +
//             "\t\t</c:Instruction>\n";


// }
