package projectInterfaces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import gameObjects.Snake;
import projectClasses.GameObject;
import projectDatatypes.Vec2D;
import projectPanels.GamePanel;


//public interface for GameObjectManager class
//This class handles GameObject(such as food/poison, snake) s for the game
public interface GameObjectManagerInterface<T extends GameObject> {
	public T Spawn(Color c);
	public ArrayList<T> MassSpawn(Vec2D[] locations);
	public T Spawn(Vec2D location);
	public void MassUpdate();
	public void MassPaint(Graphics2D graphicsContext);
	public void MassDespawn();
	public void Despawn(T obj);
	public GamePanel getGamePanel();
	public T Collide(Rectangle collisionBox, Snake sender);
	public void EndGame();
	public void RandomSpawn(int max);
}