package Game;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import gameObjects.Car;
import gameObjects.Terrain;
import graphicObjects.Background;
import neat.StatisticsTracker;

@SuppressWarnings("serial")
public class GameWindow extends JPanel {
	
	public long fps = 0l;
	public long fpsEngine = 0l;
	public int engineToScreenScale = 100;
	
	public Engine engine;
	
	public volatile Set<Character> pressedKeys;
	
	public int distanceCarLeft = 5;
	public Car followedCar;
	Background background;
	public Terrain terrain;
	
	public GameWindow() {
		super();
		background = new Background(this);
		terrain = new Terrain(this);
		pressedKeys = new HashSet<Character>();
		this.addComponentListener(new ResizeListener());
		addKeyListener();	
	}
	
	@Override
	public void paintComponent(Graphics g) {
		long start = System.currentTimeMillis();
		if(StatisticsTracker.firstCar != null) {
			followedCar = StatisticsTracker.firstCar;
		}
		background.draw(g, this.getWidth(), this.getHeight());
		if(GameSettings.drawGame) {
			drawCars(g);
			terrain.draw(g, this.getWidth(), this.getHeight());
			
		}
		
		fps = 1000/(System.currentTimeMillis()-start +1);
	}
	
	private void drawCars(Graphics g) {
		for (Iterator carsIter = engine.activeCars.iterator(); carsIter.hasNext();) {
			Car car = (Car) carsIter.next();
			car.draw(g, this.getWidth(), this.getHeight());
		}
		
		
//		followedCar.draw(g, this.getWidth(), this.getHeight());
	}
	
	public double getXPosRelToCar(double xPos) {
		return xPos - followedCar.chassis.getPosition().x + distanceCarLeft;
	}
	public double getYPosRelToScreen(double yPos) {
		return -yPos*engineToScreenScale + this.getHeight();
	}
	public double getXPosRelToScreen(double xPos) {
		return xPos*engineToScreenScale;
	}
	
	private void addKeyListener() {
		Action up = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedKeys.add('W');
				
			}
		};
		Action upReleased = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedKeys.remove('W');
				
			}
		};
		this.getActionMap().put("Up", up);
		this.getInputMap().put(KeyStroke.getKeyStroke("W"), "Up");
		this.getActionMap().put("UpReleased", upReleased);
		this.getInputMap().put(KeyStroke.getKeyStroke("released W"), "UpReleased");
		
		Action down = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedKeys.add('S');
				
			}
		};
		Action downReleased = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pressedKeys.remove('S');
				
			}
		};
		this.getActionMap().put("Down", down);
		this.getInputMap().put(KeyStroke.getKeyStroke("S"), "Down");
		this.getActionMap().put("DownReleased", downReleased);
		this.getInputMap().put(KeyStroke.getKeyStroke("released S"), "DownReleased");
	}
	
	class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            distanceCarLeft = getSize().width / 200;
        }
}
	
}
