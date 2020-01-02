package uk.ac.ucl.applications;
import static org.junit.Assert.assertEquals;
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

    //this shouldnt be like this... if u do echo "foo", the output is foo without the quotes
    // @Test
    // public void testEchoQuotes() throws Exception {
    //     testArray.add("\"foo\"");
    //     testEcho.exec(testArray, null, out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "\"foo\"");
    //     scn.close();
    // }

    @Test
    
    public void testEchoWithManySpacesShouldIgnoreAndOnlyHaveOneSpace() throws IOException{
        testArray.add("foo");
        testArray.add("foo");
        testEcho.exec(testArray, null, out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.nextLine(), "foo foo");
        scn.close();

    }

    //I beg u make this test work aswell pls
    // @Test

    // public void emptyArgsEcho() throws IOException {
    //     testEcho.exec(testArray, null, out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.nextLine(), "");
    //     scn.close();
    // }s
}