CREATE SCHEMA IF NOT EXISTS audit AUTHORIZATION qddt;

DROP TABLE IF EXISTS public.project_archived;
DROP VIEW IF  EXISTS public.project_archived;

DROP TABLE IF EXISTS public.project_archived_hierarchy;
DROP VIEW IF  EXISTS public.project_archived_hierarchy;

DROP TABLE IF EXISTS public.uar;
DROP VIEW IF  EXISTS public.uar;

DROP TABLE IF EXISTS public.uuidpath;
DROP VIEW IF  EXISTS public.uuidpath;

DROP TABLE IF EXISTS public.allrev;
DROP VIEW IF  EXISTS public.allrev;

CREATE VIEW public.allrev (id, rev, revtype, revend, tablename) AS
  SELECT author_aud.id,
    author_aud.rev,
    author_aud.revtype,
    author_aud.revend,
    'author_aud'::text AS tablename
  FROM audit.author_aud
  UNION
  SELECT category_aud.id,
    category_aud.rev,
    category_aud.revtype,
    category_aud.revend,
    'category_aud'::text AS tablename
  FROM audit.category_aud
  UNION
  SELECT category_children_aud.category_id AS id,
    category_children_aud.rev,
    category_children_aud.revtype,
    category_children_aud.revend,
         'category_children_aud'::text AS tablename
  FROM audit.category_children_aud
  UNION
  SELECT code_aud.responsedomain_id AS id,
    code_aud.rev,
    code_aud.revtype,
    code_aud.revend,
         'code_aud'::text AS tablename
  FROM audit.code_aud
  UNION
  SELECT concept_hierarchy_aud.id,
         concept_hierarchy_aud.rev,
         concept_hierarchy_aud.revtype,
         concept_hierarchy_aud.revend,
        'concept_hierarchy_aud'::text AS tablename
  FROM audit.concept_hierarchy_aud
  UNION
  SELECT concept_hierarchy_question_item_aud.element_id,
         concept_hierarchy_question_item_aud.rev,
         concept_hierarchy_question_item_aud.revtype,
         concept_hierarchy_question_item_aud.revend,
    'concept_question_item_aud'::text AS tablename
  FROM audit.concept_hierarchy_question_item_aud
  UNION
  SELECT control_construct_aud.id,
    control_construct_aud.rev,
    control_construct_aud.revtype,
    control_construct_aud.revend,
    'control_construct_aud'::text AS tablename
  FROM audit.control_construct_aud
  UNION
  SELECT control_construct_instruction_aud.instruction_id AS id,
    control_construct_instruction_aud.rev,
    control_construct_instruction_aud.revtype,
    control_construct_instruction_aud.revend,
         'control_construct_instruction_aud'::text AS tablename
  FROM audit.control_construct_instruction_aud
  UNION
  SELECT control_construct_universe_aud.universe_id AS id,
    control_construct_universe_aud.rev,
    control_construct_universe_aud.revtype,
    control_construct_universe_aud.revend,
         'control_construct_universe_aud'::text      AS tablename
  FROM audit.control_construct_universe_aud
  UNION
  SELECT instruction_aud.id,
    instruction_aud.rev,
    instruction_aud.revtype,
    instruction_aud.revend,
    'instruction_aud'::text AS tablename
  FROM audit.instruction_aud
  UNION
  SELECT instrument_aud.id,
    instrument_aud.rev,
    instrument_aud.revtype,
    instrument_aud.revend,
    'instrument_aud'::text AS tablename
  FROM audit.instrument_aud
  UNION
  SELECT instrument_node_aud.element_id AS id,
    instrument_node_aud.rev,
    instrument_node_aud.revtype,
    instrument_node_aud.revend,
         'instrument_control_construct_aud'::text AS tablename
  FROM audit.instrument_node_aud
  UNION
  SELECT control_construct_other_material_aud.owner_id as id,
         control_construct_other_material_aud.rev,
         control_construct_other_material_aud.revtype,
         control_construct_other_material_aud.revend,
         'control_construct_other_material_aud'::text AS tablename
  FROM audit.control_construct_other_material_aud
  UNION
  SELECT publication_aud.id,
    publication_aud.rev,
    publication_aud.revtype,
    publication_aud.revend,
    'publication_aud'::text AS tablename
  FROM audit.publication_aud
  UNION
  SELECT publication_element_aud.element_id,
    publication_element_aud.rev,
    publication_element_aud.revtype,
    publication_element_aud.revend,
    'publication_element_aud'::text AS tablename
  FROM audit.publication_element_aud
  UNION
  SELECT question_item_aud.id,
    question_item_aud.rev,
    question_item_aud.revtype,
    question_item_aud.revend,
    'question_item_aud'::text AS tablename
  FROM audit.question_item_aud
  UNION
  SELECT responsedomain_aud.id,
    responsedomain_aud.rev,
    responsedomain_aud.revtype,
    responsedomain_aud.revend,
    'responsedomain_aud'::text AS tablename
  FROM audit.responsedomain_aud
  
  UNION
  SELECT universe_aud.id,
    universe_aud.rev,
    universe_aud.revtype,
    universe_aud.revend,
    'universe_aud'::text AS tablename
  FROM audit.universe_aud;

