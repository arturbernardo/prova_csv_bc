package br.com.sicredi.sincronizacao.handler;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class CSVHandlerTest {

    @Autowired
    CSVHandler csvHandler = new CSVHandler();

    @Test
    public void testValidationEmptyLine() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{});
        Assert.isTrue(!validateLine.success(), "Empty line must be invalid");
    }

    @Test
    public void testValidationMissingAgencia() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"", "321", "100"});
        Assert.isTrue(!validateLine.success(), "Missing first column should invalidate the process");
    }

    @Test
    public void testValidationMissingConta() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"123", "", "100"});
        Assert.isTrue(!validateLine.success(), "Missing second column should invalidate the process");
    }

    @Test
    public void testValidationMissingValor() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"123", "321", ""});
        Assert.isTrue(!validateLine.success(), "Missing third column should invalidate the process");
    }

    @Test
    public void testValidationNotNumericalValue() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"123", "321", "ABC"});
        Assert.isTrue(!validateLine.success(), "Third column should be a valid number");
    }

    @Test
    public void testValidationNumericalValueDecimalSeparator() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"123", "321", "10,23"});
        Assert.isTrue(!validateLine.success(), "Third column should only accept numbers and dots.");
    }

    @Test
    public void testValidation() {
        ValidateLine validateLine = csvHandler.validateInput(new String[]{"123", "321", "10.23"});
        Assert.isTrue(validateLine.success(), "All values must be accepted as a valid input.");
    }

    @Test public void testBuildLineSuccess() {
        String output = csvHandler.buildLine(new String[]{"a", "b", "1"}, true);
        Assert.isTrue(output.equals("a,b,1,SUCCESS,\n"), "Output must be: a,b,1,SUCCESS,\n");
    }

    @Test public void testBuildLineWithoutNewLine() {
        String output = csvHandler.buildLine(new String[]{"a", "b", "1"}, true);
        Assert.isTrue(!output.equals("a,b,1,SUCCESS,"), "Output must have \n");
    }

    @Test public void testBuildLineError() {
        String output = csvHandler.buildLine(new String[]{"a", "b", "1"}, false);
        Assert.isTrue(output.equals("a,b,1,ERROR,SYNC\n"), "Output must be a,b,1,ERROR,SYNC\n");
    }

    @Test public void testBuildLineValidationError() {
        ValidateLine validateLine = new ValidateLine(false, Arrays.asList("ALGUM ERRO", "OUTRO ERRO"));
        String output = csvHandler.buildLineValidationError(new String[]{"a", "b", "1"}, validateLine);
        Assert.isTrue(output.equals("a,b,1,ERROR,VALIDATION: ALGUM ERRO|OUTRO ERRO\n"), "Output must be a,b,1,ERROR,VALIDATION: ALGUM ERRO|OUTRO ERRO\n");
    }
}
