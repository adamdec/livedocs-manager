INSERT INTO LD_USER VALUES (nextval('LD_USER_SEQ'), current_date, 'admin@livedocs.eu', 'admin', 'admin', '9dXr06QJJa3fDEt5Ogh1B6BDDLjxhU9JjAcFlHTZGYNentCzm42KYYzekxNiyMn0o8jHlEie+OF1MvJrDamqkw==', 'admin');

insert into LD_USER_PERMISSION values (nextval('LD_USER_PERMISSION_SEQ'), 'Manage', 'Users', 1);
insert into LD_USER_PERMISSION values (nextval('LD_USER_PERMISSION_SEQ'), 'Manage', 'Profile', 1);
insert into LD_USER_PERMISSION values (nextval('LD_USER_PERMISSION_SEQ'), 'Manage', 'Upload', 1);