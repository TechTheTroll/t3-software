package tests;

import org.junit.Before;
import org.junit.Test;

import serie.DataForSerialOutput;
import utils.Sleep;
import utils.Vec2;
import utils.permissions.ReadOnly;
import container.ServiceNames;

/**
 * Tests unitaires de la série. Plutôt un test des réponses de la STM en fait.
 * @author pf
 *
 */

public class JUnit_Serie extends JUnit_Test {

//	private SerialSTM serie;
	private DataForSerialOutput data;
	@Before
    public void setUp() throws Exception {
        super.setUp();
        data = (DataForSerialOutput) container.getService(ServiceNames.SERIAL_OUTPUT_BUFFER);
//        serie = (SerialSTM) container.getService(ServiceNames.SERIE_STM);
	}
	
	@Test
	public void test_ping() throws Exception
	{
		Sleep.sleep(300000);
	}
	
	@Test
	public void test_init_odo() throws Exception
	{
		data.initOdoSTM(new Vec2<ReadOnly>(12, 34), 1);
		Sleep.sleep(100);
	}

	@Test
	public void test_stress_test() throws Exception
	{
		for(int i = 0; i < 10000; i++)
			data.addPing();
		Sleep.sleep(300000);
	}
	
}
