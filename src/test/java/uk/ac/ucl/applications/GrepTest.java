package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Cat;
import uk.ac.ucl.applications.Grep;


public class GrepTest{

    private boolean test = false;

    public GrepTest(){
    }

    PipedInputStream in;
    PipedOutputStream out;
    Grep testGrep;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;
    

    @Before
    public void init() throws IOException {

        in = new PipedInputStream();
        out= new PipedOutputStream(in);
        testGrep = new Grep();
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }


    @Test
    public void testGrepShouldThrowExceptionIfWrongArgs(){

        testArray.add("aba");

        assertThatThrownBy(() -> {
            testGrep.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("grep: wrong number of arguments");

    }  
    @Test

    public void testGrepShouldExceptionIfNoArgsGiven(){

        assertThatThrownBy(() -> {
            testGrep.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("grep: wrong number of arguments");


    } 

    // @Test

    // public void grepShouldThrowErrorIffileIsntReadable() throws IOException {

    //     File unreadable = new File(createTempFile());
    //     unreadable.setReadable(false);
    //     testArray.add("aba");
    //     testArray.add(unreadable.getName());
    //     assertThatThrownBy(() -> {
    //         testGrep.exec(testArray, null, out, null);
    //     })
    //     .isInstanceOf(RuntimeException.class)
    //     .hasMessageContaining("grep: wrong file argument");

    // }

    @Test
    
    public void shouldThrowExceptionifWrongFile() throws IOException {

        testArray.add("ab");
        testArray.add("wrongfile.txt"); 
        testArray.add("test.txt");

        assertThatThrownBy(() -> {
            testGrep.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("grep: wrong file argument");

    }

    @Test
    
    public void shouldThrowExceptionifDir() throws IOException {

        testArray.add("a");
        testArray.add("src");

   
        assertThatThrownBy(() -> {
            testGrep.exec(testArray, null, out,null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("grep: wrong file argument");

    }

    @Test

    public void ifInputNotNullThenShouldOutputCorrect() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("ab");
       
        testGrep.exec(testArray, inputStream, System.out, null);
        String actual = outContent.toString();

        String expected = "test line absolute\nabsent\n";
        assertEquals(actual, expected);

    }
    // @Test
    // public void ifMoreThan2ArgsThenShouldGetAllPaths() throws IOException {

    //     testArray.add("ab");
    //     String tmp1  = createTempFile();
    //     String tmp2 = createTempFile();
    //     String tmp3 = createTempFile();
    //     testArray.add(tmp1);
    //     testArray.add(tmp2);
    //     testArray.add(tmp3);

    //     assertThatCode(() -> {
    //         testGrep.exec(testArray, null, out,null);
    //     }).doesNotThrowAnyException();

    //     Path[] pathArray = testGrep.getPathArray(testArray);
    //     assertThat(pathArray).hasSize(3);

    // }

    @Test
    public void testGrepWithMoreArgsShouldOutputCorrectContent() throws IOException {

        String tmp1 = createTempFile();
        String tmp2 = createTempFile();
        writeToFile(tmp1);
        writeToFile(tmp2);

        testArray.add("ab");
        testArray.add(tmp1);
        testArray.add(tmp2);

        testGrep.exec(testArray, null, System.out, null);
        String actual = outContent.toString();
        String exp  = "absent this should print on"+tmp1+'\n'+"absent this should print on"+tmp2+'\n';

        assertEquals(actual, exp);
        
    }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        writeToFile(temp1.getName());
        return temp1.getName();
    }
    private void writeToFile(String filename) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("absent this should print on" + filename);
        bw.write(System.getProperty("line.separator"));
        bw.write("nothing on this line" + filename);
        bw.flush();
        bw.close();
    }




}