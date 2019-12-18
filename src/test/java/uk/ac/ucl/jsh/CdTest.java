package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.Cd;

public class CdTest{

    public CdTest(){
    }
    PipedInputStream in;
    PipedOutputStream out;
    Cd testCd;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testCd = new Cd();
         testArray = new ArrayList<>();
       
    }

    @After
    public void tear() throws IOException {
        in.close();
        out.close();
        testCd = null;
        Jsh.setCurrentDirectory(System.getProperty("user.dir"));
    
    }

    @Test
    public void testCd() throws IOException {

        testArray.add("src");
        testCd.exec(testArray, null, out);
        String currentDir= Jsh.getCurrentDirectory();
        assertThat(currentDir).contains("src");

    }
}

