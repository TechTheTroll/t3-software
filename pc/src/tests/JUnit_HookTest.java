package tests;

import hook.Callback;
import hook.Executable;
import hook.Hook;
import hook.HookGenerator;
import hook.methodes.TirerBalles;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import robot.RobotVrai;
import robot.cartes.Deplacements;
import smartMath.Vec2;
import container.Container;

public class JUnit_HookTest {

	Container container;
	RobotVrai robotvrai;
	Deplacements deplacements;
	HookGenerator hookgenerator;
	
	@Before
	public void setUp() throws Exception {
		container = new Container();
		robotvrai = (RobotVrai) container.getService("RobotVrai");
		deplacements = (Deplacements)container.getService("Deplacements");
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		robotvrai.setPosition(new Vec2(0, 1500));
		robotvrai.setOrientation(0);
		robotvrai.set_vitesse_rotation("entre_scripts");
		robotvrai.set_vitesse_translation("entre_scripts");
	}
	
	@After
	public void tearDown() throws Exception {
		robotvrai = null;
		container.destructeur();
		container = null;
	}

	@Test
	public void test_hookAbscisse() throws Exception
	{
		int nb_balles = robotvrai.getNbrLances();
		ArrayList<Hook> hooks = new ArrayList<Hook>();
		Executable tirerballes = new TirerBalles(robotvrai);
		Hook hook = hookgenerator.hook_abscisse(20);
		hook.ajouter_callback(new Callback(tirerballes, true));
		hooks.add(hook);		
		robotvrai.avancer(10, hooks);
		Assert.assertTrue(nb_balles == robotvrai.getNbrLances());
		robotvrai.avancer(50, hooks);
		Assert.assertTrue(nb_balles != robotvrai.getNbrLances());
	}

	@Test
	public void test_hookPosition() throws Exception
	{
		int nb_balles = robotvrai.getNbrLances();
		ArrayList<Hook> hooks = new ArrayList<Hook>();
		Executable tirerballes = new TirerBalles(robotvrai);
		Hook hook = hookgenerator.hook_position(new Vec2(20, 1500));
		hook.ajouter_callback(new Callback(tirerballes, true));
		hooks.add(hook);		
		robotvrai.avancer(10, hooks);
		Assert.assertTrue(nb_balles == robotvrai.getNbrLances());
		robotvrai.avancer(50, hooks);
		Assert.assertTrue(nb_balles != robotvrai.getNbrLances());
	}

	
}