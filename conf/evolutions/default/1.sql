# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "tasks" ("id" SERIAL NOT NULL PRIMARY KEY,"description" VARCHAR(254) NOT NULL,"completed" BOOLEAN NOT NULL,"created" TIMESTAMP NOT NULL,"priority" INTEGER NOT NULL);

# --- !Downs

drop table "tasks";

