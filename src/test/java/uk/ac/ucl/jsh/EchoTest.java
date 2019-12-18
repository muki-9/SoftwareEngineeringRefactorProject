package uk.ac.ucl.jsh;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.Echo;

public class EchoTest {

    public EchoTest() {

    }


    PipedInputStream in;
    PipedOutputStream out;
    Echo testEcho;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testEcho = new Echo();
        testArray = new ArrayList<>();
    }

    @Test
    public void testEcho() throws Exception {
        testArray.add("foo");
        testEcho.exec(testArray, null, out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "foo");
        scn.close();
    }

    @Test
    public void testEchoQuotes() throws Exception {
        testArray.add("\"foo\"");
        testEcho.exec(testArray, null, out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "\"foo\"");
        scn.close();
    }

    @Test
    
    public void testEchoWithManySpacesShouldIgnoreAndOnlyHaveOneSpace() throws IOException{
        testArray.add("foo");
        testArray.add("foo");
        testEcho.exec(testArray, null, out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.nextLine(), "foo foo ");
        scn.close();

    }
    //test for echo quotes should not include the quotes in stdout


}