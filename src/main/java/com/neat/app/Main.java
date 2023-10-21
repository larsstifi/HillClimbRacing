import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import Game.Engine;
import Game.GameWindow;
import Game.SettingsWindow;


public class Main {
	static JFrame frame;
	static Thread engineThread;
	static Engine engine;
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		GameWindow gameWindow = new GameWindow();
		engine = new Engine(gameWindow);
		gameWindow.engine = engine;
		SettingsWindow settingsWindow = new SettingsWindow();
		engineThread = new Thread(engine);
		engineThread.start();
		frame = new JFrame();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                settingsWindow, gameWindow);
		frame.getContentPane().add(splitPane);
		//frame.getContentPane().add(gameWindow, BorderLayout.CENTER);
		//frame.getContentPane().add(settingsWindow, BorderLayout.WEST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(2000,1200);
		frame.setVisible(true);
	}
}
