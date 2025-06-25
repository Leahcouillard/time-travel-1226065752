--Most testing will be done between Lleida and Barcelona

----Inserting locations:

insert into location (location_id, STREET, CITY, STATE, ZIP, COUNTRY) values (0, 'In flight', null, null, null, null);
insert into location (location_id, STREET, CITY, STATE, ZIP, COUNTRY) VALUES (1, 'Polígono Industrial Zona Franca, Calle A, Sector A', 'Barcelona', 'Cataluña', 08040, 'Spain');
insert into location (location_id, STREET, CITY, STATE, ZIP, COUNTRY) VALUES (2, 'Vial C', 'Lleida', 'Cataluña', 25190, 'Spain');
insert into location (location_id, STREET, CITY, STATE, ZIP, COUNTRY) VALUES (3, 'Polígono Industrial Vara de Quart, Calle Gremis, 12', 'Valencia', 'Comunidad Valenciana', 46014, 'Spain');
insert into location (location_id, STREET, CITY, STATE, ZIP, COUNTRY) VALUES (4, 'Cam. Enmedio, 130, San José', 'Zaragoza', 'Aragón', 50013, 'Spain');

----Inserting routes:

insert into route(route_id, starting_point, ending_point) values (1, 1, 2);
insert into route(route_id, starting_point, ending_point) values (2, 1, 3);
insert into route(route_id, starting_point, ending_point) values (3, 1, 4);
insert into route(route_id, starting_point, ending_point) values (4, 2, 1);
insert into route(route_id, starting_point, ending_point) values (5, 2, 3);
insert into route(route_id, starting_point, ending_point) values (6, 2, 4);
insert into route(route_id, starting_point, ending_point) values (7, 3, 1);
insert into route(route_id, starting_point, ending_point) values (8, 3, 2);
insert into route(route_id, starting_point, ending_point) values (9, 3, 4);
insert into route(route_id, starting_point, ending_point) values (10, 4, 1);
insert into route(route_id, starting_point, ending_point) values (11, 4, 2);
insert into route(route_id, starting_point, ending_point) values (12, 4, 3);

----Inserting drones:
--Drones on stand-by at each location (one with full battery, one charging)
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (1,null, 1, 100, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (2,null, 1, 50, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (3,null, 2, 100, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (4,null, 2, 50, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (5,null, 3, 100, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (6,null, 3, 50, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (7,null, 4, 100, null, null, null);
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (8,null, 4, 50, null, null, null);

--Drones traveling between Lleida and Barcelona (Bunch of different test cases)
--Drone that is on time, traveling to Lleida, in Barcelona
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (9,1, 1, 75, '10:00', '12:00', '12:00');
--Drone that is on time, traveling to Barcelona, in Lleida
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (10,4, 2, 75, '10:40', '12:50', '12:50');
--Drone that is on time, traveling to Barcelona, in the air
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (11,4, 0, 50, '9:40', '11:50', '11:50');

--Drone that is on late, traveling to Lleida, in the air
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (12,1, 0, 75, '10:00', '12:00', '12:20');
--Drone that is on late, traveling to Barcelona, in the air
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (13,2, 0, 75, '10:40', '12:50', '1:05');
--Drone that is on late, traveling to Barcelona, in the air
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (14,2, 0, 50, '10:40', '12:50', '12:55');

--Drone that has a route and is still on the ground
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (15,1, 1, 100, '10:00', '12:00', null);

--Drone that has a route and is still in the air
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (16,1, 0, 100, '10:00', '12:00', null);

--Misc -> Cron job testing
insert into drone(drone_id, route_id, location_id, battery_percentage, departure_time, eta, arrival_time) values (17,1, 1, 75, '14:05', '14:06', '14:06');

----Inserting products:

insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (1,'Diphtheria Vaccine', 6.8, 2, 8);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (2,'Polio Vaccine', 6.8, 2, 8);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (3,'HPV Vaccine', 6.8, 2, 8);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (4,'Hepatitis A Vaccine', 6.8, 2, 8);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (5,'Insulin', 2.1, 2, 8);

insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (6,'Hake - Bulk Container', 7.5, -2, 2);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (7,'Whiting - Bulk Container', 7.5, -1, 2);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (8,'Salmon - Bulk Container', 12, 0, 4);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (9,'Peaches - Bushel', 21.7, -2, 0);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (10,'Eggs - 36 count', 15.5, 2, 8);

insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (11,'Car Battery', 22.7, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (12,'Shock Absorber', 4.4, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (13,'Muffler - Standard', 1.8, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (14,'Muffler - Heavy', 7.25, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (15,'Car Door', 27, null, null);

insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (16,'Plastic - bulk', 25, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (17,'Aluminum - bulk', 50, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (18,'Fabric', 10, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (19,'Red Clothing Dye', 15, null, null);
insert into product (product_id, product_name, product_weight, minimum_temperature, maximum_temperature) values (20,'Rubber - bulk', 20, null, null);

----Inserting containers:
--Refrigerated containers on drones
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(1,9, 1, 100, 350, 5, 2);
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(2,10, 1, 100, 350, 5, 2);

--Overweight container, refrigerated container
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(3,11, 2, 100, 350, 500, 2);

--containers that are not in a drone, refrigerated container
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(4,null, 1, 100, 350, 5, 2);
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(5,null, 2, 100, 350, 5, 2);

--containers that are not in a drone, empty refrigerated container
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(6,null, null, 100, 350, null, 2);
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(7,null, null, 100, 350, null, 2);

--Regular containers on drones
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(8,12, 11, 100, 350, 5, null);
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(9,13, 12, 100, 350, 5, null);

--Overweight container, normal container
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(10,14, 11, 100, 350, 20, null);

--containers that are not in a drone, normal container
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(11,null, 17, 100, 350, 5, null);
insert into container(container_id, drone_id, product_id, battery_percentage, max_capacity, units, temperature) values(12,null, 19, 100, 350, 5, null);