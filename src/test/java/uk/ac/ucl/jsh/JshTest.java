package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JshTest {
    
    public JshTest() {
    }
    
    PipedInputStream input;
    PipedOutputStream out;
    Jsh testJsh;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException{

        input = new PipedInputStream();
        out = new PipedOutputStream(input);
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Jsh.setCurrentDirectory(folder.getRoot().toString());

    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @After

    public void tear(){

        System.setIn(System.in);
        File delete  = new File("result.txt");
        delete.delete();
        File delete1  = new File("randomfile.txt");
        delete1.delete();
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());

    }

    @Test

    public void jshShouldThrowExceptionIfMoreThan2ArgsGiven() throws IOException {
        // ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        // PrintStream originalOut = System.out;
        // System.setOut(new PrintStream(outContent));
   
        String[] args=  {"-c", "echo", "foo"};
        
        assertThatThrownBy(() -> {
            Jsh.main(args);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("jsh: wrong number of arguments");

    }

    @Test

    public void jshShouldThrowExceptionIf1stArgNotCorrect() throws IOException {
        // ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        // PrintStream originalOut = System.out;
        // System.setOut(new PrintStream(outContent));
   
        String[] args=  {"-l", "echo"};
        
        assertThatThrownBy(() -> {
            Jsh.main(args);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("jsh: -l: unexpected argument");

    }
    @Test

    public void jshShouldNotThrowErrorIf2ArgsAndCorrectFormat(){

        String[] args=  {"-c", "pwd"};

        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException();

    }
    @Test

    public void testingPipeButWithFileShouldOutputFileAndIgnoreInputS() throws IOException {

        File tmp1 = folder.newFile();
        File tmp2 = folder.newFile();
        String[] args = {"-c","grep a " +tmp1.getName() + "| cat " + tmp2.getName()};
        String expected = "thisiswhatshoulgetOutputedJuiceWrld999";
        String ignore = "randomwordshere un\n uneccessary";
        writeToFile(tmp1,ignore);
        writeToFile(tmp2, expected);
        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).contains(expected).doesNotContain(ignore);
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 



    }

    @Test
    public void testJshDirCorrectWithSymbol() throws IOException {

        Jsh newshell = new Jsh(false);

        String input = "\r";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        newshell.takesInput();

        assertThat(outContent.toString()).isEqualTo(Jsh.getCurrentDirectory()+"> ");

    }

    @Test
    public void testJshDirCorrectWithSpaces() throws IOException {

        testJsh = new Jsh(false);

        String input = "  ";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        testJsh.takesInput();
        assertThat(outContent.toString()).isEqualTo(Jsh.getCurrentDirectory()+"> ");

    }

    @Test

    public void testJshShouldAllowCarriageReturnAsInput(){

        testJsh = new Jsh(false);

        String input = "pwd";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException();
    }

    @Test

    public void testJshWithPipe(){

        testJsh = new Jsh(false);

        String input = "grep a test2.txt | cat ";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThatCode(() -> {
            testJsh.takesInput();
        }).doesNotThrowAnyException();
    }

    @Test

    public void testJshWithSeqAndPipe() throws IOException {

        File tmp1 = folder.newFile();
        String[] args = {"-c","grep a " +tmp1.getName() + "| cat ; echo foo | cat" };
        String expected = "a Lot of sleepLess Nights";
        writeToFile(tmp1, expected + "\n testing");
        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).contains(expected, "foo").doesNotContain("testing");
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 
    }

    @Test

    public void testJshWithRedirectionGT() throws IOException {

        String[] args = {"-c","echo foo > randomfile.txt" };
        String expected = "redirection: file created successfully\n";
        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).contains(expected).doesNotContain("foo");
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 


    }

    @Test

    public void testJshWithRedirectionLT() throws IOException {

        File tmp1 = folder.newFile();
        writeToFile(tmp1, "line1ShouldBeOutputed\nThisLiNeShouldNot\nAnotherline");
        String[] args = {"-c","grep line < "+tmp1.getName()};
        String expected = "line1ShouldBeOutputed\nAnotherline";
        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).contains(expected).doesNotContain("ThisLiNeShouldNot\n");
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 

    }

    @Test

    public void testJshWithRedirectionLTAndGTGetsLastOneOfEach() throws IOException {

        File tmp1 = folder.newFile();
        File tmp2 = folder.newFile();
        String t1 = "randomwords9898\nrunchmod+x   \n output";
        String t2 = "thisiswhatshouldbereadjuiceWrld9999RiP";
        writeToFile(tmp1, t1);
        writeToFile(tmp2, t2);
        String[] args = {"-c","<" + tmp1.getName()+ " > randomfile.txt cat < "+tmp2.getName()+ " > result.txt ; cat result.txt"};

        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).contains(t2, "redirection: file created successfully").doesNotContain(t1);
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 

    }

    @Test

    public void testJshWithRedirectionCreatesFile() throws IOException {
        testJsh = new Jsh(false);

        String[] args = {"-c", "> result.txt"};

        Jsh.setTestOutput(System.out);
        Jsh.main(args);
        assertThat(outContent.toString()).isEqualTo("redirection: file created successfully\n");
        assertThatCode(() -> {
            Jsh.main(args);
        }).doesNotThrowAnyException(); 


    }

    @Test

    public void jshShouldThrowExceptionIfCannotReadUserInput(){
        testJsh = new Jsh(false);

        String input = "";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        testJsh.takesInput();

        assertThat(outContent.toString()).isEqualTo(Jsh.getCurrentDirectory()+"> jsh: No line found\n");
    }

    private void writeToFile(File file, String content) throws IOException{
        PrintWriter writer = new PrintWriter(file);
        writer.print(content);
        writer.flush();
        writer.close();
    }
}