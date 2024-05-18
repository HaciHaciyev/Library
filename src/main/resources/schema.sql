drop table if exists book;
drop table if exists author;
drop table if exists publisher;
drop table if exists order_line;
drop table if exists customer;

Create table Publisher (
    id varchar(36) not null primary key,
    publisher_name varchar(25) not null,
    state varchar(25) not null,
    city varchar(25) not null,
    street varchar(25) not null,
    home varchar(25) not null,
    phone varchar(25) not null,
    email varchar(255) not null,
    creation_date timestamp not null,
    last_modified_date timestamp not null
);
Create table Author (
    id varchar(36) not null primary key,
    first_name varchar(25) not null,
    last_name varchar(25) not null,
    email varchar(25) not null,
    state varchar(25) not null,
    city varchar(25) not null,
    street varchar(25) not null,
    home varchar(25) not null,
    created_date timestamp not null,
    last_modified_date timestamp not null
);
Create table Book (
    id varchar(36) not null primary key,
    publisher_id varchar(36) not null,
    title varchar(25) not null,
    description varchar(255) not null,
    isbn varchar(255) not null,
    price decimal(38,2) not null,
    quantity_on_hand integer not null,
    category varchar(255) not null,
    created_date timestamp not null,
    last_modified_date timestamp not null,
    CONSTRAINT book_publisher_fk foreign key (publisher_id)
                            references Publisher (id)
);
Create table Book_Author (
    id varchar(36) not null primary key,
    book_id varchar(36) not null,
    author_id varchar(36) not null,
    CONSTRAINT book_author_fk foreign key (book_id)
                         references Book (id),
    CONSTRAINT author_book_fk foreign key (author_id)
                         references Author (id)
);
Create table Customer (
    id varchar(36) not null primary key,
    first_name varchar(25) not null,
    last_name varchar(25) not null,
    email varchar(255) not null,
    password varchar(48) not null,
    state varchar(25) not null,
    city varchar(25) not null,
    street varchar(25) not null,
    home varchar(25) not null,
    creation_date timestamp not null,
    last_modified_date timestamp not null
);
Create table Order_Line (
    id varchar(36) not null primary key,
    customer_id varchar(36) not null,
    count_of_book integer not null,
    total_price decimal(38,2) not null,
    creation_date timestamp not null,
    last_modified_date timestamp not null,
    CONSTRAINT customer_order_fk foreign key (customer_id)
                            references Customer (id)
);
Create table Book_Order (
    id varchar(36) not null primary key,
    book_id varchar(36) not null,
    order_id varchar(36) not null,
    CONSTRAINT book_order_fk foreign key (book_id)
                        references Book (id),
    CONSTRAINT order_book_fk foreign key (order_id)
                        references Order_Line (id)
);