insert into User (email, password) VALUES ('Gyula@something.com', 'pass');
insert into User (email) VALUES ('Csilla@something.com');
insert into User (email) VALUES ('Alma@something.com');
insert into User (email) VALUES ('Endre@something.com');
insert into User (email) VALUES ('Laci@something.com');
insert into Locker (owner_id) values ((select id FROM USER where email like '%Alma%'));
insert into Locker (owner_id) values ((select id FROM USER where email like '%Gyula%'));
insert into Locker (owner_id) values ((select id FROM USER where email like '%Csilla%'));
insert into Locker (owner_id) values ((select id FROM USER where email like '%Laci%'));
insert into Locker (owner_id) values ((select id FROM USER where email like '%Endre%'));
insert into Locker (owner_id) values (null);
insert into Locker (owner_id) values (null);
insert into Locker (owner_id) values (null);
insert into Locker (owner_id) values (null);
insert into Locker (owner_id) values (null);