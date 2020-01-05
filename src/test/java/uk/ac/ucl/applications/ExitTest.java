package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class ExitTest{

    PipedInputStream in;
    PipedOutputStream out;
    Exit testExit;
    ArrayList<String> testArray;
    
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testExit= new Exit();
         testArray = new ArrayList<>();
    
       
    }

    @Test

    public void exitShouldExitJsh() throws IOException {

        exit.expectSystemExit();
        testExit.exec(testArray, null, out, null);


    }

    @Test

    public void exitShouldNotExitIfArgsGivenShoulfThrowException(){

        testArray.add("jsh");
        assertThatThrownBy(() ->{

            testExit.exec(testArray, null, out, null);

        }).isInstanceOf(RuntimeException.class)
        .hasMessage("exit: wrong number of arguments");


    }



}