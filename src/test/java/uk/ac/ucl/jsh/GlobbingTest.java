package uk.ac.ucl.jsh;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

public class GlobbingTest {

    public GlobbingTest() {

    }

     ArrayList<String> testArray;
     Globbing glob;
     ArrayList<Boolean> testGlobb;
     String currentDir = Jsh.getCurrentDirectory();
     Path currDir = Paths.get(currentDir);
    @Before

    public void init() {

        testArray = new ArrayList<>();
        testGlobb = new ArrayList<>();

    }

    @Test

    public void globbShouldExpandEachArgWhichContainsUnquotedAsterisk() throws IOException {
        String tmp1 = createTempFile(currentDir);
        String tmp2 = createTempFile(currentDir);
        String tmp3 = createTempFile(currentDir);
        String tmp4 = createTempFile(currentDir);
        
        testArray.add(tmp1.substring(0, 10)+"*");
        testArray.add(tmp2);
        testArray.add(tmp3+"*");
        testArray.add(tmp4);
        testGlobb.add(true);
        testGlobb.add(false);
        testGlobb.add(false);
        testGlobb.add(true);

        glob= new Globbing(testGlobb);
        ArrayList<String> result = glob.globbing(testArray);

        assertThat(result).containsOnly(tmp1, tmp2, tmp3+"*", tmp4);

    }

    @Test

    public void globbShouldNotThrowExceptionIfNoFilesInDir() throws IOException {
        String tmp1 = createTempDir();
        
        testArray.add(tmp1+"/*");
        testGlobb.add(true);
        glob= new Globbing(testGlobb);
        ArrayList<String> result = glob.globbing(testArray);
        assertThat(result).isEmpty();

    }

    // @Test

    // public void globbShouldProduceSeveralArgsifPathsfound() throws IOException {

    //     String tmpDir1 = createTempDir();
    //     String tmpFile1 = createTempFile(currentDir+"/"+tmpDir1);
    //     String tmpFile2 = createTempFile(currentDir+"/"+tmpDir1);
    //     String tmp2 = createTempFile(currentDir);

    //     testArray.add(tmpDir1.substring(0,10)+"*/*");
    //     testArray.add(tmp2.substring(0,12)+"*");

    //     testGlobb.add(true);
    //     testGlobb.add(true);

    //     glob= new Globbing(testGlobb);
    //     ArrayList<String> result = glob.globbing(testArray);
    //     assertThat(result).containsOnly(tmpDir1+"/"+tmpFile1, tmpDir1+"/"+tmpFile2, tmp2);


    // }

    private String createTempFile(String path) throws IOException{
        File temp1 = File.createTempFile("input", ".txt", new File(path));
        temp1.deleteOnExit();
        return temp1.getName();
    }

    private String createTempDir() throws IOException{
        File temp1 = Files.createTempDirectory(currDir, "input").toFile();
        temp1.deleteOnExit();
        return temp1.getName();
    }




}