CREATE VIEW public.project_archived (id, path, name, is_archived, parent_id) AS
  SELECT id,
         '/concept'::text AS path,
         name,
         is_archived,
         parent_id
  FROM public.concept_hierarchy;


CREATE VIEW public.project_archived_hierarchy (id, path, name, is_archived, ancestors) AS
  WITH RECURSIVE tree AS (
    SELECT project_archived.id,
           project_archived.path,
           project_archived.name,
           project_archived.is_archived,
           ARRAY[]::uuid[] AS ancestors
    FROM project_archived
    WHERE (project_archived.parent_id IS NULL)
    UNION ALL
    SELECT project_archived.id,
           project_archived.path AS kind,
           project_archived.name,
           project_archived.is_archived,
           (tree_1.ancestors || project_archived.parent_id)
    FROM project_archived,
         tree tree_1
    WHERE (project_archived.parent_id = tree_1.id)
  )
  SELECT tree.id,
         tree.path,
         tree.name,
         tree.is_archived,
         tree.ancestors
  FROM tree;

CREATE VIEW public.uar (id, email, username, name, authority) AS
  SELECT ua.id,
        ua.email,
        ua.username,
        a.name,
        a.authority
  FROM ((user_account ua
     LEFT JOIN public.user_account_authorities au ON ((au.user_id = ua.id)))
     LEFT JOIN public.authority a ON ((au.authorities_id = a.id)))
  WHERE ua.is_enabled;

CREATE VIEW public.uuidpath (id, path, name, modified_by_id) AS
  SELECT c.id,
    '/categories'::text AS path,
    c.name,
    c.modified_by_id
  FROM public.category c
  WHERE ((c.category_kind)::text = 'CATEGORY'::text)
  UNION
  SELECT c.id,
    '/missing'::text AS path,
    c.name,
    c.modified_by_id
  FROM public.category c
  WHERE ((c.category_kind)::text = 'MISSING_GROUP'::text)
  UNION
  SELECT cc.id,
    '/questions'::text AS path,
    cc.name,
    cc.modified_by_id
  FROM public.control_construct cc
  WHERE ((cc.control_construct_kind)::text = 'QUESTION_CONSTRUCT'::text)
  UNION
  SELECT cc.id,
    '/sequences'::text AS path,
    cc.name,
    cc.modified_by_id
  FROM public.control_construct cc
  WHERE ((cc.control_construct_kind)::text = 'SEQUENCE_CONSTRUCT'::text)
  UNION
  SELECT instrument.id,
    '/instruments'::text AS path,
    instrument.name,
    instrument.modified_by_id
  FROM public.instrument
  UNION
  SELECT publication.id,
    '/publications'::text AS path,
    publication.name,
    publication.modified_by_id
  FROM public.publication
  UNION
  SELECT question_item.id,
    '/questionitems'::text AS path,
    question_item.name,
    question_item.modified_by_id
  FROM public.question_item
  UNION
  SELECT responsedomain.id,
    '/responsedomains'::text AS path,
    responsedomain.name,
    responsedomain.modified_by_id
  FROM public.responsedomain
  UNION
  SELECT concept_hierarchy.id,
         CASE WHEN class_kind='CONCEPT' THEN '/concept'
              WHEN class_kind='TOPIC_GROUP' THEN '/module'
              WHEN class_kind='STUDY' THEN '/study'
              WHEN class_kind='SURVEY_PROGRAM' THEN '/survey'
             END
    ::text AS path,
         concept_hierarchy.name,
         concept_hierarchy.modified_by_id
  FROM public.concept_hierarchy;

