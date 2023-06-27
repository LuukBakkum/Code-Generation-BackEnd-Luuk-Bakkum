package nl.inholland.codegeneration.services;

import java.math.BigInteger;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

// IBAN Generator class based on: https://en.wikipedia.org/wiki/International_Bank_Account_Number#Algorithms
@Component
public class IBANGenerator implements IdentifierGenerator {
    private static String meinBankIBAN;
    private static final Random RND = new Random(94837L); // Possibly replace with seed from e.g. config file
    private final int RND_ORIGIN = 2; // 0 is an invalid IBAN account number and 1 is reserved for the bank
    private final int RND_BOUND = 1000000000; // Makes sure IBAN account numbers won't have more than 9 characters
    private final String COUNTRY_CODE = "NL"; // Country code of the IBAN provided in the project guide
    private final String BANK_CODE = "INHO0"; // Trails with zero based on IBAN standard provided in project guide
    private final int IBAN_LENGTH = 18; // (Country code) + (Check digits) + (Bank code) + (Account number) = 18
    private final int MODULO_OPERATOR = 97; // Standard modulo operator used in IBAN calculation algorithms

    // Generate a random IBAN
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String IBAN;
        do {
            int randomAccountNr = RND.nextInt(RND_ORIGIN, RND_BOUND);
            // First IBAN always has account number 1
            if (meinBankIBAN == null) {
                randomAccountNr = 1;
            }
            String accountNumber = String.format("%09d", randomAccountNr);
            IBAN = COUNTRY_CODE + "00" + BANK_CODE + accountNumber;
            int checkDigits = calculateCheckDigits(IBAN);
            IBAN = IBAN.substring(0, COUNTRY_CODE.length()) + String.format("%02d", checkDigits) + IBAN.substring(2 + COUNTRY_CODE.length());
        } while (!this.validateIBAN(IBAN));
        // After first IBAN is generated set it to the meinBankIBAN for future reference
        if (meinBankIBAN == null) {
            meinBankIBAN = IBAN;
        }
        return IBAN;
    }

    // Calculate the check digits for a given IBAN
    private int calculateCheckDigits(String IBAN) throws IdentifierGenerationException {
        return Math.abs(this.calculateMod97(IBAN) - (MODULO_OPERATOR + 1));
    }

    // Convert an IBAN into a remainder int using the method provided in https://en.wikipedia.org/wiki/International_Bank_Account_Number#Algorithms
    int calculateMod97(String IBAN) throws IdentifierGenerationException {
        if (IBAN.length() != IBAN_LENGTH) {
            throw new IdentifierGenerationException("Something went wrong while generating a new IBAN");
        }

        IBAN = IBAN.substring(4) + IBAN.substring(0, 4);
        for(int i = 0; i < IBAN.length(); i++) {
            if (!Character.isDigit(IBAN.charAt(i))) {
                IBAN = IBAN.substring(0, i) + Character.getNumericValue(IBAN.charAt(i)) + IBAN.substring(i + 1);
            }
        }

        BigInteger IBANInteger = new BigInteger(IBAN);
        return IBANInteger.mod(BigInteger.valueOf(MODULO_OPERATOR)).intValue();
    }

    // Validate an IBAN by recalculating the check digits and using the mod-97 method to check if the remainder is 1.
    // If the remainder is 1, the IBAN is valid. More information is provided at https://en.wikipedia.org/wiki/International_Bank_Account_Number#Algorithms
    public boolean validateIBAN(String IBAN) throws HibernateException {
        return calculateMod97(IBAN) == 1;
    }

    // used for future references to the meinBankIBAN
    public static String getMeinBankIBAN() {
        return meinBankIBAN;
    }

    // used for PreAuthorize since it can't access static members
    public String getNonStaticMeinBankIBAN() {
        return meinBankIBAN;
    }
}
