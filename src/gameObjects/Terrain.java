package gameObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import Game.GameWindow;

public class Terrain implements GameObject{
	
	int length = 1000000;
	int stepSize = 10;
	int grassHeight = 30;
	GameWindow gameWindow;
	Polygon polygonDirt;
	Polygon polygonGrass;
	
	double[] noiseMap;
	
	public Body body;

	public Terrain(GameWindow gameWindow) {
		this.gameWindow = gameWindow;
		generateTerrain();
	}
	
	@Override
	public void draw(Graphics g, int windowWidth, int windowHeight) {
		int startPoint = (int)((gameWindow.followedCar.chassis.getPosition().x - gameWindow.distanceCarLeft)*gameWindow.engineToScreenScale);
		startPoint = startPoint<0?0:startPoint;
		int endPoint = startPoint + gameWindow.getWidth();
		endPoint = endPoint>=length?length-1:endPoint;
		polygonGrass = new Polygon();
		polygonDirt = new Polygon();
		for(int i = startPoint; i <= endPoint; i+=stepSize) {
			polygonDirt.addPoint(i-startPoint, windowHeight-(int)noiseMap[i/stepSize]+grassHeight);
			polygonGrass.addPoint(i-startPoint, windowHeight-(int)noiseMap[i/stepSize]);
		}
		polygonDirt.addPoint(endPoint-startPoint, windowHeight+1);
		polygonDirt.addPoint(0, windowHeight+1);
		polygonGrass.addPoint(endPoint-startPoint, windowHeight+1);
		polygonGrass.addPoint(0, windowHeight+1);
		g.setColor(new Color(0,153,0));
		g.fillPolygon(polygonGrass);
		g.setColor(new Color(102,51,0));
		g.fillPolygon(polygonDirt);
	}
	
	private void generateTerrain() {
		noiseMap = generateNoiseMap();
	}
	private double[] generateNoiseMap() {
		double scale = 800;
		double heightMult = 600;
		double baseHight = 100;
		double persistance = 0.6;
		double lacunarity = 1.2;
		int octaves = 6;
		
		
		
		
		double[] noiseMap = new double[length];
		SimplexNoise_octave noise = new SimplexNoise_octave(SimplexNoise_octave.RANDOMSEED);
		for (int i = 0; i < noiseMap.length; i++) {
			double curHeightMult = Math.pow(((double)i/noiseMap.length), 0.2 )* heightMult;
			//double curHeightMult = heightMult/2.0;
			double frequency = 1f;
			double amplitude = 1f;
			for (int j = 0; j < octaves; j++) {
				noiseMap[i] += (noise.noise((i*stepSize / scale)*frequency, 0)+1)/2 * curHeightMult * amplitude;
				frequency *= lacunarity;
				amplitude *= persistance;
			}
			noiseMap[i] += baseHight;
			
		}
		return noiseMap;
	}
	
	public void createBody(World world) {
		BodyDef terrainDef = new BodyDef();
		terrainDef.type = BodyType.STATIC;
		body = world.createBody(terrainDef);
		body.setType(BodyType.STATIC);
		addTerrainFixtures(body);
	}
	
	public void newBody(World world) {
		world.destroyBody(body);
		generateTerrain();
		createBody(world);
	}
	
	private void addTerrainFixtures(Body body) {
		int tile = 20;
		ChainShape terrainChain = new ChainShape();
		Vec2[] vertices = new Vec2[length/tile+2];
		for (int i = length/tile-1; i >= 0; i--) {
			vertices[length/tile-1-i] = new Vec2(((float)i*tile)/gameWindow.engineToScreenScale, ((float)noiseMap[i*tile/stepSize]) / gameWindow.engineToScreenScale);
		}
		vertices[length/tile-2] = new Vec2(0f, 10f);
		vertices[length/tile-1] = new Vec2((float)(length-1)/gameWindow.engineToScreenScale, 10f);
		
		terrainChain.createLoop(vertices, length/tile);
		FixtureDef terrainFixtureDef = new FixtureDef();
		terrainFixtureDef.shape = terrainChain;
		terrainFixtureDef.friction = 0.9f;
		terrainFixtureDef.filter.categoryBits = 2;
		terrainFixtureDef.filter.maskBits = 1;
		body.createFixture(terrainFixtureDef);
	}
	
}
