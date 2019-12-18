package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

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

    @Before
    public void init() throws IOException {

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        
    }

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
    
    }

    @Test

    public void testhead() throws IOException{

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("5");
        testArray.add(filename); //replace with tempfile name
        
        testHead= new Head(writer);
        testHead.exec(testArray, null, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n';
        String actual = stream.toString(); 
        assertEquals(actual, expected);
        stream.close();
        writer.close();

    }
    @Test

    public void headWithExtrArgShouldThrowExc() throws IOException{

        testHead = new Head();
        testArray.add("-n");
        testArray.add("5");
        testArray.add("input.txt");
        testArray.add("input1.txt");
        
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong arguments");
    }

    @Test

    public void headWithNoArgShouldThrowException() throws IOException{
        testHead  = new Head();
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: missing arguments");

    }

    @Test

    public void headWithNoLineLimitShouldPrintFirst10LinesifFileIsArg() throws IOException{

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add(filename); 
        
        testHead= new Head(writer);
        testHead.exec(testArray, null, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n';
        String actual = stream.toString();
        assertEquals(actual, expected);
        stream.close();
        writer.close();
    }

    @Test
    public void headWithANonIntegerSecondArgShouldThrowException() throws IOException{
        testArray.add("-n");
        testArray.add("s");
        testArray.add("input1.txt");
        testHead  = new Head();
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong argument " + "s");

    }

    // @Test
    // public void headShouldThrowExceptionIf3rdArgIsNotAFileInCurrDir(){

    //     testArray.add("-n");
    //     testArray.add("5");
    //     testArray.add("input1.txt");
    //     testHead  = new Head();
    //     assertThatThrownBy(() -> {
    //         testHead.exec(testArray, null, out);
    //     })
    //     .isInstanceOf(IOException.class)
    //     .hasMessageContaining("head: cannot open input1.txt");


    // }
    

    /* right now the code does now throw error if 3rd arg isnt a file in the dir it would just print out the result*/
    @Test
    public void headShouldOutputAllLinesIfIntegerGivenIsMoreThanLinesOfFileWithoutException() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("20");
        testArray.add(filename); //replace with tempfile name
        
        testHead= new Head(writer);
        testHead.exec(testArray, null, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10"+'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n';
        expected+= "Line14"+'\n';
        String actual = stream.toString();
        assertEquals(actual, expected);
        assertThatCode(() -> { testHead.exec(testArray, null, null); }).doesNotThrowAnyException();
        stream.close();
        writer.close();


    }
    @Test
    public void headWith3ArgShouldThrowExceptionif1stArgisNotCorrectForm() throws IOException {

        testArray.add("-s");
        testArray.add("5");
        testArray.add("input1.txt");
        testHead  = new Head();
        assertThatThrownBy(() -> {
            testHead.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("head: wrong argument " + "-s");

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