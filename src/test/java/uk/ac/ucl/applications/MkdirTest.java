package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.jsh.Jsh;

public class MkdirTest{

    PipedInputStream in;
    PipedOutputStream out;
    Mkdir testMkdir;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testMkdir = new Mkdir();
         testArray = new ArrayList<>();
         outContent  = new ByteArrayOutputStream();
         System.setOut(new PrintStream(outContent));
       
    }
    @Rule
    public TemporaryFolder folder  = new TemporaryFolder();

    @After

    public void tear1(){
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @AfterClass
    public static void tear(){

        File del = new File("testfile2");
        del.delete();
    }

    @Test

    public void mkdirShouldCreateFileIfDoesntExist() throws IOException {
        testArray.add("testfile2");


        Scanner scn = new Scanner(in);

        testMkdir.exec(testArray, null, out, null );

        String line = scn.nextLine();
        assertThat(line).isEqualTo("Folder created sucessfully");

        scn.close();

    }
    @Test
    
    
    public void mkdirShouldThrowExceptionIfDirExists() throws IOException {

        Jsh.setCurrentDirectory(folder.getRoot().toString());

        File tmp = folder.newFolder();
        testArray.add(tmp.getName());

        assertThatThrownBy(() ->{

            testMkdir.exec(testArray, null, out, null );
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("mkdir: File already exists, choose different name");
    }
    @Test
    
    public void mkdirWithNoInputShouldThrowException(){

        assertThatThrownBy(() ->{

            testMkdir.exec(testArray, null, out, null );
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("mkdir: no filename given");
    }
}

