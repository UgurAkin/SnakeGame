package projectInterfaces;

import projectDatatypes.DirectionType;

//Public interface of Snake objects
public interface SnakeInterface {
	public void Grow(int byAmt);
	public void Shrink(int byAmt);
	public void SetDirection(DirectionType newDirection);
	public void Boost(boolean status);
	public void AddScore(int byAmt);
	public int getScore();
}

