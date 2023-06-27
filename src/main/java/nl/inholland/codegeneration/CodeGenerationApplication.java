package nl.inholland.codegeneration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
// @CrossOrigin("http://localhost:5173/")
public class CodeGenerationApplication {

	// TODO: isDeleted constraints voor output
	public static void main(String[] args) {
		SpringApplication.run(CodeGenerationApplication.class, args);
	}
}
