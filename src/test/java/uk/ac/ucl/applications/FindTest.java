package uk.ac.ucl.applications;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Find;
import uk.ac.ucl.jsh.Jsh;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;



public class FindTest{

    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Find testFind;
    ByteArrayOutputStream outContent;
    @Before
    public void init() throws IOException{

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        testFind = new Find();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

    }
    @After
    public void tear() throws IOException{

        in.close();
        out.close();

    }
    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));


    @Test
    public void testFindShouldthrowsExceptionWhenWrongNumberofArgs(){

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: wrong number of arguments");

        testArray.add("/workspaces");
        testArray.add("-name");
        testArray.add("-l");
        testArray.add("*a");

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: wrong number of arguments");

    }

    @Test
    public void findShouldThrowExceptionIfSecondLastArgWrong() throws IOException{

        testArray.add("-wrong");
        testArray.add("*a");

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: args not correct");


    }

    @Test
    public void findShouldFilterAllPathsDependingOnPattern() throws IOException{

        folder.newFile("input.java");
        folder.newFile("input.java");
        folder.newFile("input345.txt");
        folder.newFile("random.out");
        testArray.add("-name");
        testArray.add("in*xt");

        testFind.exec(testArray, null, System.out, null);

        assertThat(outContent.toString()).startsWith(".").endsWith(".txt\n").doesNotContain("random.out", "input.java");

  
    }


        @Test
    public void findShouldThrowExceptionIfPathIncorrect() throws IOException{

        testArray.add("randompath/src");
        testArray.add("-name");
        testArray.add("*java");


        assertThatThrownBy(() ->{
            testFind.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("find: randompath/src does not exist");

    }

    @Test

    public void ifDoenstContainsPathShouldContainDotAtStart() throws IOException {

        testArray.add("-name");
        testArray.add("*java");

        testFind.exec(testArray, null, System.out, null);

        assertThat(outContent.toString()).startsWith(".");
        
    }
    @Test

    public void ifContainsPathShouldpathnameAtStart() throws IOException {
        testArray.add("target");
        testArray.add("-name");
        testArray.add("*java");

        testFind.exec(testArray, null, System.out, null);

        assertThat(outContent.toString()).startsWith("target");
        
    }

    @Test

    public void catchExceptionIfCannotWriteToStdout() throws IOException {

        PipedOutputStream out = new PipedOutputStream();

        testArray.add("-name");
        testArray.add("*java");

        assertThatThrownBy(() ->{
            testFind.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("cannot write to output");

    }


}