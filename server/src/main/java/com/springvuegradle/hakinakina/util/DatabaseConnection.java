package com.springvuegradle.hakinakina.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String H2_DATABASE_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private Connection connection;

    // Static attribute to make singleton, lazy initialization, so leave null until needed
    private static DatabaseConnection instance = null;

    /**
     * Private constructor to disallow creating other instances
     * @throws SQLException Thrown if creation or connection to database fails
     */
    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(H2_DATABASE_URL, "team300", "");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS User (" +
                        "   user_id         Long PRIMARY KEY AUTO_INCREMENT, " +
                        "   firstname      VARCHAR(MAX) NOT NULL, " +
                        "   lastname      VARCHAR(MAX) NOT NULL," +
                        "   middlename      VARCHAR(MAX)," +
                        "   gender      enum('MAlE', 'FEMALE', 'NON-BINARY') NOT NULL, " +
                        "   password      VARCHAR(MAX) NOT NULL, " +
                        "   bio      VARCHAR(MAX), " +
                        "   nickname      VARCHAR(MAX), " +
                        "   birthdate      Date NOT NULL, " +
                        "   fitnesslevel      INT NOT NULL, " +
                        "   salt       VARCHAR(MAX) NOT NULL, " +
                        "   primaryemail       VARCHAR(MAX) NOT NULL," +
                        "   permissionlevel   INTEGER NOT NULL" +
                        ");"
        ).execute();
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Activity (" +
                        "id             Long PRIMARY KEY AUTO_INCREMENT," +
                        " name           VARCHAR(MAX) NOT NULL," +
                        "     description           VARCHAR(MAX) NOT NULL," +
                        "continuous           boolean NOT NULL," +
                        "starttime      Date NOT NULL," +
                        "endtime      Date," +
                        "location           VARCHAR(MAX) NOT NULL" +
                        ");"
        ).execute();
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ActivityType ( " +
                        " name           VARCHAR(MAX) PRIMARY KEY NOT NULL" +
                        ");"
        ).execute();
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Email (" +
                        " emailid             Long PRIMARY KEY AUTO_INCREMENT," +
                        "email           VARCHAR(MAX) NOT NULL" +
                        ");"
        ).execute();
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS PassportCountry (" +
                        "countryId             VARCHAR(MAX) PRIMARY KEY," +
                        "name           VARCHAR(MAX) NOT NULL" +
                        ");"
        ).execute();
        connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS Session (" +
                        " token             VARCHAR(MAX) PRIMARY KEY" +
                        ");"
        ).execute();
    }

    /**
     * Static function to make a new / get the existing instance
     * @return Singleton instance of DatabaseConnection class
     * @throws SQLException Thrown if unable to create a connection to the database
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Resets the database back to an 'empty' state, where tables exist but are empty
     */
    public void clearDatabase() throws SQLException {
        String[] sqlStatements = new String[] {
                "DELETE FROM User;",
                "DELETE FROM Activity;",
                "DELETE FROM ActivityType;",
                "DELETE FROM Email;",
                "DELETE FROM PassportCountry;",
                "DELETE FROM Session;"
        };
        Statement statement = getInstance().getConnection().createStatement();
        for (String sql : sqlStatements) {
            statement.addBatch(sql);
        }
        statement.executeBatch();
    }

}
