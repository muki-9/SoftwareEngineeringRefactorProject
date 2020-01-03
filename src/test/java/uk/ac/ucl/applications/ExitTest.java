package uk.ac.ucl.applications;

import uk.ac.ucl.jsh.Jsh;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
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