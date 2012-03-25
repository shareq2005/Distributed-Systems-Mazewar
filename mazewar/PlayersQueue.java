import java.net.*;
import java.io.*;
import java.util.*;

public class PlayersQueue {

	//Make an array list for a list of the players, players must first register here
	//for a client ID
	private static ArrayList<MazewarPacket> player_id = new ArrayList<MazewarPacket>();
		
	//synchronize the array list of the players
	private static List<MazewarPacket> player_id_list = Collections.synchronizedList(player_id);
	
	//Array list of out streams
	public static ArrayList<ObjectOutputStream> out_streams = new ArrayList<ObjectOutputStream>();
	
	//Synchronized Array list of out streams
	public static List<ObjectOutputStream> out_streams_list = Collections.synchronizedList(out_streams);
	
	//returns the player id (or the Client ID) list
	public synchronized static List<MazewarPacket> get_player_list() {
		return player_id_list;		
	}
	
	//Sets the ID of the players
	public synchronized static void set_player_list(List<MazewarPacket> list) {
		player_id_list = list;
	}
	
	public synchronized static List<ObjectOutputStream> get_out_streams() {
		return out_streams_list;
	}
	
	public synchronized static void set_out_streams(List<ObjectOutputStream> list) {
		out_streams_list = list;
	}
}