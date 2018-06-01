package projectPanels;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import frame.GameWindow;
import gameObjects.Snake;
import projectClasses.Player;

//Panel that handles GUI
//Nothing interesting here :D

//Loads relevant GUI components and 
//unloads rest based on what menu is on
public class MenuPanel extends JPanel implements ActionListener{
	
	private static enum ButtonCode {
			LocalGame,
			LanGame,
			Host,
			Join,
			Play,
			ChangeColors,
			Back
	};
	
	private static enum MenuCode {
		Main,
		Local,
		Lan,
		LanWaiting,
		LanJoin
	};
	
	Button[] Buttons;
	
	MenuCode PreviousMenu;
	MenuCode CurrentMenu;
	
	static final int HSLabelMargin = 10;
	
	static final Point FirstButtonLocation = new Point(GameWindow.MapBoundary/4, GameWindow.MapBoundary/4);
	static final Point SecondButtonLocation = new Point(GameWindow.MapBoundary/4, 11*GameWindow.MapBoundary/20);
	static final Point BackButtonLocation = new Point(9*GameWindow.MapBoundary/11, 9*GameWindow.MapBoundary/11);
	
	static final Dimension NormalButtonSize = new Dimension(GameWindow.MapBoundary/2, GameWindow.MapBoundary/5);
	static final Dimension BackButtonSize = new Dimension(120, 80);
	static final Dimension ColorLabelSize = new Dimension(GameWindow.MapBoundary/16, GameWindow.MapBoundary/16);
	static final Dimension HSLabelSize = new Dimension(GameWindow.MapBoundary/5, GameWindow.MapBoundary/12);
	
	static final Point HostColorLabelLocation = new Point(GameWindow.MapBoundary/8, 13*GameWindow.MapBoundary/20);
	static final Point AwayColorLabelLocation = new Point((int) (GameWindow.MapBoundary - (GameWindow.MapBoundary/8 +  ColorLabelSize.getWidth())), 13*GameWindow.MapBoundary/20);
	static final Point HostHSLabelLocation = new Point(HostColorLabelLocation.x - HSLabelMargin, 28*GameWindow.MapBoundary/40);
	static final Point AwayHSLabelLocation = new Point(AwayColorLabelLocation.x - HSLabelMargin, 28*GameWindow.MapBoundary/40);
	
	static final Font ButtonFont = new Font("Arial", Font.PLAIN, 50);
	static final Font LabelFont = new Font("Arial", Font.PLAIN, 15);
	static final String WaitText = "Waiting for join..."; 
	static final String JoinText = "Looking for hosts...";
	static final String HostMessage = "looking for partner";
	static final String JoinMessage = "ready to join";
	static final String HSText = "High Score: ";
	
	Label LanLabel;
	Label HostColorLabel;
	Label AwayColorLabel;
	JLabel HostHSLabel;
	JLabel AwayHSLabel;
	Player HostPlayer;
	Player JoinPlayer;
	Color HostColor;
	Color JoinColor;
	int HostHighScore;
	int AwayHighScore;
	
	boolean isCanceled;
	GameWindow Window; //Reference to game window (JFrame)
	
