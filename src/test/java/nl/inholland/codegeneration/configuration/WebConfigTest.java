package nl.inholland.codegeneration.configuration;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfigTest {

    @Mock
    CorsRegistry corsRegistry;

    @Mock
    CorsRegistration corsRegistration;

    @Test
    void testCorsConfigurer() {

      // corsRegistry = mock(CorsRegistry.class);
       corsRegistration = mock(CorsRegistration.class);

        // Create an instance of WebConfig
        WebConfig webConfig = new WebConfig();

        when(corsRegistration.allowedOrigins("http://localhost:5173")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("Authorization", "Cache-Control", "Content-Type")).thenReturn(corsRegistration);
        when(corsRegistration.exposedHeaders("Authorization", "Cache-Control", "Content-Type")).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(true)).thenReturn(corsRegistration);

        // Create a mock CorsRegistry
        CorsRegistry corsRegistry = mock(CorsRegistry.class);
        
        // Mock the behavior of the CorsRegistry method
        when(corsRegistry.addMapping("/**")).thenReturn(corsRegistration);

        // Call the corsConfigurer() method
        WebMvcConfigurer configurer = webConfig.corsConfigurer();

        // Invoke the addCorsMappings method on the mock CorsRegistry
        configurer.addCorsMappings(corsRegistry);

        // Verify that the CorsRegistration methods were called with the expected arguments
        verify(corsRegistration).allowedOrigins("http://localhost:5173");
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(corsRegistration).allowedHeaders("Authorization", "Cache-Control", "Content-Type");
        verify(corsRegistration).exposedHeaders("Authorization", "Cache-Control", "Content-Type");
        verify(corsRegistration).allowCredentials(true);
    }
}
