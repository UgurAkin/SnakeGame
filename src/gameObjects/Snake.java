
package gameObjects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import frame.GameWindow;
import projectClasses.GameObject;
import projectDatatypes.*;
import projectInterfaces.GameObjectManagerInterface;
import projectInterfaces.SnakeInterface;

//		Try updating less?
//Snake GameObject Implementation
public class Snake extends GameObject implements SnakeInterface {
	public static final Color[] SnakeColors =  {
		Color.CYAN, Color.GREEN, Color.YELLOW,
		Color.BLUE, Color.RED, Color.ORANGE
	};
	private static final float MaxVelocity = 4f; // 10^-1 pixels per ms
	private static final float MinVelocity = 1f; // 10^-1 pixels per ms
	private static final float BoostPercentage = 3f / 10; // 10^-1 pixels per ms
	private static final int MinQueueLength = 2;
	private static final float MinStartOffset = 70f; // in px
	private static final float DefaultShapeWidth = 20f; // in px
	private static final float DefaultShapeHeight = 10f; // in px
	private static final float SlowPerQueue = 0.25f; // in 10^-1 pixels per ms
	private static final float HeadRadius = 8f; // in px;
	private static final float HeadDiameter = HeadRadius * 2; // in px
	private static final float DefaultArcWidth = 3f;
	private static final float DefaultCollisionBias = 2f; // in px
	private static final int BoostDecayTreshold = 20; // in ticks

	static Vec2D[] StartingPositions = { new Vec2D(MinStartOffset, MinStartOffset),
			new Vec2D(GameWindow.MapBoundary - MinStartOffset, MinStartOffset),
			new Vec2D(MinStartOffset, GameWindow.MapBoundary - MinStartOffset),
			new Vec2D(GameWindow.MapBoundary - MinStartOffset, GameWindow.MapBoundary - MinStartOffset) };
	static DirectionType[] StartingDirections = { DirectionType.Right, DirectionType.Down, DirectionType.Up,
			DirectionType.Left };
	static final Vec2D[] MoveVectors = { new Vec2D(0, -1), // up
			new Vec2D(1, 0), // right
			new Vec2D(0, 1), // down
			new Vec2D(-1, 0) // left
	};

	private int QueueLength;
	private float Velocity; // 10^-1 pixels per ms
	private float BoostVelocity;
	private DirectionType Direction;
	private Deque<DirectionType> SnakeQueueDirections;
	private float ShapeWidth;
	private float ShapeHeight;
	private float DistanceSinceShift;
	private boolean isBoosted;
	private int BoostTicks;
	private int Score;

	// Constructor
	public Snake(Color color, Object manager, int index) {
		super(StartingPositions[index], color, manager);
		QueueLength = MinQueueLength;
		Location = StartingPositions[index % StartingPositions.length];
		Direction = StartingDirections[index % StartingDirections.length];
		Velocity = MaxVelocity;
		SnakeQueueDirections = new LinkedList<DirectionType>();
		for (int i = 0; i < QueueLength; i++) {
			SnakeQueueDirections.add(Direction);
		}
		Score = 0;
		ShapeWidth = DefaultShapeWidth;
		ShapeHeight = DefaultShapeHeight;
		DistanceSinceShift = 0;
		isBoosted = false;
	}

	// ------Interface Implementation-------------------
	@Override
	public void SetDirection(DirectionType newDirection) {
		if (newDirection != null) {
			int directionDiff = Math.abs(newDirection.ordinal() - Direction.ordinal());
			if (directionDiff != 2) {
				Direction = newDirection;
			}
		}
	}
	public void SetDirection(Point clickLoc) {
		Vec2D distVector = new Vec2D(clickLoc.x, clickLoc.y).Sub(Location);
		if (distVector.getX() > 0 && distVector.getY() < 0) { //1st quadrant
			if (distVector.getX() > distVector.getY()) {
				SetDirection(DirectionType.Right);
			}
			else {
				SetDirection(DirectionType.Up);
			}
		}
		else if (distVector.getX() < 0 && distVector.getY() < 0) { //1st quadrant
			if (Math.abs(distVector.getX()) > distVector.getY()) {
				SetDirection(DirectionType.Left);
			}
			else {
				SetDirection(DirectionType.Up);
			}
		}
		else if (distVector.getX() < 0 && distVector.getY() > 0) { //1st quadrant
			if (Math.abs(distVector.getX()) > Math.abs(distVector.getY())) {
				SetDirection(DirectionType.Left);
			}
			else {
				SetDirection(DirectionType.Down);
			}
		}
		else if (distVector.getX() > 0 && distVector.getY() > 0) { //1st quadrant
			if (distVector.getX() > Math.abs(distVector.getY())) {
				SetDirection(DirectionType.Right);
			}
			else {
				SetDirection(DirectionType.Down);
			}
		}
	}

