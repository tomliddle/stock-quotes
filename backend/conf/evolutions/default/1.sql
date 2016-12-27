# --- !Ups

create table "stocks" ("id" VARCHAR(254) NOT NULL PRIMARY KEY, "name" VARCHAR(254) NOT NULL);
create table "quotes" ("ticker" VARCHAR(254) NOT NULL,"price" DECIMAL(10,3) NOT NULL, "datetime" TIMESTAMP NOT NULL);


# --- !Downs

drop table "stocks";
drop table "quotes";