DROP TABLE IF EXISTS public.change_log;
DROP VIEW IF  EXISTS public.change_log;

create view change_log
            (ref_id, ref_rev, ref_kind, ref_change_kind, ref_modified, ref_modified_by, ref_action, element_id,
             element_revision, element_kind, name)
as
SELECT ca.id                                           AS ref_id,
       ca.rev                                          AS ref_rev,
       'CONCEPT_HIERARCHY'::text                       AS ref_kind,
       ca.change_kind                                  AS ref_change_kind,
       ca.modified                                     AS ref_modified,
       ca.modified_by_id                               AS ref_modified_by,
       ca.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(ca.name, 'Deleted'::character varying) AS name
FROM audit.concept_hierarchy_aud ca
UNION
SELECT cc.id                                           AS ref_id,
       cc.rev                                          AS ref_rev,
       'CONTROL_CONSTRUCT'::text                       AS ref_kind,
       cc.change_kind                                  AS ref_change_kind,
       cc.modified                                     AS ref_modified,
       cc.modified_by_id                               AS ref_modified_by,
       cc.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(cc.name, 'Deleted'::character varying) AS name
FROM audit.control_construct_aud cc
UNION
SELECT ai.id                                           AS ref_id,
       ai.rev                                          AS ref_rev,
       'INSTRUMENT'::text                              AS ref_kind,
       ai.change_kind                                  AS ref_change_kind,
       ai.modified                                     AS ref_modified,
       ai.modified_by_id                               AS ref_modified_by,
       ai.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(ai.name, 'Deleted'::character varying) AS name
FROM audit.instrument_aud ai
UNION
SELECT qi.id                                           AS ref_id,
       qi.rev                                          AS ref_rev,
       'QUESTION_ITEM'::text                           AS ref_kind,
       qi.change_kind                                  AS ref_change_kind,
       qi.modified                                     AS ref_modified,
       qi.modified_by_id                               AS ref_modified_by,
       qi.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(qi.name, 'Deleted'::character varying) AS name
FROM audit.question_item_aud qi
UNION
SELECT rd.id                                           AS ref_id,
       rd.rev                                          AS ref_rev,
       'RESPONSEDOMAIN'::text                          AS ref_kind,
       rd.change_kind                                  AS ref_change_kind,
       rd.modified                                     AS ref_modified,
       rd.modified_by_id                               AS ref_modified_by,
       rd.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(rd.name, 'Deleted'::character varying) AS name
FROM audit.responsedomain_aud rd
UNION
SELECT ap.id                                           AS ref_id,
       ap.rev                                          AS ref_rev,
       'PUBLICATION'::text                             AS ref_kind,
       ap.change_kind                                  AS ref_change_kind,
       ap.modified                                     AS ref_modified,
       ap.modified_by_id                               AS ref_modified_by,
       ap.revtype                                      AS ref_action,
       NULL::uuid                                      AS element_id,
       NULL::integer                                   AS element_revision,
       NULL::character varying                         AS element_kind,
       COALESCE(ap.name, 'Deleted'::character varying) AS name
FROM audit.publication_aud ap
UNION
SELECT cqi.parent_id                                            AS ref_id,
       cqi.rev                                                  AS ref_rev,
       'CONCEPT_HIERARCHY'::text                                AS ref_kind,
       'UPDATED_HIERARCHY_RELATION'::text                       AS ref_change_kind,
       r2.modified                                              AS ref_modified,
       r2.modified_by_id                                        AS ref_modified_by,
       cqi.revtype                                              AS ref_action,
       cqi.element_id,
       cqi.element_revision,
       cqi.element_kind,
       COALESCE(cqi.element_name, 'Deleted'::character varying) AS name
FROM audit.concept_hierarchy_question_item_aud cqi
         LEFT JOIN revinfo r2 ON cqi.rev = r2.id
UNION
SELECT ccom.owner_id             AS ref_id,
       ccom.rev                  AS ref_rev,
       'CONTROL_CONSTRUCT'::text AS ref_kind,
       NULL::character varying   AS ref_change_kind,
       r.modified                AS ref_modified,
       r.modified_by_id          AS ref_modified_by,
       ccom.revtype              AS ref_action,
       NULL::uuid                AS element_id,
       NULL::integer             AS element_revision,
       'OTHER_MATERIAL'::text    AS element_kind,
       ccom.original_name        AS name
