package grasshopper;

import java.sql.*;                          // Imported to get the necessary libraries to create a JDBC connection with the database
import java.util.Scanner;                   // Imported to get input from the user, read files
import org.dhatim.fastexcel.Workbook;       // Imported to have access to excel functionality
import org.dhatim.fastexcel.Worksheet;      // Imported to have access to excel functionality
import java.util.ArrayList;                 // Imported so the program can use the ArrayList data structure
import java.util.Arrays;                    // Imported so the program can use the array data structure
import java.io.File;                        // Imported so the program can read files
import java.io.FileNotFoundException;       // Imported to handle any potential file errors
import java.io.IOException;                 // Important to handle IO exceptions
import java.io.OutputStream;                // Important to handle file IO
import java.nio.file.Files;                 // Imported to generate files
import java.nio.file.Paths;                 // Imported to generate paths

public class App {
    public static String userInput = "";    // Used to track input entered by the user

    /**
     * Method Creation Date: 3/6/2025, Zachary Treichler
     * Most Recent Change: 13/6/2025, Zachary Treichler
     * Method Description:The main method is used to call the other methods in the program. There are currently 4 potential cases.
     *  Case 1: Handles the creation of the database's tables, triggers, and views
     *  Case 2: Handles the creation of the database's mock data
     *  Case 3: Handles the deletion of the database (including all tables and data)
     *  Case 4: Enter the view interface
     *  Case 5: Generate spreadsheets containing existing database data
     *  Case 6: Exit the command line interface
     * Functions Using This Method: N/A
     * Description of Variables:
     * @param args - Used to store any arguments passed in by the user
     * @param queries - ArrayList that holds table, function, and constraint generation statements and data population statements as well
     * @param scan - Scanner used to interpret user input
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<String> queries = new ArrayList<>();
        ArrayList<String> spreadsheetHeaders = new ArrayList<>();
        do{
            System.out.print("[1] Initialize Database\n[2] Populate Data\n[3] Reset Mock Data\n[4] View Tables\n[5] Export Data To Excel\n[6] Exit\nSelect an option: ");
            userInput = scan.nextLine();
            switch(userInput){
                case "1":
                    System.out.println("Generating database...");
                    queries = new ArrayList<>(Arrays.asList(Queries.MAKE_DRONE_TABLE, Queries.MAKE_ROUTE_TABLE, Queries.MAKE_LOCATION_TABLE, Queries.MAKE_CONTAINER_TABLE, Queries.MAKE_PRODUCT_TABLE,
                    Queries.ADD_DRONE_CONSTRAINT1, Queries.ADD_DRONE_CONSTRAINT2, Queries.ADD_ROUTE_CONSTRAINT1, Queries.ADD_ROUTE_CONSTRAINT2, Queries.ADD_CONTAINER_CONSTRAINT1, Queries.ADD_CONTAINER_CONSTRAINT2,
                    Queries.CREATE_ORDERS_VIEW, Queries.ADD_ROUTE_TRIGGER, Queries.ADD_DRONE_TRIGGER, Queries.MOVE_TO_FLIGHT_FUNCTION, Queries.MOVE_TO_DESTINATION_FUNCTION, Queries.SET_NULL_ROUTE_WHEN_AT_DESTINATION_FUNCTION,
                    Queries.CHARGE_DRONE_BATTERIES, Queries.DRAIN_DRONE_BATTERIES));
                    updateDatabase(queries);
                    queries.clear();
                    break;
                case "2":
                    System.out.println("Populating database...");
                    populateData("C:\\Users\\Ztrei\\desktop\\work_stuff\\Grasshopper\\admin-cli\\DataGeneration.sql"); //This will have to be changed once branch is copied
                    break;
                case "3":
                    System.out.print("Are you sure? [y] or [n]: ");
                    userInput = scan.nextLine();
                    if(userInput.equals("y")||userInput.equals("Y")){
                        System.out.println("Clearing database...");
                        queries = new ArrayList<>(Arrays.asList(Queries.TRUNCATE_TABLES));
                        updateDatabase(queries);
                        queries.clear();
                    }
                    else{
                        break;
                    }
                    break;
                case "4":
                    viewOptions(scan);
                    break;
                case "5":
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "DRONE ID", "ROUTE ID", "LOCATION ID", "BATTERY PERCENTAGE", "DEPARTURE TIME", "ETA", "ARRIVAL TIME"));
                    generateSpreadsheet("DRONE_INFO", spreadsheetHeaders, Queries.OUTPUT_DRONE_DATA);
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "LOCATION ID", "STREET", "CITY", "STATE", "ZIP", "COUNTRY"));
                    generateSpreadsheet("LOCATION_INFO", spreadsheetHeaders, Queries.OUTPUT_LOCATION_DATA);
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "ROUTE ID", "STARTING ID", "ENDING ID"));
                    generateSpreadsheet("ROUTE_INFO", spreadsheetHeaders, Queries.OUTPUT_ROUTE_DATA);
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "PRODUCT ID", "NAME", "WEIGHT","MIN TEMP", "MAX TEMP"));
                    generateSpreadsheet("PRODUCT_INFO", spreadsheetHeaders, Queries.OUTPUT_PRODUCT_DATA);
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "CONTAINER ID", "DRONE ID", "PRODUCT ID","BATTERY PERCENTAGE", "MAX CAPACITY", "TEMPERATURE", "UNITS"));
                    generateSpreadsheet("CONTAINER_INFO", spreadsheetHeaders, Queries.OUTPUT_CONTAINER_DATA);
                    spreadsheetHeaders = new ArrayList<>(Arrays.asList( "ORDER ID","CONTAINER STATUS", "CONTAINER ID", "ORDER STATUS", "DELIVERY STATUS", "DEPARTURE TIME","ESTIMATED ARRIVAL TIME", "DELIVERY LOCATION"));
                    generateOrdersSpreadsheet("ORDER_INFO", spreadsheetHeaders, Queries.OUTPUT_ORDER_DATA);
                    break;
                case "6":
                    break;
                default:
                    System.out.println("Enter a valid input (integer value 1-6)");
            }
        }
        while(!userInput.equals("6"));
        scan.close();
    }

    /////////////////////////////////////////////////// DATABASE MANAGEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Method Creation Date: 28/5/2025, Zachary Treichler
     * Most Recent Change: 11/6/2025, Zachary Treichler
     * Method Description: This method is used to generate tables in a database and define their constraints. It establishes a connection using a database URI,
     * and then executes a prepared statement based on the query variable that was passed into the method. This method uses try with resources, so the connection
     * closes when any error occurs. The purpose of this is to prevent zombie instances in the database which are detrimental to performance. There are a variety of
     * cases in the catch block that are used to handle errors; look at the codes to see what each error code represents. There is also functionality to handle the
     * exit output, which is described in the code.
     * Functions Using This Method: main, populateData
     * Description of Variables:
     * @param query - Used to store the SQL query that will be executed by this method
     * @param DATABASE_URI - This needs to be exported locally as an environment variable before the program can properly execute. This is done for security reasons.
     * @param numberOfQueries - Tracks the number of queries that should be executed after a reset of the database
     * @param executedQueries - Tracks the number of queries that were actually executed. If executedQueries < numberOfQueries, the database was already initialized
     *                              **Special case: For the initialize tables option, the database was initialized if executedQueries = 5.
     */
    public static void updateDatabase(ArrayList<String> queries){
        int numberOfQueries = queries.size();
        int executedQueries = 0;
        for(String query: queries){
            try(
                Connection c = DriverManager.getConnection(System.getenv("DATABASE_URI"));
            ){
                try(
                    PreparedStatement stmt1 = c.prepareStatement(query);
                ){
                    stmt1.executeUpdate();
                }
                c.close();
            }
            catch(SQLException e){
                //Table already exists error state
                if(e.getSQLState().equals("42710")){
                    continue;
                }
                //View already exists error state
                if(e.getSQLState().equals("42P07")){
                    continue;
                }
                //Data already exists error state (duplicate primary keys)
                if(e.getSQLState().equals("23505")){
                    continue;
                }
                //Function already exists error state
                if(e.getSQLState().equals("42723")){
                    continue;
                }
                System.out.println("SQL error - " + e.getMessage());
                System.out.println("SQL state at time of error - " + e.getSQLState());
            }
            executedQueries++;
        }
        /*
         * For some reason duplicate tables still triggers executed queries to increment, even though this should be throwing an error. There are
         * five tables in the database, so that is why executedQueries is equal to 5 in the if-then statement.
         */
        if((executedQueries == 5) && (userInput.equals("1"))){
            System.out.println("Database has already been initialized. Please select a different option.");
        }
        else if((executedQueries < numberOfQueries) && (userInput.equals("2"))){
            System.out.println("Database has already been populated with data. Please select a different option.");
        }
        else{
            return;
        }
    }
    /**
     * Method Creation Date: 4/6/2025, Zachary Treichler
     * Most Recent Change: 11/6/2025, Zachary Treichler
     * Method Description: This method takes a file as input and then executes queries to insert data into the database. If there is a file error, the method
     * throws a file not found exception. The file is then trimmed so it removes any comment lines to optimize performance. The method then calls updateDatabase
     * to execute the insertion queries from the file.
     * Functions Using This Method: main
     * Description of Variables:
     * @param filePath - Is used to store the path of the file that is going to be read
     * @param fileName - Refers to the file object that is created from filePath
     * @param fileReader - Scanner object used to read from the file
     * @param dataGenerationQueries - Used to store each line in the file and the query it contains
     */
    public static void populateData(String filePath){
        ArrayList<String> dataGenerationQueries = new ArrayList<>();
        try{
            File fileName = new File(filePath);
            Scanner fileReader = new Scanner(fileName);
            while (fileReader.hasNextLine()){
                dataGenerationQueries.add(fileReader.nextLine());
            }
        fileReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("File error - " + e.getMessage());
        }
        for(int i = 0; i < dataGenerationQueries.size(); i++){
            if(dataGenerationQueries.get(i).contains("--")){
                dataGenerationQueries.remove(i);
            }
        }
        updateDatabase(dataGenerationQueries);
    }

