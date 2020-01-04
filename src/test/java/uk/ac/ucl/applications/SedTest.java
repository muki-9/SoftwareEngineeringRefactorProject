package uk.ac.ucl.applications;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Sed;
import static org.assertj.core.api.Assertions.assertThat;


public class SedTest{
    public SedTest(){

    }
    PipedInputStream in;
    PipedOutputStream out;
    Sed testSed;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;
    @Before
    public void init() throws IOException {

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testSed = new Sed();
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

    }
    @Test
    public void sedShouldProduceCorrectOutputwithInputStream() throws IOException {
        
        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        testArray.add("s/t/b/");

        testSed.exec(testArray, inputStream, System.out, null);

        assertThat(outContent.toString()).isEqualTo("best line absolute\n2nd line!\nabsenb\n");


    }
    @Test
    public void sedShouldProduceCorrectOutputwithInputAndG() throws IOException {
        
        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        testArray.add("s/t/b/g");

        testSed.exec(testArray, inputStream, System.out, null);
        assertThat(outContent.toString()).isEqualTo("besb line absolube\n2nd line!\nabsenb\n");

    }

    @Test
    public void sedShouldProduceCorrectOutputwithNoInputAndG() throws IOException {
        
        String tmp = createTempFile();

        testArray.add("s/e/E/g");
        testArray.add(tmp);

        testSed.exec(testArray, null, System.out, null);
        assertThat(outContent.toString()).isEqualTo("REpEat0\nREpEat1\nREpEat2\n");

    }



    @Test

    public void anySymbolCanBeUsedAsDelimeterShouldNotThrowExceptionUnlessInArgs() throws IOException {
        String tmp1 = createTempFile();
        testArray.add("s$a$b$");

        testArray.add(tmp1);

        assertThatCode(() ->{
            testSed.exec(testArray, null, out, null);
        }).doesNotThrowAnyException();
    }

    @Test

    public void sedWithoutG() throws IOException {

        testArray.add("s/e/E/");
        String tmp1 = createTempFile();
        testArray.add(tmp1);
        
        testSed.exec(testArray, null, System.out, null);

        assertThat(outContent.toString()).isEqualTo("REpeat0\nREpeat1\nREpeat2\n");


    }


    @Test
    public void ifSedArgsis1AndInputNotNullThenNoException(){

        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testArray.add("s/a/b/");

        assertThatCode(() -> {
            testSed.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();
    }

    @Test
    public void ifInputArgIs2AndInputIsNotNullNoException() throws IOException {
        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        String tmp1 = createTempFile();

        testArray.add("s/a/b/");
        testArray.add(tmp1);

        assertThatCode(() -> {
            testSed.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();

    }

    @Test
    public void ifSedArgs1OrMoreAndInputNullThenThrowException(){

        testArray.add("s/a/b/");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong arguments");
    }

    @Test

    public void ifNoArgsGivenThenThrowException(){
        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong arguments");

    }
    @Test

    public void ifMoreThan2ArgsGivenThenThrowException(){

        testArray.add("s/a/b/g");
        testArray.add("file.txt");
        testArray.add("random.txt");
        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong arguments");

    }



    @Test
    public void ifRegexEndsWithoutDelimiter() throws IOException {
        testArray.add("s/a/b");
        testArray.add("filename");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong number of delimiters");
    }

    @Test
    public void ifRegexEndsWithoutG() throws IOException {
        testArray.add("s/a/b/a");
        testArray.add("filename");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: last char should be delimiter or g");
    }

    @Test
    public void checkRegexInCorrectForm(){

        testArray.add("c/a/b/");
        testArray.add("test.txt");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: regex in incorrect form");

    }

    @Test
    public void sedThrowsExceptionIfFileUnreadable() throws IOException {

        File unreadable = new File(createTempFile());
        unreadable.setReadable(false);
        testArray.add("s/a/b/");
        testArray.add(unreadable.getName());
        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: cannot open " + unreadable.getName());
        
    }

    @Test
    public void sedThrowsExceptionIfDir(){

        testArray.add("s/a/b/");
        testArray.add("src");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: wrong file argument");
    

    }
    @Test
    public void sedThrowsExceptionIfWrongFile(){

        testArray.add("s/a/b/");
        testArray.add("index.txt");

        assertThatThrownBy(() -> {
            testSed.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("sed: cannot open index.txt");
    

    }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        writeNewStringToFile(temp1.getName());
        return temp1.getName();
    }
    private void writeNewStringToFile(String filename) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for(int i=0; i<3; i++){
            bw.write("Repeat"+ i);
            bw.write(System.getProperty("line.separator"));
        }
        bw.close();
    }
}