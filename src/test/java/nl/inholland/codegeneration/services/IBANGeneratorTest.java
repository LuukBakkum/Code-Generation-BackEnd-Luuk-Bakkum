package nl.inholland.codegeneration.services;

import nl.inholland.codegeneration.services.IBANGenerator;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IBANGeneratorTest {

    private IBANGenerator ibanGenerator;

    @BeforeEach
    public void setUp() {
        ibanGenerator = new IBANGenerator();
    }

    @Test
    public void testGenerate() {
        String iban = (String) ibanGenerator.generate(null, null);
        assertNotNull(iban, "IBAN should not be null");
        assertEquals(18, iban.length(), "IBAN length should be 18");
        assertTrue(ibanGenerator.validateIBAN(iban), "Generated IBAN should be valid");
    }

    @Test
    public void testCalculateMod97() {
        // a sample valid IBAN
        String iban = "NL06INHO0000000001"; // the inholland iban doesnt work NL01INHO0000000001
        
        int expectedRemainder = 1; // The expected remainder when the IBAN is valid.
        
        int actualRemainder = ibanGenerator.calculateMod97(iban);
        assertEquals(expectedRemainder, actualRemainder, "Modulo 97 of a valid IBAN should be 1");
    }

    @Test
    public void testCalculateMod97WithInvalidLength() {
        // a sample invalid IBAN because the length is not correct
        String invalidIban = "NL01";
        
        assertThrows(IdentifierGenerationException.class, () -> ibanGenerator.calculateMod97(invalidIban),
                "Should throw IdentifierGenerationException for an invalid IBAN length");
    }

    @Test
    public void testValidateIBAN() {
    String validIban = "NL02ABNA0123456789"; // a sample valid IBAN
    String invalidIban = "NL00INHO0123456789"; // a sample invalid IBAN

    assertTrue(ibanGenerator.validateIBAN(validIban), "Should return true for a valid IBAN");
    assertFalse(ibanGenerator.validateIBAN(invalidIban), "Should return false for an invalid IBAN");
    }

    @Test
    public void testValidateIBANWithException() {
        String invalidIban = "NL91INHO0123456789ABC"; // a sample invalid IBAN

        assertThrows(IdentifierGenerationException.class, () -> ibanGenerator.validateIBAN(invalidIban),
                "Should throw IdentifierGenerationException for an invalid IBAN");
    }
}