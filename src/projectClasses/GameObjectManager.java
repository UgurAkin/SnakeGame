package projectClasses;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import gameObjects.Snake;
import projectDatatypes.Vec2D;
import projectInterfaces.GameObjectManagerInterface;
import projectPanels.GamePanel;


//TODO threads?
public class GameObjectManager<T extends GameObject> implements GameObjectManagerInterface<T> {
	
	private final Class<T> ObjectType;
	private ArrayList<T> SpawnedObjects;
	private int AmountSpawned;
	private GamePanel Owner;
		
	public GameObjectManager(Class<T> type, GamePanel owner) {
		Owner = owner;
		AmountSpawned = 0;
		SpawnedObjects = new ArrayList<T>();
		ObjectType = type;
	}
	
	//------------ Interface Implementation----------------------
	
	@Override
	public void MassUpdate() {
		//Concurrent changes might happen!
		int len = SpawnedObjects.size();
		for (int i = 0; i < len; i++) {
			if (i >= SpawnedObjects.size()) {
				return;
			}
			else {
				SpawnedObjects.get(i).Update();
			}
		}
		
	}
	
	@Override
	public void MassPaint(Graphics2D graphicsContext) {
		assert SpawnedObjects != null;
		for (T gameObj : SpawnedObjects) {
			gameObj.Paint(graphicsContext);
		}
		
	}
	
	@Override
	public ArrayList<T> MassSpawn(Vec2D[] locations) {
		ArrayList<T> objs = new ArrayList<>();
		for (int i = 0; i < locations.length; i++) {
			T newObj = Spawn(locations[i]);
			objs.add(newObj);
		}
		
		return objs;
	}
	
	@Override
	public T Spawn(Color color) {
		try {
			T newObj = ObjectType.getConstructor(Color.class,Object.class,int.class).newInstance(color, (Object) this , AmountSpawned);
			SpawnedObjects.add(newObj);
			AmountSpawned++;
			return newObj;
		}catch (Exception e) {
			assert false;
		}
		return null;
	}
	
	@Override
	public T Spawn(Vec2D location) {
		try {
			T newObj = ObjectType.getConstructor(Vec2D.class, Object.class).newInstance(location, (Object) this);
			SpawnedObjects.add(newObj);
			AmountSpawned++;
			return newObj;
		}catch (Exception e) {
			assert false;
		}
		return null;
	}
	
	@Override
	public void RandomSpawn(int spawnLimit) {
		float spawnProbability = 100 - (((float)AmountSpawned)*100 / spawnLimit );
		Random random = new Random();
		if (random.nextInt(100) < spawnProbability) {
			int randX = random.nextInt(800);
			int randY = random.nextInt(800);
			Spawn(new Vec2D(randX, randY));
		}
		
	}
	
	@Override
	public void MassDespawn() {
		SpawnedObjects.clear();
	}
	
	@Override
	public void Despawn(T obj) {
		if (obj != null && AmountSpawned > 0) {
			int ind = SpawnedObjects.indexOf(obj);
			if (ind >= 0) {
				SpawnedObjects.remove(ind);
				AmountSpawned--;
			}
		}
	}
	
	@Override
	public GamePanel getGamePanel() {
		return Owner;
	}
	
	//Collision detection function for snakes
	@Override
	public T Collide(Rectangle collisionBox, Snake  sender) {
		for (T obj : SpawnedObjects) {
			if(sender != obj) {
				if(obj.Collide(collisionBox)) {
					return obj;
				}
			}
		}
		return null;
	}
	
	//TODO game over screen
	@Override
	public void EndGame() {
		Owner.EndGame();
	}
}
