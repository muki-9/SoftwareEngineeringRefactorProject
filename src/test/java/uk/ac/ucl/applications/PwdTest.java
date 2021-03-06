package uk.ac.ucl.applications;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ucl.applications.Pwd;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
public class PwdTest {

    public PwdTest(){


    }
    PipedInputStream in;
    PipedOutputStream out;
    ArrayList<String> testArray;
    Pwd testPwd;

    @Before
    public void init() throws IOException{

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testArray = new ArrayList<>();
        testPwd = new Pwd();
    }


    @Test
    public void testPwd() throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        testPwd.exec(testArray, null, out, null);
        Scanner scn = new Scanner(in);
        String line = scn.nextLine();
        assertEquals(line, currentDirectory ); 
        scn.close();

    }

    @Test

    public void pwdShouldThrowExceptionIfArgsGiven(){

        testArray.add("src");

        assertThatThrownBy(() ->{
            testPwd.exec(testArray, null, out, null);
        }).isInstanceOf(RuntimeException.class)
        .hasMessage("pwd: too many arguments");
    }

}