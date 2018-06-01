package frame;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import projectClasses.Player;
import projectPanels.GamePanel;
import projectPanels.MenuPanel;

//Application window
public class GameWindow extends JFrame {
	
	
	public static int MapBoundary = 1000;
	private static final int GamePort = 6442;
	GamePanel gamePanel;
	MenuPanel menuPanel;

	InetAddress addr;

	public GameWindow(String label) {
		super(label);
		gamePanel = null;
		menuPanel = null;
		try {
			addr = InetAddress.getByName("192.168.1.11");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			assert false;
		}
		this.setFocusable(true);

	}

	public int getPort() {
		return GamePort;
	}

	public InetAddress getAddress() {
		return addr;
	}

	public void LoadGame(Player host, Player join) {
		// TODO fix players
		if (menuPanel != null) {
			this.remove(menuPanel);
			revalidate();
			repaint();
			menuPanel = null;
		}
		Player[] players = new Player[] { host, join };
		gamePanel = new GamePanel(players);
		gamePanel.setWindow(this);
		this.add(gamePanel);
		this.addKeyListener(gamePanel);
		this.addMouseListener(gamePanel);
		this.setVisible(true);
		repaint();
	}

	public void LoadMenu(int host, int away) {

		if (gamePanel != null) {
			this.remove(gamePanel);
			revalidate();
			repaint();
			gamePanel = null;
		}
		menuPanel = new MenuPanel(host, away);
		menuPanel.setWindow(this);
		this.add(menuPanel);
		this.setVisible(true);
		repaint();
	}
}