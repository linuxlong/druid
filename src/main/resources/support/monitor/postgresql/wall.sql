CREATE TABLE IF NOT EXISTS druid_wall(
	id SERIAL8 NOT NULL , 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid integer  NOT NULL,
	collectTime TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	name varchar(256), 
	checkCount bigint,
	hardCheckCount bigint,
	violationCount bigint,
	whiteListHitCount bigint,
	blackListHitCount bigint,
	syntaxErrorCount bigint,
	violationEffectRowCount bigint,
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_index ON druid_wall (collectTime, domain, app);

CREATE TABLE IF NOT EXISTS druid_wall_sql(
	id SERIAL8 NOT NULL , 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), pid integer  NOT NULL,
	collectTime TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	sqlHash bigint,
	sqlSHash bigint,
	sqlSampleHash bigint,
	executeCount bigint,
	fetchRowCount bigint,
	updateCount bigint,
	syntaxError INTEGER,
	violationMessage varchar(256), 
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_sql_index ON druid_wall_sql (collectTime, domain, app);

CREATE TABLE IF NOT EXISTS druid_wall_table(
	id SERIAL8 NOT NULL , 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid integer  NOT NULL,
	collectTime TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	name varchar(256), 
	selectCount bigint,
	selectIntoCount bigint,
	insertCount bigint,
	updateCount bigint,
	deleteCount bigint,
	truncateCount bigint,
	createCount bigint,
	alterCount bigint,
	dropCount bigint,
	replaceCount bigint,
	deleteDataCount bigint,
	updateDataCount bigint,
	insertDataCount bigint,
	fetchRowCount bigint,
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_table_index ON druid_wall_table (collectTime, domain, app);

CREATE TABLE IF NOT EXISTS druid_wall_function(
	id SERIAL8 NOT NULL , 
	domain varchar(45)  NOT NULL, 
	app varchar(45)  NOT NULL, 
	cluster varchar(45)  NOT NULL, 
	host varchar(128), 
	pid integer  NOT NULL,
	collectTime TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	name varchar(256), 
	invokeCount bigint,
	PRIMARY KEY(id)
);
CREATE INDEX druid_wall_function_index ON druid_wall_function (collectTime, domain, app);