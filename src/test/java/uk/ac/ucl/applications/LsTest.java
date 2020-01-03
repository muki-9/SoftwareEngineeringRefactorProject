package uk.ac.ucl.applications;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Ls;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class LsTest{

    public LsTest(){


    }
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
    }

    @Test

    public void testLsWhenNoArgumentsShouldOutputFilesOfCurrentDir() throws IOException {
        //check if output same as what is should be
        testLs.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String ls = scn.nextLine();
        assertThat(ls).contains("analysis", "test", "Dockerfile", "target", "pom.xml", "coverage");
        scn.close();

    }
    @Test

    public void testLsWithOneArgShouldOutputFilesOfArgDirIfInCurrDir() throws IOException{

        testArray.add("src");
        testLs.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String content = scn.nextLine();
        assertThat(content).contains("test", "main");
        scn.close();
    }

    @Test

    public void testLsWithMoreThanOneArgShouldThrowException() throws IOException{

        testArray.add("src");
        testArray.add("test");
        assertThatThrownBy(() -> { 

            testLs.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class )
        .hasMessageContaining("ls: too many arguments");

    }

    @Test

    public void testLsShouldIgnoreFilesStartingWithDot() throws IOException{

        String tmp = createTempFile();
        testLs.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String content = scn.nextLine();
        assertThat(content).doesNotContain(tmp);
        scn.close();

    }
    @Test
    public void testLsShouldSeparateFilesUsingTabs() throws IOException{

        testLs.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String content = scn.nextLine();
        assertThat(content).contains("\t");
        scn.close();
    }

    private String createTempFile() throws IOException{

        File temp1 = File.createTempFile(".tmp", ".out", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        return temp1.getName();

    }

}