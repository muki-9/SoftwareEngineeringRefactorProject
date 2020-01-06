package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GlobbingTest {

    public GlobbingTest() {

    }

     ArrayList<String> testArray;
     Globbing glob;
     ArrayList<Boolean> testGlobb;
     String currentDir = Jsh.getCurrentDirectory();
     Path currDir = Paths.get(currentDir);

    @Rule
    public TemporaryFolder folder  = new TemporaryFolder(new File(Jsh.getHomeDirectory()));

    @Before
    public void init() {
        testArray = new ArrayList<>();
        testGlobb = new ArrayList<>();
    }

    @After
    public void tear() throws IOException {
        testGlobb = null;
        Jsh.setCurrentDirectory(Jsh.getHomeDirectory());
    }

    @Test
    public void globbShouldExpandEachArgWhichContainsUnquotedAsterisk() throws IOException {
        File file1 = folder.newFile();
        File file2 = folder.newFile();
        File file3 = folder.newFile();
        File file4 = folder.newFile();
        
        testArray.add(file1.getName().substring(0, 10)+"*");
        testArray.add(file2.getName());
        testArray.add(file3.getName()+"*");
        testArray.add(file4.getName());
        testGlobb.add(true);
        testGlobb.add(false);
        testGlobb.add(false);
        testGlobb.add(true);

        Jsh.setCurrentDirectory(folder.getRoot().toString());
        glob = new Globbing(testGlobb);
        ArrayList<String> result = glob.globbing(testArray);

        assertThat(result).containsOnly(file1.getName(), file2.getName(), file3.getName()+"*", file4.getName());

    }

    @Test

    public void globbShouldReturnArgIfNoFilesInDir() throws IOException {
        File file1 = folder.newFile();
        
        testArray.add(file1.getName()+"/*");
        testGlobb.add(true);
        Jsh.setCurrentDirectory(folder.getRoot().toString());
        glob = new Globbing(testGlobb);
        ArrayList<String> result = glob.globbing(testArray);
        assertThat(result.get(0)).isEqualTo(file1.getName()+"/*");

    }

}