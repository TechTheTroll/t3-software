package tests.graphicLib;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {

	private Fenetre fenetre;

	public MouseListener(Fenetre fenetre)
	{
		super();
		this.fenetre = fenetre;
		fenetre.addMouseListener(this);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		System.out.println("AAAAAAAAAAAAA");
		Point point = e.getPoint();
		fenetre.afficheCoordonnees(point);
		fenetre.repaint();
	}
}