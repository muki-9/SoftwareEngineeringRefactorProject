package uk.ac.ucl.applications;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Ls;
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
    //when ls inputted with no arguments then should output contents of current directory. 
    //if ls inputted with argument, should output contents of that directory. 
    //find out what the files in current directory is and files in another directory and store to compare.

public class LsTest{

    public LsTest(){


    }
    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Ls testLs;
    ByteArrayOutputStream outContent;

    @Before 
    public void init() throws IOException {
         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testArray = new ArrayList<>();
         testLs = new Ls();
         outContent  = new ByteArrayOutputStream();
         System.setOut(new PrintStream(outContent));
         Jsh.setCurrentDirectory(folder.getRoot().toString());
    }
    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @After

    public void tear(){
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());

    }

    @Test

    public void testLsWhenNoArgumentsShouldOutputFilesOfCurrentDir() throws IOException {
        //check if output same as what is should be
        folder.newFile("test");
        folder.newFile("ben");
        folder.newFile("raghib");
        folder.newFile("muki");
        //check if output same as what is should be
        testLs.exec(testArray, null, System.out,null);
        assertThat(outContent.toString()).contains("muki", "ben", "raghib", "test");
    }
    @Test

    public void testLsWithOneArgShouldOutputFilesOfArgDirIfInCurrDir() throws IOException{

        folder.newFile("random");
        testArray.add(folder.getRoot().toPath().toFile().getName());
        testLs.exec(testArray, null, System.out,null);
        assertThat(outContent.toString()).contains("random");
  
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

    public void lsShouldThrowExceptionifFileArgDoesntExist(){


        testArray.add("test");
        assertThatThrownBy(() -> { 

            testLs.exec(testArray, null, out,null);
        })
        .isInstanceOf(RuntimeException.class )
        .hasMessageContaining("ls: no such directory");


    }

    @Test

    public void testLsShouldIgnoreFilesStartingWithDot() throws IOException{
        Jsh.setCurrentDirectory(folder.getRoot().toString());

        File tmp = folder.newFile(".random.out");
        testLs.exec(testArray, null, System.out,null);
        assertThat(outContent.toString()).doesNotContain(tmp.getName());

    }
    @Test
    public void testLsShouldSeparateFilesUsingTabs() throws IOException{
        
        folder.newFile("muki");
        folder.newFile("ben");
        folder.newFile("raghib");
        testArray.add(folder.getRoot().toPath().toFile().getName());

        testLs.exec(testArray, null, out,null);
        Scanner scn = new Scanner(in);
        String content = scn.nextLine();
        assertThat(content).contains("\t");
        scn.close();
    }

}