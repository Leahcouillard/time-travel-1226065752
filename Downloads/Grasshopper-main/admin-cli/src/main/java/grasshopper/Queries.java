package grasshopper;



public class Queries {

    ////////////////////////////////////////////////////////// TABLE CREATION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String MAKE_DRONE_TABLE =
        "CREATE TABLE IF NOT EXISTS DRONE ("
        + "DRONE_ID SERIAL PRIMARY KEY, "
        + "ROUTE_ID INTEGER, "
        + "LOCATION_ID INTEGER NOT NULL, "
        + "BATTERY_PERCENTAGE DECIMAL NOT NULL CHECK (BATTERY_PERCENTAGE BETWEEN 0 AND 100),"
        + "DEPARTURE_TIME TIME,"
        + "ETA TIME,"
        + "ARRIVAL_TIME TIME"
        + ");";

    public static final String MAKE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS ROUTE ("
            + "ROUTE_ID SERIAL PRIMARY KEY, "
            + "STARTING_POINT INTEGER NOT NULL, "
            + "ENDING_POINT INTEGER NOT NULL, "
            + "UNIQUE (STARTING_POINT, ENDING_POINT)"
            + ");";

    public static final String MAKE_LOCATION_TABLE =
            "CREATE TABLE IF NOT EXISTS LOCATION ("
            + "LOCATION_ID SERIAL PRIMARY KEY, "
            + "STREET TEXT, "
            + "CITY TEXT, "
            + "STATE TEXT, "
            + "ZIP TEXT, "
            + "COUNTRY TEXT,"
            + "UNIQUE (STREET, CITY, STATE, ZIP, COUNTRY)"
            + ");";

    public static final String MAKE_CONTAINER_TABLE =
            "CREATE TABLE IF NOT EXISTS CONTAINER ("
            + "CONTAINER_ID SERIAL PRIMARY KEY, "
            + "DRONE_ID INTEGER, "
            + "PRODUCT_ID INTEGER,"
            + "BATTERY_PERCENTAGE DECIMAL NOT NULL CHECK (BATTERY_PERCENTAGE BETWEEN 0 AND 100), "
            + "MAX_CAPACITY DECIMAL NOT NULL,"
            + "TEMPERATURE DECIMAL,"
            + "UNITS INTEGER"
            + ");";

    public static final String MAKE_PRODUCT_TABLE =
            "CREATE TABLE IF NOT EXISTS PRODUCT ("
            + "PRODUCT_ID SERIAL PRIMARY KEY, "
            + "PRODUCT_NAME TEXT NOT NULL, "
            + "PRODUCT_WEIGHT DECIMAL NOT NULL,"
            + "MINIMUM_TEMPERATURE DECIMAL,"
            + "MAXIMUM_TEMPERATURE DECIMAL,"
            + "UNIQUE (PRODUCT_NAME, PRODUCT_WEIGHT)"
            + ");";

    ////////////////////////////////////////////////////////// TABLE CREATION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// CONSTRAINT DEFINITION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String ADD_DRONE_CONSTRAINT1 = "ALTER TABLE DRONE ADD CONSTRAINT FK_LOCATION FOREIGN KEY (LOCATION_ID) REFERENCES LOCATION(LOCATION_ID)";

    public static final String ADD_DRONE_CONSTRAINT2 = "ALTER TABLE DRONE ADD CONSTRAINT FK_ROUTE FOREIGN KEY (ROUTE_ID) REFERENCES ROUTE(ROUTE_ID) ON DELETE SET NULL";

    public static final String ADD_ROUTE_CONSTRAINT1 = "ALTER TABLE ROUTE ADD CONSTRAINT FK_START FOREIGN KEY (STARTING_POINT) REFERENCES LOCATION(LOCATION_ID)";

    public static final String ADD_ROUTE_CONSTRAINT2 = "ALTER TABLE ROUTE ADD CONSTRAINT FK_END FOREIGN KEY (ENDING_POINT) REFERENCES LOCATION(LOCATION_ID)";

    public static final String ADD_CONTAINER_CONSTRAINT1 = "ALTER TABLE CONTAINER ADD CONSTRAINT FK_DRONE FOREIGN KEY (DRONE_ID) REFERENCES DRONE(DRONE_ID) ON DELETE SET NULL";

    public static final String ADD_CONTAINER_CONSTRAINT2 = "ALTER TABLE CONTAINER ADD CONSTRAINT FK_PRODUCT FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT(PRODUCT_ID) ON DELETE SET NULL";

    ///////////////////////////////////////////////////////// CONSTRAINT DEFINITION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// VIEW CREATION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String CREATE_ORDERS_VIEW =
            "CREATE VIEW ORDERS AS ("
            + "WITH SUBQUERY AS ("
            + "SELECT DRONE.DRONE_ID AS ORDER_ID, CONTAINER_ID, STREET, DEPARTURE_TIME AS \"DEPARTURE TIME\", "
            + "ETA AS \"ESTIMATED ARRIVAL TIME\", ARRIVAL_TIME AS \"ACTUAL ARRIVAL TIME\", ROUTE.ENDING_POINT "
            + "FROM DRONE NATURAL JOIN LOCATION, CONTAINER, ROUTE "
            + "WHERE CONTAINER.DRONE_ID = DRONE.DRONE_ID "
            + "AND DRONE.ROUTE_ID = ROUTE.ROUTE_ID "
            + ") "
            + "SELECT ORDER_ID, \"DEPARTURE TIME\", \"ESTIMATED ARRIVAL TIME\", \"ACTUAL ARRIVAL TIME\", "
            + "CONTAINER_ID AS \"CONTAINER ID\", SUBQUERY.STREET AS \"CURRENT LOCATION\", LOCATION.STREET AS \"DESTINATION\" "
            + "FROM LOCATION, SUBQUERY "
            + "WHERE LOCATION.LOCATION_ID = SUBQUERY.ENDING_POINT "
            + ");";

    ///////////////////////////////////////////////////////// VIEW CREATION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// TRIGGER DEFINITION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //This trigger deletes all routes associated with a location when that location is deleted
    public static final String ADD_ROUTE_TRIGGER =
            "CREATE OR REPLACE FUNCTION HANDLE_ROUTE_LOCATION_DELETION()\n"
            +"RETURNS TRIGGER AS\n"
            +"$$\n"
            +"BEGIN\n"
            +"DELETE FROM ROUTE\n"
            +"WHERE STARTING_POINT = OLD.LOCATION_ID\n"
            +    "OR ENDING_POINT = OLD.LOCATION_ID;\n"
            +"RETURN OLD;\n"
            +"END;\n"
            +"$$\n"
            +"LANGUAGE plpgsql;\n"
            +"CREATE TRIGGER DELETE_ROUTES_ON_LOCATION_DELETION\n"
            +"BEFORE DELETE ON LOCATION\n"
            +"FOR EACH ROW\n"
            +"EXECUTE FUNCTION HANDLE_ROUTE_LOCATION_DELETION();\n";

    //This trigger assigns a drone to a random location (not 0) whenever its location is deleted
    public static final String ADD_DRONE_TRIGGER =
            "CREATE OR REPLACE FUNCTION HANDLE_DRONE_LOCATION_DELETION()\n"
            +"RETURNS TRIGGER AS\n"
            +"$$\n"
            +"BEGIN\n"
            +"UPDATE DRONE\n"
            +"SET LOCATION_ID = (SELECT LOCATION_ID FROM LOCATION WHERE LOCATION_ID <> 0 ORDER BY RANDOM() LIMIT 1)\n"
            +"WHERE DRONE.LOCATION_ID = OLD.LOCATION_ID;\n"
            +"RETURN OLD;\n"
            +"END;\n"
            +"$$\n"
            +"LANGUAGE plpgsql;\n"
            +"\n"
            +"CREATE TRIGGER UPDATE_DRONE_ON_LOCATION_DELETION\n"
            +"BEFORE DELETE ON LOCATION\n"
            +"FOR EACH ROW\n"
            +"EXECUTE FUNCTION HANDLE_DRONE_LOCATION_DELETION();";

    ///////////////////////////////////////////////////////// TRIGGER DEFINITION STATEMENTS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// DATABASE TRUNCATION STATEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String TRUNCATE_TABLES = "TRUNCATE DRONE, LOCATION, ROUTE, PRODUCT, CONTAINER";

    ///////////////////////////////////////////////////////// DATABASE TRUNCATION STATEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// DRONE TRANSIT SIMULATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Function to update a drone's location so it's moved to the air when the current time is the drone's departure time
    public static final String MOVE_TO_FLIGHT_FUNCTION =
            "CREATE FUNCTION MOVE_DRONE_TO_AIR() "
            +"RETURNS VOID "
            +"LANGUAGE plpgsql "
            +"AS $$ "
            +"BEGIN "
            +"UPDATE DRONE "
            +"SET LOCATION_ID = 0 "
            +"WHERE DRONE.DEPARTURE_TIME = DATE_TRUNC('minute', CURRENT_TIMESTAMP)::time; "
            +"END; "
            +"$$;";
    // Function to update a drone's location so it's moved to its destination when the current time is the drone's arrival time
    public static final String MOVE_TO_DESTINATION_FUNCTION =
            "CREATE FUNCTION MOVE_DRONE_TO_DESTINATION()"
            +"RETURNS VOID "
            +"LANGUAGE plpgsql "
            +"AS $$ "
            +"BEGIN "
            +"UPDATE DRONE "
            +"SET LOCATION_ID = (SELECT R.ENDING_POINT FROM ROUTE R WHERE R.ROUTE_ID = DRONE.ROUTE_ID) "
            +"WHERE ARRIVAL_TIME = DATE_TRUNC('minute', CURRENT_TIMESTAMP)::time; "
            +"END; "
            +"$$;";
     // Function to set a drone's route to null once it reaches that route's destination
     public static final String SET_NULL_ROUTE_WHEN_AT_DESTINATION_FUNCTION =
            "CREATE FUNCTION SET_NULL_ROUTE_WHEN_AT_DESTINATION() "
            +"RETURNS VOID "
            +"LANGUAGE plpgsql "
            +"AS $$ "
            +"BEGIN "
            +"UPDATE DRONE "
            +"SET ROUTE_ID = NULL "
            +"WHERE LOCATION_ID = (SELECT R.ENDING_POINT FROM ROUTE R WHERE R.ROUTE_ID = DRONE.ROUTE_ID); "
            +"END; "
            +"$$";
     // Function to automatically charge the batteries of drones that are not in transit
     public static final String CHARGE_DRONE_BATTERIES =
            "CREATE FUNCTION CHARGE_DRONE_BATTERIES() "
            +"RETURNS VOID "
            +"LANGUAGE plpgsql "
            +"AS $$ "
            +"BEGIN "
            +"UPDATE DRONE "
            +"SET BATTERY_PERCENTAGE = BATTERY_PERCENTAGE + 1 "
            +"WHERE ROUTE_ID IS NULL "
            +"AND LOCATION_ID > 0 "
            +"AND BATTERY_PERCENTAGE < 100; "
            +"END; "
            +"$$; ";
     // Function to automatically drain the batteries of drones that are in transit
     public static final String DRAIN_DRONE_BATTERIES =
            "CREATE FUNCTION DRAIN_DRONE_BATTERIES() "
            +"RETURNS VOID "
            +"LANGUAGE plpgsql "
            +"AS $$ "
            +"BEGIN "
            +"UPDATE DRONE "
            +"SET BATTERY_PERCENTAGE = BATTERY_PERCENTAGE - 1 "
            +"WHERE ROUTE_ID IS NOT NULL "
            +"AND BATTERY_PERCENTAGE > 0; "
            +"END; "
            +"$$; ";

    ///////////////////////////////////////////////////////// DRONE TRANSIT SIMULATION \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ///////////////////////////////////////////////////////// TABLE OUTPUT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public static final String OUTPUT_DRONE_DATA = "SELECT * FROM DRONE ORDER BY DRONE_ID;";

    public static final String OUTPUT_CONTAINER_DATA = "SELECT * FROM CONTAINER ORDER BY CONTAINER_ID;";

    public static final String OUTPUT_LOCATION_DATA = "SELECT * FROM LOCATION WHERE LOCATION_ID > 0 ORDER BY LOCATION_ID;";

    public static final String OUTPUT_ROUTE_DATA = "SELECT * FROM ROUTE ORDER BY ROUTE_ID;";

    public static final String OUTPUT_PRODUCT_DATA = "SELECT * FROM PRODUCT ORDER BY PRODUCT_ID;";

    public static final String OUTPUT_ORDER_DATA = "SELECT * FROM ORDERS ORDER BY ORDER_ID;";

    ///////////////////////////////////////////////////////// TABLE OUTPUT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
}
