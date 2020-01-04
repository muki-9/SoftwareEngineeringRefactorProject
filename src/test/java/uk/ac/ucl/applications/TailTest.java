package uk.ac.ucl.applications;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Tail;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
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

public class TailTest{

    public TailTest(){
    }

    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Tail testTail;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException {
        
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        testTail = new Tail();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test

    public void tailWithInputShouldOutputLast2() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("-n");
        testArray.add("2");
        testTail.exec(testArray, inputStream, System.out, null);
        String actual1 = outContent.toString();
        assertThat(actual1).isEqualTo("2nd line!\nabsent\n");
    }

    @Test

    public void tailWithInputNoArgsShouldOutputAll() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testTail.exec(testArray, inputStream, System.out, null);
        String actual1 = outContent.toString();
        assertThat(actual1).isEqualTo("test line absolute\n2nd line!\nabsent\n");


    }

    @Test

    public void testTailWithoutInputSWithOption () throws IOException{

        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("5");
        testArray.add(filename); //replace with tempfile name
        
        testTail.exec(testArray, null, System.out, null);

        String expected = "Line10" +'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n'+"Line14"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);

    }

    @Test

    public void ifArgSizeIs2AndInputNullShouldThrowExc(){

        testArray.add("-n");
        testArray.add("5");
    
        
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");


    }
    @Test

    public void tailWithExtrArgShouldThrowExc() throws IOException{

        testArray.add("-n");
        testArray.add("5");
        testArray.add("input.txt");
        testArray.add("input1.txt");
        
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");
    }

    @Test
//should work so edit code to allow it
    public void tailWithExtrArgShouldThrowExcWithInput() throws IOException{

        String originalString = "test line absolute\n2nd line!\nabsent"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("-n");
        testArray.add("5");
        testArray.add("test2.txt");

        
        assertThatThrownBy(() -> {
            testTail.exec(testArray, inputStream, out,null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");
    }

    @Test

    public void tailWithNoArgShouldThrowException() throws IOException{
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");

    }

    @Test

    public void tailWithNoLineLimitShouldPrintLast10LinesifFileIsArg() throws IOException{

        String filename = createTempFile();

        testArray.add(filename); 
        testTail.exec(testArray, null, System.out, null);

        String expected = "Line5" +'\n'+"Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10" +'\n';
        expected+="Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n'+"Line14"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);

    }

    @Test
    public void tailWithANonIntegerSecondArgShouldThrowException() throws IOException{
        testArray.add("-n");
        testArray.add("s");
        testArray.add("input1.txt");
        testTail  = new Tail();
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong argument: " + "s");

    }

    @Test
    public void tailShouldThrowExceptionIf3rdArgIsNotAFileInCurrDir(){

        testArray.add("-n");
        testArray.add("5");
        testArray.add("input1.txt");
        testTail  = new Tail();
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: input1.txt does not exist");


    }
    
    @Test
    public void tailShouldOutputAllLinesIfIntegerGivenIsMoreThanLinesOfFileWithoutException() throws IOException {

        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("20");
        testArray.add(filename); //replace with tempfile name
        
        testTail.exec(testArray, null, System.out, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10"+'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n';
        expected+= "Line14"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);
        assertThatCode(() -> { testTail.exec(testArray, null, System.out, null); }).doesNotThrowAnyException();



    }
    @Test
    public void tailWith3ArgShouldThrowExceptionif1stArgisNotCorrectForm() throws IOException {

        testArray.add("-s");
        testArray.add("5");
        testArray.add("input1.txt");
        testTail  = new Tail();
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong argument: " + "-s");

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