package graphicObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Game.GameWindow;


public class Background implements GraphicObjects{
	Image backgroundImage;
	GameWindow gameWindow;

	public Background(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
		try {
			backgroundImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Background.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void draw(Graphics g, int windowWidth, int windowHeight) {

		int height = windowHeight;
		int width = (int)(height/424.0 * 1000);
		g.drawImage(backgroundImage, 0, 0, width, height, null);
		g.setColor(Color.red);
		g.drawString(gameWindow.fps + " FPS", 10, 10);
		g.drawString(gameWindow.fpsEngine + " FPS", 10, 20);
		g.drawString(gameWindow.followedCar.currentScore + " Meters", 10, 30);
		g.drawString(String.format("Current Generation: %d Current Batch: %d", gameWindow.engine.currentGeneration, gameWindow.engine.currentBatch) , 10, 40);
	}

}
