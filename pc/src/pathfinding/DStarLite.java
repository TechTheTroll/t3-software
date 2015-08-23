package pathfinding;

import java.util.ArrayList;
import java.util.Iterator;

import permissions.ReadOnly;
import utils.Config;
import utils.Log;
import utils.Vec2;
import container.Service;

/**
 * Recherche de chemin avec replanification rapide.
 * @author pf
 *
 */

public class DStarLite implements Service
{
	protected Log log;
	private GridSpace gridspace;

	public DStarLite(Log log, GridSpace gridspace)
	{
		this.log = log;
		this.gridspace = gridspace;
		for(int i = 0; i < GridSpace.NB_POINTS_POUR_DEUX_METRES * GridSpace.NB_POINTS_POUR_TROIS_METRES; i++)
		{
			memory[i] = new DStarLiteNode(i);
		}
	}
	
	private DStarLiteNode[] memory = new DStarLiteNode[GridSpace.NB_POINTS_POUR_DEUX_METRES * GridSpace.NB_POINTS_POUR_TROIS_METRES];

	private ArrayList<DStarLiteNode> openset = new ArrayList<DStarLiteNode>();
	private int km;
	private DStarLiteNode arrivee;
	private DStarLiteNode depart;
	private int last;
	private long nbPF = 0;
	
	private Cle knew = new Cle();
	private Cle inutile = new Cle();

	private Cle calcKey(DStarLiteNode s, Cle copy)
	{
		copy.set(add(Math.min(s.g,s.rhs), distanceHeuristique(s.gridpoint), km),
				Math.min(s.g, s.rhs));
		return copy;
	}

	public DStarLiteNode getFromMemory(int gridpoint)
	{
		DStarLiteNode out = memory[gridpoint];
		
		/**
		 * Si ce point n'a pas encore été utilisé pour ce pathfinding, on l'initialise
		 */
		if(out.nbPF != nbPF)
		{
			out.g = Integer.MAX_VALUE;
			out.rhs = Integer.MAX_VALUE;
			out.nbPF = nbPF;
		}
		return out;
	}
	
	/**
	 * Ajout dans une liste triée
	 * @param u
	 */
	private void addToOpenset(DStarLiteNode u)
	{
		if(openset.contains(u))
			log.critical("Déjà dans openset !");
		Iterator<DStarLiteNode> iterator = openset.listIterator();
		int i = 0;
		while(iterator.hasNext())
		{
			if(iterator.next().cle.isLesserThan(u.cle))
			{
				openset.add(i, u);
				return;
			}
			i++;
		}
		openset.add(u);
		
/*		int borneInf = 0;
		int borneSup = openset.size()-1;
		int milieu = (borneInf + borneSup) / 2;
		
		if(!openset.get(borneInf).cle.isLesserThan(u.cle))
			openset.add(0, u);
		else if(!u.cle.isLesserThan(openset.get(borneSup).cle))
			openset.add(u);
		else
		{
			while(borneSup - borneInf > 1)
			{
				Cle c = openset.get(milieu).cle;
				if(c.isLesserThan(u.cle))
					borneInf = milieu;
				else if(u.cle.isLesserThan(c))
					borneSup = milieu;
				else
				{
					// On a trouvé une clé égale
					openset.add(milieu, u);
					return;
				}
				milieu = (borneInf + borneSup) / 2;
			}
			openset.add(borneSup, u);
		}*/
	}
	
	private void updateVertex(DStarLiteNode u)
	{
		boolean contains = openset.contains(u);
		if(u.g != u.rhs)
		{
			calcKey(u, u.cle);
			if(contains)
				openset.remove(u);
			addToOpenset(u);
		}
		else if(contains)
			openset.remove(u);
	}
	
