CREATE DATABASE dc09hcdktafoks
  WITH OWNER = ahheansgceypsj
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
GRANT ALL ON DATABASE dc09hcdktafoks TO ahheansgceypsj;
REVOKE ALL ON DATABASE dc09hcdktafoks FROM public;


CREATE TABLE artikkel
(
  artikkel_id serial NOT NULL,
  pealkiri character varying(50),
  kasutaja_id integer,
  lyhisisu character varying(300),
  sisu character varying(3000),
  aeg timestamp without time zone,
  pilt character varying(100),
  CONSTRAINT artikkel_pkey PRIMARY KEY (artikkel_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE artikkel
  OWNER TO ahheansgceypsj;


CREATE TABLE kasutaja
(
  kasutaja_id serial NOT NULL,
  kasutajanimi character varying(30),
  email character varying(30),
  autor boolean,
  CONSTRAINT kasutaja_pkey PRIMARY KEY (kasutaja_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE kasutaja
  OWNER TO ahheansgceypsj;

  
CREATE TABLE kommentaar
(
  kommentaar_id serial NOT NULL,
  kasutaja_id integer,
  artikkel_id integer,
  aeg date,
  sisu character varying(200),
  CONSTRAINT kommentaar_pkey PRIMARY KEY (kommentaar_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE kommentaar
  OWNER TO ahheansgceypsj;
  

CREATE TABLE tag
(
  tag_id serial NOT NULL,
  nimi character varying(30),
  CONSTRAINT tag_pkey PRIMARY KEY (tag_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tag
  OWNER TO ahheansgceypsj;


CREATE TABLE artikkel_tag
(
  artikkel_id integer NOT NULL,
  tag_id integer NOT NULL,
  CONSTRAINT artikkel_id FOREIGN KEY (artikkel_id)
      REFERENCES artikkel (artikkel_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tag_id FOREIGN KEY (tag_id)
      REFERENCES tag (tag_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE artikkel_tag
  OWNER TO ahheansgceypsj;


CREATE OR REPLACE VIEW artikkel_create AS 
 SELECT artikkel.pealkiri,
    artikkel.sisu,
    artikkel.aeg,
    kasutaja.kasutajanimi,
    artikkel.lyhisisu
   FROM artikkel
   JOIN kasutaja ON artikkel.kasutaja_id = kasutaja.kasutaja_id
  ORDER BY artikkel.aeg DESC;

ALTER TABLE artikkel_create
  OWNER TO ahheansgceypsj;





