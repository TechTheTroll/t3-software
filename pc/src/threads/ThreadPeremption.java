package threads;

import pathfinding.dstarlite.GridSpace;
import utils.Config;
import utils.ConfigInfo;
import utils.Log;
import utils.Sleep;
import container.Service;

/**
 * Thread qui gère la péremption des obstacles en dormant
 * le temps exact entre deux péremptions.
 * @author pf
 *
 */

public class ThreadPeremption extends Thread implements Service
{
	private GridSpace gridspace;
	protected Log log;

	private int dureePeremption;

	public ThreadPeremption(Log log, GridSpace gridspace)
	{
		this.log = log;
		this.gridspace = gridspace;
	}
	
	@Override
	public void run()
	{
		if(true) return; // Adieu
		while(true)
		{
			gridspace.deleteOldObstacles();

			long prochain = gridspace.getNextDeathDate();
			
			/**
			 * S'il n'y a pas d'obstacles, on dort de dureePeremption, qui est la durée minimale avant la prochaine péremption.
			 */
			if(prochain == Long.MAX_VALUE)
				Sleep.sleep(dureePeremption);
			else
				// Il faut toujours s'assurer qu'on dorme un temps positif. Il y a aussi une petite marge
				Sleep.sleep(Math.min(dureePeremption, Math.max(prochain - System.currentTimeMillis() + 5, 10)));
		}
//		log.debug("Fermeture de ThreadPeremption");
	}

	@Override
	public void updateConfig(Config config)
	{}

	@Override
	public void useConfig(Config config)
	{
		dureePeremption = config.getInt(ConfigInfo.DUREE_PEREMPTION_OBSTACLES);
	}

}
