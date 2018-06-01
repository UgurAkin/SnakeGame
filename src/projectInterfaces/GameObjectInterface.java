package projectInterfaces;

import java.awt.Graphics2D;
import java.awt.Rectangle;

//public interface for GameObject s
public interface GameObjectInterface {
	public void Update();							//Update object
	public void Paint(Graphics2D graphicContext);	//Paint object
	public boolean Collide(Rectangle collisionBox);	//Check for collisions
	public void Clear();							//Remove the object from game
}
