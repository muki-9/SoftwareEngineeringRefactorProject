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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Sed;
import uk.ac.ucl.jsh.Jsh;

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
        Jsh.setCurrentDirectory(folder.getRoot().toString());

    }
    @After

    public void tear(){

        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    
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
        
        File tmp = folder.newFile();
        writeNewStringToFile(tmp);

        testArray.add("s/e/E/g");
        testArray.add(tmp.getName());

        testSed.exec(testArray, null, System.out, null);
        assertThat(outContent.toString()).isEqualTo("REpEat0\nREpEat1\nREpEat2\n");

    }



    @Test

    public void anySymbolCanBeUsedAsDelimeterShouldNotThrowExceptionUnlessInArgs() throws IOException {
        File tmp1 = folder.newFile();

        testArray.add("s$a$b$");

        testArray.add(tmp1.getName());

        assertThatCode(() ->{
            testSed.exec(testArray, null, out, null);
        }).doesNotThrowAnyException();
    }

    @Test

    public void sedWithoutG() throws IOException {

        testArray.add("s/e/E/");
        File tmp1 = folder.newFile();
        writeNewStringToFile(tmp1);
        testArray.add(tmp1.getName());
        
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
        File tmp1 = folder.newFile();

        testArray.add("s/a/b/");
        testArray.add(tmp1.getName());

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

        File unreadable = folder.newFile();
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
    public void sedThrowsExceptionIfDir() throws IOException {

        File tmp = folder.newFolder();

        testArray.add("s/a/b/");
        testArray.add(tmp.getName());

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

    private void writeNewStringToFile(File filename) throws IOException{

        PrintWriter out1 = new PrintWriter(filename);
        for(int i =0; i<3; i++){
            out1.write("Repeat"+ i);
            out1.write(System.getProperty("line.separator"));
        }
        out1.flush();
        out1.close();
    }
}