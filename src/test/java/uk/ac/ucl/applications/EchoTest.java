package uk.ac.ucl.applications;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.*;
import uk.ac.ucl.applications.Echo;
import uk.ac.ucl.jsh.Jsh;

public class EchoTest {

    public EchoTest() {

    }

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));


    PipedInputStream in;
    PipedOutputStream out;
    Echo testEcho;
    ArrayList<String> testArray;
    ByteArrayOutputStream outContent;

    @Before
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testEcho = new Echo();
        testArray = new ArrayList<>();
        outContent  = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testEchoShouldOutputFoo() throws Exception {
        testArray.add("foo");
        testEcho.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "foo");
        scn.close();
    }

    @Test

    public void testGlobbingWithEcho() throws IOException {
        folder.newFile("input.txt");
        folder.newFile("juice.txt");
        folder.newFile("wrld.txt");
        ArrayList<Boolean> globb = new ArrayList<>();
        globb.add(true);
        testArray.add(folder.getRoot().toPath().getFileName().toString()+"/*");
        testEcho.exec(testArray, null, System.out, globb);

        assertThat(outContent.toString()).contains("input.txt", "juice.txt", "wrld.txt");
    
    }

    @Test

    public void emptyArgsEcho() throws IOException {

        assertThatCode(() ->{
            testEcho.exec(testArray, null, out, null);
        }).doesNotThrowAnyException();
  
    }
}