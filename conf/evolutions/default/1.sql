# --- !Ups

create table "stocks" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"desc" VARCHAR(254) NOT NULL);
create table "quotes" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"price" DECIMAL(10,3) NOT NULL);

# --- !Downs
;
drop table "stocks";
drop table "quotes"