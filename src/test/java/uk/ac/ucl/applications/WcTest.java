package uk.ac.ucl.applications;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.Wc;


public class WcTest{


    public WcTest(){


    }

    PipedInputStream in;
    PipedOutputStream out;
    Wc testWc;
    ArrayList<String> testArray;


    @Before 
    public void init() throws IOException {
        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        testWc = new Wc();
        testArray = new ArrayList<>();

    }

    @Test

    public void wcShouldThrowExceptionifNoArgsAndNoInputGiven(){

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong number of arguments");
    }

    @Test

    public void wcShouldNotThrowExceptionIfInputGivenAndNoArgs(){

        String originalString = "test line absolute\n2nd line!\nabsent";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        assertThatCode(() -> {
            testWc.exec(testArray, inputStream, out, null);
        }).doesNotThrowAnyException();

    }
    @Test
    public void wcShouldThrowExcpetionifOneArgWithOptionsAndNoInput(){
        testArray.add("-m");

        assertThatThrownBy(() -> {
            testWc.exec(testArray, null, out, null);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("wc: wrong number of arguments");
    }

    @Test

    public void testOutputWithNoArgsButInput() throws IOException {

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        int byteNo = originalString.getBytes().length +1;
        int wordNo = 6;
        int linesNo = 3;
        testWc.exec(testArray, inputStream, out, null);
        Scanner scn = new Scanner(in);
        int linesAct = Integer.parseInt(scn.next());
        int wordAct = Integer.parseInt(scn.next());
        int byteAct = Integer.parseInt(scn.next());
        scn.close();

        assertThat(byteAct).isEqualTo(byteNo);
        assertThat(wordAct).isEqualTo(wordNo);
        assertThat(linesAct).isEqualTo(linesNo);

    }
    @Test

    public void testOutputWithOneArgAndInput() throws IOException {

        int byteNo = 36;
        int wordNo = 6;
        int linesNo = 3;

        testArray.add("-m");
        int result = optionResult(testArray);
        assertThat(result).isEqualTo(byteNo);

        ArrayList<String> newArray = new ArrayList<>();
        newArray.add("-l");
        int result1 = optionResult(newArray);
        assertThat(result1).isEqualTo(linesNo);

        ArrayList<String> newArray1= new ArrayList<>();
        newArray1.add("-w");
        int result2 = optionResult(newArray1);
        assertThat(result2).isEqualTo(wordNo);

    }
    @Test
    public void testWcfor2orMoreArgsShouldProduceCorrectOutput() throws IOException {

        testArray.add("-m");
        String tmp1 = createTempFile();
        String tmp2 = createTempFile();
        testArray.add(tmp1);
        testArray.add(tmp2);

        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);

        String line = scn.nextLine();
        String[] split = line.split("\\s");
        int bytesNo = "Line 1\nLine 2\nLine3\n".getBytes().length +1;
         
        assertThat(Integer.parseInt(split[0])).isEqualTo(bytesNo);

        String line1= scn.nextLine();
        String[] split1 = line1.split("\\s");

        assertThat(Integer.parseInt(split1[0])).isEqualTo(bytesNo);

        String line2= scn.nextLine();
        String[] split2 = line2.split("\\s");

        assertThat(Integer.parseInt(split2[0])).isEqualTo(2*bytesNo);
        scn.close();

    }

    @Test

    public void testWcWithoutOptFor2orMoreArgs() throws IOException {

        String tmp1 = createTempFile();
        String tmp2 = createTempFile();
        testArray.add(tmp1);
        testArray.add(tmp2);

        testWc.exec(testArray, null, out, null);

        Scanner scn = new Scanner(in);

        String line = scn.nextLine();
        String[] split = line.split("\\s");

        testOutput(false, split);

        String line1 = scn.nextLine();
        String[] split1= line1.split("\\s");

        testOutput(false, split1);

        String line2 = scn.nextLine();
        String[] split2= line2.split("\\s");

        testOutput(true, split2);

        scn.close();

    }

    private void testOutput(boolean last, String[] split){

        int bytesNo = "Line 1\nLine 2\nLine3\n".getBytes().length +1;
        int wordsNo = 6;
        int linesNo = 3;
        Integer[] intArray= {linesNo, wordsNo, bytesNo};
        if(!last){
            for(int i=0; i<3; i++){
                assertThat(Integer.parseInt(split[i])).isEqualTo(intArray[i]);
            }

        }else{
            for(int i=0; i<3; i++){
                assertThat(Integer.parseInt(split[i])).isEqualTo(intArray[i]*2);
            }

        }
    
    }

    private int optionResult(ArrayList<String> test) throws IOException {

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        String originalString = "test line absolute\n2nd line!\nabsent"; //if only print new lie counts then 
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
    
        testWc.exec(test, inputStream, out, null);
        Scanner scn = new Scanner(in);
        int optAct = Integer.parseInt(scn.next());
        scn.close();
        return optAct;


    }

    private String createTempFile() throws IOException{

        File temp1 = File.createTempFile("input", ".txt", new File("/workspaces/jsh-team-44"));
        temp1.deleteOnExit();
        writeToFile(temp1.getName());
        return temp1.getName();

    }
    private void writeToFile(String filename) throws IOException{

        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for(int i =0; i<3; i++){
            bw.write("Line "+ i);
            bw.write(System.getProperty("line.separator"));
        }
        bw.close();

     }





    

    
}