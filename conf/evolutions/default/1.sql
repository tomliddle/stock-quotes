# --- !Ups

create table "stocks" ("id" VARCHAR(254) NOT NULL PRIMARY KEY, "name" VARCHAR(254) NOT NULL);
create table "quotes" ("id" VARCHAR(254) NOT NULL PRIMARY KEY,"price" DECIMAL(10,3) NOT NULL);


# --- !Downs
;
drop table "stocks";
drop table "quotes"