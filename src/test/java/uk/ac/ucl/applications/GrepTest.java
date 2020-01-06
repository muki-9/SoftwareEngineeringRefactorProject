package uk.ac.ucl.applications;

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


public class GrepTest{

    public GrepTest() {
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

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder();

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
        testGrep = null;
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
        .hasMessageContaining("grep: cannot open file");

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

    @Test
    public void testGrepWithMoreArgsShouldOutputCorrectContent() throws IOException {
        File tmp1 = folder.newFile();
        File tmp2 = folder.newFile();
        writeToFile(tmp1);
        writeToFile(tmp2);

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        
        testArray.add("ab");
        testArray.add(tmp1.getName());
        testArray.add(tmp2.getName());

        testGrep.exec(testArray, null, System.out, null);
        String actual = outContent.toString();
        String exp  = "absent this should print on"+tmp1.getName()+'\n'+"absent this should print on"+tmp2.getName()+'\n';

        assertEquals(actual, exp);
        
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

    // @Test
    // public void testGrepWith2Args() throws IOException {
    //     File file1 = createTempFile();
    //     File file2 = createTempFile();

    //     testArray.add("ab");
    //     testArray.add(file1.getName());
    //     testArray.add(file2.getName());

    //     testGrep.exec(testArray, null, System.out, null);
    //     String exp  = "absent this should print on"+file1.getName()+'\n'+"absent this should print on"+file2.getName()+'\n';
    //     assertEquals(outContent.toString(), exp);
    // }

    // private File createTempFile() throws IOException {
    //     File temp1 = File.createTempFile("input", ".txt", new File(Jsh.getHomeDirectory()));
    //     temp1.deleteOnExit();
    //     writeToFile(temp1);
    //     return temp1;
    // }

    private void writeToFile(File filename) throws IOException{
        PrintWriter out1 = new PrintWriter(filename);
        out1.print("absent this should print on" + filename.getName());
        out1.print(System.getProperty("line.separator"));
        out1.print("nothing on this line" + filename);
        out1.flush();
        out1.close();
    }
}