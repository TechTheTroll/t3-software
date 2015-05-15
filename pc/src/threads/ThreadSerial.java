package threads;

import java.util.ArrayList;

import permissions.ReadOnly;
import planification.Pathfinding;
import planification.dstar.GridPoint;
import planification.dstar.LocomotionNode;
import serial.SerialConnexion;
import table.ObstacleManager;
import utils.Config;
import utils.Log;
import utils.Vec2;
import container.Service;
import exceptions.FinMatchException;
import exceptions.SerialConnexionException;

/**
 * Thread qui écoute la série et y répond si besoin.
 * Il peut:
 * - prévenir la table si un obstacle arrive
 * - demander au pathfinding le chemin à suivre
 * @author pf
 *
 */

public class ThreadSerial extends Thread implements Service
{

	protected Log log;
	protected Config config;
	private SerialConnexion serie;
	private Pathfinding pathfinding;
	private IncomingDataBuffer buffer;
	
	public ThreadSerial(Log log, Config config, Pathfinding pathfinding, Pathfinding strategie, ObstacleManager obstaclemanager, SerialConnexion serie, IncomingDataBuffer buffer)
	{
		this.log = log;
		this.config = config;
		this.serie = serie;
		this.pathfinding = pathfinding;
		this.buffer = buffer;
		
		Thread.currentThread().setPriority(2);
		updateConfig();
	}

	@Override
	public void run()
	{
		/**
		 * StartMatchLock permet de signaler le départ du match aux autres threads
		 * Il est utilisé par ThreadTimer
		 */
		StartMatchLock lock = StartMatchLock.getInstance();
		ArrayList<String> data = new ArrayList<String>();
		while(!Config.stopThreads && !Config.finMatch)
		{
			try {
				synchronized(serie)
				{
					serie.wait();
				}
			} catch (InterruptedException e) {
				// TODO
				e.printStackTrace();
			}
			String first = serie.read();
			log.debug(first);
			data.clear();
			switch(first)
			{
				case "obs":
					int xBrut = Integer.parseInt(serie.read());
					int yBrut = Integer.parseInt(serie.read());
					int xEnnemi = Integer.parseInt(serie.read());
					int yEnnemi = Integer.parseInt(serie.read());

					buffer.add(new IncomingData(new Vec2<ReadOnly>(xBrut, yBrut), new Vec2<ReadOnly>(xEnnemi, yEnnemi)));
					break;

				case "nxt":
					int x = Integer.parseInt(serie.read());
					int y = Integer.parseInt(serie.read());
					// Réécrire avec x, y
					ArrayList<LocomotionNode> itineraire = pathfinding.getPath(new GridPoint(x,y));
					try {
						serie.communiquer(itineraire);
					} catch (SerialConnexionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FinMatchException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				
				case "go":
					synchronized(lock)
					{
						lock.notifyAll();
					}
					break;
					
				case "end":
					// Fin du match, on coupe la série et on arrête ce thread
					serie.close();
					return;
			}
		}
	}
	
	@Override
	public void updateConfig() {
		serie.updateConfig();
		pathfinding.updateCost();
		buffer.updateConfig();
	}

}