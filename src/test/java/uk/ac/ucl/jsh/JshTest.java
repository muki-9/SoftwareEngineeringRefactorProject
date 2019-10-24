package uk.ac.ucl.jsh;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class JshTest {
    public JshTest() {
    }

    @Test
    public void testEcho() throws Exception {
        Jsh echoShell = new Jsh();
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        echoShell.eval("echo foo", out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "foo");
        scn.close();
    }

    public void testPwd() throws IOException {
        Jsh pwdShell = new Jsh();
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        pwdShell.eval("pwd", out);
        Scanner scn = new Scanner(in);
        assertEquals(scn.next(), "pwd");
        scn.close();
    }

    public void testCd() throws IOException {
        Jsh cdShell = new Jsh();
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        cdShell.eval("cd", out);

    }

}
