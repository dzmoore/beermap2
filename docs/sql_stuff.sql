create table user2 (
	id int(21) not null auto_increment,
	username varchar(40) not null,
	password varchar(60) not null,
	active tinyint(1),
	last_mod timestamp default current_timestamp on update current_timestamp,
	salt varchar(40),
	primary key (id)
) engine=InnoDB;
	
insert into user2
	(id, username, password, active, last_mod, salt)
	select id, username, password, active, last_mod, salt from user
;

rename table user to user_old;

rename table user2 to user;

create table lv_user_roles (
        id int (21) not null auto_increment,
        name varchar(30),
		last_mod timestamp default current_timestamp on update current_timestamp,
        primary key (id)
) engine=InnoDB;

insert into lv_user_roles (name) values ('basic');
insert into lv_user_roles (name) values ('admin');

create table user_roles (
        id int(21) not null auto_increment,
        userFk int(21) not null,
        lv_user_roleFk int(21) not null,
		active tinyint(1) default 1,
        last_mod timestamp default current_timestamp on update current_timestamp,
        primary key (id),
        foreign key (userFk) references user(id),
		foreign key (lv_user_roleFk) references lv_user_roles(id)
) engine=InnoDB;

create table beers2 (
	id int(21) not null auto_increment,
	brewery_id int(21) not null default 0,
	name varchar(255) not null,
	cat_id int(11) not null default 0,
	style_id int(11) not null default 0,
	abv float not null default 0,
	ibu float not null default 0,
	srm float not null default 0,
	upc int(40) not null default 0,
	filepath varchar(255),
	descript text,
	last_mod timestamp default current_timestamp on update current_timestamp,
	add_user_fk int(21),
	primary key (id),
	foreign key (add_user_fk) references user(id)

) engine=InnoDB;

insert into beers2
	(id, brewery_id, name, cat_id, style_id, abv, ibu, srm, upc, filepath, descript, last_mod, add_user_fk)
	select id, brewery_id, name, cat_id, style_id, abv, ibu, srm, upc, filepath, descript, last_mod, add_user_fk
	from beers;
	
rename table beers to beers_old;
rename table beers2 to beers;

create table breweries2 (
	id int(21) not null auto_increment,
	name varchar(255) not null,
	address1 varchar(255),
	address2 varchar(255),
	city varchar(255),
	state varchar(255),
	code varchar(25),
	country varchar(255),
	phone varchar(50),
	website varchar(255),
	filepath varchar(255),
	descript text,
	add_user_fk int(21) default null,
	last_mod timestamp default current_timestamp on update current_timestamp,
	primary key (id),
	foreign key (add_user_fk) references user (id)
) engine=InnoDB;

insert into breweries2
	(id, name, address1, address2, city, state, code, country,
	phone, website, filepath, descript, last_mod)
	select id, name, address1, address2, city, state, code, country,
	phone, website, filepath, descript, last_mod from breweries;
	
rename table breweries to breweries_old;
rename table breweries2 to breweries;

create table beer_rating2 (
	id int(21) not null auto_increment,
	user_fk int(21) not null,
	beer_fk int(21) not null,
	comment text,
	last_mod timestamp default current_timestamp on update current_timestamp,
	rating_value int(21),
	primary key (id),
	foreign key (user_fk) references user (id),
	foreign key (beer_fk) references beers (id)
) engine=InnoDB;

insert into beer_rating2
	(id, user_fk, beer_fk, comment, last_mod, rating_value)
	select id, user_fk, beer_fk, comment, last_mod, rating_value
	from beer_rating where user_fk != -1
	
rename table beer_rating to beer_rating_old;
rename table beer_rating2 to beer_rating;

drop table upc;
drop table beer_rating_lv;

show table status where engine != 'InnoDB' and Name not like '%old'\G