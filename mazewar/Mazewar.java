/*
Copyright (C) 2004 Geoffrey Alan Washburn

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.
 */

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * The entry point and glue code for the game.  It also contains some helpful
 * global utility methods.
 * @author Geoffrey Washburn &lt;<a href="mailto:geoffw@cis.upenn.edu">geoffw@cis.upenn.edu</a>&gt;
 * @version $Id: Mazewar.java 371 2004-02-10 21:55:32Z geoffw $
 */

public class Mazewar extends JFrame {

	/**
	 * The default width of the {@link Maze}.
	 */
	private final int mazeWidth = 20;

	/**
	 * The default height of the {@link Maze}.
	 */
	private final int mazeHeight = 10;

	/**
	 * The default random seed for the {@link Maze}.
	 * All implementations of the same protocol must use 
	 * the same seed value, or your mazes will be different.
	 */
	private final int mazeSeed = 42;

	/**
	 * The {@link Maze} that the game uses.
	 */
	private Maze maze = null;

	/**
	 * The {@link GUIClient} for the game.
	 */
	private GUIClient guiClient = null;

	/**
	 * The panel that displays the {@link Maze}.
	 */
	private OverheadMazePanel overheadPanel = null;

	public int gui_client_id;

	/**
	 * The table the displays the scores.
	 */
	private JTable scoreTable = null;

	/** 
	 * Create the textpane statically so that we can 
	 * write to it globally using
	 * the static consolePrint methods  
	 */
	private static final JTextPane console = new JTextPane();
	private final Map client_map = new HashMap();
	/** 
	 * Write a message to the console followed by a newline.
	 * @param msg The {@link String} to print.
	 */ 
	public static synchronized void consolePrintLn(String msg) {
		console.setText(console.getText()+msg+"\n");
	}

	/** 
	 * Write a message to the console.
	 * @param msg The {@link String} to print.
	 */ 
	public static synchronized void consolePrint(String msg) {
		console.setText(console.getText()+msg);
	}

	/** 
	 * Clear the console. 
	 */
	public static synchronized void clearConsole() {
		console.setText("");
	}

	/**
	 * Static method for performing cleanup before exiting the game.
	 */
	public static void quit() {
		// Put any network clean-up code you might have here.
		// (inform other implementations on the network that you have 
		//  left, etc.)

		System.exit(0);
	}


