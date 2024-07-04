drop table if exists Publishers;
create table Publishers (
                            id varchar(36) not null,
                            publisher_name varchar(255) not null,
                            state varchar(51) not null,
                            city varchar(51) not null,
                            street varchar(51) not null,
                            home varchar(51) not null,
                            phone varchar(25) not null,
                            email varchar(255) not null,
                            creation_date timestamp not null,
                            last_modified_date timestamp not null,
--                             unique (phone),
--                             unique (email),
                            primary key (id)
);
create unique index publisher_email_index on Publishers(email);
create unique index publisher_phone_index on Publishers(phone);

drop table if exists Authors;
create table Authors (
                         id varchar(36) not null,
                         first_name varchar(25) not null,
                         last_name varchar(25) not null,
                         email varchar(255) not null,
                         state varchar(51) not null,
                         city varchar(51) not null,
                         street varchar(51) not null,
                         home varchar(51) not null,
                         creation_date timestamp not null,
                         last_modified_date timestamp not null,
--                          unique (email),
                         primary key (id)
);
create unique index author_email_index on Authors(email);

drop table if exists Books;
drop table if exists Book_Author;
create table Books (
                       id varchar(36) not null,
                       publisher_id varchar(36) not null,
                       title varchar(55) not null,
                       description varchar(255) not null,
                       isbn varchar(255) not null,
                       price decimal(38, 2) not null,
                       quantity_on_hand int not null,
                       category varchar(255) not null,
                       creation_date timestamp not null,
                       last_modified_date timestamp not null,
--                        unique (isbn),
                       primary key (id),
                       constraint book_publisher_fk foreign key (publisher_id) references Publishers (id)
);
create unique index isbn_index on Books(isbn);
create table Book_Author (
                             book_id varchar(36) not null,
                             author_id varchar(36) not null,
                             primary key (book_id, author_id),
                             constraint book_author_fk foreign key (book_id) references Books (id),
                             constraint author_book_fk foreign key (author_id) references Authors (id)
);

drop table if exists Customers;
create table Customers (
                           id varchar(36) not null,
                           first_name varchar(25) not null,
                           last_name varchar(25) not null,
                           email varchar(255) not null,
                           password varchar(48) not null,
                           state varchar(51) not null,
                           city varchar(51) not null,
                           street varchar(51) not null,
                           home varchar(51) not null,
                           creation_date timestamp not null,
                           last_modified_date timestamp not null,
--                            unique (email),
                           primary key (id)
);
create unique index customer_email on Customers(email);

drop table if exists Orders;
Create table Orders (
                        id varchar(36) not null,
                        customer_id varchar(36) not null,
                        count_of_book int not null,
                        total_price decimal(38, 2) not null,
                        paid_amount decimal(38, 2) not null,
                        change_of_order decimal(38, 2) not null,
                        credit_card_number char(16) not null,
                        credit_card_expiration char(10) not null,
                        creation_date timestamp not null,
                        Primary key (id),
                        constraint customer_order_fk
                            foreign key (customer_id) references Customers (id)
);

-- drop table if exists Book_Order;
Create table Book_Order (
                            book_id varchar(36) not null,
                            order_id varchar(36) not null,
                            count_of_book_copies integer not null,
                            primary key (book_id, order_id),
                            constraint book_order_fk
                                foreign key (book_id) references Books (id),
                            constraint order_book_fk
                                foreign key (order_id) references Orders (id)
);
