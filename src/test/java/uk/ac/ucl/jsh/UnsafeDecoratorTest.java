package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.Cd;
import uk.ac.ucl.applications.Echo;
import uk.ac.ucl.applications.Pwd;

public class UnsafeDecoratorTest {

    UnsafeDecorator app;
    PipedInputStream in;
    PipedOutputStream out;


    @Before
    public void init() throws IOException {
        in = new PipedInputStream();
        out= new PipedOutputStream(in);
        app= new UnsafeDecorator(new Pwd());
    }
    @After
    public void tear() throws IOException {

        app  = null;
        in.close();
        out.close();
        
    }


    @Test

    public void UnsafeshouldCatchExceptionAndWriteErrorToStdout() throws IOException {

        ArrayList<String> args = new ArrayList<>();
        args.add("/randompath");
        ArrayList<Boolean> globb = new ArrayList<>();
        globb.add(true);
        globb.add(true);
        app.exec(args, null, out, globb);
    }
    @Test

    public void UnsafeshouldNotCatch() throws IOException {

        ArrayList<String> args = new ArrayList<>();
        ArrayList<Boolean> globb = new ArrayList<>();
        globb.add(true);
        app.exec(args, null, out, globb);
    }

}