Drop table if exists Customers;

Create table Customers (
  id varchar(36) not null,
  first_name varchar(25) not null,
  last_name varchar(25) not null,
  email varchar(255) not null Unique,
  password varchar(48) not null,
  state varchar(51) not null,
  city varchar(51) not null,
  street varchar(51) not null,
  home varchar(51) not null,
  creation_date timestamp not null,
  last_modified_date timestamp not null,
  Primary key (id)
);

Create Unique Index
  customer_email on Customers (email);