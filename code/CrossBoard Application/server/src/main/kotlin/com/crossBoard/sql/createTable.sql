create table users (
                       id serial primary key,
                       token VARCHAR(255) unique not null,
                       username VARCHAR(255) unique not null,
                       email VARCHAR(255) unique not null,
                       password VARCHAR(255) not null,
                       state VARCHAR(10) not null
);

create table match (
                        id serial primary key,
                        board jsonb not null,
                        player1 int not null,
                        player2 int,
                        match_type VARCHAR(64) not null,
                        version int not null,
                        state varchar(20) not null,
                        winner int,
    FOREIGN KEY (player1) References users(id),
    FOREIGN KEY (player2) References users(id)
);