package gameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.jbox2d.dynamics.joints.WheelJoint;
import org.jbox2d.dynamics.joints.WheelJointDef;

import Game.GameWindow;
import neat.StatisticsTracker;
import neat.GenerationManager.FitnessGenome;

public class Car implements GameObject {

	// car design
	float scale = 0.8f;
	float wheelRadius = 0.5f;
	float wheelOffsetY = 0.8f;
	float wheelOffsetX = 1.25f;

	// persondesign
	float driverBodyHeight = 0.8f;
	float driverBodyWidth = 0.3f;
	float driverOffsetX = -0.5f;

	World world;

	// car assets
	Image carImage;
	Image wheelImage;
	Image driverImage;
	public double startingXPos;
	public double startingYPos;
	GameWindow gameWindow;
	double height;
	double width;
	public Body frontWheel;
	public Body backWheel;
	public WheelJoint frontJoint;
	public WheelJoint backJoint;
	public Body chassis;
	public Body driver;
	RevoluteJoint driverSeat;

	
	FitnessGenome fitnessGenome;
	int speciesID;
	
	
	
	//track score
	public float currentScore;
	
	public boolean isDead = false;

	// inputs defining how the car behaves
	public float applyTorque = 0;

	public Car(double startingXPos, double startingYPos, GameWindow gameWindow, FitnessGenome fitnessGenome) {
		this.fitnessGenome = fitnessGenome;
		this.startingXPos = startingXPos;
		this.startingYPos = startingYPos;
		this.gameWindow = gameWindow;

		height = (scale * 231.0 / gameWindow.engineToScreenScale);
		width = (scale * 366.0 / gameWindow.engineToScreenScale);

		// load car image
		try {
			carImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Sportscar.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			wheelImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("wheel.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			driverImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Classic-driver.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called by repaint to draw whole car
	@Override
	public void draw(Graphics g, int windowWidth, int windowHeight) {
		if(isInView()) {
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform old = g2d.getTransform();
			// draw wheel
			float diameter = (float) (scale * wheelRadius) * 2;
			int frontDrawLocationX = (int) gameWindow
					.getXPosRelToScreen(gameWindow.getXPosRelToCar(frontWheel.getPosition().x - diameter / 2.0));
			int frontDrawLocationY = (int) gameWindow.getYPosRelToScreen(frontWheel.getPosition().y + diameter / 2.0);
			double rotationRequiredFront = -frontWheel.getAngle();
			double frontLocationX = (diameter * gameWindow.engineToScreenScale) / 2;
			double frontLocationY = (diameter * gameWindow.engineToScreenScale) / 2;
			AffineTransform frontTx = AffineTransform.getRotateInstance(rotationRequiredFront, frontLocationX,
					frontLocationY);
			AffineTransform frontTy = AffineTransform.getTranslateInstance(frontDrawLocationX, frontDrawLocationY);
			g2d.transform(frontTy);
			g2d.drawImage(wheelImage, frontTx, null);
			g2d.setTransform(old);
	
			int backDrawLocationX = (int) gameWindow
					.getXPosRelToScreen(gameWindow.getXPosRelToCar(backWheel.getPosition().x - diameter / 2.0));
			int backDrawLocationY = (int) gameWindow.getYPosRelToScreen(backWheel.getPosition().y + diameter / 2.0);
			double rotationRequiredback = -backWheel.getAngle();
			double backLocationX = (diameter * gameWindow.engineToScreenScale) / 2;
			double backLocationY = (diameter * gameWindow.engineToScreenScale) / 2;
			AffineTransform backTx = AffineTransform.getRotateInstance(rotationRequiredback, backLocationX, backLocationY);
			AffineTransform backTy = AffineTransform.getTranslateInstance(backDrawLocationX, backDrawLocationY);
			g2d.transform(backTy);
			g2d.drawImage(wheelImage, backTx, null);
			g2d.setTransform(old);
	
			// draw driver
			float imageScale = 0.3f;
			int imageHeight = 332;
			int imageWidth = 241;
			float driverImageOffsetX = -0.5f * imageScale;
			Image currentImage = driverImage.getScaledInstance((int) (imageWidth * imageScale),
					(int) (imageHeight * imageScale), Image.SCALE_DEFAULT);
			int driverDrawLocationX = (int) gameWindow.getXPosRelToScreen(
					gameWindow.getXPosRelToCar(driver.getPosition().x - driverBodyWidth / 2.0 + driverImageOffsetX));
			int driverDrawLocationY = (int) gameWindow.getYPosRelToScreen(driver.getPosition().y + driverBodyHeight / 2.0);
			double rotationRequireddriver = -driver.getAngle();
			double driverLocationX = (driverBodyWidth * gameWindow.engineToScreenScale) / 2
					- driverImageOffsetX * gameWindow.engineToScreenScale;
			double driverLocationY = (driverBodyHeight * gameWindow.engineToScreenScale) / 2;
			AffineTransform driverTx = AffineTransform.getRotateInstance(rotationRequireddriver, driverLocationX,
					driverLocationY);
			AffineTransform driverTy = AffineTransform.getTranslateInstance(driverDrawLocationX, driverDrawLocationY);
			g2d.transform(driverTy);
			g2d.drawImage(currentImage, driverTx, null);
			g2d.setTransform(old);
	
			// draw chassis
			float carOffsetX = 0.4f;
			float carOffsetY = 0.05f;
			int drawLocationX = (int) gameWindow
					.getXPosRelToScreen(gameWindow.getXPosRelToCar(getUpperLeftXPos() - scale * carOffsetX));
			int drawLocationY = (int) gameWindow.getYPosRelToScreen(getUpperLeftYPos() + scale * carOffsetY);
	
			// Rotation information
	
			double rotationRequired = -chassis.getAngle();
			double locationX = (int) (width * gameWindow.engineToScreenScale) / 2
					+ scale * carOffsetX * gameWindow.engineToScreenScale;
			double locationY = (int) (height * gameWindow.engineToScreenScale) / 2
					+ scale * carOffsetY * gameWindow.engineToScreenScale;
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
	
			// Drawing the rotated image at the required drawing locations
			AffineTransform ty = AffineTransform.getTranslateInstance(drawLocationX, drawLocationY);
			g2d.transform(ty);
			g2d.drawImage(carImage, tx, null);
			
			g2d.setTransform(old);
//			g2d.drawRect(
//					(int) gameWindow.getXPosRelToScreen(gameWindow.getXPosRelToCar(chassis.getPosition().x - wheelOffsetX)),
//					(int) gameWindow.getYPosRelToScreen(chassis.getPosition().y + height / 8),
//					(int) (wheelOffsetX * 2f * gameWindow.engineToScreenScale),
//					(int) (height * gameWindow.engineToScreenScale / 4));
//			g2d.drawRect(
//					(int) gameWindow
//							.getXPosRelToScreen(gameWindow.getXPosRelToCar(driver.getPosition().x - driverBodyWidth / 2f)),
//					(int) gameWindow.getYPosRelToScreen(driver.getPosition().y + driverBodyHeight / 2f),
//					(int) (driverBodyWidth * gameWindow.engineToScreenScale),
//					(int) (driverBodyHeight * gameWindow.engineToScreenScale));
			drawLocationX = (int) gameWindow
					.getXPosRelToScreen(gameWindow.getXPosRelToCar(getUpperLeftXPos()+scale*1.5));
			drawLocationY = (int) gameWindow.getYPosRelToScreen(getUpperLeftYPos());
			g2d.setColor(new Color(0, 0, 0));
			g2d.setFont(new Font("Arial", 0, 40));
			g2d.drawString(Integer.toString(fitnessGenome.speciesID), drawLocationX, drawLocationY);
			
		}

	}

	private double getUpperLeftXPos() {
		return chassis.getPosition().x - width / 2.0;
	}

	private double getUpperLeftYPos() {
		return chassis.getPosition().y + height / 2.0;
	}

	// called by engine to create body
	public void createBody(World world) {
		this.world = world;
		// create chassis
		PolygonShape chassisShape = new PolygonShape();
		chassisShape.setAsBox((float) wheelOffsetX, (float) height / 8);
		BodyDef chassisDef = new BodyDef();
		chassisDef.type = BodyType.DYNAMIC;
		chassisDef.position.set((float) startingXPos, (float) startingYPos);
		FixtureDef chassisFixture = new FixtureDef();
		chassisFixture.density = 1f;
		chassisFixture.shape = chassisShape;
		chassisFixture.friction = 0.1f;
		chassisFixture.filter.categoryBits = 1;
		chassisFixture.filter.maskBits = 2;
		chassis = world.createBody(chassisDef);
		chassis.createFixture(chassisFixture);

		// create wheels
		CircleShape frontWheelShape = new CircleShape();
		frontWheelShape.setRadius((float) (scale * wheelRadius));
		BodyDef frontWheelDef = new BodyDef();
		frontWheelDef.type = BodyType.DYNAMIC;
		frontWheelDef.position.set((float) startingXPos + wheelOffsetX * (float) scale,
				(float) startingYPos - wheelOffsetY * (float) scale);
		FixtureDef frontWheelFixture = new FixtureDef();
		frontWheelFixture.density = 0.1f;
		frontWheelFixture.shape = frontWheelShape;
		frontWheelFixture.friction = 0.9f;
		frontWheelFixture.filter.categoryBits = 1;
		frontWheelFixture.filter.maskBits = 2;
		frontWheel = world.createBody(frontWheelDef);
		frontWheel.createFixture(frontWheelFixture);

		CircleShape backWheelShape = new CircleShape();
		backWheelShape.setRadius((float) (scale * wheelRadius));
		BodyDef backWheelDef = new BodyDef();
		backWheelDef.type = BodyType.DYNAMIC;
		backWheelDef.position.set((float) startingXPos - wheelOffsetX * (float) scale,
				(float) startingYPos - wheelOffsetY * (float) scale);
		FixtureDef backWheelFixture = new FixtureDef();
		backWheelFixture.density = 0.1f;
		backWheelFixture.shape = backWheelShape;
		backWheelFixture.friction = 0.9f;
		backWheelFixture.filter.categoryBits = 1;
		backWheelFixture.filter.maskBits = 2;
		backWheel = world.createBody(backWheelDef);
		backWheel.createFixture(backWheelFixture);

		WheelJointDef frontJointDef = new WheelJointDef();
		frontJointDef.bodyA = chassis;
		frontJointDef.bodyB = frontWheel;
		frontJointDef.localAnchorA.set(new Vec2(wheelOffsetX * (float) scale, -wheelOffsetY * (float) scale));
		frontJointDef.localAnchorB.set(0, 0);
		frontJointDef.enableMotor = true;
		frontJointDef.maxMotorTorque = 2f;
		frontJointDef.dampingRatio = 10f;
		frontJointDef.frequencyHz = 12f;
		frontJointDef.localAxisA.set(new Vec2(0, -wheelOffsetY * (float) scale));
		frontJointDef.collideConnected = false;
		frontJoint = (WheelJoint) world.createJoint(frontJointDef);

		WheelJointDef backJointDef = new WheelJointDef();
		backJointDef.bodyA = chassis;
		backJointDef.bodyB = backWheel;
		backJointDef.localAnchorA.set(new Vec2(-wheelOffsetX * (float) scale, -wheelOffsetY * (float) scale));
		backJointDef.localAnchorB.set(0, 0);
		backJointDef.enableMotor = true;
		backJointDef.motorSpeed = -20f;
		backJointDef.maxMotorTorque = 0f;
		backJointDef.dampingRatio = 10f;
		backJointDef.frequencyHz = 12f;
		backJointDef.localAxisA.set(new Vec2(0, -wheelOffsetY * (float) scale));
		backJointDef.collideConnected = false;
		backJoint = (WheelJoint) world.createJoint(backJointDef);

		// create driver
		PolygonShape driverShape = new PolygonShape();
		driverShape.setAsBox(driverBodyWidth * scale / 2f, driverBodyHeight * scale / 2f);
		BodyDef driverDef = new BodyDef();
		driverDef.type = BodyType.DYNAMIC;
		driverDef.setPosition(new Vec2((float) startingXPos, (float) startingYPos));
		FixtureDef driverFixture = new FixtureDef();
		driverFixture.shape = driverShape;
		driverFixture.density = 0.1f;
		driverFixture.isSensor = true;
		driverFixture.filter.maskBits = 2;
		driver = world.createBody(driverDef);
		driver.createFixture(driverFixture);

		RevoluteJointDef driverSeatDef = new RevoluteJointDef();
		driverSeatDef.bodyA = chassis;
		driverSeatDef.bodyB = driver;
		driverSeatDef.localAnchorA.set(driverOffsetX * scale, 0);
		driverSeatDef.localAnchorB.set(0, -driverBodyHeight * scale / 2f);
		driverSeatDef.collideConnected = false;
		driverSeatDef.enableLimit = true;
		driverSeatDef.enableMotor = false;
		driverSeat = (RevoluteJoint) world.createJoint(driverSeatDef);

	}

	// called by engine every timeStep
	public void timeStep() {
		checkForDeath();
		if (!isDead) {
			evaluateGenome();
			updateTorque();
			this.currentScore = (float) (chassis.getPosition().x-startingXPos > currentScore? chassis.getPosition().x-startingXPos:currentScore);
		}
		StatisticsTracker.update(this);
	}

	// updates torque based on applyTorque
	
	private void updateTorque() {
		float maxSpeed = 20f;
		float maxTorque = 60f;
		float torqueIncrease = 1f;
		float angularImpulse = 0.1f;
		float angularDrag = 0.03f;
		float currentTorque = backJoint.getMaxMotorTorque();
		float breakingTorque = 4f;


		float newTorque;
		
		
		if(applyTorque == 0f) {
			newTorque = 0;
		}else {
			newTorque = currentTorque + torqueIncrease * Math.abs(applyTorque);
		}
		if (newTorque > maxTorque) {
			newTorque = maxTorque;
		} else if (newTorque < -maxTorque) {
			newTorque = -maxTorque;
		}
		
		float newAngularImpulse = angularImpulse * applyTorque;
		chassis.applyAngularImpulse(newAngularImpulse);
		//drag
		float currentAngularVel = chassis.getAngularVelocity();
		chassis.setAngularVelocity(currentAngularVel - currentAngularVel * angularDrag);
		
		if(applyTorque < 0) {
			backJoint.setMotorSpeed(maxSpeed);
			frontJoint.setMotorSpeed(maxSpeed);
		}else if (applyTorque > 0) {
			backJoint.setMotorSpeed(-maxSpeed);
			frontJoint.setMotorSpeed(-maxSpeed);
		}
		if(chassis.getLinearVelocity().x * frontJoint.getMotorSpeed() > 0) {
			frontJoint.setMaxMotorTorque(breakingTorque * Math.abs(applyTorque));
		}else {
			frontJoint.setMaxMotorTorque(0f);
		}
		
		
		
		backJoint.setMaxMotorTorque(newTorque);	
	}

	// reads data to output to NN
	private void evaluateGenome() {
		float angle = chassis.getAngle() / (float)Math.PI;
		float angularVelocity = chassis.getAngularVelocity()/3f;
		Vec2 velocity = chassis.getLinearVelocity().clone();
		velocity.normalize();
		float velocityX = velocity.x;
		float velocityY = velocity.y;
		float touchingGround;
		if ((backWheel.getContactList() != null && backWheel.getContactList().contact.getFixtureA().getFilterData().categoryBits == 2)
				|| (frontWheel.getContactList() != null && frontWheel.getContactList().contact.getFixtureA().getFilterData().categoryBits == 2)) {
			touchingGround = 1f;
		}else {
			touchingGround = -1f;
		}
		
		
		float[] out = fitnessGenome.genome.evaluate(new float[] {angle, angularVelocity, velocityX, velocityY, touchingGround});
		this.applyTorque = (out[0]-out[1])/2f;
		
	}
	
	public void resetCar() {
	}

	private void checkForDeath() {
		if (!isDead && driver.getContactList() != null
				&& driver.getContactList().contact.getFixtureA().getFilterData().categoryBits == 2) {
			isDead = true;
			applyTorque = 0;
		}
	}
	
	public void destroyBody() {
		world.destroyBody(backWheel);
		world.destroyBody(frontWheel);
		world.destroyBody(chassis);
		world.destroyBody(driver);
		world.destroyJoint(frontJoint);
		world.destroyJoint(backJoint);
		world.destroyJoint(driverSeat);
	}
	
	private boolean isInView() {
		return gameWindow.getXPosRelToCar(chassis.getPosition().x) > -(wheelOffsetX * 2f + 0.5f) ;
	}

}
