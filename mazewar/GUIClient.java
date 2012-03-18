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

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.*;
/**
 * An implementation of {@link LocalClient} that is controlled by the keyboard
 * of the computer on which the game is being run.  
 * @author Geoffrey Washburn &lt;<a href="mailto:geoffw@cis.upenn.edu">geoffw@cis.upenn.edu</a>&gt;
 * @version $Id: GUIClient.java 343 2004-01-24 03:43:45Z geoffw $
 */

public class GUIClient extends LocalClient implements KeyListener {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	public int x_coordinate;
	public int y_coordinate;

	/**
	 * Create a GUI controlled {@link LocalClient}.  
	 */
	public GUIClient(String name, int client_id, ObjectOutputStream out_to_server, ObjectInputStream in_from_server,int x, int y) 
	{
		super(name,client_id);
		out = out_to_server;
		in  = in_from_server;
		 x_coordinate = x;
		 y_coordinate = y;
	}
	public void update_coordinates(int x,int y)
	{
		 x_coordinate = x;
		 y_coordinate = y;
	}
	/**
	 * Handle a key press.
	 * @param e The {@link KeyEvent} that occurred.
	 */
	public void keyPressed(KeyEvent e) {

		MazewarPacket packet_to_server = new MazewarPacket();
		packet_to_server.type = MazewarPacket.CLIENT_PACKET;
		packet_to_server.client_id = getClientID();
		packet_to_server.client_name = getName();
		//NEW ADDITIONS
		packet_to_server.x_coordinate = x_coordinate;
		packet_to_server.y_coordinate = y_coordinate;

		synchronized(ClientQueue.lock2) {

			// If the user pressed Q, invoke the cleanup code and quit. 
			if((e.getKeyChar() == 'q') || (e.getKeyChar() == 'Q')) {
				Mazewar.quit();
				// Up-arrow moves forward.
			} else if(e.getKeyCode() == KeyEvent.VK_UP) {
				packet_to_server.action = MazewarPacket.MOVE_UP;     
				//forward();
				// Down-arrow moves backward.
			} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				packet_to_server.action = MazewarPacket.MOVE_DOWN;
				//backup();
				// Left-arrow turns left.
			} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				packet_to_server.action = MazewarPacket.MOVE_LEFT;
				//turnLeft();
				// Right-arrow turns right.
			} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				packet_to_server.action = MazewarPacket.MOVE_RIGHT;
				//turnRight();
				// Spacebar fires.
			} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				packet_to_server.action = MazewarPacket.SPACE_FIRE;
				//fire();
			}

  

			//write to the output stream
			try {
				out.writeObject(packet_to_server);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	/**
	 * Handle a key release. Not needed by {@link GUIClient}.
	 * @param e The {@link KeyEvent} that occurred.
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Handle a key being typed. Not needed by {@link GUIClient}.
	 * @param e The {@link KeyEvent} that occurred.
	 */
	public void keyTyped(KeyEvent e) {
	}

}