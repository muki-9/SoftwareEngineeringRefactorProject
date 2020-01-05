package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JshTest {
    
    public JshTest() {
    }
    
    PipedInputStream input;
    PipedOutputStream out;
    Jsh testJsh;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException{

        input = new PipedInputStream();
        out = new PipedOutputStream(input);
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

    }

    @After

    public void tear(){

        System.setIn(System.in);
        File delete  = new File("result.txt");
        delete.delete();

    }

    @Test

    public void jshShouldThrowExceptionIfMoreThan2ArgsGiven() throws IOException {
        // ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        // PrintStream originalOut = System.out;
        // System.setOut(new PrintStream(outContent));
   
        String[] args=  {"-c", "echo", "foo"};
        
        assertThatThrownBy(() -> {
            Jsh.main(args);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("jsh: wrong number of arguments");

    }

    @Test

    public void jshShouldThrowExceptionIf1stArgNotCorrect() throws IOException {
        // ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        // PrintStream originalOut = System.out;
        // System.setOut(new PrintStream(outContent));
   
        String[] args=  {"-l", "echo"};
        
        assertThatThrownBy(() -> {
            Jsh.main(args);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("jsh: -l: unexpected argument");

    }
    @Test

    public void jshShouldNotThrowErrorIf2ArgsAndCorrectFormat(){

        String[] args=  {"-c", "pwd"};

        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException();

    }

    @Test
    public void testJshDirCorrectWithSymbol() throws IOException {

        Jsh newshell = new Jsh(false);

        String input = "\r";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        newshell.takesInput();

        assertThat(outContent.toString()).isEqualTo(Jsh.getHomeDirectory()+"> ");

    }

    @Test
    public void testJshDirCorrectWithSpaces() throws IOException {

        testJsh = new Jsh(false);

        String input = "  ";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

      
        testJsh.takesInput();
        assertThat(outContent.toString()).isEqualTo(Jsh.getHomeDirectory()+"> ");
    
        // scn.close();

    }

    @Test

    public void testJshShouldAllowCarriageReturnAsInput(){

        testJsh = new Jsh(false);

        String input = "pwd";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException();
    }

    @Test

    public void testJshWithPipe(){

        testJsh = new Jsh(false);

        String input = "grep a test2.txt | cat ";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException();
    }

    @Test

    public void testJshWithSeqAndPipe(){

        testJsh = new Jsh(false);

        String input = "grep a file.txt | cat ; echo foo | cat";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException(); //get output??
    }

    @Test

    public void testJshWithRedirectionGT(){

        testJsh = new Jsh(false);

        String input = "echo foo > file.txt";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException(); 


    }

    @Test

    public void testJshWithRedirectionLT(){
        testJsh = new Jsh(false);

        String input = "grep class < pom.xml > result.txt";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException(); 


    }

    @Test

    public void testJshWithRedirectionCreatesFile(){
        testJsh = new Jsh(false);

        String input = "> result.txt";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException(); 


    }


    @Test

    public void testJshFromMain(){
        String[] args = {};

        String input = "pwd";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException();
    }

    @Test

    public void jshShouldThrowExceptionIfCannotReadUserInput(){
        testJsh = new Jsh(false);

        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        testJsh.takesInput();

        assertThat(outContent.toString()).isEqualTo(Jsh.getHomeDirectory()+"> jsh: No line found\n");
    }
}