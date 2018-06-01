package frame;
import java.awt.*;

import javax.swing.*;



/* Snake Project
 * 
 * Author:	Ugur Akin
 * 
 * Controls: 
 * 		- Host: WASD + Space for Boost
 * 		- Away: ArrowKeys + Enter for Boost
 * 
 * Description:
 * - This project implements a simple snake game. 
 * - The GameWindow extends JFrame and it loads menu/game
 * - There are two Panels for this game: MenuPanel and GamePanel
 * - MenuPanel loads buttons and labels based on which menu is on
 * - GamePanel contains the game and a Timer.
 * - Game Mechanics:
 * 		- There is a base class called GameObject. Every game object extends this class
 * 		- There is a class called GameObjectManager<T> which manages these objects (e.g spawn, despawn, update...)
 * 		- Every 20ms, Timer sends actions and GameObjects are updated and repainted via GameObjectManager<T>s
 * 		- Every GameObject has its own Update function for updating and ComputeShapes to compute shapes to paint
 * 		- When Snake (extends GameObject) objects are updated, they check for collision.
 * 		- If collision is with a snake then they die. If it is with a Consumable, they consume it.
 * 		- Foods (extends Consumable (extends GameObject)) grow the snake that eats them and increase score.	
 * 		- Poisons (extends Consumable (extends GameObject)) shrink the snake that eats them and decrease score.
 * 		- Snakes slow as they grow and speed-up as they shrink. They can also activate boost but they shrink over time.
 * 		- When the game ends, scores are compared against highscores and updated.
 * 	
 * - Bugs/Failed Implementations:
 * 		- Network connection set-up is implemented but not working properly:
 * 			- Players can either host or join a lobby
 * 			- Hosting players broadcast a message every 2 seconds and wait for a response for 2 seconds.
 * 			- Joining players listen to broadcast messages and respond when they recieve one.
 * 			- PROBLEM: Host sends packets but joining players do not receive them.
 * 			- PROBLEM: IP-Address is hard-coded and wont work for every machine.
 * 			- PROBLEM: Input separation not implemented due to failure to connect devices 
 * 		- Food Sizes and Values:
 * 			- Originally food (and poison) sizes were to shrink and lose power.
 * 			- Implementation was clunky and there was no time to improve so feature is unused.
 * 		- Threads:
 * 			- Threads would be very useful for network but not implemented due to failure to implement network.
 * 		- Game Bounds:
 * 			- Initially we were going to bound the game area to the screen but we decided to keep it unbouded later on.
 * 
 * NOTE:
 * 	- Game play changes depend mostly on static members of GameObject class and its derivations. 
 * 	- This makes playing around with numbers and improve game play experience very easy.
 */


public class Test {
	

	//Main method, init. window and load menu
	public static void main(String[] args) {
		GameWindow gameWindow = new GameWindow("Snake Game");
		gameWindow.setBounds(0, 0, GameWindow.MapBoundary, GameWindow.MapBoundary);
		gameWindow.setLocationRelativeTo(null);
		gameWindow.getContentPane().setBackground(Color.BLACK);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setResizable(false);
		gameWindow.LoadMenu(0,0);
		//gameWindow.LoadGame(Players);
	}
}

