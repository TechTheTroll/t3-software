package tests;

import org.junit.Before;
import org.junit.Test;

import utils.ConfigInfo;
import utils.Sleep;

/**
 * Test les threads
 * @author pf
 *
 */

public class JUnit_Thread extends JUnit_Test
{
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void test_fin_match() throws Exception
    {
    	config.set(ConfigInfo.FIN_MATCH, true);
    	Sleep.sleep(5000);
    }

    
}
