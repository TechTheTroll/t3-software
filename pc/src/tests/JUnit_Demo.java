package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import hook.Callback;
import hook.Executable;
import hook.Hook;
import hook.HookGenerator;
import hook.methodes.TakeFire;
import hook.methodes.TirerBalles;

import org.junit.Before;
import org.junit.Test;

import robot.Cote;
import robot.RobotChrono;
import robot.RobotVrai;
import scripts.Script;
import scripts.ScriptManager;
import smartMath.Vec2;
import table.Table;

public class JUnit_Demo extends JUnit_Test {

	private ScriptManager scriptmanager;
	private Script s;
	private RobotVrai robotvrai;
	private RobotChrono robotchrono;
	private Table table;
	private HookGenerator hookgenerator;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		config.set("couleur", "jaune");
		scriptmanager = (ScriptManager)container.getService("ScriptManager");
		robotvrai = (RobotVrai)container.getService("RobotVrai");
		robotchrono = new RobotChrono(config, log);
		robotchrono.majRobotChrono(robotvrai);
		table = (Table)container.getService("Table");
 		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		robotvrai.setPosition(new Vec2(1300, 1200));
		robotvrai.setOrientation((float)Math.PI);
		robotvrai.set_vitesse_rotation("entre_scripts");
		robotvrai.set_vitesse_translation("entre_scripts");
		container.getService("threadPosition");
		container.demarreThreads();

	}

	@Test
	public void defile() throws Exception {
		robotvrai.avancer(1000);
		robotvrai.tourner_relatif(-((float)Math.PI));
		robotvrai.avancer(800);
	}
	
	@Test
	public void arbre() throws Exception {
		s = (Script)scriptmanager.getScript("ScriptTree");
		s.agit(1, robotvrai, table, true);
		robotvrai.avancer(500);
	}

	@Test
	public void ramasse_feu_droit() throws Exception
	{
		robotvrai.avancer(100);
		robotvrai.takefire(Cote.DROIT);
	}
	
	@Test
	public void ramasse_feu_gauche() throws Exception
	{
		robotvrai.avancer(100);
		robotvrai.takefire(Cote.GAUCHE);
	}
	
	@Test
	public void depose_fruits() throws Exception
	{
		s = (Script)scriptmanager.getScript("ScriptDeposerFruits");
		s.agit(0, robotvrai, table, false);
	}
	
	@Test
	public void tir_balles() throws Exception
	{
		robotvrai.avancer(1000);
		s = (Script)scriptmanager.getScript("ScriptLances");
		s.agit(0, robotvrai, table, true);
	}
	
	@Test
	public void depose_feu_gauche() throws Exception
	{
		robotvrai.avancer(100);
		robotvrai.lever_pince(Cote.DROIT);
		robotvrai.lever_pince(Cote.GAUCHE);
		robotvrai.takefire(Cote.GAUCHE);
		s = (Script)scriptmanager.getScript("ScriptDeposerFeu");
		s.agit(2, robotvrai, table, true);
	}

	@Test
	public void depose_feu_droit() throws Exception
	{
		robotvrai.avancer(100);
		robotvrai.lever_pince(Cote.DROIT);
		robotvrai.lever_pince(Cote.GAUCHE);
		robotvrai.takefire(Cote.DROIT);
		s = (Script)scriptmanager.getScript("ScriptDeposerFeu");
		s.agit(2, robotvrai, table, true);
	}
	
	@Test
	public void choré1() throws Exception
	{
		hookgenerator = (HookGenerator)container.getService("HookGenerator");
		ArrayList<Hook> hooks = new ArrayList<Hook>();
		Executable takefire = new TakeFire(robotvrai, Cote.DROIT);
		Hook hook = hookgenerator.hook_feu(Cote.GAUCHE);
		hook.ajouter_callback(new Callback(takefire, false));
		hooks.add(hook);
		robotvrai.avancer(1000,hooks);
		robotvrai.tourner((float)(3*(Math.PI)/2));
		robotvrai.avancer(500,hooks);
		robotvrai.tourner(0);
		robotvrai.avancer(1000,hooks);
	}
	
	
}
