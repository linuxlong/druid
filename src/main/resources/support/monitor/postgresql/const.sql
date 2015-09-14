CREATE TABLE IF NOT EXISTS druid_const (
	id SERIAL8 NOT NULL,
	domain varchar(45) NOT NULL, 
	app varchar(45) NOT NULL, 
	type varchar(45) NOT NULL,
	hash bigint NOT NULL,
	value text,
	PRIMARY KEY (id),
	UNIQUE (domain, app, type, hash)
);