FROM audit.control_construct_other_material_aud ccom
         LEFT JOIN revinfo r ON ccom.rev = r.id
UNION
SELECT tgom.owner_id             AS ref_id,
       tgom.rev                  AS ref_rev,
       'CONCEPT_HIERARCHY'::text AS ref_kind,
       NULL::character varying   AS ref_change_kind,
       r3.modified               AS ref_modified,
       r3.modified_by_id         AS ref_modified_by,
       tgom.revtype              AS ref_action,
       NULL::uuid                AS element_id,
       NULL::integer             AS element_revision,
       'OTHER_MATERIAL'::text    AS element_kind,
       tgom.original_name        AS name
FROM audit.concept_hierarchy_other_material_aud tgom
         LEFT JOIN revinfo r3 ON tgom.rev = r3.id;


CREATE FUNCTION searchStr(text default '%') RETURNS text AS $$
SELECT REPLACE($1,'*','%')
$$ LANGUAGE SQL;

--Add primary agency
INSERT INTO public.agency (id, modified, name, xml_lang ) VALUES('1359ded1-9f18-11e5-8994-feff819cdc9f','2018-01-01', 'Admin-qddt','en-GB');
INSERT INTO public.agency (id, modified, name, xml_lang) VALUES('1359ded2-9f18-11e5-8994-feff819cdc9f','2018-01-01', 'int.esseric','en-GB');
INSERT INTO public.agency (id, modified, name, xml_lang) VALUES('1359ded3-9f18-11e5-8994-feff819cdc9f','2018-01-01', 'Guest','en-GB');

--Add two demo accounts
--admin:password & user:password (bcrypt(10) passwords)
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c311-4ff9-11e5-885d-feff819cdc9f', 'admin', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'admin@example.org',  '1359ded1-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c312-4ff9-11e5-885d-feff819cdc9f', 'stig', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'stig.norland@nsd.no', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c313-4ff9-11e5-885d-feff819cdc9f', 'editor', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'editor@example.org', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c314-4ff9-11e5-885d-feff819cdc9f', 'Leah', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'leah.watson@city.ac.uk', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c315-4ff9-11e5-885d-feff819cdc9f', 'Sarah', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'sarah.butt.1@city.ac.uk', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c316-4ff9-11e5-885d-feff819cdc9f', 'Hilde', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'hilde.orten@nsd.no', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c317-4ff9-11e5-885d-feff819cdc9f', 'Maria', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'maria.idemchenko@gmail.com', '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c318-4ff9-11e5-885d-feff819cdc9f', 'user', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'user@example.org',    '1359ded2-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c319-4ff9-11e5-885d-feff819cdc9f', 'review', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'review@example.org','1359ded3-9f18-11e5-8994-feff819cdc9f',true,now());
INSERT INTO public.user_account(id, username, password, email, agency_id,is_enabled, modified) VALUES('83d4c31a-4ff9-11e5-885d-feff819cdc9f', 'guest', '$2a$10$O1MMi3SLcvwtJIT9CSZyN.aLtFKN.K2LtKyHZ52wElo0zh5gI1EyW', 'guestw@example.org', '1359ded3-9f18-11e5-8994-feff819cdc9f',true,now());




--Create ADMIN and USER authorities
INSERT INTO public.authority (id, name, authority) VALUES('9bec2d6a-4ff9-11e5-885d-feff819cdc9f', 'ToolAdmin'       , 'ROLE_ADMIN'  );
INSERT INTO public.authority (id, name, authority) VALUES('9bec2d6b-4ff9-11e5-885d-feff819cdc9f', 'Editor'          , 'ROLE_EDITOR' );
INSERT INTO public.authority (id, name, authority) VALUES('9bec2d6c-4ff9-11e5-885d-feff819cdc9f', 'ConceptualEditor', 'ROLE_CONCEPT');
INSERT INTO public.authority (id, name, authority) VALUES('9bec2d6d-4ff9-11e5-885d-feff819cdc9f', 'Viewer'          , 'ROLE_VIEW'   );
INSERT INTO public.authority (id, name, authority) VALUES('9bec2d6e-4ff9-11e5-885d-feff819cdc9f', 'Guest'           , 'ROLE_GUEST'  );


