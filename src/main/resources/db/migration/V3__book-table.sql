Drop table if exists Books;
Drop table if exists Book_Author;

Create table Books (
  id varchar(36) not null,
  publisher_id varchar(36) not null,
  title varchar(55) not null,
  description varchar(255) not null,
  isbn varchar(255) not null Unique,
  price decimal(38, 2) not null,
  quantity_on_hand int not null,
  category varchar(255) not null,
  creation_date timestamp not null,
  last_modified_date timestamp not null,
  primary key (id),
  constraint book_publisher_fk
  foreign key (publisher_id) references Publishers (id)
);

Create Unique Index
  isbn_index on Books (isbn);

Create table Book_Author (
  book_id varchar(36) not null,
  author_id varchar(36) not null,
  primary key (book_id, author_id),
  constraint book_author_fk
  foreign key (book_id) references Books (id),
  constraint author_book_fk
  foreign key (author_id) references Authors (id)
);

