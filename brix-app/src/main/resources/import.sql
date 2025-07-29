--
-- PostgreSQL database dump
--

-- Dumped from database version 16.8
-- Dumped by pg_dump version 16.9 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: actioncommandconfig; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: actioncommandconfig_commandtraits; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: endpoint; Type: TABLE DATA; Schema: public; Owner: brixadmin
--

INSERT INTO public.endpoint (createtime, id, updatetime, endpointclass_type, address, description, name, systemtype) VALUES ('2025-06-30 19:28:15.20831+00', 1, '2025-06-30 19:28:15.208521+00', 'SEEndpoint', 'http://localhost:8091/', 'Simple1 Server', 'Simple1', 'SIMPLEEP');
INSERT INTO public.endpoint (createtime, id, updatetime, endpointclass_type, address, description, name, systemtype) VALUES ('2025-06-30 19:28:26.666352+00', 2, '2025-06-30 19:28:26.666417+00', 'SEEndpoint', 'http://localhost:8092/', 'Simple2 Server', 'Simple2', 'SIMPLEEP');


--
-- Data for Name: artifact; Type: TABLE DATA; Schema: public; Owner: brixadmin
--

INSERT INTO public.artifact (createtime, endpoint_id, id, updatetime, artifactclass_type, artifactkeystring, artifacttype) VALUES ('2025-06-30 19:28:40.495859+00', 1, 1, '2025-06-30 19:28:40.496457+00', 'Artifact', '1', 'SEISSUE');
INSERT INTO public.artifact (createtime, endpoint_id, id, updatetime, artifactclass_type, artifactkeystring, artifacttype) VALUES ('2025-06-30 19:28:44.475292+00', 2, 2, '2025-06-30 19:28:44.475773+00', 'Artifact', '1', 'SEISSUE');


--
-- Data for Name: artifactmap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: genattrmap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: artifactmap_genattrmap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: artifactrelationship; Type: TABLE DATA; Schema: public; Owner: brixadmin
--

INSERT INTO public.artifactrelationship (createtime, id, lasttransactiontime, sourceartifact_id, targetartifact_id, updatetime, description, name, state, status) VALUES ('2025-06-30 19:28:58.972409+00', 2, '2025-06-30 19:28:58.972417+00', 1, 2, '2025-06-30 19:28:58.973343+00', '', '', 'INITIALIZED', 'INITIALIZED');


--
-- Data for Name: attributeproperties; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: attributeproperties_attributetraits; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: broute; Type: TABLE DATA; Schema: public; Owner: brixadmin
--

INSERT INTO public.broute (createtime, id, sourceendpoint_id, targetendpoint_id, updatetime) VALUES ('2025-06-30 19:28:33.859011+00', 1, 1, 2, '2025-06-30 19:28:33.861957+00');


--
-- Data for Name: broute_artifactmap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: genattrmap_attributetraits; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: genvaluemap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: genattrmap_genvaluemap; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: rule; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: ruleactionconfig; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: rule_ruleactionconfig; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: rulecondition; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: rule_rulecondition; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Data for Name: transaction; Type: TABLE DATA; Schema: public; Owner: brixadmin
--



--
-- Name: abstractentity_seq; Type: SEQUENCE SET; Schema: public; Owner: brixadmin
--

SELECT pg_catalog.setval('public.abstractentity_seq', 1, true);


--
-- Name: artifact_seq; Type: SEQUENCE SET; Schema: public; Owner: brixadmin
--

SELECT pg_catalog.setval('public.artifact_seq', 1, true);


--
-- Name: endpoint_seq; Type: SEQUENCE SET; Schema: public; Owner: brixadmin
--

SELECT pg_catalog.setval('public.endpoint_seq', 1, true);


--
-- PostgreSQL database dump complete
--

