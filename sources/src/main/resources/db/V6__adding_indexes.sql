create index from_who_index
on friendRequest (fromWho_username);

create index to_whom_index
on friendRequest (toWhom_username);