package projectClasses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import projectDatatypes.Vec2D;
import projectInterfaces.ConsumableInterface;

//TODO Variable Clean-up

//Base class for Consumable type GameObjects, namely Food and Poison
public abstract class Consumable extends GameObject implements ConsumableInterface {
	
	protected static final int InfLifeTime = -1;
	protected static final float MaxLifeTime = 10000; // in ms (10 secs)
	protected static final float DefaultLifeTime = 8000; // in ms (5 secs) 
	protected static final float DefaultDecayTime = 2500; // in ms (1 sec)
	protected static final int DefaultPowerLevel = 1;//3;	// in unit blocks
	protected static final int DefaultShapeCapasity = 3;
	protected static final float DefaultWidth = 10f; //in px
	protected static final float DefaultDecayWidth = 2f; //in px
	protected static final float MinWidth = 2f; //in px
	
	protected int PowerLevel;
	protected float ShapeWidth;
	protected float DecayWidth;
	protected float LifeTime;	 	// in ms 
	protected int DecayTicks; 	  //amt ticks before decay in GrowthValue
	protected int TicksSinceDecay;  //amt ticks since last decay in GrowthValue
	protected int LifeTicksRemaining;  //amt ticks since spawn
	
	protected Consumable(Vec2D location, Object manager, Color color, int shapeCapacity, int powerLevel, float lifeTime, float decayTime){
		
		super(location, shapeCapacity, color, manager);
		
		PowerLevel = powerLevel;
		TicksSinceDecay = 0;
		
		if(lifeTime > 0) {
			if (lifeTime < MaxLifeTime) {
				LifeTime = lifeTime;
			}
			else {
				LifeTime = MaxLifeTime;
			}
		}
		else {
			LifeTime = InfLifeTime;
		}
		
		ShapeWidth = DefaultWidth;
		DecayWidth = DefaultDecayWidth;
		DecayTicks = Math.round(decayTime / TickInterval);
		LifeTicksRemaining = Math.round(lifeTime / TickInterval);
		
		ComputeShapes();
	}
	protected Consumable(Vec2D location, Object manager, Color color, int powerLevel, float lifeTime, float decayTime){
		this(location, manager, color, DefaultShapeCapasity, powerLevel, lifeTime, decayTime);
	}
	protected Consumable(Vec2D location, Object manager, int powerLevel, float lifeTime, float decayTime) {
		this(location, manager, DefaultColor, DefaultShapeCapasity, powerLevel, lifeTime, decayTime);
	}
	protected void Tick() {
		if (LifeTicksRemaining == 0) {
			this.Clear();
			return;
		}
		else if(LifeTime != InfLifeTime) {
			if (TicksSinceDecay == DecayTicks) {
				Decay();
			}			
		}
		TicksSinceDecay++;
		LifeTicksRemaining--;
	}
	
	protected void Decay() {
		PowerLevel -= PowerLevel == 1 ? 0 : 1;
		ShapeWidth -= ShapeWidth > MinWidth ? DefaultDecayWidth : 0;
		TicksSinceDecay = 0;
	}
	@Override
	protected void ComputeShapes() {
		Clear();
		Shape objCircle = new Ellipse2D.Float(Location.getX(), Location.getY(), ShapeWidth, ShapeWidth);
		AddShape(objCircle);
	}
	@Override
	public void Update() {
		//Tick(); //Doesn't function well TODO improve
		ComputeShapes();
		return;
	}
	
	@Override
	public Rectangle getCollisionBox() {
		int startX = Math.round((Location.getX() - ShapeWidth/2));
		int startY =  Math.round((Location.getY() - ShapeWidth/2));
		return new Rectangle(startX, startY, (int) ShapeWidth, (int)ShapeWidth);
	}
}
