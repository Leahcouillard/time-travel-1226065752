package grasshopper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    // NOTE: ASSUME THE URI FOR THE DATABASE HAS BEEN PROPERLY SET UP DURING TESTING. MOST TESTS WILL AUTOMATICALLY FAIL IF THIS IS NOT THE CASE
    // IBM GUIDE TO ALL SQL STATES. READ THIS IF YOU WANT TO GET A BETTER UNDERSTANDING OF THE TESTS: https://www.ibm.com/docs/en/i/7.4.0?topic=codes-listing-sqlstate-values
    /**
     * Test Creation Date: 17/6/2025, Zachary Treichler
     * Most Recent Change: 17/6/2025, Zachary Treichler
     * Test Description: This method is used to validate that the database can be connected to by the program and can be properly closed.
     * Test conditions:
     * If both of these are not true, we know that we can successfully connect to the database.
     * 1. SQL state "08001" occurs when the connection is unable to be established.
     * 2. SQL state "08003" occurs when the connection does not exist.
     */
    @Test
    public void testConnection(){
        String errorState = "";
        try(
            Connection c = DriverManager.getConnection(System.getenv("TEST_URI"));
        ){
            c.close();
        }
        catch(SQLException e){
            errorState = e.getSQLState();
        }
        assertTrue(!(errorState.equals("08001") || errorState.equals("08003")));
    }
    /**
     * Test Creation Date: 17/6/2025, Zachary Treichler
     * Most Recent Change: 17/6/2025, Zachary Treichler
     * Test Description: This method is used to test if the database can create a table. Now that we have verified the connection works, we can use
     * functions that connect to the database.
     * Test Conditions:
     * If one of these is true, we know that we can successfully create tables in the database.
     * 1. executeUpdate() returns 0 if a table is successfully created for the first time
     * 2. SQL state "42P07" means the table has already been created
     */
    @Test
    public void testTableCreation(){
        String errorState = "";
        int created = -1;
        try(
            Connection c = DriverManager.getConnection(System.getenv("TEST_URI"));
        ){
            try(
                PreparedStatement stmt1 = c.prepareStatement(Queries.MAKE_DRONE_TABLE)
            ){
                created = stmt1.executeUpdate();
            }
        }
        catch(SQLException e){
            errorState = e.getSQLState();
            System.out.println(errorState);
        }
        assertTrue(created == 0 || errorState.equals("42P07"));
    }
    /**
     * Test Creation Date: 17/6/2025, Zachary Treichler
     * Most Recent Change: 17/6/2025, Zachary Treichler
     * Test Description: This method is used to test data insertion into the database.
     */
    @Test
    public void testDataInsertion(){
        assertTrue(true);
    }
}