--Set up admin authorities
--ToolAdmin
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c311-4ff9-11e5-885d-feff819cdc9f', '9bec2d6a-4ff9-11e5-885d-feff819cdc9f');
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c312-4ff9-11e5-885d-feff819cdc9f', '9bec2d6a-4ff9-11e5-885d-feff819cdc9f');
--Editor
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c313-4ff9-11e5-885d-feff819cdc9f', '9bec2d6b-4ff9-11e5-885d-feff819cdc9f');
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c314-4ff9-11e5-885d-feff819cdc9f', '9bec2d6b-4ff9-11e5-885d-feff819cdc9f');
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c315-4ff9-11e5-885d-feff819cdc9f', '9bec2d6b-4ff9-11e5-885d-feff819cdc9f');
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c316-4ff9-11e5-885d-feff819cdc9f', '9bec2d6b-4ff9-11e5-885d-feff819cdc9f');
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c317-4ff9-11e5-885d-feff819cdc9f', '9bec2d6a-4ff9-11e5-885d-feff819cdc9f');
--Conceptua
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c318-4ff9-11e5-885d-feff819cdc9f', '9bec2d6c-4ff9-11e5-885d-feff819cdc9f');
--Viewer
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c319-4ff9-11e5-885d-feff819cdc9f', '9bec2d6d-4ff9-11e5-885d-feff819cdc9f');
--Guest
INSERT INTO public.user_account_authorities (user_id, authorities_id) VALUES('83d4c31a-4ff9-11e5-885d-feff819cdc9f', '9bec2d6e-4ff9-11e5-885d-feff819cdc9f');

insert into public.revinfo (id, timestamp, modified, modified_by_id) values(1,1498471892683,now(),'83d4c311-4ff9-11e5-885d-feff819cdc9f');


--Add two deaudit.mo accounts
--admin:passaudit.word & user:password (bcrypt(10) passwords)

--- merge scripts...
drop sequence hibernate_sequence;
CREATE SEQUENCE hibernate_sequence INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 5 NO CYCLE;


INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (0, null, 0, 'NOT_PUBLISHED', 'No publication', null);
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (1, null, 1, 'INTERNAL_PUBLICATION', 'Internal publication', null);
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (2, null, 2, 'EXTERNAL_PUBLICATION', 'External publication', null);
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (3, 1, 0, 'INTERNAL_PUBLICATION', 'Designmeeting1', 'Elements shared after first meeting to discuss questionnaire.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (5, 1, 2, 'INTERNAL_PUBLICATION', 'Designmeeting3', 'Elements shared  after third meeting to discuss questionnaire.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (4, 1, 1, 'INTERNAL_PUBLICATION', 'Designmeeting2', 'Elements shared after second meeting to discuss questionnaire.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (11, 1, 8, 'INTERNAL_PUBLICATION', 'No Milestone', 'Use for publication of elements between key milestones.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (10, 1, 7, 'INTERNAL_PUBLICATION', 'FinalSource – SQP/TMT', 'Elements agreed as going into the final source questionnaire.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (9, 1, 6, 'INTERNAL_PUBLICATION', 'PostPilot', 'Elements reviewed on basis of the results from the pilot.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (8, 1, 5, 'INTERNAL_PUBLICATION', 'Pilot – SQP/TMT', 'Elements agreed for pilot, export to SQP and translation');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (7, 1, 4, 'INTERNAL_PUBLICATION', 'PostEarlyTesting', 'Elements reviewed on basis of the results from the early testing.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (6, 1, 3, 'INTERNAL_PUBLICATION', 'Earlytesting - SQP/TMT', 'Elements agreed for early pre-testing, export to SQP and translation.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (12, 2, 0, 'EXTERNAL_PUBLICATION', 'Export to Public History', 'In addition to the final elements, the development history will be made available to the public.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (14, 2, 2, 'EXTERNAL_PUBLICATION', 'Export to QVD', 'Once finalized, elements will be exported to the QVDB to be made publically available');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (13, 2, 1, 'EXTERNAL_PUBLICATION', 'Export to Public', 'Elements agreed as going into the final source questionnaire.');
INSERT INTO publication_status (id, parent_id, parent_idx, published, label, description) VALUES (15, 0, 0, 'NOT_PUBLISHED', 'Not Published', 'Elements and discussion made available for key members of a questionnaire design sub group, but not designed to be published internally ');
