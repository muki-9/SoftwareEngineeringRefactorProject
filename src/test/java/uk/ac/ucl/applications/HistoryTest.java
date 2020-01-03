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
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;


public class HistoryTest{

    private Path currDir = Paths.get(Jsh.getCurrentDirectory());

    PipedInputStream in;
    PipedOutputStream out;
    History testHis;
    ArrayList<String> testArray;

    @Before
    public void init() throws IOException {

         in = new PipedInputStream();
         out = new PipedOutputStream(in);
         testHis= new History();
         testArray = new ArrayList<>();
    
       
    }

    @Test

    public void historyShouldPrintListOfPreviousCommands() throws IOException {

        Scanner scn = new Scanner(in);
        Jsh shell = new Jsh();
        shell.eval("pwd", out);
        shell.eval("history", out);
        
        scn.nextLine();
        String line2 = scn.nextLine();
        assertThat(line2).isEqualTo("pwd");
        scn.close();

    }

    @Test

    public void historyShouldThrowErrorIfAnyArgGiven(){

        testArray.add("src");

        assertThatThrownBy(() ->{
            testHis.exec(testArray, null, out, null);

        }).isInstanceOf(RuntimeException.class)
        .hasMessage("history: wrong number of arguments");


    }
//history will always be a command 
    // @Test

    // public void historyShouldNothrowErrorIfNoCommandsInHistory() throws IOException {
    //     Jsh shell= new Jsh();
        

    //     Scanner scn = new Scanner(in);

    //     assertThatCode(() ->{
    //         shell.eval("history", out);

    //     }).doesNotThrowAnyException();

    //     String output = scn.nextLine();
    //     assertThat(output).isEqualTo("There are no commands in history");
    //     scn.close();

    // }



}