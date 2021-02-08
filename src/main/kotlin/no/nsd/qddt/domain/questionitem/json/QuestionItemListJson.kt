// package no.nsd.qddt.domain.questionitem.json;

// import java.sql.Timestamp;
// import java.util.UUID;

// /**
//  * @author Stig Norland
//  */
// public class QuestionItemListJson {

//     @Type(type = "pg-uuid")
//     private UUID id;

//     private String name;

//     private String classKind;
 
//     private Timestamp modified;

//     private User modifiedBy;

//     private Agency agency;

//     @Embedded
//     private Version version;

//     private String question;

//     private String intent;

//     private String responseDomainName;

// //    private ResponseDomainJsonView responseDomain;


//     public QuestionItemListJson() {
//     }

//     public QuestionItemListJson(QuestionItem entity) {
//         if (entity == null) return;
//         id = entity.getId();
//         name = entity.name;
//         agency = new Agency(entity.agency);
//         version = entity.version;
//         modified = entity.getModified();
//         modifiedBy = entity.getModifiedBy();
//         question = entity.getQuestion();
//         intent = entity.getIntent();
//         responseDomainName =  (entity.getResponseDomainRef() != null) ? entity.getResponseDomainRef().name : "";
//         classKind = "QUESTION_ITEM";
// //        responseDomain = new ResponseDomainJsonView(entity.getResponseDomain());
// //        responseDomain.version.setRevision(entity.getResponseDomainRevision());
//     }

//     public UUID getId() {
//         return id;
//     }


//     public String name {
//         return name;
//     }


//     public String getQuestion() {
//         return question;
//     }


//     public String getIntent() {
//         return intent;
//     }

//     public String getResponseDomainName() {
//         return responseDomainName;
//     }

//     public Timestamp getModified() {
//         return modified;
//     }


//     public User getModifiedBy() {
//         return modifiedBy;
//     }


//     public Agency agency {
//         return agency;
//     }


//     public Version version {
//         return version;
//     }

//     public String getClassKind() {
//         return classKind;
//     }

// }
