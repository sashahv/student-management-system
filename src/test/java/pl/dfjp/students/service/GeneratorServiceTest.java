package pl.dfjp.students.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GeneratorServiceTest {
    @Autowired
    private GeneratorService generatorService;

    @Test
    void should_check_if_given_zip_code_is_equals_to_generation_method(){
        String zipCode = "12234";

        String generatedZipCod = generatorService.generateZipCode(zipCode);

        assertEquals(generatedZipCod, zipCode);
    }

    @Test
    void should_return_true_if_given_zip_code_matches_pattern(){
        String zipCodePattern = "^$|^[0-9]{2}[- ][0-9]{3}|[0-9]{5}$";

        String zipCode = "20070";

        Pattern pattern = Pattern.compile(zipCodePattern);
        Matcher matcher = pattern.matcher(zipCode);

        assertTrue(matcher.matches());
    }

    @Test
    void should_return_true_if_generated_zip_code_matches_pattern(){
        String zipCodePattern = "^$|^[0-9]{2}[- ][0-9]{3}|[0-9]{5}$";

        String zipCode = "20 070";

        String generatedZipCod = generatorService.generateZipCode(zipCode);

        Pattern pattern = Pattern.compile(zipCodePattern);
        Matcher matcher = pattern.matcher(generatedZipCod);

        System.out.println(generatedZipCod);
        assertTrue(matcher.matches());
    }

    @Test
    void should_return_true_if_generated_zip_code_not_matches_pattern(){
        String zipCodePattern = "^$|^[0-9]{2}[- ][0-9]{3}|[0-9]{5}$";

        String zipCode = "122345";

        String generatedZipCod = generatorService.generateZipCode(zipCode);

        Pattern pattern = Pattern.compile(zipCodePattern);
        Matcher matcher = pattern.matcher(generatedZipCod);

        assertThat(matcher.matches()).isFalse();
    }

    @Test
    void should_return_true_if_generated_phone_number_matches_pattern(){
        String phoneNumberPattern = "^(\\+48)? [0-9]{9}$|^(\\+48)? [0-9]{3} [0-9]{3} [0-9]{3}$|^(\\+48)?[0-9]{9}$";

        String phoneNumber = "123456789";

        String generatedPhoneNumber = generatorService.generatePhoneNumber(phoneNumber);

        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(generatedPhoneNumber);

        assertTrue(matcher.matches());
    }

    @Test
    void should_return_true_if_phone_number_matches_pattern(){
        String phoneNumberPattern = "^(\\+48)? [0-9]{9}$|^(\\+48)? [0-9]{3} [0-9]{3} [0-9]{3}$|^(\\+48)?[0-9]{9}$";

        String phoneNumber = "123456789";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);

        assertTrue(matcher.matches());
    }

    @Test
    void should_return_true_if_phone_number_not_matches_pattern(){
        String phoneNumberPattern = "^(\\+48)? [0-9]{9}$|^(\\+48)? [0-9]{3} [0-9]{3} [0-9]{3}$|^(\\+48)?[0-9]{9}$";

        String phoneNumber = "+481234567892";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);

        assertThat(matcher.matches()).isFalse();
    }
}