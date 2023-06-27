package nl.inholland.codegeneration.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import nl.inholland.codegeneration.services.JwtService;

@TestConfiguration
public class apiTestConfiguration {

    @MockBean
    private JwtService jwtService;
}
