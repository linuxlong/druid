create table IF NOT EXISTS druid_domain (
	id SERIAL8 NOT NULL,
	domain varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE(domain)
);

insert into druid_domain (domain) values ('default');

create table IF NOT EXISTS druid_app (
	id SERIAL8  NOT NULL,
	domain varchar(45) NOT NULL,
	app varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app)		  
);

insert into druid_app (domain, app) values ('default', 'default');

create table IF NOT EXISTS druid_cluster (
	id SERIAL8  NOT NULL,
	domain varchar(45) NOT NULL,
	app varchar(45) NOT NULL,
	cluster varchar(45) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app, cluster)		  
);

insert into druid_cluster (domain, app, cluster) values ('default', 'default', 'default');

create table IF NOT EXISTS druid_inst (
	id SERIAL8  NOT NULL,
	app varchar(45) NOT NULL,
	domain varchar(45) NOT NULL,
	cluster varchar(45) NOT NULL,
	host varchar(128) NOT NULL,
	ip varchar(32) NOT NULL,
	lastActiveTime TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	lastPID bigint NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (domain, app, cluster, host)		  
);