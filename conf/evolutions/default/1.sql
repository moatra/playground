# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"first_name" VARCHAR(254) NOT NULL,"last_name" VARCHAR(254) NOT NULL,"email" VARCHAR(254) NOT NULL,"password" VARCHAR(254) NOT NULL,"confirm" VARCHAR(254),"confirmed" BOOLEAN NOT NULL,"registered" TIMESTAMP NOT NULL,"last_login" TIMESTAMP,"admin" BOOLEAN NOT NULL);
create unique index "users_email_index" on "users" ("email");

# --- !Downs

drop table "users";

