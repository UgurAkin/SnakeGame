package projectClasses;

import java.awt.Color;

import gameObjects.Snake;


//Tiny struct for player info
public class Player {
	
	public Snake snake;
	public Color color;
	public String name;
	private int highScore;
	

	
	public Player(Color c, String _name, int hs) {
		color = c;
		name = _name;
		highScore = hs;
		snake = null;
	}
	
	public int setAndGetHighScore(int score) {
		highScore = score > highScore ? score : highScore;
		return highScore;
	}
}
