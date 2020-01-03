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
import java.util.ArrayList;

public class TailTest{

    public TailTest(){
    }

    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Tail testTail;

    @Before
    public void init() throws IOException {
        
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
    }

    @Test

    public void tailWithInputShouldOutput() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testTail= new Tail(writer);

        testArray.add("-n");
        testArray.add("2");
        testTail.exec(testArray, inputStream, null, null);
        String actual1 = stream.toString();
        assertThat(actual1).isEqualTo("2nd line!\nabsent\n");

        writer.close();
        stream.close();

    }

    @Test

    public void tailWithInputNoArgsShouldOutputAll() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testTail= new Tail(writer);
        testTail.exec(testArray, inputStream, null, null);
        String actual1 = stream.toString();
        assertThat(actual1).isEqualTo("test line absolute\n2nd line!\nabsent\n");

        writer.close();
        stream.close();

    }

    @Test

    public void testTail() throws IOException{

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("5");
        testArray.add(filename); //replace with tempfile name
        
        testTail= new Tail(writer);
        testTail.exec(testArray, null, null, null);

        String expected = "Line10" +'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n'+"Line14"+'\n';
        String actual = stream.toString();
        assertEquals(actual, expected);
        stream.close();
        writer.close();

    }
    @Test

    public void tailWithExtrArgShouldThrowExc() throws IOException{

        testTail= new Tail();
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

    // @Test
//should work so edit code to allow it
    // public void tailWithExtrArgShouldThrowExcWithInput() throws IOException{

    //     String originalString = "test line absolute\n2nd line!\nabsent"; 
    //     InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

    //     testTail= new Tail();
    //     testArray.add("-n");
    //     testArray.add("5");
    //     testArray.add("test2.txt");
        
    //     assertThatThrownBy(() -> {
    //         testTail.exec(testArray, inputStream, out);
    //     })
    //     .isInstanceOf(RuntimeException.class)
    //     .hasMessageContaining("tail: wrong arguments");
    // }

    @Test

    public void tailWithNoArgShouldThrowException() throws IOException{
        testTail  = new Tail();
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: missing arguments");

    }

    @Test

    public void tailWithNoLineLimitShouldPrintLast10LinesifFileIsArg() throws IOException{

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add(filename); 
        
        testTail= new Tail(writer);
        testTail.exec(testArray, null, null, null);

        String expected = "Line5" +'\n'+"Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10" +'\n';
        expected+="Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n'+"Line14"+'\n';
        String actual = stream.toString();
        assertEquals(actual, expected);
        stream.close();
        writer.close();
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
        .hasMessageContaining("tail: wrong argument " + "s");

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
    

    // /* right now the code does now throw error if 3rd arg isnt a file in the dir it would just print out the result*/
    @Test
    public void tailShouldOutputAllLinesIfIntegerGivenIsMoreThanLinesOfFileWithoutException() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        String filename = createTempFile();

        testArray.add("-n");
        testArray.add("20");
        testArray.add(filename); //replace with tempfile name
        
        testTail= new Tail(writer);
        testTail.exec(testArray, null, null, null);

        String expected = "Line0" +'\n'+"Line1"+'\n'+"Line2"+'\n'+"Line3"+'\n'+"Line4"+'\n'+"Line5" +'\n';
        expected+="Line6"+'\n'+"Line7"+'\n'+"Line8"+'\n'+"Line9"+'\n'+"Line10"+'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n';
        expected+= "Line14"+'\n';
        String actual = stream.toString();
        assertEquals(actual, expected);
        assertThatCode(() -> { testTail.exec(testArray, null, null, null); }).doesNotThrowAnyException();
        stream.close();
        writer.close();


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
        .hasMessageContaining("tail: wrong argument " + "-s");

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