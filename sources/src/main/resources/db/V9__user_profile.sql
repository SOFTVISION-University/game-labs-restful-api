create table userProfile
(
    id bigint not null,
    user_username character varying(255),
    points bigint,
    sharedlinkid character varying(255),
    sharedlink character varying(255),
    primary key (id),
    foreign key (user_username) references public.gamelabz_user (username)
);

create index profile_user_index
on userProfile (user_username);