Drop table if exists Orders;

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