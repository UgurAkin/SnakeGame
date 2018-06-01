package projectPanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.Timer;

import frame.GameWindow;
import gameObjects.Food;
import gameObjects.Poison;
import gameObjects.Snake;
import projectClasses.GameObjectManager;
import projectClasses.Player;
import projectDatatypes.DirectionType;
import projectDatatypes.Vec2D;

//Panel that contains and updates the game
//Panel interacts with GameObjectManagers and
//GameObjectManagers manage GameObjects
public class GamePanel extends JComponent implements ActionListener,KeyListener,MouseListener{

	GameObjectManager<Snake> snakeManager;
	GameObjectManager<Food> foodManager;
	GameObjectManager<Poison> poisonManager;
	
	GameWindow Window;
	
	JLabel ScoreLabel;
	Timer timer;
	Player[] players;
	
	public GamePanel(Player[] _players) {
		players = _players;
		snakeManager = new GameObjectManager<>(Snake.class, this);
		foodManager = new GameObjectManager<>(Food.class, this);
		poisonManager = new GameObjectManager<>(Poison.class, this);
		Window = null;
		
		ScoreLabel = new JLabel("");
		ScoreLabel.setLocation(new Point(350, 50));
		ScoreLabel.setSize(100,100);
		ScoreLabel.setFont(new Font("Arial", Font.BOLD, 15));
		ScoreLabel.setForeground(Color.WHITE);
		ScoreLabel.setOpaque(false);
		ScoreLabel.setVisible(true);
		this.add(ScoreLabel);
		
		PopulateGame();
		
		
		timer = new Timer(20, this);
		timer.start();
	}
	
	
	public void setWindow(GameWindow window) {
		Window = window;
	}

	
	//Every tick, game objects are updated and repainted to frame.
	@Override
	public void actionPerformed(ActionEvent e) {

		foodManager.RandomSpawn(Food.SpawnLimit);
		poisonManager.RandomSpawn(Poison.SpawnLimit);
		
		snakeManager.MassUpdate();
		foodManager.MassUpdate();
		poisonManager.MassUpdate();
		UpdateScores();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g;
		snakeManager.MassPaint(graphics2d);
		foodManager.MassPaint(graphics2d);
		poisonManager.MassPaint(graphics2d);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_W:
				players[0].snake.SetDirection(DirectionType.Up);
				break;
			case KeyEvent.VK_S:
				players[0].snake.SetDirection(DirectionType.Down);
				break;
			case KeyEvent.VK_A:
				players[0].snake.SetDirection(DirectionType.Left);
				break;
			case KeyEvent.VK_D:
				players[0].snake.SetDirection(DirectionType.Right);
				break;
			case KeyEvent.VK_UP:
				players[1].snake.SetDirection(DirectionType.Up);
				break;
			case KeyEvent.VK_DOWN:
				players[1].snake.SetDirection(DirectionType.Down);
				break;
			case KeyEvent.VK_LEFT:
				players[1].snake.SetDirection(DirectionType.Left);
				break;
			case KeyEvent.VK_RIGHT:
				players[1].snake.SetDirection(DirectionType.Right);
				break;
			case KeyEvent.VK_SPACE:
				players[0].snake.Boost(true);
				break;
			case KeyEvent.VK_ENTER:
				players[1].snake.Boost(true);
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			players[0].snake.Boost(false);
			break;
		case KeyEvent.VK_ENTER:
			players[1].snake.Boost(false);
			break;
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public Object getGameObjectManager(String type){
		if (type.equals("Snake")) {
			return  (Object) snakeManager;
		}
		else if (type.equals("Food")) {
			return (Object) foodManager;
		}
		else if(type.equals("Poison")) {
			return (Object) poisonManager;
		}
		else {
			return null;
		}
	}
	
	public void EndGame() {
		
	
		int hostHighScore = players[0].setAndGetHighScore(players[0].snake.getScore());
		int awayHighScore = players[1].setAndGetHighScore(players[1].snake.getScore());
		
		Window.LoadMenu(hostHighScore, awayHighScore);
	}
	
	//-------Helper Functions--------------------
	private void PopulateGame() {
		for (Player player : players) {
			if (player != null) {
				player.snake = snakeManager.Spawn(player.color);
			}
		}
		
		Vec2D[] fLocs = new Vec2D[Food.SpawnLimit];
		for (int i = 0; i < fLocs.length; i++) {
			Random random = new Random();
			fLocs[i] = new Vec2D(random.nextInt(GameWindow.MapBoundary), random.nextInt(GameWindow.MapBoundary) );
		}
		foodManager.MassSpawn(fLocs);
		
		Vec2D[] pLocs = new Vec2D[Poison.SpawnLimit];
		for (int i = 0; i < pLocs.length; i++) {
			Random random = new Random();
			pLocs[i] = new Vec2D(random.nextInt(GameWindow.MapBoundary), random.nextInt(GameWindow.MapBoundary) );
		}
		poisonManager.MassSpawn(pLocs);
	}
	
	private void UpdateScores() {
		String labelText = "<html>";
		for (Player player : players) {
			labelText += player.name + ": "+ player.snake.getScore() + "<br>";
		}
		labelText += "</html>";
		ScoreLabel.setText(labelText);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		Point clickLocation = e.getPoint();
		players[0].snake.SetDirection(clickLocation);
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



}




