package com.springvuegradle.Hakinakina.acceptance_tests.steps;

import com.springvuegradle.Hakinakina.util.DatabaseConnection;
import io.cucumber.java.en.Given;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SharedSteps {
    @Given("I have a connection to the database")
    public void i_have_a_connection_to_the_database() throws SQLException {
        assertNotNull(DatabaseConnection.getInstance().getConnection());
    }

    @Given("there is no data already existing in the database")
    public void there_is_no_data_already_existing_in_the_database() throws SQLException {
        DatabaseConnection.getInstance().clearDatabase();
    }
}
