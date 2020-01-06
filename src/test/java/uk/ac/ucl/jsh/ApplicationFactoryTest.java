package uk.ac.ucl.jsh;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.applications.*;


public class ApplicationFactoryTest{


    public ApplicationFactoryTest(){

    }
    ApplicationFactory af;

    @Before
    public void init(){

        af = new ApplicationFactory();

    }


    @Test

    public void testUnsafeCdShouldCreateUnsafeCdObject(){

        Application app = af.mkApplication("_cd");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafePwdShouldCreateUnsafePwdObject(){
        Application app = af.mkApplication("_pwd");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeFindShouldCreateUnsafeFindObject(){
        Application app = af.mkApplication("_find");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);
    }
    @Test
    public void testUnsafeLsShouldCreateUnsafeLsObject(){
        Application app = af.mkApplication("_ls");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeCatShouldCreateUnsafeCatObject(){
        Application app = af.mkApplication("_cat");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeEchoShouldCreateUnsafeEchoObject(){
        Application app = af.mkApplication("_echo");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeHeadShouldCreateUnsafeHeadObject(){
        Application app = af.mkApplication("_head");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeTailShouldCreateUnsafeTailObject(){
        Application app = af.mkApplication("_tail");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeGrepShouldCreateUnsafeGrepObject(){
        Application app = af.mkApplication("_grep");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);
    }

    @Test
    public void testUnsafeWcShouldCreateUnsafeWcObject(){
        Application app = af.mkApplication("_wc");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);
    }


    @Test
    public void testUnsafeSedShouldCreateUnsafeSedObject(){
        Application app = af.mkApplication("_sed");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeHistoryShouldCreateHUnsafeistoryObject(){
        Application app = af.mkApplication("_history");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);
    }
    @Test
    public void testUnsafeExitShouldCreateUnsafeExitObject(){
        Application app = af.mkApplication("_exit");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void testUnsafeMkdirShouldCreateUnsafeMkdirObject(){
        Application app = af.mkApplication("_mkdir");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }
    @Test
    public void tesUnsafetRmdirShouldCreateUnsafeRmdirObject(){
        Application app = af.mkApplication("_rmdir");
        assertThat(app).isInstanceOf(UnsafeDecorator.class);

    }

    @Test

    public void testCdShouldCreateCdObject(){
        Cd app = (Cd) af.mkApplication("cd");
        assertNotNull(app);

    }
    @Test
    public void testPwdShouldCreatePwdObject(){
        Pwd app = (Pwd) af.mkApplication("pwd");
        assertNotNull(app);

    }
    @Test
    public void testFindShouldCreateFindObject(){
        Find app = (Find) af.mkApplication("find");
        assertNotNull(app);

    }
    @Test
    public void testLsShouldCreateLsObject(){
        Ls app = (Ls) af.mkApplication("ls");
        assertNotNull(app);

    }
    @Test
    public void testCatShouldCreateCatObject(){
        Cat app = (Cat) af.mkApplication("cat");
        assertNotNull(app);

    }
    @Test
    public void testEchoShouldCreateEchoObject(){
        Echo app = (Echo) af.mkApplication("echo");
        assertNotNull(app);

    }
    @Test
    public void testHeadShouldCreateHeadObject(){
        Head app = (Head) af.mkApplication("head");
        assertNotNull(app);

    }
    @Test
    public void testTailShouldCreateTailObject(){
        Tail app = (Tail) af.mkApplication("tail");
        assertNotNull(app);

    }
    @Test
    public void testGrepShouldCreateGrepObject(){
        Grep app = (Grep) af.mkApplication("grep");
        assertNotNull(app);

    }

    @Test
    public void testWcShouldCreateWcObject(){
        Wc app = (Wc) af.mkApplication("wc");
        assertNotNull(app);

    }


    @Test
    public void testSedShouldCreateSedObject(){
        Sed app = (Sed) af.mkApplication("sed");
        assertNotNull(app);

    }
    @Test
    public void testHistoryShouldCreateHistoryObject(){
        History app = (History) af.mkApplication("history");
        assertNotNull(app);

    }
    @Test
    public void testExitShouldCreateExitObject(){
        Exit app = (Exit) af.mkApplication("exit");
        assertNotNull(app);

    }
    @Test
    public void testMkdirShouldCreateMkdirObject(){
        Mkdir app = (Mkdir) af.mkApplication("mkdir");
        assertNotNull(app);

    }
    @Test
    public void testRmdirShouldCreateRmdirObject(){
        Rmdir app = (Rmdir) af.mkApplication("rmdir");
        assertNotNull(app);

    }
    @Test

    public void shouldThrowErrorIfWrongAppName(){

        assertThatThrownBy(() ->{
            af.mkApplication("random");

        }).isInstanceOf(RuntimeException.class)
        .hasMessageContaining("random: unknown application");
    }
}