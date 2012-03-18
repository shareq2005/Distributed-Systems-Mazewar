import java.util.*;
import java.util.concurrent.locks.Lock;
import java.net.*;
import java.io.*;

public class ServerQueue {

	//declare an array list for a queue of events
	private static ArrayList<MazewarPacket> server_queue = new ArrayList<MazewarPacket>();

	//synchronize the array list declared previously
	public static List<MazewarPacket> server_queue_list = Collections.synchronizedList(server_queue);	

	//returns the event queue
	public static List<MazewarPacket> get_event_queue() {
		return server_queue_list;	//not sure, return queue or queue_list ??
	}

	//sets the event queue
	public static void set_event_queue(List<MazewarPacket> queue) {
		server_queue_list = queue;
	}

	//Declare locks here
	public static Object lock1 = new Object();
	public static Object lock2 = new Object();

	public static Lock lock_one;
	public static Lock lock_two;
}