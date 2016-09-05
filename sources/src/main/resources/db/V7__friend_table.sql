create table friend
(
    id bigint not null,
    user_username character varying(255),
    friend_username character varying(255),
    primary key (id),
    foreign key (user_username) references public.gamelabz_user (username),
    foreign key (friend_username) references public.gamelabz_user (username)
);

create index user_from_friend_index
on friend (user_username);

create index friend_from_friend_index
on friend (friend_username);