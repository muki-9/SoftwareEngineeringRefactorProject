package uk.ac.ucl.applications;


import uk.ac.ucl.jsh.Jsh;
import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

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

    @Test

    public void cdShouldThrowExceptionifNoArgsOrMoreThan1Arg(){

        // assertThatThrownBy(() -> {
        //     testCd.exec(testArray, null, out);
        // })
        // .isInstanceOf(RuntimeException.class)
        // .hasMessageContaining("cd: missing argument");

        testArray.add("jsh");
        testArray.add("jsh");

        assertThatThrownBy(() -> {
            testCd.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("cd: too many arguments");

    }

    @Test

    public void throwExceptionifArgisNotCorrectDir(){
        testArray.add("index.txt");

        assertThatThrownBy(() -> {
            testCd.exec(testArray, null, out);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("cd: index.txt is not an existing directory");

    }



}

