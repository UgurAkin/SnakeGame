package gameObjects;

import projectClasses.Consumable;
import projectClasses.GameObjectManager;
import projectDatatypes.Vec2D;
import projectInterfaces.SnakeInterface;

public class Food extends Consumable {
	
	public static final int SpawnLimit = 40;
	private static final int Points = 2;
	
	//-------- Constructors -----------------------
	public Food(Vec2D location, Object manager, int powerLevel, float lifeTime, float decayTime){
		super(location,manager, powerLevel,lifeTime,decayTime);
	}
	public Food(Vec2D location, Object manager, int powerLevel, float lifeTime){
		this(location, powerLevel, lifeTime, DefaultDecayTime);	
	}
	public Food(Vec2D location, Object manager, float lifeTime, float decayTime){
		this(location, manager, DefaultPowerLevel, lifeTime, decayTime);
	}
	public Food(Vec2D location,Object manager, int powerLevel){
		this(location, manager, powerLevel, DefaultLifeTime, DefaultDecayTime);
	}
	public Food(Vec2D location,Object manager, float lifeTime){
		this(location, manager, DefaultPowerLevel, lifeTime, DefaultDecayTime);
	}
	public Food(Vec2D location , Object manager){
		this(location, manager, DefaultPowerLevel, DefaultLifeTime, DefaultDecayTime);
	}
	
	//-------- Members --------------------------
	@SuppressWarnings("unchecked")
	public void Consume(SnakeInterface consumedBy) {
		consumedBy.Grow(PowerLevel);
		((GameObjectManager<Food>)Manager).Despawn(this);
		consumedBy.AddScore(Points);
	}
	
}
