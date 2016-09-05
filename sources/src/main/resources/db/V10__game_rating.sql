create table gamerating
(
    id bigint not null,
    game_id bigint,
    rating numeric(5, 2),
    numberofratings bigint,
    primary key (id),
    foreign key (game_id) references public.game (id)
);

create table userratinglink
(
    id bigint not null,
    user_username character varying(255),
    game_id bigint,
    rating numeric(5, 2),
    primary key (id),
    foreign key (game_id) references public.game (id),
    foreign key (user_username) references public.gamelabz_user (username)
);

create index rating_game_index
on gamerating (game_id);

create index rating_link_game_index
on userratinglink (game_id);

create index rating_link_user_index
on userratinglink (user_username);