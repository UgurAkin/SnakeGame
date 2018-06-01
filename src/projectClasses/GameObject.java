package projectClasses;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import projectDatatypes.Vec2D;
import projectInterfaces.GameObjectInterface;

import java.awt.Rectangle;

//Base class for GameObject types
//Update and ComputeShapes methods are to be implemented in derived classes 
//because behavior changes for each different GameObject derived classes.
//Intended use of these methods are to Update the GameObject temporally and
//recompute shapes to be drawn immediately after each update.
public abstract class GameObject implements GameObjectInterface {
	
	//Every GameObject needs to know the tick interval for accurate updates
	//TickInterval is set to a default and should be set before starting the game
	//via SetTickInterval(float) method 
	private static final float DefaultTickInterval = 20f; // in ms
	private static final int DefaultCapacity = 10;
	
	protected static final Color DefaultColor = Color.WHITE;
	protected static float TickInterval = DefaultTickInterval; // in ms
	
	protected Vec2D Location;
	private ArrayList<Shape> Shapes;
	protected Color FillColor;
	protected Object Manager;
	
 	protected GameObject(Vec2D location, int capacity , Color color, Object manager) {
 		Location = location;
		Shapes = new ArrayList<>(capacity);
		FillColor = color;
		Manager = manager;
	}
	
	protected GameObject(Vec2D location, Color color,Object manager) {
		this(location, DefaultCapacity, color, manager);
	}
	
	protected GameObject(Vec2D location, Object manager) {
		this(location, DefaultCapacity, DefaultColor, manager);
	}
	
	public void setTickInterval(float interval) {
		TickInterval = interval;
	}
	
	//----- Visual Implementation of Game Objects ----------------------------------
	
	public void setColor(Color newColor) {
		FillColor = newColor;
	}
	
	public Vec2D getLocation() {
		return Location;
	}
	
	@Override
	public void Paint(Graphics2D graphicsContext) {
		
		assert Shapes != null;
		if (!Shapes.isEmpty()) {
			graphicsContext.setColor(FillColor);
			for (Shape vc : Shapes) {
				if (vc != null) {
//					graphicsContext.setStroke(new BasicStroke(8));
//					graphicsContext.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					graphicsContext.fill(vc);
				}
			} 
		}
	}
	
	protected void AddShape(Shape newShape) {
		if (newShape != null) {
			Shapes.add(newShape);
		}
	}	
	
	protected Shape PopShape() {
		if (Shapes.isEmpty()) {
			return null;
		}
		return Shapes.remove(Shapes.size() - 1);
	}
	@Override
	public void Clear() {
		Shapes.clear();
	}
	
	@Override
	public boolean Collide(Rectangle collisionBox) {
		for (Shape shape : Shapes) {
			Rectangle sRectangle = shape.getBounds();
			if(sRectangle.intersects(collisionBox)) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract void ComputeShapes();
	
	//-----Implement for collision detection-------------------------------
	
	public abstract Rectangle getCollisionBox();
}
