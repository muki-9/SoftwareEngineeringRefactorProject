package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.jsh.Jsh;

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
        Jsh.setCurrentDirectory(folder.getRoot().toString());
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        testTail = new Tail();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder();

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

        File filename = folder.newFile();
        writeToFile(filename);
        testArray.add("-n");
        testArray.add("5");
        testArray.add(filename.getName()); //replace with tempfile name
        
        testTail.exec(testArray, null, System.out, null);

        String expected = "Line10" +'\n'+"Line11"+'\n'+"Line12"+'\n'+"Line13"+'\n'+"Line14"+'\n';
        String actual = outContent.toString();
        assertEquals(actual, expected);

    }

    @Test

    public void tailShouldThrowExceptionIfMoreThan3Args(){
        testArray.add("-n");
        testArray.add("5");
        testArray.add("test.txt");
        testArray.add("otherfile.txt");
    
        
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");


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

    public void ifNoArgsAndInputNullShouldThrowExc(){
    
        assertThatThrownBy(() -> {
            testTail.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("tail: wrong arguments");

    }

    @Test

    public void tailWithExtrArgShouldNotThrowExceptionWithInput() throws IOException{

        String originalString = "test line absolute\n2nd line!\nabsent"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        File tmp1 = folder.newFile();
        writeToFile(tmp1);
        testArray.add("-n");
        testArray.add("5");
        testArray.add(tmp1.getName());

        
        assertThatCode(() -> {
            testTail.exec(testArray, inputStream, System.out ,null);
        })
        .doesNotThrowAnyException();
        String actual = outContent.toString();
        assertThat(actual).contains("Line").doesNotContain("test line absolute");
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

        File filename = folder.newFile();
        writeToFile(filename);
        testArray.add(filename.getName()); 
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

        File filename = folder.newFile();
        writeToFile(filename);
        testArray.add("-n");
        testArray.add("20");
        testArray.add(filename.getName()); //replace with tempfile name
        
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

    private void writeToFile(File file) throws IOException{
        PrintWriter out1 = new PrintWriter(file);
        for(int i =0; i<15; i++){
            out1.write("Line"+ i);
            out1.write(System.getProperty("line.separator"));
        }
        out1.flush();
        out1.close();
    }

}