    /////////////////////////////////////////////////// DATABASE MANAGEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /////////////////////////////////////////////////// TABLE VIEW \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Method Creation Date: 12/6/2025, Zachary Treichler
     * Most Recent Change: 13/6/2025, Zachary Treichler
     * Method Description: This method generates the interface to view the tables within the database. There are currently 6 options
     *  Case 1: View All Drones
     *  Case 2: View All Containers
     *  Case 3: View All Locations
     *  Case 4: View All Routes
     *  Case 5: View All Products
     *  Case 6: View All Orders
     *  Case 6: Exit
     * For each view case, a format is specified for each column to follow and this is passed into the outputTable method.
     * Functions Using This Method: main
     * Description of Variables:
     * @param scan - Scanner used to handle user input
     * @param tableFormatting - This array is used to specify the formatting of each column in the output
     * @param userInput - This refers to the integer entered by the user
     */
    public static void viewOptions(Scanner scan){
        ArrayList<String> tableFormatting = new ArrayList<>();
        do{
            System.out.print("[1] View All Drones\n[2] View All Containers\n[3] View All Locations\n[4] View All Routes\n"
            +"[5] View All Products\n[6] View All Orders\n[7] Return\nSelect an option: ");
            userInput = scan.nextLine();
            switch(userInput){
                case "1":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-10s |", " %-10s |", " %-15s |", " %-20s |", " %-20s |", " %-25s |", " %-21s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2) + tableFormatting.get(3) + tableFormatting.get(4) +
                    tableFormatting.get(5) + tableFormatting.get(6) + "\n","DRONE ID","ROUTE ID", "LOCATION ID", "BATTERY PERCENTAGE", "DEPARTURE TIME", "ESTIMATED ARRIVAL TIME", "ACTUAL ARRIVAL TIME");
                    outputTable(Queries.OUTPUT_DRONE_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "2":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-15s |", " %-10s |", " %-15s |", " %-20s |", " %-20s |", " %-25s |", " %-10s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2) + tableFormatting.get(3) + tableFormatting.get(4) +
                    tableFormatting.get(5) + tableFormatting.get(6) + "\n","CONTAINER ID","DRONE ID", "PRODUCT ID", "BATTERY PERCENTAGE", "MAX CAPACITY", "TEMPERATURE", "UNITS");
                    outputTable(Queries.OUTPUT_CONTAINER_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "3":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-12s |", " %-60s |", " %-15s |", " %-20s |", " %-10s |", " %-10s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2) + tableFormatting.get(3) + tableFormatting.get(4) +
                    tableFormatting.get(5) + "\n","LOCATION ID","STREET", "CITY", "STATE", "ZIP", "COUNTRY");
                    outputTable(Queries.OUTPUT_LOCATION_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "4":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-10s |", " %-25s |", " %-20s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2)
                    + "\n","ROUTE ID","STARTING POINT", "ENDING POINT");
                    outputTable(Queries.OUTPUT_ROUTE_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "5":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-12s |", " %-25s |", " %-20s |", " %-20s |", " %-20s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2) + tableFormatting.get(3) +
                    tableFormatting.get(4) + "\n","PRODUCT ID","NAME", "WEIGHT", "MINIMUM TEMPERATURE", "MAXIMUM TEMPERATURE");
                    outputTable(Queries.OUTPUT_PRODUCT_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "6":
                    tableFormatting = new ArrayList<>(Arrays.asList("| %-10s |", " %-20s |", " %-15s |", " %-15s |", " %-20s |", " %-20s |", " %-25s |", " %-60s |"));
                    System.out.printf(tableFormatting.get(0) + tableFormatting.get(1) + tableFormatting.get(2) + tableFormatting.get(3) + tableFormatting.get(4) +
                    tableFormatting.get(5) + tableFormatting.get(6) + tableFormatting.get(7) + "\n","ORDER ID","CONTAINER STATUS", "CONTAINER ID", "ORDER STATUS", "DELIVERY STATUS", "DEPARTURE TIME","ESTIMATED ARRIVAL TIME", "DELIVERY LOCATION");
                    outputOrders(Queries.OUTPUT_ORDER_DATA, tableFormatting);
                    tableFormatting.clear();
                    break;
                case "7":
                    break;
                default:
                    System.out.println("Enter a valid input (integer value 1-7)");
            }
        }
        while(!userInput.equals("7"));
    }
    /**
     * Method Creation Date: 12/6/2025, Zachary Treichler
     * Most Recent Change: 12/6/2025, Zachary Treichler
     * Method Description: This method is used to generate the output for the database's tables. To make this as broad as efficient as possible,
     * I made the method accept a tableFormatting and output query so it could be used for any table in the database. This method uses try with
     * resources to ensure that the database connection closes if any exception happens.
     * Functions Using This Method: viewOptions
     * Description of Variables:
     * @param outputQuery - the method takes a query as a param to specify the output (Ex: Select * from drone)
     * @param tableFormatting - This array specifies how each column of the output will be aligned
     * @param size - This is the size of the tableFormatting array. This is calculated separately to optimize performance
     * @param column - This refers to a specific column within a table. The columns are added together to create a line of output
     * @param line - This represents the contents of an entire row
     * @param stmt1 - This is the prepared statement used to interact with the database
     */
    public static void outputTable(String outputQuery, ArrayList<String> tableFormatting){
        int size = tableFormatting.size();
        String column = "";
        String line = "";
        try(
            Connection c = DriverManager.getConnection(System.getenv("DATABASE_URI"));
        ){
            try(
                PreparedStatement stmt1 = c.prepareStatement(outputQuery);
            ){
                ResultSet tableOutput = stmt1.executeQuery();
                while(tableOutput.next()){
                    line = "";
                    for(int i = 0; i < size; i++){
                        column = String.format(tableFormatting.get(i), tableOutput.getString(i+1));
                        line += column;
                    }
                    System.out.println(line);
                }
            }
            c.close();
        }
        catch(SQLException e){
            System.out.println("SQL error - " + e.getMessage());
            System.out.println("SQL state at time of error - " + e.getSQLState());
        }
    }
    /**
     * Method Creation Date: 13/6/2025, Zachary Treichler
     * Most Recent Change: 13/6/2025, Zachary Treichler
     * Method Description: I'll fully admit that this code looks kind of gross, but given the irregular nature of the output, there was no was to
     * refine it further. The outputOrders method is based on the outputTable method, but makes a change to allow for some additional columns to
     * be generated such as order status, container status, and delivery status. This method uses try with resources to avoid any instances of
     * zombie processes, and outputs the error state and message whenever an exception occurs.
     * Functions Using This Method: viewOptions
     * Description of Variables:
     * @param outputQuery - the method takes a query as a param to specify the output (Ex: Select * from orders)
     * @param tableFormatting - This array specifies how each column of the output will be aligned
     * @param column - This refers to a specific column within a table. The columns are added together to create a line of output
     * @param line - This represents the contents of an entire row
     * @param stmt1 - This is the prepared statement used to interact with the database
     */
    public static void outputOrders(String outputQuery, ArrayList<String> tableFormatting){
        String column = "";
        String line = "";
        try(
            Connection c = DriverManager.getConnection(System.getenv("DATABASE_URI"));
        ){
            try(
                PreparedStatement stmt1 = c.prepareStatement(outputQuery);
            ){
                ResultSet tableOutput = stmt1.executeQuery();
                while(tableOutput.next()){
                    line = "";
                    /////////////////////////////////////// COLUMN 1 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    column = String.format(tableFormatting.get(0), tableOutput.getString(1));
                    line += column;
                    /////////////////////////////////////// COLUMN 2 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    if(tableFormatting.get(5) != null){
                        column = String.format(tableFormatting.get(1), "Container Selected");
                        line += column;
                    }
                    else{
                        column = String.format(tableFormatting.get(1), "Container Pending");
                        line += column;
                    }
                    /////////////////////////////////////// COLUMN 3 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    column = String.format(tableFormatting.get(2), tableOutput.getString(5));
                    line += column;
                    /////////////////////////////////////// COLUMN 4 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    if(tableOutput.getString(3).compareTo(tableOutput.getString(4)) < 0){
                        column = String.format(tableFormatting.get(3), "Late");
                        line += column;
                    }
                    else if(tableOutput.getString(3).compareTo(tableOutput.getString(4)) > 0){
                        column = String.format(tableFormatting.get(3), "Early");
                        line += column;
                    }
                    else{
                        column = String.format(tableFormatting.get(3), "On time");
                        line += column;
                    }
                    /////////////////////////////////////// COLUMN 5 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    if(tableOutput.getString(6).equals(tableOutput.getString(7))){
                        column = String.format(tableFormatting.get(4), "Delivered");
                        line += column;
                    }
                    else{
                        column = String.format(tableFormatting.get(4), "Processing");
                        line += column;
                    }
                    /////////////////////////////////////// COLUMN 6 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    column = String.format(tableFormatting.get(5), tableOutput.getString(2));
                    line += column;
                    /////////////////////////////////////// COLUMN 7 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    column = String.format(tableFormatting.get(6), tableOutput.getString(3));
                    line += column;
                    /////////////////////////////////////// COLUMN 8 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    column = String.format(tableFormatting.get(7), tableOutput.getString(7));
                    line += column;
                    /////////////////////////////////////// PRINT LINE \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                    System.out.println(line);
                }
            }
            c.close();
        }
        catch(SQLException e){
            System.out.println("SQL error - " + e.getMessage());
            System.out.println("SQL state at time of error - " + e.getSQLState());
        }
    }

    /////////////////////////////////////////////////// TABLE VIEW \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /////////////////////////////////////////////////// GENERATE SPREADSHEET \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Method Creation Date: 13/6/2025, Zachary Treichler
     * Most Recent Change: 13/6/2025, Zachary Treichler
     * Method Description: This method takes the contents of the database and places them in an excel spreadsheet so they can be exported. This relies on the Fastexcel
     * dependencies in the pom.xml file. I also recommend installing the excel viewing extension so you can natively work with excel files through VS code. Some of this
     * code is shamelessly copied from https://www.baeldung.com/java-microsoft-excel, so view the link for more info.
     * Functions Using This Method: main
     * Description of Variables:
     * @param currentDirectory - The current directory for the repository
     * @param path - Generates a path for the file directory
     * @param fileLocation - Creates a file path for the spreadsheet
     * @param size - Calculates the size of the spreadsheet header array. This is calculated separately to optimize performance
     * @param ws - The current worksheet that the database will write to
     * @param stmt1 - The statement that will be run to interact with the database
     * @param tableOutput - List that holds the results of the query
     */
    public static void generateSpreadsheet(String sheetName, ArrayList<String> spreadsheetHeaders, String query) {
        File currentDirectory = new File(".");
        String path = currentDirectory.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "spreadsheets\\" + sheetName + ".xlsx";
        int size = spreadsheetHeaders.size();
        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet ws = wb.newWorksheet(sheetName);
            for(int i = 0; i < size; i++){
                ws.width(i, 25);
                ws.value(0, i, spreadsheetHeaders.get(i));
            }
            try(
                Connection c = DriverManager.getConnection(System.getenv("DATABASE_URI"));
            ){
                try(
                    PreparedStatement stmt1 = c.prepareStatement(query);
                ){
                    ResultSet tableOutput = stmt1.executeQuery();
                    int row = 1;
                    while(tableOutput.next()){
                        for(int i = 0; i < size; i++){
                            ws.value(row, i, tableOutput.getString(i+1));
                        }
                        row++;
                    }
                }
                c.close();
            }
            catch(SQLException e){
                System.out.println("SQL error - " + e.getMessage());
                System.out.println("SQL state at time of error - " + e.getSQLState());
            }
        }
        catch(IOException e){
            System.out.println("An error occurred - " + e.getMessage());
        }
        spreadsheetHeaders.clear();
    }
    /**
     * Method Creation Date: 14/6/2025, Zachary Treichler
     * Most Recent Change: 14/6/2025, Zachary Treichler
     * Method Description: This method takes the contents of the database and places them in an excel spreadsheet so they can be exported. This relies on the Fastexcel
     * dependencies in the pom.xml file. I also recommend installing the excel viewing extension so you can natively work with excel files through VS code. Some of this
     * code is shamelessly copied from https://www.baeldung.com/java-microsoft-excel, so view the link for more info. This is a slight variation of the generateSpreadsheet
     * method, since the orders table requires so additional calculation to be output properly.
     * Functions Using This Method: main
     * @param currentDirectory - The current directory for the repository
     * @param path - Generates a path for the file directory
     * @param fileLocation - Creates a file path for the spreadsheet
     * @param size - Calculates the size of the spreadsheet header array. This is calculated separately to optimize performance
     * @param ws - The current worksheet that the database will write to
     * @param stmt1 - The statement that will be run to interact with the database
     * @param tableOutput - List that holds the results of the query
     */
    public static void generateOrdersSpreadsheet(String sheetName, ArrayList<String> spreadsheetHeaders, String query){
        File currentDirectory = new File(".");
        String path = currentDirectory.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "spreadsheets\\" + sheetName + ".xlsx";
        int size = spreadsheetHeaders.size();
        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet ws = wb.newWorksheet(sheetName);
            for(int i = 0; i < size; i++){
                ws.width(i, 25);
                ws.value(0, i, spreadsheetHeaders.get(i));
            }
            try(
                Connection c = DriverManager.getConnection(System.getenv("DATABASE_URI"));
            ){
                try(
                    PreparedStatement stmt1 = c.prepareStatement(query);
                ){
                    ResultSet tableOutput = stmt1.executeQuery();
                    int row = 1;
                    while(tableOutput.next()){
                        /////////////////////////////////////// COLUMN 1 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        ws.value(row, 0, tableOutput.getString(1));
                        /////////////////////////////////////// COLUMN 2 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        if(tableOutput.getString(5) != null){
                            ws.value(row, 1, "Container Selected");
                        }
                        else{
                            ws.value(row, 1, "Container Pending");
                        }
                        /////////////////////////////////////// COLUMN 3 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        ws.value(row, 2, tableOutput.getString(5));
                        /////////////////////////////////////// COLUMN 4 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        if(tableOutput.getString(3).compareTo(tableOutput.getString(4)) < 0){
                            ws.value(row, 3, "Late");
                        }
                        else if(tableOutput.getString(3).compareTo(tableOutput.getString(4)) > 0){
                            ws.value(row, 3, "Early");
                        }
                        else{
                            ws.value(row, 3, "On time");
                        }
                        /////////////////////////////////////// COLUMN 5 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        if(tableOutput.getString(6).equals(tableOutput.getString(7))){
                            ws.value(row, 4, "Delivered");
                        }
                        else{
                            ws.value(row, 4, "Processing");
                        }
                        /////////////////////////////////////// COLUMN 6 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        ws.value(row, 5, tableOutput.getString(2));
                        /////////////////////////////////////// COLUMN 7 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        ws.value(row, 6, tableOutput.getString(3));
                        /////////////////////////////////////// COLUMN 8 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        ws.value(row, 7, tableOutput.getString(7));
                        /////////////////////////////////////// COLUMN 9 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
                        row++;
                    }
                }
                c.close();
            }
            catch(SQLException e){
                System.out.println("SQL error - " + e.getMessage());
                System.out.println("SQL state at time of error - " + e.getSQLState());
            }
        }
        catch(IOException e){
            System.out.println("An error occurred - " + e.getMessage());
        }
        spreadsheetHeaders.clear();
    }

    /////////////////////////////////////////////////// GENERATE SPREADSHEET \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

}
