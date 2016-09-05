CREATE SEQUENCE hibernate_sequence;

CREATE TABLE game
(
  id bigint NOT NULL,
  name character varying(255),
  description character varying(255),
  releasedate bigint,
  CONSTRAINT game_pkey PRIMARY KEY (id)
);

CREATE TABLE gamegenrelink
(
  id bigint NOT NULL,
  genre character varying(255),
  game_id bigint,
  CONSTRAINT gamegenrelink_pkey PRIMARY KEY (id),
  CONSTRAINT fk74fb082b8ae256dc FOREIGN KEY (game_id)
      REFERENCES public.game (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE gamelabz_user
(
  username character varying(255) NOT NULL,
  email character varying(255),
  password character varying(255),
  usertype character varying(255),
  CONSTRAINT gamelabz_user_pkey PRIMARY KEY (username)
);

CREATE TABLE gameoffer
(
  id bigint NOT NULL,
  pricecash bigint,
  pricepoints bigint,
  CONSTRAINT gameoffer_pkey PRIMARY KEY (id)
);

CREATE TABLE gameofferlink
(
  id bigint NOT NULL,
  game_id bigint,
  gameoffer_id bigint,
  CONSTRAINT gameofferlink_pkey PRIMARY KEY (id),
  CONSTRAINT fkc256048417a51438 FOREIGN KEY (gameoffer_id)
      REFERENCES public.gameoffer (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkc25604848ae256dc FOREIGN KEY (game_id)
      REFERENCES public.game (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);