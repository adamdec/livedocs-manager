INSERT INTO LD_USER VALUES (LD_USER_SEQ.nextval, CURRENT_DATE(), 'admin@livedocs.eu', 'admin', 'admin', '9dXr06QJJa3fDEt5Ogh1B6BDDLjxhU9JjAcFlHTZGYNentCzm42KYYzekxNiyMn0o8jHlEie+OF1MvJrDamqkw==', 'admin');

insert into LD_USER_PERMISSION values (LD_USER_PERMISSION_SEQ.nextval, 'Manage', 'Users', 1);
insert into LD_USER_PERMISSION values (LD_USER_PERMISSION_SEQ.nextval, 'Manage', 'Profile', 1);
insert into LD_USER_PERMISSION values (LD_USER_PERMISSION_SEQ.nextval, 'Manage', 'Upload', 1);