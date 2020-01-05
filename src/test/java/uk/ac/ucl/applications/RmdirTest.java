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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.jsh.Jsh;


public class RmdirTest{

    PipedInputStream in;
    PipedOutputStream out;
    Rmdir testRmdir;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testRmdir = new Rmdir();
         testArray = new ArrayList<>();
         outContent  = new ByteArrayOutputStream();
         System.setOut(new PrintStream(outContent));
    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @After

    public void tear1(){

        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @AfterClass
    public static void tear(){

        File del = new File("testfile2");
        del.delete();
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Test

    public void RmdirShouldDeleteFileIfExists() throws IOException {
        Jsh.setCurrentDirectory(folder.getRoot().toString());
        File tmp = folder.newFolder();
        testArray.add(tmp.getName());


        testRmdir.exec(testArray, null, System.out, null );

        assertThat(outContent.toString()).isEqualTo("Folder removed sucessfully\n");


    }
    @Test
    
    
    public void RmdirShouldThrowExceptionIfDirDoesntExists() throws IOException {
        testArray.add("randomfolder");

        assertThatThrownBy(() ->{

            testRmdir.exec(testArray, null, out, null );
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("rmdir: File does not exist");
    }

    @Test

    public void rmdirShouldThrowExceptionIfTryingtoRemoveNonEmptyDir() throws IOException {

        testArray.add(folder.getRoot().toPath().getFileName().toString());
        folder.newFolder();
        assertThatThrownBy(() ->{

            testRmdir.exec(testArray, null, out, null );
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("rmdir: Cannot remove non-empty file");
    }


    @Test
    
    public void rmdirWithNoInputShouldThrowException(){

        assertThatThrownBy(() ->{

            testRmdir.exec(testArray, null, out, null );
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("rmdir: no filename given");


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




}

