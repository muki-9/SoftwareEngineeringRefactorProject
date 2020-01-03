package uk.ac.ucl.applications;

import uk.ac.ucl.jsh.Jsh;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import uk.ac.ucl.applications.Cd;

public class MkdirTest{

    PipedInputStream in;
    PipedOutputStream out;
    Mkdir testMkdir;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testMkdir = new Mkdir();
         testArray = new ArrayList<>();
       
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

        String tmp = createTempFile();
        testArray.add(tmp);

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
//works for everything ive tried.. check if necessary


    // @Test

    // public void mkdirShouldNotCreateDirIfNotPossible() throws IOException {

    //     testArray.add("()()(cdcd))");

    //     Scanner scn = new Scanner(in);

    //     testMkdir.exec(testArray, null, out, null );

    //     String line = scn.nextLine();
    //     assertThat(line).isEqualTo("Folder created sucessfully");
    //     scn.close();



    // }

    private String createTempFile() throws IOException{
        File temp1 = File.createTempFile("testfile", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        return temp1.getName();
    }




}

