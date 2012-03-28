import java.net.*;
import java.util.*;
import java.io.*;


public class ClientQueue{

	//declare an array list for a queue of events
	private static ArrayList<MazewarPacket> client_queue1 = new ArrayList<MazewarPacket>();
	private static ArrayList<MazewarPacket> client_queue2 = new ArrayList<MazewarPacket>();
	private static ArrayList<MazewarPacket> client_queue3 = new ArrayList<MazewarPacket>();
	private static ArrayList<MazewarPacket> client_queue4 = new ArrayList<MazewarPacket>();


	//synchronize the array list declared previously
	public static List<MazewarPacket> client_queue_zero = Collections.synchronizedList(client_queue1);
	public static List<MazewarPacket> client_queue_one = Collections.synchronizedList(client_queue2);
	public static List<MazewarPacket> client_queue_two = Collections.synchronizedList(client_queue3);
	public static List<MazewarPacket> client_queue_three = Collections.synchronizedList(client_queue4);

	public static int seq_c0;
	public static int seq_c1;
	public static int seq_c2;
	public static int seq_c3;
	

	public int sequence_number = 0;

	//Make an array list for a list of the players, players must first register here
	//for a client ID
	private static ArrayList<MazewarPacket> player_id = new ArrayList<MazewarPacket>();

	//synchronize the array list of the players
	private static List<MazewarPacket> player_id_list = Collections.synchronizedList(player_id);

	//returns the EVENTS queue
	public static List<MazewarPacket> get_event_queue(int client_id) {

		if(client_id == 0)
			return client_queue_zero;	//not sure, return queue or queue_list ??
		else if(client_id == 1)
			return client_queue_one;
		else if(client_id == 2)
			return client_queue_two;	
		else if(client_id == 3)
			return client_queue_three;

		//return null if the client ID was not matched
		return null;

	}
	
	public static synchronized int get_sequence(int client_id) 
	{
		if(client_id == 0)
			return seq_c0;
		else if(client_id == 1)
			return seq_c1;
		else if(client_id == 2)
			return seq_c2;
		else if(client_id == 3)
			return seq_c2;
		
		return -1;
	}

	public static synchronized void update_sequence(int client_id,int seq)
	{
		if(client_id == 0)
			seq_c0 = seq;
		else if(client_id == 1)
			seq_c1 = seq;
		else if(client_id == 2)
			seq_c2 = seq;
		else if(client_id == 3)
			seq_c2 = seq;
	}
	
	public static synchronized void increment_sequence(int client_id)
	{
		if(client_id == 0)
			seq_c0++;
		else if(client_id == 1)
			seq_c1++;
		else if(client_id == 2)
			seq_c2++;
		else if(client_id == 3)
			seq_c2++;
	}
	
	//sets the events
	public static synchronized void set_event_queue(List<MazewarPacket> queue, int client_id) {

		if(client_id == 0)
			client_queue_zero = queue;
		else if(client_id == 1)
			client_queue_one = queue;
		else if(client_id == 2)
			client_queue_two = queue;
		else if(client_id == 3)
			client_queue_three = queue;;

	}	

	public static synchronized void add_element(MazewarPacket packet, int client_id) {

		if(client_id == 0) {
			client_queue_zero.add(packet);
		}
		else if(client_id == 1) {
			client_queue_one.add(packet);
		}
		else if(client_id == 2) {
			client_queue_two.add(packet);
		}
		else if(client_id == 3) {
			client_queue_three.add(packet);
		};

	}
	
	public static synchronized void remove_element(int client_id,int x) {

		if(client_id == 0) {
			client_queue_zero.remove(x);
		}
		else if(client_id == 1) {
			client_queue_one.remove(x);
		}
		else if(client_id == 2) {
			client_queue_two.remove(x);
		}
		else if(client_id == 3) {
			client_queue_three.remove(x);
		};

	}
	

	//returns the player id (or the Client ID) list
	public static List<MazewarPacket> get_player_queue() {
		return player_id_list;		
	}

	//Declare locks here
	public static Object lock1 = new Object();
	public static Object lock2 = new Object();

}
