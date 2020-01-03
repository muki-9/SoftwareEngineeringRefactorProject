package uk.ac.ucl.applications;

import uk.ac.ucl.jsh.Jsh;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


public class RmdirTest{

    private Path currDir = Paths.get(Jsh.getCurrentDirectory());

    PipedInputStream in;
    PipedOutputStream out;
    Rmdir testRmdir;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testRmdir = new Rmdir();
         testArray = new ArrayList<>();
       
    }

    @AfterClass
    public static void tear(){

        File del = new File("testfile2");
        del.delete();
    }

    @Test

    public void RmdirShouldDeleteFileIfExists() throws IOException {

        String tmp = createTempDir();
        testArray.add(tmp);


        Scanner scn = new Scanner(in);

        testRmdir.exec(testArray, null, out, null );

        String line = scn.nextLine();
        assertThat(line).isEqualTo("Folder removed sucessfully");

        scn.close();

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

        String tmpDir = createTempDir();
        createTempFile("/workspaces/jsh-team-44/"+tmpDir);
        testArray.add(tmpDir);

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

    private String createTempFile(String path) throws IOException{
        File temp1 = File.createTempFile("testfile", ".txt", new File(path));
        temp1.deleteOnExit();
        return temp1.getName();
    }

    private String createTempDir() throws IOException{
        File temp1 = Files.createTempDirectory(currDir, "input").toFile();
        temp1.deleteOnExit();
        return temp1.getName();
    }




}

