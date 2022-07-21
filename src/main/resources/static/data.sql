INSERT INTO user(city,number,street,zipcode,firstname,password,surname,username) values
("bucuresti", 2,"lalelelor",'123',"adminFirst","password","lastname","adminUsername),
 ("Timisoara", 22,"Petru-Voda",'12341',"ClientFirst","password_2","lastname","ClientUsername),
("Timisoara", 22,"Petru-Voda",'12341',"ClientFirst","password_2","lastname","expeditortUsername);

INSERT INTO user_roles values(1,'ADMIN'),(1,'EXPEDITOR');
INSERT INTO user_roles values(2,'CLIENT');
INSERT INTO user_roles values(3,'EXPEDITOR');