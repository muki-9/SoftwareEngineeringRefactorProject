package uk.ac.ucl.applications;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Find;
import static org.assertj.core.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;



public class FindTest{

    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Find testFind;

    @Before
    public void init() throws IOException{

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        testFind = new Find();

    }
    @After
    public void tear() throws IOException{

        in.close();
        out.close();

    }


    @Test
    public void testFindShouldthrowsExceptionWhenWrongNumberofArgs(){

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: wrong number of arguments");

        testArray.add("/workspaces");
        testArray.add("-name");
        testArray.add("-l");
        testArray.add("*a");

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: wrong number of arguments");

    }

    @Test
    public void findShouldThrowExceptionIfSecondLastArgWrong() throws IOException{

        testArray.add("-wrong");
        testArray.add("*a");

        assertThatThrownBy(() -> {
            testFind.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("find: arg -wrong not correct");


    }

    @Test
    public void findShouldFilterAllPathsDependingOnPattern() throws IOException{

        createTempFile(".java");
        createTempFile(".txt");
        createTempFile(".txt");
        createTempFile(".out");
        testArray.add("-name");
        testArray.add("in*xt");

        testFind.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        String output2 = scn.nextLine();

        assertThat(output).startsWith(".").contains(".txt");
        assertThat(output2).contains(".txt");
        scn.close();
  
    }

    @Test
    public void findShouldThrowExceptionIfPathIncorrect() throws IOException{

        testArray.add("randompath/src");
        testArray.add("-name");
        testArray.add("*java");


        assertThatThrownBy(() ->{
            testFind.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("find: randompath/src does not exist");

    }

    @Test

    public void ifContainsPathShouldContainPathNameAtStart() throws IOException {

        testArray.add("target");
        testArray.add("-name");
        testArray.add("*java");

        testFind.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);
        String output = scn.nextLine();
        assertThat(output).startsWith("target");
        scn.close();
        
    }

    // @Test

    // public void catchExceptionIfCannotWriteToStdout() throws IOException {

    //     PipedOutputStream out = new PipedOutputStream();

    //     testArray.add("-name");
    //     testArray.add("*java");

    //     assertThatThrownBy(() ->{
    //         testFind.exec(testArray, null, out, null);
    //     }).isInstanceOf(RuntimeException.class)
    //     .hasMessage("cannot write to output");

    // }


    private String createTempFile(String extension) throws IOException{

        File temp1 = File.createTempFile("input", extension, new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        return temp1.getName();

    }





}