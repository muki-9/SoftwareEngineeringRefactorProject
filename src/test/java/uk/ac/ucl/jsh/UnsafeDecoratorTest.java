package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        args.add("/src");
        assertThatCode(() ->{
            app.exec(args, null, out, null);
        }).doesNotThrowAnyException();
    }
    @Test

    public void UnsafeshouldNotCatch() throws IOException {

        ArrayList<String> args = new ArrayList<>();

        assertThatCode(() ->{
            app.exec(args, null, out, null);
        }).doesNotThrowAnyException();
    }

}