	private void computeShortestPath()
	{
		DStarLiteNode u;
		while(!openset.isEmpty() && ((u = openset.get(0)).cle.isLesserThan(calcKey(depart, inutile)) || depart.rhs > depart.g))
		{
//			Iterator<DStarLiteNode> iterator = openset.listIterator();
//			while(iterator.hasNext())
//				log.debug(iterator.next().cle);
//			log.debug("Nouvelle itération, u = "+u.gridpoint);
//			log.debug("Taille openset = "+openset.size());
			
			Cle kold = u.cle.clone();
			calcKey(u, knew);
			if(kold.isLesserThan(knew))
			{
				knew.copy(u.cle);
				openset.remove(0);
				addToOpenset(u);
			}
			else if(u.g > u.rhs)
			{
				u.g = u.rhs;
				openset.remove(0);
				for(int i = 0; i < 8; i++)
				{
					int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
					if(voisin < 0)
						continue;
					DStarLiteNode s = getFromMemory(voisin);
					s.rhs = Math.min(s.rhs, add(gridspace.distance(u.gridpoint, i), u.g));
					updateVertex(s);
				}
			}
			else
			{
				int gold = u.g;
				u.g = Integer.MAX_VALUE;
				for(int i = 0; i < 8; i++)
				{
					int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
					if(voisin < 0)
						continue;
					DStarLiteNode s = getFromMemory(voisin);
					if(s == null)
						continue;
					if(s.rhs == add(gridspace.distance(u.gridpoint, i), gold) && s.gridpoint != arrivee.gridpoint)
					{
						s.rhs = Integer.MAX_VALUE;
						for(int j = 0; j < 8; j++)
						{
							voisin = gridspace.getGridPointVoisin(s.gridpoint, j);
							if(voisin < 0)
								continue;
							DStarLiteNode s2 = getFromMemory(voisin);
							s.rhs = Math.min(s.rhs, add(gridspace.distance(s.gridpoint, j), s2.g));
						}
					}
					updateVertex(s);
				}
				if(u.rhs == gold && u.gridpoint != depart.gridpoint)
				{
					u.rhs = Integer.MAX_VALUE;
					for(int i = 0; i < 8; i++)
					{
						int voisin = gridspace.getGridPointVoisin(u.gridpoint, i);
						if(voisin < 0)
							continue;
						DStarLiteNode s = getFromMemory(voisin);
						u.rhs = Math.min(u.rhs, add(gridspace.distance(u.gridpoint, i), s.g));
					}
				}
				updateVertex(u);
			}

		}
	}

	/**
	 * Calcule un nouvel itinéraire.
	 * @param arrivee
	 * @param depart
	 */
	public void computeNewPath(Vec2<ReadOnly> arrivee, Vec2<ReadOnly> depart)
	{
		nbPF++;
		km = 0;
		this.depart = getFromMemory(gridspace.computeGridPoint(depart));
		last = this.depart.gridpoint;

		this.arrivee = getFromMemory(gridspace.computeGridPoint(arrivee));
		this.arrivee.rhs = 0;
		this.arrivee.cle.set(distanceHeuristique(this.arrivee.gridpoint), 0);

		openset.clear();
		openset.add(this.arrivee);

		computeShortestPath();
	}
	
	private int distanceHeuristique(int gridpoint)
	{
		return gridspace.distanceHeuristique(depart.gridpoint, gridpoint);
	}

	@Override
	public void updateConfig(Config config)
	{}

	@Override
	public void useConfig(Config config)
	{}
	
	/**
	 * Met à jour le pathfinding
	 */
	public void updatePath(Vec2<ReadOnly> positionRobot)
	{
		depart = getFromMemory(gridspace.computeGridPoint(positionRobot));
		km += distanceHeuristique(last);
		last = depart.gridpoint;
		
		// TODO
		
		computeShortestPath();
	}
	
	public int add(int a, int b)
	{
		if(a == Integer.MAX_VALUE || b  == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return a + b;
	}

	public int add(int a, int b, int c)
	{
		if(a == Integer.MAX_VALUE || b  == Integer.MAX_VALUE || c  == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		return a + b + c;
	}

}
