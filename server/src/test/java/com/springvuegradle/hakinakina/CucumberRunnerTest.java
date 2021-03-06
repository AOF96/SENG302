package com.springvuegradle.hakinakina;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"}, // How to format test report, "pretty" is good for human eyes
        glue = {"com.springvuegradle.hakinakina.acceptance_tests.steps"}, // Where to look for your tests' steps
        features = {"classpath:com.springvuegradle.hakinakina.features/"}, // Where to look for your features
        strict = true // Causes cucumber to fail if any step definitions are still undefined
)
@SpringBootTest
@ContextConfiguration(classes = Main.class)
public class CucumberRunnerTest { } // Classname ends with "Test" so it will be picked up by JUnit and hence by 'gradle test'
