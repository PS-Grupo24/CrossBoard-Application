create table users (
                       id serial primary key,
                       token VARCHAR(255) unique not null,
                       username VARCHAR(255) unique not null,
                       email VARCHAR(255) unique not null,
                       password VARCHAR(255) not null
);

create table match (
                       id serial primary key,
                       board jsonb not null,
                       player1 int unique not null,
                       player2 int unique,
                       gametype VARCHAR(64) not null,
                       version int not null
);

insert into users (token, username, email, password) values ('1', 'Rúben Louro', 'A48926@alunos.isel.pt', 'Aa12345!');
insert into users (token, username, email, password) values ('2', 'Luís Reis', 'A48318@alunos.isel.pt', 'Aa12345!');
insert into users (token, username, email, password) values ('3', 'Pedro Pereira', 'palex@cc.isel.ipl.pt', 'Aa12345!');