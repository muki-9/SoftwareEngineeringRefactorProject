package uk.ac.ucl.applications;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Ls;
import uk.ac.ucl.jsh.Jsh;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.Scanner;

public class LsTest{

    ByteArrayOutputStream outContent;
    public LsTest(){


    }

    private Path currDir = Paths.get(Jsh.getCurrentDirectory());
    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Ls testLs;
    @Before 
    public void init() throws IOException {
         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testArray = new ArrayList<>();
         testLs = new Ls();
         outContent  = new ByteArrayOutputStream();
         System.setOut(new PrintStream(outContent));
    }

    @Test

    public void testLsWhenNoArgumentsShouldOutputFilesOfCurrentDir() throws IOException {
        //check if output same as what is should be
        testLs.exec(testArray, null, System.out, null);
        assertThat(outContent.toString()).contains("analysis", "test", "Dockerfile", "target", "pom.xml", "coverage");


    }
    @Test

    public void testLsWithOneArgShouldOutputFilesOfArgDirIfInCurrDir() throws IOException{

        testArray.add("src");
        testLs.exec(testArray, null, System.out, null);
        assertThat(outContent.toString()).contains("test", "main");

    }

    @Test

    public void testLsWithMoreThanOneArgShouldThrowException() throws IOException{

        testArray.add("src");
        testArray.add("test");
        assertThatThrownBy(() -> { 

            testLs.exec(testArray, null, out,null);
        })
        .isInstanceOf(RuntimeException.class )
        .hasMessageContaining("ls: too many arguments");

    }

    @Test

    public void lsShouldThrowExceptionIfPathDoesNotExist(){

        testArray.add("a");
        assertThatThrownBy(() -> { 

            testLs.exec(testArray, null, out,null);
        })
        .isInstanceOf(RuntimeException.class )
        .hasMessageContaining("ls: no such directory");


    }

    @Test
    public void checkEmptyLsDoesNotPrintExtraNewLine() throws IOException {

        String dir = createTempDir();
        testArray.add(dir);
        testLs.exec(testArray, null, System.out ,null);
        assertThat(outContent.toString()).isEqualTo("");

    }

    @Test

    public void testLsShouldIgnoreFilesStartingWithDot() throws IOException{

        String tmp = createTempFile();
        testLs.exec(testArray, null, System.out,null);
        assertThat(outContent.toString()).doesNotContain(tmp);

    }
    @Test
    public void testLsShouldSeparateFilesUsingTabs() throws IOException{

        testLs.exec(testArray, null, System.out ,null);
        assertThat(outContent.toString()).contains("\t");

    }

    private String createTempFile() throws IOException{

        File temp1 = File.createTempFile(".tmp", ".out", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        return temp1.getName();

    }

    private String createTempDir() throws IOException{
        File temp1 = Files.createTempDirectory(currDir, "input").toFile();
        temp1.deleteOnExit();
        return temp1.getName();
    }

}