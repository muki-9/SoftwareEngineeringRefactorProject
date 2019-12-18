package uk.ac.ucl.jsh;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.ac.ucl.applications.Cat;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CatTest{

    public CatTest(){
    }
    Cat testCat;
    ArrayList<String> testArray;
    PipedInputStream in;
    PipedOutputStream out;
   

    @Before
    public void testShell() throws IOException{
        testArray = new ArrayList<>();
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testCat = new Cat();
    }

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        testCat = null;

    
    }
    
    @Test
    public void testCatWithTwoInputs() throws IOException {
 
        String tmp1 = createTempFile();
        String tmp2 = createTempFile();
        writeToFile(tmp1, "First File");
        writeToFile(tmp2, "Second File");

        testArray.add(tmp1);
        testArray.add(tmp2);

        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        
        testCat.exec(testArray, null, out);
        String line = null;
        Scanner scn = new Scanner(in);
        line = scn.nextLine() + '\n';
        line+= scn.nextLine() + '\n';
        assertEquals(line, "First File\nSecond File\n");
        scn.close();
        // byte[] actualResult = stream.toByteArray();
        // String expected = "First File\nSecond File\n";
        // byte[] expectedResult = expected.getBytes();

        // assertArrayEquals(actualResult, expectedResult);
     }

    @Test
    public void testCatWithNoInputs() throws IOException{

        assertThatThrownBy(()->{
            testCat.exec(testArray, null, out);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("cat: missing arguments");

    }

    @Test
    public void catWithNonFileArgShouldThrowException() throws IOException{

        testArray.add("notafile.txt");
        assertThatThrownBy(() -> {
            testCat.exec(testArray, null, out);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("cat: file does not exist");
        
    }

    private String createTempFile() throws IOException{

        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        //File temp1 = new File("/workspaces/jsh-team-44/temp1.txt");
        temp1.deleteOnExit();
        //temp1.deleteOnExit();
        return temp1.getName();
    }

    private void writeToFile(String filename, String content) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(content);
        bw.close();
    }
}