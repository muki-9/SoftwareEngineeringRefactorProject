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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Cat;
import uk.ac.ucl.jsh.Jsh;


public class CatTest{

    public CatTest(){
    }
    Cat testCat;
    ArrayList<String> testArray;
    PipedInputStream in;
    PipedOutputStream out;
    ByteArrayOutputStream outContent;
   

    @Before
    public void testShell() throws IOException{
        testArray = new ArrayList<>();
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testCat = new Cat();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        testCat = null;
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    
    }
    
    @Test
    public void testCatWithTwoInputs() throws IOException {
        File file = folder.newFile();
        writeToFile(file, "First File");
        File file1 = folder.newFile();
        writeToFile(file1, "Second File");

        testArray.add(file.getName());
        testArray.add(file1.getName());

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        testCat.exec(testArray, null, System.out, null);
        assertEquals(outContent.toString(), "First File\nSecond File\n");
      

     }

    @Test
    public void testCatWithNoInputs() throws IOException{
        assertThatThrownBy(()->{
            testCat.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("cat: missing arguments");

    }

    @Test

    public void testCatWithNoArgsButInputShouldOutput() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent\n"; 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        testCat.exec(testArray, inputStream, System.out, null);

        assertThat(outContent.toString()).isEqualTo(originalString);
    }

    @Test

    public void ifFileisDirShouldOutputToConsoleAndNoExceptionThrown() throws IOException {

        testArray.add("src");
        testCat.exec(testArray, null, System.out, null);

        String expected = "cat: src is a directory";
        assertEquals(outContent.toString(),expected);


    }

    @Test
    public void catWithNonFileArgShouldThrowException() throws IOException{

        testArray.add("notafile.txt");
        assertThatThrownBy(() -> {
            testCat.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("cat: file does not exist");
        
    }

    private void writeToFile(File file, String content) throws IOException{
        PrintWriter out1 = new PrintWriter(file);
        out1.print(content);
        out1.flush();
        out1.close();
    }
}