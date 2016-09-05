create table friendRequest
(
    id bigint not null,
    fromWho_username character varying(255),
    toWhom_username character varying(255),
    primary key (id),
    foreign key (fromWho_username) references public.gamelabz_user (username),
    foreign key (toWhom_username) references public.gamelabz_user (username)
);