package uk.ac.ucl.jsh;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class JshTest {
    
    public JshTest() {
    }

    // Jsh jshell;

    // @Before
    // public void testShell() {
    //     jshell = new Jsh();
    // }

    // @Test
    // public void emptyShell() throws IOException {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("", out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "");
    //     scn.close();
    // }

    // @Test
    // public void onlySpaces() throws IOException {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("    ", out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "");
    //     scn.close();
    // }

    // @Test
    // public void testEcho() throws Exception {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("echo foo", out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "foo");
    //     scn.close();
    // }

    // @Test
    // public void testEchoQuotes() throws Exception {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("echo \"foo\" ", out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "foo");
    //     scn.close();
    // }

    // @Test
    // public void testPwd() throws IOException {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("pwd", out);
    //     Scanner scn = new Scanner(in);
    //     assertEquals(scn.next(), "pwd");
    //     scn.close();
    // }

    // public void testCd() throws IOException {
    //     PipedInputStream in = new PipedInputStream();
    //     PipedOutputStream out = new PipedOutputStream(in);
    //     jshell.eval("cd", out);
    // }

}
