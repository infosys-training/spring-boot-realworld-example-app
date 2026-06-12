create table flights (
  id varchar(255) primary key,
  flight_number varchar(20) not null,
  airline varchar(100) not null,
  origin varchar(100) not null,
  destination varchar(100) not null,
  departure_time TIMESTAMP not null,
  arrival_time TIMESTAMP not null,
  price double not null,
  available_seats int not null,
  created_at TIMESTAMP not null,
  updated_at TIMESTAMP not null default CURRENT_TIMESTAMP
);

create table bookings (
  id varchar(255) primary key,
  user_id varchar(255) not null,
  flight_id varchar(255) not null,
  passenger_name varchar(255) not null,
  passenger_email varchar(255) not null,
  passenger_phone varchar(50),
  seat_number varchar(10),
  status varchar(20) not null default 'CONFIRMED',
  created_at TIMESTAMP not null,
  updated_at TIMESTAMP not null default CURRENT_TIMESTAMP,
  foreign key (user_id) references users(id),
  foreign key (flight_id) references flights(id)
);

-- Seed sample flights
insert into flights (id, flight_number, airline, origin, destination, departure_time, arrival_time, price, available_seats, created_at, updated_at)
values
  ('f1', 'AI101', 'Air India', 'Delhi', 'Mumbai', '2026-07-01 06:00:00', '2026-07-01 08:00:00', 4500.00, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f2', 'AI202', 'Air India', 'Mumbai', 'Delhi', '2026-07-01 10:00:00', '2026-07-01 12:00:00', 4800.00, 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f3', '6E301', 'IndiGo', 'Bangalore', 'Chennai', '2026-07-02 07:30:00', '2026-07-02 08:30:00', 3200.00, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f4', 'UK401', 'Vistara', 'Delhi', 'Bangalore', '2026-07-02 14:00:00', '2026-07-02 17:00:00', 6500.00, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('f5', 'SG501', 'SpiceJet', 'Hyderabad', 'Kolkata', '2026-07-03 09:00:00', '2026-07-03 11:30:00', 5100.00, 160, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
