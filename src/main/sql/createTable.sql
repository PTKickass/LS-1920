begin;
	CREATE TABLE USERS (
		uid					serial PRIMARY key CHECK (uid >= 0),
		name				varchar(50) NOT NULL,
		email		        varchar(50) NOT NULL,
		UNIQUE(email)
	);

	CREATE TABLE ROOM (
		rid					serial PRIMARY key CHECK (rid >= 0),
		name				varchar(30) NOT NULL,
		description         varchar(50),
		location	 		varchar(50) not NULL,
		capacity		  	int CHECK (capacity > 0),
		UNIQUE (name)
	);
	
	CREATE TABLE LABEL (
		lid					serial PRIMARY key CHECK (lid >= 0),
		name				varchar(30) unique NOT NULL
	);
	
	CREATE TABLE ROOMLABEL (
		lid					int REFERENCES LABEL(lid),
		rid				 	int REFERENCES ROOM(rid),
		PRIMARY KEY  (lid, rid)
	);

	CREATE TABLE BOOKING (
		bid					serial PRIMARY key CHECK (bid >= 0),
		uid 				int NOT NULL REFERENCES USERS(uid),
		rid					int not null references ROOM(rid),
		begin_inst			timestamp NOT null CONSTRAINT BEGIN_INST_MULTIPLE_OF_10 CHECK (extract(minute from begin_inst) :: int % 10 = 0),
		end_inst			timestamp NOT null CONSTRAINT END_INST_MULTIPLE_OF_10 CHECK (extract(minute from end_inst) :: int % 10 = 0),
		CONSTRAINT BEGIN_INST_LOWER_THAN_END_INST CHECK (begin_inst < end_inst),
		CONSTRAINT DURATION_LESS_THAN_10 CHECK ((extract(epoch from end_inst::timestamp-begin_inst::timestamp) / 60) >= 10)
	);

commit;
end;
