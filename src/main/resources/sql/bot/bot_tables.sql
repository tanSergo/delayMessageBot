create table users (
	id int(11) not null,
    name varchar(50) not null,
    role_id int(11) not null
) engine=InnoDB default charset=utf8;

create table events (
	id int(11) not null,
    event_name varchar(100) not null,
    last_update datetime not null,
    awakening datetime not null,
    sender_id int(11) not null,
    receiver_id int(11) not null,
    message text(1000) not null
) engine=InnoDB default charset=utf8;

create table roles (
	id int(11) not null,
    name varchar(50) not null
) engine=InnoDB default charset=utf8;

create table event_scheduler(
  id int(11)  not null,
  event_name varchar(50) not null,
  scheduler_name varchar(50) not null,
  trigger_name varchar(50) not null,
  trigger_group varchar(50) not null
) engine=InnoDB default charset=utf8;