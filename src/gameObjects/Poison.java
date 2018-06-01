package gameObjects;

import java.awt.Color;

import projectClasses.Consumable;
import projectClasses.GameObjectManager;
import projectDatatypes.Vec2D;
import projectInterfaces.SnakeInterface;;

public class Poison extends Consumable {
	
	static final Color PoisonColor = Color.MAGENTA;
	static final int Points = -5;
	public static final int SpawnLimit = 15;
	
	//-------- Constructors -----------------------
	public Poison(Vec2D location, Object manager, int powerLevel, float lifeTime, float decayTime){
		super(location, manager, PoisonColor, powerLevel, lifeTime, decayTime);
	}
	public Poison(Vec2D location, Object manager, int powerLevel, float lifeTime){
		this(location, manager, powerLevel, lifeTime, DefaultDecayTime);	
	}
	public Poison(Vec2D location, Object manager, float lifeTime, float decayTime){
		this(location, manager, DefaultPowerLevel, lifeTime, decayTime);
	}
	public Poison(Vec2D location, Object manager, int powerLevel){
		this(location, manager, powerLevel, DefaultLifeTime, DefaultDecayTime);
	}
	public Poison(Vec2D location, Object manager, float lifeTime){
		this(location, DefaultPowerLevel, lifeTime, DefaultDecayTime);
	}
	public Poison(Vec2D location, Object manager){
		this(location, manager, DefaultPowerLevel, DefaultLifeTime, DefaultDecayTime);
	}
	
	//-------- Members --------------------------
	@SuppressWarnings("unchecked")
	public void Consume(SnakeInterface consumedBy) {
		consumedBy.Shrink(PowerLevel);
		((GameObjectManager<Poison>)Manager).Despawn(this);
		consumedBy.AddScore(Points);
		this.Clear();
	}
}