Drop table if exists Publishers;

Create table Publishers (
  id varchar(36) not null,
  publisher_name varchar(255) not null,
  state varchar(25) not null,
  city varchar(51) not null,
  street varchar(51) not null,
  home varchar(51) not null,
  phone varchar(51) not null Unique,
  email varchar(255) not null Unique,
  creation_date timestamp not null,
  last_modified_date timestamp not null,
  Primary Key (id)
);

Create Unique Index
  publisher_email_index On Publishers (email);

Create Unique Index
  publisher_phone_index On Publishers (phone);