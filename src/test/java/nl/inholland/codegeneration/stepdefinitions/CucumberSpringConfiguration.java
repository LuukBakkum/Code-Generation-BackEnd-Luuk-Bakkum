package nl.inholland.codegeneration.stepdefinitions;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.inholland.codegeneration.CodeGenerationApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = CodeGenerationApplication.class)
public class CucumberSpringConfiguration { }