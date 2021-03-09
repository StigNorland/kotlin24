DROP TABLE agency_aud;
CREATE TABLE agency_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, name CHARACTER VARYING(50), PRIMARY KEY (id, rev));
DROP TABLE author_aud;
CREATE TABLE author_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, about CHARACTER VARYING(500), authors_affiliation CHARACTER VARYING(255), email CHARACTER VARYING(255), homepage CHARACTER VARYING(255), name CHARACTER VARYING(70), picture CHARACTER VARYING(255), user_id UUID, PRIMARY KEY (id, rev));
DROP TABLE authority_aud;
CREATE TABLE authority_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, authority CHARACTER VARYING(255), name CHARACTER VARYING(255), PRIMARY KEY (id, rev));
DROP TABLE category_aud;
CREATE TABLE category_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), category_kind CHARACTER VARYING(255), classification_level CHARACTER VARYING(255), description CHARACTER VARYING(2000), format CHARACTER VARYING(255), hierarchy_level CHARACTER VARYING(255), maximum CHARACTER VARYING(255), minimum CHARACTER VARYING(255), label CHARACTER VARYING(255), user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE category_children_aud;
CREATE TABLE category_children_aud (rev INTEGER NOT NULL, category_id UUID NOT NULL, children_id UUID NOT NULL, category_idx INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, category_id, children_id, category_idx));
DROP TABLE code_aud;
CREATE TABLE code_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, responsedomain_id UUID NOT NULL, responsedomain_idx INTEGER NOT NULL, revend INTEGER, alignment CHARACTER VARYING(255), code_value CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, responsedomain_id, responsedomain_idx));
DROP TABLE concept_aud;
CREATE TABLE concept_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(10000), is_archived BOOLEAN, label CHARACTER VARYING(255), topicgroup_id UUID, user_id UUID, agency_id UUID, concept_id UUID, PRIMARY KEY (id, rev));
DROP TABLE concept_question_item_aud;
CREATE TABLE concept_question_item_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, concept_id UUID NOT NULL, concept_idx INTEGER NOT NULL, revend INTEGER, element_id UUID, version_label CHARACTER VARYING(255), major INTEGER, minor INTEGER, element_kind CHARACTER VARYING(255), element_revision INTEGER, name CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, concept_id, concept_idx));
DROP TABLE control_construct_aud;
CREATE TABLE control_construct_aud (id UUID NOT NULL, rev INTEGER NOT NULL, control_construct_kind CHARACTER VARYING(31) NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), label CHARACTER VARYING(255), user_id UUID, agency_id UUID, description CHARACTER VARYING(1500), questionitem_revision INTEGER, questionitem_id UUID, question_name CHARACTER VARYING(25), question_text CHARACTER VARYING(500), control_construct_super_kind CHARACTER VARYING(255), PRIMARY KEY (id, rev));
DROP TABLE control_construct_instruction_aud;
CREATE TABLE control_construct_instruction_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, control_construct_id UUID NOT NULL, instruction_idx INTEGER NOT NULL, revend INTEGER, instruction_rank CHARACTER VARYING(255), instruction_id UUID, PRIMARY KEY (rev, revtype, control_construct_id, instruction_idx));
DROP TABLE control_construct_other_material_aud;
CREATE TABLE control_construct_other_material_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, owner_id UUID NOT NULL, owner_idx INTEGER NOT NULL, revend INTEGER, original_name CHARACTER VARYING(255), original_owner UUID, file_name CHARACTER VARYING(255), size BIGINT, description CHARACTER VARYING(255), file_type CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, owner_id, owner_idx));
DROP TABLE control_construct_sequence_aud;
CREATE TABLE control_construct_sequence_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, sequence_id UUID NOT NULL, sequence_idx INTEGER NOT NULL, revend INTEGER, element_id UUID, version_label CHARACTER VARYING(255), major INTEGER, minor INTEGER, element_kind CHARACTER VARYING(255), element_revision INTEGER, name CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, sequence_id, sequence_idx));
DROP TABLE control_construct_universe_aud;
CREATE TABLE control_construct_universe_aud (rev INTEGER NOT NULL, question_construct_id UUID NOT NULL, universe_id UUID NOT NULL, universe_idx INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, question_construct_id, universe_id, universe_idx));
DROP TABLE instruction_aud;
CREATE TABLE instruction_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(255), user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE instrument_aud;
CREATE TABLE instrument_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(255), external_instrument_location CHARACTER VARYING(255), instrument_kind CHARACTER VARYING(255), label CHARACTER VARYING(255), user_id UUID, agency_id UUID, study_id UUID, PRIMARY KEY (id, rev));
DROP TABLE instrument_element_aud;
CREATE TABLE instrument_element_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, element_id UUID, element_kind CHARACTER VARYING(255), element_revision INTEGER, major INTEGER, minor INTEGER, name CHARACTER VARYING(255), version_label CHARACTER VARYING(255), _idx INTEGER, instrument_element_id UUID, PRIMARY KEY (id, rev));
DROP TABLE instrument_element_parameter_aud;
CREATE TABLE instrument_element_parameter_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, instrument_element_id UUID NOT NULL, setordinal INTEGER NOT NULL, revend INTEGER, name CHARACTER VARYING(255), referenced_id UUID, PRIMARY KEY (rev, revtype, instrument_element_id, setordinal));
DROP TABLE instrument_instrument_element_aud;
CREATE TABLE instrument_instrument_element_aud (rev INTEGER NOT NULL, instrument_id UUID NOT NULL, id UUID NOT NULL, _idx INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, instrument_id, id, _idx));
DROP TABLE publication_aud;
CREATE TABLE publication_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), purpose CHARACTER VARYING(255), user_id UUID, agency_id UUID, status_id BIGINT, PRIMARY KEY (id, rev));
DROP TABLE publication_element_aud;
CREATE TABLE publication_element_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, publication_id UUID NOT NULL, publication_idx INTEGER NOT NULL, revend INTEGER, element_id UUID, version_label CHARACTER VARYING(255), major INTEGER, minor INTEGER, element_kind CHARACTER VARYING(255), element_revision INTEGER, name CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, publication_id, publication_idx));
DROP TABLE question_item_aud;
CREATE TABLE question_item_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), intent CHARACTER VARYING(3000), question CHARACTER VARYING(2000), responsedomain_name CHARACTER VARYING(255), responsedomain_revision INTEGER, responsedomain_id UUID, user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE responsedomain_aud;
CREATE TABLE responsedomain_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(2000), display_layout CHARACTER VARYING(255), maximum CHARACTER VARYING(255), minimum CHARACTER VARYING(255), response_kind CHARACTER VARYING(255), schema_id UUID, user_id UUID, agency_id UUID, category_id UUID, PRIMARY KEY (id, rev));
DROP TABLE study_aud;
CREATE TABLE study_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(10000), is_archived BOOLEAN, user_id UUID, agency_id UUID, survey_id UUID, PRIMARY KEY (id, rev));
DROP TABLE study_authors_aud;
CREATE TABLE study_authors_aud (rev INTEGER NOT NULL, study_id UUID NOT NULL, author_id UUID NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, study_id, author_id));
DROP TABLE survey_program_aud;
CREATE TABLE survey_program_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(10000), is_archived BOOLEAN, user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE survey_program_authors_aud;
CREATE TABLE survey_program_authors_aud (rev INTEGER NOT NULL, survey_id UUID NOT NULL, author_id UUID NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, survey_id, author_id));
DROP TABLE topic_group_aud;
CREATE TABLE topic_group_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(10000), is_archived BOOLEAN, study_id UUID, user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE topic_group_authors_aud;
CREATE TABLE topic_group_authors_aud (rev INTEGER NOT NULL, topicgroup_id UUID NOT NULL, author_id UUID NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, topicgroup_id, author_id));
DROP TABLE topic_group_other_material_aud;
CREATE TABLE topic_group_other_material_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, owner_id UUID NOT NULL, owner_idx INTEGER NOT NULL, revend INTEGER, original_name CHARACTER VARYING(255), original_owner UUID, file_name CHARACTER VARYING(255), size BIGINT, description CHARACTER VARYING(255), file_type CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, owner_id, owner_idx));
DROP TABLE topic_group_question_item_aud;
CREATE TABLE topic_group_question_item_aud (rev INTEGER NOT NULL, revtype SMALLINT NOT NULL, topicgroup_id UUID NOT NULL, topicgroup_idx INTEGER NOT NULL, revend INTEGER, element_id UUID, version_label CHARACTER VARYING(255), major INTEGER, minor INTEGER, element_kind CHARACTER VARYING(255), element_revision INTEGER, name CHARACTER VARYING(255), PRIMARY KEY (rev, revtype, topicgroup_id, topicgroup_idx));
DROP TABLE universe_aud;
CREATE TABLE universe_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, updated TIMESTAMP(6) WITHOUT TIME ZONE, based_on_object UUID, based_on_revision INTEGER, change_comment CHARACTER VARYING(255), change_kind CHARACTER VARYING(255), name CHARACTER VARYING(255), major INTEGER, minor INTEGER, version_label CHARACTER VARYING(255), xml_lang CHARACTER VARYING(255), description CHARACTER VARYING(2000), user_id UUID, agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE user_account_aud;
CREATE TABLE user_account_aud (id UUID NOT NULL, rev INTEGER NOT NULL, revtype SMALLINT, revend INTEGER, email CHARACTER VARYING(255), is_enabled BOOLEAN, updated TIMESTAMP(6) WITHOUT TIME ZONE, username CHARACTER VARYING(255), password CHARACTER VARYING(255), agency_id UUID, PRIMARY KEY (id, rev));
DROP TABLE user_authority_aud;
CREATE TABLE user_authority_aud (rev INTEGER NOT NULL, user_id UUID NOT NULL, authority_id UUID NOT NULL, revtype SMALLINT, revend INTEGER, PRIMARY KEY (rev, user_id, authority_id));
ALTER TABLE "agency_aud" ADD CONSTRAINT fkmh5nk40v67ybqxrksa7p0erp1 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "agency_aud" ADD CONSTRAINT fkhkwnx38flmtgfp2qhwd6wj7yp FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "author_aud" ADD CONSTRAINT fk4gh4hava2an2qfcslwass6ds FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "author_aud" ADD CONSTRAINT fka5nlvh0eptw9df90726v1et52 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "authority_aud" ADD CONSTRAINT fk3fiaui9nu7my1tolfjsxsgjjw FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "authority_aud" ADD CONSTRAINT fk6jjpsoqfp4w5nhjw8t47vgtpj FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "category_aud" ADD CONSTRAINT fkc9m640crhsib2ws80um6xuk1w FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "category_aud" ADD CONSTRAINT fknwfvp1r5owwtds6e542wd49fb FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "category_children_aud" ADD CONSTRAINT fkor4k72fvdjaab6v75itnu9fux FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "category_children_aud" ADD CONSTRAINT fk4kl7pjwbysqql9csmhbh9xx2p FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "code_aud" ADD CONSTRAINT fkng60x4976yaputir4h08xmqh1 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "code_aud" ADD CONSTRAINT fkxn52fa5stu6bats5itdlhdga FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "concept_aud" ADD CONSTRAINT fkfbs3ri8ukp8238rcevo1eol4l FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "concept_aud" ADD CONSTRAINT fk7qa9xcx6a99v5ci345k0a3wdk FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "concept_question_item_aud" ADD CONSTRAINT fkndsdut37swxqhf6j47d0626sx FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "concept_question_item_aud" ADD CONSTRAINT fkph1m59hul85fiemepqpprbx9j FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_aud" ADD CONSTRAINT fkrfi56l7pplyucmb4ewkrlcjih FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_aud" ADD CONSTRAINT fkcy6n5886k5okudqlbjw8qsr4y FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_instruction_aud" ADD CONSTRAINT fk9sb70ku6jge60wxribtwnjbh4 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_instruction_aud" ADD CONSTRAINT fk1usuh4ph31vypj7esgkr1t6ey FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_other_material_aud" ADD CONSTRAINT fk5v9vs049hk1u794rjm1elmp3x FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_other_material_aud" ADD CONSTRAINT fk3aowdn4130d1j9ejn1nyo6hkf FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_sequence_aud" ADD CONSTRAINT fkga3xsp0uwmig54b1ifhngy6xv FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_sequence_aud" ADD CONSTRAINT fkpkis3uvgbj57r1p3aajdak5x0 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_universe_aud" ADD CONSTRAINT fkqm107evjwvcripg8042onm7oa FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "control_construct_universe_aud" ADD CONSTRAINT fkrfhukowv0bsf5vcrbtthhq3m8 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instruction_aud" ADD CONSTRAINT fk3g3cgs4t4dg3h7o4p014y4sfy FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instruction_aud" ADD CONSTRAINT fkiyst9n991kukvsvso7bbs57cl FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_aud" ADD CONSTRAINT fklt62stdrgbvcgfy186ri2wj1m FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_aud" ADD CONSTRAINT fkk8ypvsxmsuuiqb2yqy5ym8df1 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_element_aud" ADD CONSTRAINT fkmk6xmsa0nq11q09f5r03g33rf FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_element_aud" ADD CONSTRAINT fkceifiy7kay0xtdkgtjq2fqvg2 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_element_parameter_aud" ADD CONSTRAINT fk31d0ua34kf09px04h3gqss1gf FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_element_parameter_aud" ADD CONSTRAINT fkjmdfhpx0nryclyejv49loulrl FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_instrument_element_aud" ADD CONSTRAINT fklkf11qhdhkvfxpq68x0up9219 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "instrument_instrument_element_aud" ADD CONSTRAINT fkpkjq2vk99pc34n43ho922gcqb FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "publication_aud" ADD CONSTRAINT fkjdi6h0anlhc7iahgb2ihv2i3o FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "publication_aud" ADD CONSTRAINT fk3iwwlm1ponnko57mu075vaw1s FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "publication_element_aud" ADD CONSTRAINT fkcq0u03q75hv9res5yvjqftnmc FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "publication_element_aud" ADD CONSTRAINT fklfgq2o2wp1584vtu5lex1i5hd FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "question_item_aud" ADD CONSTRAINT fk4dp5lspcxkcjdd5657khcumx5 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "question_item_aud" ADD CONSTRAINT fkf45cjexk38h55vkcs58wugv16 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "responsedomain_aud" ADD CONSTRAINT fkhr6bjpff2x5r5250d4pvxehb8 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "responsedomain_aud" ADD CONSTRAINT fkdikmfy7ubdxyw03pvthmjllbb FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "study_aud" ADD CONSTRAINT fk2kqsit0ra3kb9kkr62y36uona FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "study_aud" ADD CONSTRAINT fkobgc704fulq0wd3rodyg8jsph FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "study_authors_aud" ADD CONSTRAINT fknjalqdh2g5i4f6laxcjy6s94f FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "study_authors_aud" ADD CONSTRAINT fkspwpr5qbkj9y602wa63g8pe4k FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "survey_program_aud" ADD CONSTRAINT fkkc8fokpxgyhf2wmnq5xp3el3n FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "survey_program_aud" ADD CONSTRAINT fkbm458iyg6qivei77upso464uu FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "survey_program_authors_aud" ADD CONSTRAINT fk72vf17jpm0q7rqtvchbijtp9u FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "survey_program_authors_aud" ADD CONSTRAINT fkfn4ju0bg9soqotjjfr39oemfh FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_aud" ADD CONSTRAINT fkp45g7avoxlrk3e6grnpcspcbo FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_aud" ADD CONSTRAINT fkst9r31aco9sp0vce4ewogs93d FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_authors_aud" ADD CONSTRAINT fk19wbgig3id48lh9uhwyil4966 FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_authors_aud" ADD CONSTRAINT fk5hs5l21xt8s7eg642q0o703bn FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_other_material_aud" ADD CONSTRAINT fk3yf6ykpu1hgqr94b5pavf2k2s FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_other_material_aud" ADD CONSTRAINT fkb6dskttqj4fom1o5rtv6fjyq6 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_question_item_aud" ADD CONSTRAINT fk5quunfjay93scvxmy1h29mnnp FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "topic_group_question_item_aud" ADD CONSTRAINT fk4qwbqw5aj3o03hqpr73yuddnu FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "universe_aud" ADD CONSTRAINT fketb577r178fvlnk6kbj3gksdw FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "universe_aud" ADD CONSTRAINT fko2w6vr53iubl2tif2377325yw FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "user_account_aud" ADD CONSTRAINT fkhvfceciynu26iccnapnt5xb4y FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "user_account_aud" ADD CONSTRAINT fk4ckicbdylykcx6ap4a8ryk2fe FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "user_authority_aud" ADD CONSTRAINT fkmk5y54lh1wtlqr2tnxrxi7s1m FOREIGN KEY ("rev") REFERENCES "qddt-dev"."public"."revinfo" ("id");
ALTER TABLE "user_authority_aud" ADD CONSTRAINT fklyr6fn4ptx8218hdx2ay1lev0 FOREIGN KEY ("revend") REFERENCES "qddt-dev"."public"."revinfo" ("id");