	public MenuPanel(int host, int away) {
		HostPlayer = null;
		JoinPlayer = null;
		this.setOpaque(false);
		this.setLayout(null);
		Window = null;
		
		
		HostHighScore = host;
		AwayHighScore = away;
		
		
		HostColor = Snake.SnakeColors[0];
		JoinColor = Snake.SnakeColors[1];
		
		HostColorLabel = new Label("");
		HostColorLabel.setBackground(HostColor);
		HostColorLabel.setLocation(HostColorLabelLocation);
		HostColorLabel.setSize(ColorLabelSize);
		
		HostHSLabel = new JLabel(HSText + HostHighScore);
		HostHSLabel.setFont(LabelFont);
		HostHSLabel.setLocation(HostHSLabelLocation);
		HostHSLabel.setSize(HSLabelSize);
		HostHSLabel.setForeground(Color.WHITE);
		HostHSLabel.setOpaque(false);
		
		
		AwayColorLabel = new Label("");
		AwayColorLabel.setBackground(JoinColor);
		AwayColorLabel.setLocation(AwayColorLabelLocation);
		AwayColorLabel.setSize(ColorLabelSize);
		
		AwayHSLabel = new JLabel(HSText + AwayHighScore);
		AwayHSLabel.setFont(LabelFont);
		AwayHSLabel.setLocation(AwayHSLabelLocation);
		AwayHSLabel.setSize(HSLabelSize);
		AwayHSLabel.setForeground(Color.WHITE);
		AwayHSLabel.setOpaque(false);
		
		
		Buttons = new Button[ButtonCode.values().length];
		
		Buttons[ButtonCode.LocalGame.ordinal()] = MakeButton(ButtonCode.LocalGame,FirstButtonLocation);
		Buttons[ButtonCode.LanGame.ordinal()] = MakeButton(ButtonCode.LanGame,SecondButtonLocation);
		Buttons[ButtonCode.Host.ordinal()] = MakeButton(ButtonCode.Host,FirstButtonLocation);
		Buttons[ButtonCode.Join.ordinal()] = MakeButton(ButtonCode.Join,SecondButtonLocation);
		Buttons[ButtonCode.Play.ordinal()] = MakeButton(ButtonCode.Play,FirstButtonLocation);
		Buttons[ButtonCode.ChangeColors.ordinal()] = MakeButton(ButtonCode.ChangeColors,SecondButtonLocation);
		Buttons[ButtonCode.Back.ordinal()] = MakeButton(ButtonCode.Back,BackButtonLocation,BackButtonSize);
		
		LanLabel = new Label(WaitText);
		LanLabel.setSize(new Dimension(500, 150));
		LanLabel.setLocation(new Point(200, 300));
		LanLabel.setFont(ButtonFont);
		LanLabel.setForeground(Color.WHITE);
		LanLabel.setBackground(Color.BLACK);
		
		PreviousMenu = null;
		CurrentMenu = MenuCode.Main;
		isCanceled = false;
		
		LoadMenu(CurrentMenu);
	}
	
	private Button MakeButton(ButtonCode buttonCode, Point loc) {
		return MakeButton(buttonCode, loc, NormalButtonSize);
	}
	private Button MakeButton(ButtonCode buttonCode, Point loc, Dimension size) {
		Button button = new Button(buttonCode.name());
		button.setLocation(loc);
		button.setSize(size);
		button.setFont(ButtonFont);
		button.addActionListener(this);
		return button;
	}
	
	public void setWindow(GameWindow window) {
		this.Window = window;
	}
		
	private void LoadMenu(MenuCode menuCode) {
		if (menuCode == null) {
			return;
		}
		ResetGUI();
		switch (menuCode) {
		case Main:
			PreviousMenu = null;
			CurrentMenu = MenuCode.Main;
			LoadButton(ButtonCode.LocalGame);
			LoadButton(ButtonCode.LanGame);
			break;
		case Local:
			PreviousMenu = MenuCode.Main;
			CurrentMenu = MenuCode.Local;
			LoadButton(ButtonCode.Play);
			LoadButton(ButtonCode.ChangeColors);
			LoadButton(ButtonCode.Back);
			LoadColorLabels();
			break;
		case Lan:
			PreviousMenu = MenuCode.Main;
			CurrentMenu = MenuCode.Local;
			LoadButton(ButtonCode.Host);
			LoadButton(ButtonCode.Join);
			LoadButton(ButtonCode.Back);
			break;
		case LanWaiting:
			LanLabel.setText(WaitText);
			LanLabel.setVisible(true);
			this.add(LanLabel);
			break;
		case LanJoin:
			LanLabel.setText(JoinText);
			LanLabel.setVisible(true);
			this.add(LanLabel);
			break;
		default:
			break;
		}
		repaint();		
	}
	
	private void LoadButton(ButtonCode btnCode) {
		Button button = Buttons[btnCode.ordinal()];
		button.setEnabled(true);
		button.setVisible(true);
		this.add(button);
	}
	
	private void LoadColorLabels() {
		AssignRandomColors();
		HostColorLabel.setVisible(true);
		HostColorLabel.setBackground(HostColor);
		this.add(HostColorLabel);
		
		AwayColorLabel.setVisible(true);
		AwayColorLabel.setBackground(JoinColor);
		this.add(AwayColorLabel);
		repaint();
	}
	
	private void LoadHighScoreLabels() {
		HostHSLabel.setVisible(true);
		HostHSLabel.setText(HSText + HostHighScore);
		this.add(HostHSLabel);
		
		AwayHSLabel.setVisible(true);
		AwayHSLabel.setText(HSText + AwayHighScore);
		this.add(AwayHSLabel);
		repaint();
	}

