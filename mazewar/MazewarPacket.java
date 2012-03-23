import java.io.Serializable;

public class MazewarPacket implements Serializable {

	/* define packet formats */
	public static final int MAZEWAR_NULL    = 0;
	public static final int SERVER_PACKET = 100;
	public static final int CLIENT_PACKET   = 200;
	public static final int MAZEWAR_BYE     = 300;
	public static final int CLIENT_REGISTRATION = 400;
	public static final int SERVER_ACKNOWLEDGEMENT = 500;
	public static final int GUI_CLIENT_ACK = 600;
	public static final int REMOTE_CLIENT_ACK = 700;
	public static final int CLIENT_UPDATE = 900;
	public static final int CLIENT_KILLED = 800;
	/* All the potential actions */
	public static final int QUIT = 301;
	public static final int MOVE_UP = 302;
	public static final int MOVE_DOWN = 303;
	public static final int MOVE_LEFT = 304;
	public static final int MOVE_RIGHT = 305;
	public static final int SPACE_FIRE = 306;

	/* trying to add sequence numbers as mentioned in the lab hand out */
	public int sequence_Num = 0;

	/* the packet payload */

	/* initialized to be a null packet */
	public int type = MAZEWAR_NULL;

	/* The name of the client, clients may have the same name */
	public String client_name;

	/* An integer ID of a client, a different ID is given to each client */
	public int client_id;
	
	/* Client ID of the destination, used for equeing at the correct queue at the receiver end*/
	public int destination_clientID;
	
	/* The action taken by the client */
	public int action;

	/* Maze height */
	public int mazeHeight;

	/* Maze width */
	public int mazeWidth;

	/* the X coordinate */
	public int x_coordinate;

	/* the y coordinate */
	public int y_coordinate;

	/* the direction 
	 *
	 * NORTH - 0
	 * EAST - 1
	 * SOUTH - 2
	 * WEST - 3
	 */
	public int direction;
	
	/* The port from which the client will accept connections from other clients*/
	public int client_port;
	
	/* Hostname/IP of the client which will accept connections from other clients*/
	public String client_host;

}