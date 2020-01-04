package uk.ac.ucl.applications;

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
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.Head;


public class HeadTest {

    public HeadTest(){
    }

    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Head testHead;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        testHead = new Head();
    }

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
    
    }

    @Test

    public void testhead() throws IOException{
        String filename = createTempFile();
        testArray.add("-n");
        testArray.add("5");
        testArray.add(filename); //replace with tempfile name
        
        testHead.exec(testArray, null, System.out, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n';
        assertEquals(outContent.toString(),expected);
    }

    @Test

    public void headWithInputShouldOutput() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("-n");
        testArray.add("2");
        testHead.exec(testArray, inputStream, System.out, null);

        assertThat(outContent.toString()).isEqualTo("test line absolute\n2nd line!\n");

    }
    @Test

    public void headWithNoArgsAndInputShouldOutput() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testHead.exec(testArray, inputStream, System.out, null);

        assertThat(outContent.toString()).isEqualTo("test line absolute\n2nd line!\nabsent\n");

    }
    @Test
    public void testHeadWithNoArgsButInputShouldNotThrowException(){

        String originalString = "test line absolute\n2nd line!\nabsent"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        assertThatCode(() ->{

            testHead.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();
    }

    @Test

    public void headWithExtrArgShouldThrowExc() throws IOException{
   
        testArray.add("-n");
        testArray.add("5");
        testArray.add("input.txt");
        testArray.add("input1.txt");
        
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong arguments");
    }

    @Test

    public void headWithNoArgShouldThrowExceptionNoInput() throws IOException{
     
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong arguments");

    }
    //need to change code to allow this test

    @Test

    public void headWithInputShouldNotThrowExceptionWithMoreThan2Args() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        String tmp = createTempFile();
        testArray.add("-n");
        testArray.add("15");
        testArray.add(tmp);

        assertThatCode(() -> {
            testHead.exec(testArray, inputStream, System.out, null);
        })
        .doesNotThrowAnyException();
        String actual = outContent.toString();
        assertThat(actual).contains("Line").doesNotContain("test line absolute");
    }

    @Test

    public void headWithNoLineLimitShouldPrintFirst10LinesifFileIsArg() throws IOException{
        String filename = createTempFile();

        testArray.add(filename); 
    
        testHead.exec(testArray, null, System.out, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);
    }

    @Test
    public void headWithANonIntegerSecondArgShouldThrowException() throws IOException{
        testArray.add("-n");
        testArray.add("s");
        testArray.add("input1.txt");
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong argument: " + "s");

    }

    @Test
    public void headShouldThrowExceptionIf3rdArgIsNotAFileInCurrDir(){

        testArray.add("-n");
        testArray.add("5");
        testArray.add("input1.txt");
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: input1.txt does not exist");
    }
    

    /* right now the code does now throw error if 3rd arg isnt a file in the dir it would just print out the result*/
    @Test
    public void headShouldOutputAllLinesIfIntegerGivenIsMoreThanLinesOfFileWithoutException() throws IOException {
        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("20");
        testArray.add(filename); //replace with tempfile name
    ;
        testHead.exec(testArray, null, System.out, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10"+'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n';
        expected+= "Line14"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);
        assertThatCode(() -> { testHead.exec(testArray, null, System.out, null); }).doesNotThrowAnyException();
    }

    @Test
    public void headWith3ArgShouldThrowExceptionif1stArgisNotCorrectForm() throws IOException {
        testArray.add("-s");
        testArray.add("5");
        testArray.add("input1.txt");
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong argument: " + "-s");
    }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        writeToFile(temp1.getName());
        return temp1.getName();
    }
    private void writeToFile(String filename) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for(int i =0; i<15; i++){
            bw.write("Line"+ i);
            bw.write(System.getProperty("line.separator"));
        }
        bw.close();
    }
}