	private void ResetGUI() {
		for (Button btn : Buttons) {
			btn.setEnabled(false);
			btn.setVisible(false);
			this.remove(btn);
		}
		LanLabel.setEnabled(false);
		LanLabel.setVisible(false);
		this.remove(LanLabel);
		
		HostColorLabel.setEnabled(false);
		HostColorLabel.setVisible(false);
		this.remove(HostColorLabel);
		
		AwayColorLabel.setEnabled(false);
		AwayColorLabel.setVisible(false);
		this.remove(AwayColorLabel);
		
		HostHSLabel.setEnabled(false);
		HostHSLabel.setVisible(false);
		this.remove(HostHSLabel);
		
		AwayHSLabel.setEnabled(false);
		AwayHSLabel.setVisible(false);
		this.remove(AwayHSLabel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String btnName = ((Button) e.getSource()).getLabel().replaceAll(" ","");
		ButtonCode btnCode = ButtonCode.valueOf(btnName);
		switch (btnCode) {
		case LocalGame:
			LoadMenu(MenuCode.Local);
			LoadHighScoreLabels();
			break;
		case LanGame:
			LoadMenu(MenuCode.Lan);
			break;
		case Host:
			LoadMenu(MenuCode.LanWaiting);
			HostLobby();
			break;
		case Join:
			LoadMenu(MenuCode.LanJoin);
			JoinLobby();
			break;
		case Play:
			//AssignRandomColors();
			HostPlayer = new Player(HostColor,"Host",HostHighScore);
			JoinPlayer = new Player(JoinColor,"Away",AwayHighScore);
			Window.LoadGame(HostPlayer, JoinPlayer);
			break;
		case ChangeColors:
			LoadColorLabels();
			break;
		case Back:
			LoadMenu(PreviousMenu);
			break;
		default:
			//Error
			break;
		}
		
	}
	
	void AssignRandomColors() {
		Random random = new Random();
		int len = Snake.SnakeColors.length;
		int hostInd = random.nextInt(len), joinInd = hostInd;
		while(hostInd == joinInd) {
			joinInd = random.nextInt(len);
		}
		HostColor = Snake.SnakeColors[hostInd];
		JoinColor = Snake.SnakeColors[joinInd];
		
	}
	
	//These are network implementation failures
	//Partially working/not working due to time constrictions and failed attempts 
	private void HostLobby() {
		isCanceled = false;
		HostPlayer = new Player(Color.CYAN,"Host",HostHighScore);
		Integer i = 0; //TODO delete
		try {
			MulticastSocket hostSocket = new MulticastSocket();
			hostSocket.setNetworkInterface(NetworkInterface.getByInetAddress((Window.getAddress())));
			hostSocket.setBroadcast(true);
			hostSocket.setSoTimeout(1000);
			byte[] buf = HostMessage.getBytes();
			DatagramPacket readyMessage = new DatagramPacket(buf,buf.length,InetAddress.getByName("192.168.1.255"),Window.getPort());
			DatagramPacket responseMessage = new DatagramPacket(new byte[500], 500);
			hostSocket.send(readyMessage);
			while(!isCanceled) {
				try {
					hostSocket.receive(responseMessage);
					LanLabel.setText(responseMessage.getData().toString());
					repaint();
					Thread.sleep(5000);
					isCanceled = true;
				}
				catch (SocketTimeoutException e) {
					LanLabel.setText(e.getMessage() + i.toString());
					i++;
					repaint();
					hostSocket.send(readyMessage);
					if (i == 10) {
						isCanceled = true;
					}
				}
				catch (SocketException e) {
				}
			}
			hostSocket.close();
		}
		catch (Exception e) {
			LanLabel.setText(e.getMessage());
			repaint();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		isCanceled = false;
		LoadMenu(MenuCode.Lan);
	}
	private void JoinLobby() {
		isCanceled = false;
		JoinPlayer = new Player(Color.GREEN, "Away",AwayHighScore);
		try {
			DatagramSocket joinSocket = new DatagramSocket(Window.getPort());
			joinSocket.setBroadcast(false);
			joinSocket.setSoTimeout(8000);
			
			DatagramPacket hostMessage = new DatagramPacket(new byte[500], 500);
			while(!isCanceled) {
				try {
					joinSocket.receive(hostMessage);
					if(hostMessage.getData().toString() == HostMessage) {
						byte[] buf = JoinMessage.getBytes();
						DatagramPacket readyMessage = new DatagramPacket(buf,buf.length,hostMessage.getAddress(),hostMessage.getPort());
						joinSocket.send(readyMessage);
					}
					
				}
				catch (SocketTimeoutException e) {
					LanLabel.setText("Failed to locate host");
					repaint();
					isCanceled = true;
				}
				catch (SocketException e) {
				}
			}
			joinSocket.close();
		}
		catch (Exception e) {
			LanLabel.setText(e.getMessage());
			repaint();
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isCanceled = false;
		LoadMenu(MenuCode.Lan);
	}	
	
}
