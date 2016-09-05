create table gameOrder
(
    id bigint not null,
    user_username character varying(255),
    dateOfOrder bigint,
    orderType character varying(30),
    cashValue numeric(10,2),
    recieverUser_username character varying(255),
    giftValue numeric(10,2),
    keyValue character varying(255),
    pointsValue bigint,
    primary key (id),
    foreign key (user_username) references public.gamelabz_user (username),
    foreign key (recieverUser_username) references public.gamelabz_user (username)
);

create table ownedGame
(
    id bigint not null,
    user_username character varying(255),
    game_id bigint,
    order_id bigint,
    primary key(id),
    foreign key (user_username) references public.gamelabz_user (username),
    foreign key (game_id) references public.game (id),
    foreign key (order_id) references public.gameOrder (id)
);

create table generatedKey
(
    id bigint not null,
    generatedKey character varying(255),
    user_username character varying(255),
    game_id bigint,
    order_id bigint,
    logicalDelete boolean,
    primary key(id),
    foreign key (user_username) references public.gamelabz_user (username),
    foreign key (game_id) references public.game (id),
    foreign key (order_id) references public.gameOrder (id)
);

create index order_user_index
on gameOrder (user_username);

create index order_recUser_index
on gameOrder (recieverUser_username);

create index owned_user_index
on ownedGame (user_username);

create index owned_game_index
on ownedGame (game_id);

create index owned_game_order_index
on ownedGame (order_id);

create index key_user_index
on generatedKey (user_username);

create index key_game_index
on generatedKey (game_id);

create index key_order_index
on generatedKey (order_id);