	/** 
	 * The place where all the pieces are put together. 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public Mazewar(String hostname, int port) {
		super("ECE419 Mazewar");
		consolePrintLn("ECE419 Mazewar started!");

		// Create the maze
		maze = new MazeImpl(new Point(mazeWidth, mazeHeight), mazeSeed,hostname,port); // passing hostname and port to mazeimpl so that i can access it for killClient
		assert(maze != null);

		// Have the ScoreTableModel listen to the maze to find
		// out how to adjust scores.
		ScoreTableModel scoreModel = new ScoreTableModel();
		assert(scoreModel != null);
		maze.addMazeListener(scoreModel);

		// Throw up a dialog to get the GUIClient name.
		String name = JOptionPane.showInputDialog("Enter your name");
		if((name == null) || (name.length() == 0)) {
			Mazewar.quit();
		}		

		System.out.println("HOSTNAME IS "+hostname);
		System.out.println("PORT is "+port);

		// You may want to put your network initialization code somewhere in here.
		//Initialize the socket, the inputstream and the output stream
		Socket MazewarSocket = null;
		ObjectOutputStream out_to_server = null;
		ObjectInputStream in_from_server = null;
		int client_id = 0;	//Added by Shareq

		int X_COORDINATE = 0;
		int Y_COORDINATE = 0;

		try {

			MazewarSocket = new Socket(hostname, port);

			out_to_server = new ObjectOutputStream(MazewarSocket.getOutputStream());
			in_from_server = new ObjectInputStream(MazewarSocket.getInputStream());			

			//create a registration packet and add it to the 
			MazewarPacket registration_packet = new MazewarPacket();
			registration_packet.type = MazewarPacket.CLIENT_REGISTRATION;
			registration_packet.client_name = name;
			registration_packet.mazeHeight = mazeHeight;
			registration_packet.mazeWidth = mazeWidth;

			//Send out a registration packet to the server
			out_to_server.writeObject(registration_packet);
			System.out.println("IN TRY BLOCK 2");
			//Wait for the server to send an acknowledgement packet stating that all clients have connected
			//start the game once the ack is received
			MazewarPacket packet_from_server;

			int player_counter = 0;	//counter to count the number of players added to the maze

			while (( packet_from_server = (MazewarPacket) in_from_server.readObject()) != null) {
				/* process message */
				System.out.println("IN while loop of ack");
				if(packet_from_server.type == MazewarPacket.GUI_CLIENT_ACK) {

					System.out.println("RECEIVED GUI CLIENT ACK");

					//extract the client ID
					client_id = packet_from_server.client_id;
					gui_client_id = client_id;
					System.out.println("THE CLIENT ID IS "+client_id);

					//Add some statements later to check that the name received is 
					//the same as the name sent
					System.out.println("THE GeneraTed X is "+packet_from_server.x_coordinate);
					System.out.println("THE generated y is "+packet_from_server.y_coordinate);

					X_COORDINATE = packet_from_server.x_coordinate;
					Y_COORDINATE = packet_from_server.y_coordinate;

					// Create the GUIClient and connect it to the KeyListener queue
					guiClient = new GUIClient(name,client_id,out_to_server,in_from_server,X_COORDINATE,Y_COORDINATE);

					maze.addClient(guiClient,X_COORDINATE,Y_COORDINATE);
					client_map.put(client_id, guiClient);
					this.addKeyListener(guiClient);

					//break out of the loop if all the clients get connected
					player_counter++;
					if(player_counter == 4)
						break;;

				}
				else if(packet_from_server.type == MazewarPacket.REMOTE_CLIENT_ACK)
				{
					System.out.println("RECEIVED REMOTE CLIENT ACK");

					//extract the client ID
					client_id = packet_from_server.client_id;
					System.out.println("THE CLIENT ID IS "+client_id);


					//Add some statements later to check that the name received is 
					//the same as the name sent
					System.out.println("THE GeneraTed X is "+packet_from_server.x_coordinate);
					System.out.println("THE generated y is "+packet_from_server.y_coordinate);

					//Extract the X and Y coordinates from the packet
					X_COORDINATE = packet_from_server.x_coordinate;
					Y_COORDINATE = packet_from_server.y_coordinate;

					RemoteClient temp = new RemoteClient(packet_from_server.client_name,packet_from_server.client_id);

					maze.addClient(temp,X_COORDINATE,Y_COORDINATE);
					client_map.put(client_id, temp);
					//break out of the loop if all the clients get connected
					player_counter++;
					if(player_counter == 4)
						break;;


				}
				else
				{	
					/* if code comes here, there is an error in the packet */
					System.err.println("ERROR: Unknown packet, shouldn't be here!!");
					System.exit(-1);
				}
			}

		} catch (UnknownHostException e) {
			System.err.println("ERROR: Don't know where to connect!!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR: Couldn't get I/O for the connection.");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// Create the panel that will display the maze.
		overheadPanel = new OverheadMazePanel(maze, guiClient);
		assert(overheadPanel != null);
		maze.addMazeListener(overheadPanel);

		// Don't allow editing the console from the GUI
		console.setEditable(false);
		console.setFocusable(false);
		console.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));

		// Allow the console to scroll by putting it in a scrollpane
		JScrollPane consoleScrollPane = new JScrollPane(console);
		assert(consoleScrollPane != null);
		consoleScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Console"));

		// Create the score table
		scoreTable = new JTable(scoreModel);
		assert(scoreTable != null);
		scoreTable.setFocusable(false);
		scoreTable.setRowSelectionAllowed(false);

		// Allow the score table to scroll too.
		JScrollPane scoreScrollPane = new JScrollPane(scoreTable);
		assert(scoreScrollPane != null);
		scoreScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Scores"));

		// Create the layout manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		getContentPane().setLayout(layout);

		// Define the constraints on the components.
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 3.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(overheadPanel, c);
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.weightx = 2.0;
		c.weighty = 1.0;
		layout.setConstraints(consoleScrollPane, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		layout.setConstraints(scoreScrollPane, c);

		// Add the components
		getContentPane().add(overheadPanel);
		getContentPane().add(consoleScrollPane);
		getContentPane().add(scoreScrollPane);

		// Pack everything neatly.
		pack();

		// Let the magic begin.
		setVisible(true);
		overheadPanel.repaint();
		this.requestFocusInWindow();

		System.out.println("AFTER MAZE");

		//Enqueueing thread starts here
		new ClientEnqueueHandler(in_from_server,gui_client_id).start();

		MazewarPacket packet_from_server;
		System.out.println("AFTER new try");

		//pass the output stream
		maze.pass_output_stream(out_to_server);
		maze.pass_local_client_id(gui_client_id);

		System.out.println("GUI CLIENT ID IS "+gui_client_id);
		List<MazewarPacket> client_queue_list = ClientQueue.get_event_queue(gui_client_id);
		int local_client_id = client_id;

		boolean listening = true;
		int count = 0;
		while(listening) {

			if (client_queue_list.size() > 0) 
			{
				MazewarPacket packet_from_queue;
				packet_from_queue = client_queue_list.get(0);

				if(packet_from_queue.type == MazewarPacket.SERVER_PACKET) {

					//System.out.println("RECEIVED PACKET");
					Client temp_guy = (Client) client_map.get(packet_from_queue.client_id);

					int packet_client_id = packet_from_queue.client_id;

					if(packet_from_queue.action == MazewarPacket.CLIENT_KILLED)
					{
						if(packet_client_id != gui_client_id) {

							maze.respawn_remote_client(temp_guy, packet_from_queue.x_coordinate,packet_from_queue.y_coordinate);
						};
					}
					else if(packet_from_queue.action == MazewarPacket.MOVE_UP)
					{
						temp_guy.forward();
					}
					else if(packet_from_queue.action == MazewarPacket.MOVE_DOWN)
					{
						temp_guy.backup();
					}
					else if(packet_from_queue.action == MazewarPacket.MOVE_LEFT)
					{
						temp_guy.turnLeft();
					}
					else if(packet_from_queue.action == MazewarPacket.MOVE_RIGHT)
					{
						temp_guy.turnRight();
					}
					else if(packet_from_queue.action == MazewarPacket.SPACE_FIRE)
					{
						temp_guy.fire();
					};


					//remove the first element in the client queue
					ClientQueue.remove_element(gui_client_id);

				}
				else
				{
					System.out.println("SHOULD NEVER BE HERE");
				}
			};


			//get the client queue
			client_queue_list = ClientQueue.get_event_queue(gui_client_id);

		}


	}

	/**
	 * Entry point for the game.  
	 * @param args Command-line arguments.
	 */
	public static void main(String args[]) throws IOException, ClassNotFoundException {

		/* variables for hostname/port */
		String hostname = "localhost";
		int port = 4444;

		if(args.length == 2 ) {
			hostname = args[0];
			port = Integer.parseInt(args[1]);
		} else {
			System.err.println("ERROR: Invalid arguments!");
			System.exit(-1);
		}

		new Mazewar(hostname,port);
	}
}