	@Override
	public void Grow(int byAmt) {
		QueueLength += byAmt;
		for (int i = 0; i < byAmt; i++) {
			SnakeQueueDirections.addFirst(null);
		}
		Slow(byAmt * SlowPerQueue);
	}
	
	public void AddScore(int byAmt) {
		Score += byAmt;
	}
	public int getScore() {
		return Score;
	}
	@Override
	public void Shrink(int byAmt) {
		byAmt = byAmt > (QueueLength - MinQueueLength) ? (QueueLength - MinQueueLength) : byAmt;
		QueueLength -= byAmt;
		for (int i = 0; i < byAmt; i++) {
			SnakeQueueDirections.poll();
		}
		SpeedUp(byAmt * SlowPerQueue);
	}

	@Override
	public void Boost(boolean status) {
		isBoosted = status;
		BoostTicks = status ? BoostTicks : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void Update() {

		// Detect collision
		String collidedType = DetectCollision();
		if (collidedType != null && collidedType.equals("Snake")) {
			((GameObjectManagerInterface<Snake>) Manager).Despawn(this);
			((GameObjectManagerInterface<Snake>) Manager).EndGame();
		}

		// Determine Current Velocity
		if (QueueLength == MinQueueLength) {
			isBoosted = false;
		}
		float currentVelocity = Velocity;
		if (isBoosted) {
			currentVelocity = BoostVelocity;
			Decay();
		}

		// Move head location based on distance velocity
		float distanceMoved = currentVelocity * TickInterval / 10; // in px
		Location = Location.Add(getMoveVector(Direction, distanceMoved));
		DistanceSinceShift += distanceMoved;
		
		// Shift if enough distance traveled
		if (DistanceSinceShift >= ShapeWidth) {
			Shift();
		}
		// Make java.awt.Shape s of the snake (head and queue)
		ComputeShapes();
	}

	// -----Helper Functions---------------------------
	private void Slow(float byAmt) {
		Velocity -= (byAmt * SlowPerQueue);
		Velocity = Velocity < MinVelocity ? MinVelocity : Velocity;
		BoostVelocity = Velocity * (1f + BoostPercentage);
	}

	private void SpeedUp(float byAmt) {
		Velocity += (byAmt * SlowPerQueue);
		Velocity = Velocity > MaxVelocity ? MaxVelocity : Velocity;
		BoostVelocity = Velocity * (1f + BoostPercentage);
	}

	private void Decay() {
		if (BoostTicks == BoostDecayTreshold) {
			BoostTicks = 0;
			Shrink(1);
		} else {
			BoostTicks++;
		}
	}

	private void Shift() {
		SnakeQueueDirections.poll();
		SnakeQueueDirections.add(Direction);
		DistanceSinceShift -= ShapeWidth;
	}

	private DirectionType getOppositeDirection(DirectionType dir) {
		return DirectionType.values()[(dir.ordinal() + 2) % 4];
	}

	private boolean isVertical(DirectionType dir) {
		return dir.ordinal() % 2 == 0;
	}
	
	private Vec2D getMoveVector(DirectionType dir, float m) {
		return MoveVectors[dir.ordinal()].Scale(m);
	}

	// -------Collision Detection-------------------------------
	// Snakes can't collide with themselves
	@SuppressWarnings("unchecked")
	private String DetectCollision() {
		Rectangle collisionBox = getCollisionBox();
		GameObjectManagerInterface<Snake> managerInterface = ((GameObjectManagerInterface<Snake>) Manager);
		Object collided = managerInterface.Collide(collisionBox, this);
		if (collided != null) {
			return "Snake";
		} else {
			collided = ((GameObjectManagerInterface<Food>) managerInterface.getGamePanel().getGameObjectManager("Food"))
					.Collide(collisionBox, this);
			if (collided != null) {
				((Food) collided).Consume(this);
				return "Food";
			} else {
				collided = ((GameObjectManagerInterface<Poison>) managerInterface.getGamePanel()
						.getGameObjectManager("Poison")).Collide(collisionBox, this);
				if (collided != null) {
					((Poison) collided).Consume(this);
					return "Poison";
				}
			}

		}
		return null;
	}

	@Override
	public Rectangle getCollisionBox() {
		int startX = 0, endX = 0, startY = 0, endY = 0;
		Vec2D headEnd = Location.Add(getMoveVector(Direction, HeadRadius + DefaultCollisionBias));

		if (isVertical(Direction)) { // vertical movement
			startX = Math.round(Location.getX() - (HeadRadius - DefaultCollisionBias));
			endX = Math.round(Location.getX() + (HeadRadius - DefaultCollisionBias));
			startY = Math.round(Math.min(Location.getY(), headEnd.getY()));
			endY = Math.round(Math.max(Location.getY(), headEnd.getY()));
		} else { // horizontal movement
			startX = Math.round(Math.min(Location.getX(), headEnd.getX()));
			endX = Math.round(Math.max(Location.getX(), headEnd.getX()));
			startY = Math.round(Location.getY() - (HeadRadius - DefaultCollisionBias));
			endY = Math.round(Location.getY() + (HeadRadius - DefaultCollisionBias));
		}

		return new Rectangle(startX, startY, endX - startX, endY - startY);

	}

	//---------Rendering purposes--------------------------
	@Override
	protected void ComputeShapes() {
		Clear();

		DirectionType up = DirectionType.Up;
		DirectionType left = DirectionType.Left;

		// Add head to shapes
		Vec2D headUpperLeft = Location
				.Add(MoveVectors[up.ordinal()].Add(MoveVectors[left.ordinal()]).Scale(HeadRadius));
		Shape Head = new Ellipse2D.Float(headUpperLeft.getX(), headUpperLeft.getY(), HeadDiameter, HeadDiameter);
		AddShape(Head);

		// NOTE AddHeadBlock is to add margins before shift
		// CutTailBlock is to cut the same margin from tail
		// together they help make a continuously moving snake

		// Add queue blocks to shapes
		Vec2D lastLoc = Location;
		Vec2D curLocation = AddHeadBlockMargin();
		float previousSize = DistanceSinceShift == 0 ? ShapeWidth : DistanceSinceShift;
		Iterator<DirectionType> itr = SnakeQueueDirections.descendingIterator();
		while (itr.hasNext()) {
			DirectionType nextDir = itr.next();
			lastLoc = curLocation;
			curLocation = AddNextBlockShape(curLocation, previousSize, nextDir, ShapeWidth, ShapeHeight);
			previousSize = ShapeWidth;
		}

		// If the last block's direction is null, we don't need to cut.
		if (SnakeQueueDirections.peekLast() != null) {
			CutTailBlock(lastLoc);
		}

	}

	private Vec2D AddNextBlockShape(Vec2D previousLoc, float previousDist, DirectionType dir, float shapeWidth,
			float shapeHeight) {

		// Last block newly added so don't paint
		if (dir == null) {
			return previousLoc;
		}
		Vec2D newLoc = previousLoc.Add(getMoveVector(getOppositeDirection(dir), shapeWidth / 2 + previousDist / 2));

		// Compute upper left corner of current block
		float upOffset = 0, leftOffset = 0;
		if (isVertical(dir)) // block is vertical oriented
		{
			upOffset = shapeWidth / 2;
			leftOffset = shapeHeight / 2;
		} else // block is horizontal oriented
		{
			upOffset = shapeHeight / 2;
			leftOffset = shapeWidth / 2;
		}
		Vec2D upperLeftCorner = newLoc.Add(getMoveVector(DirectionType.Up, upOffset))
				.Add(getMoveVector(DirectionType.Left, leftOffset));
		Shape curBlock = new RoundRectangle2D.Float(upperLeftCorner.getX(), upperLeftCorner.getY(), leftOffset * 2,
				upOffset * 2, DefaultArcWidth, DefaultArcWidth);
		AddShape(curBlock);

		return newLoc;
	}

	private Vec2D AddHeadBlockMargin() {


		// Specific for this alg. subject to change
		if (DistanceSinceShift == 0) {
			//return Location.Add(getMoveVector(getOppositeDirection(Direction), HeadRadius - ShapeWidth / 2));
			return Location;
		}

		return AddNextBlockShape(Location, HeadDiameter, Direction, DistanceSinceShift, ShapeHeight);
	}

	private void CutTailBlock(Vec2D loc) {
		if (DistanceSinceShift <= 0) {
			return;
		}
		Shape lastShape = PopShape();
		if (lastShape == null) {
			return;
		}

		AddNextBlockShape(loc, ShapeWidth, SnakeQueueDirections.peek(), ShapeWidth - DistanceSinceShift, ShapeHeight);
	}
	
}
