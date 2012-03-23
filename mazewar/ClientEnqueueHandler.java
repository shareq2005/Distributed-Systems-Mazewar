import java.io.*;
import java.net.*;
import java.util.List;
import java.lang.*;


public class ClientEnqueueHandler extends Thread{
	int gui_client_id;
	Socket socket = null;

	public ClientEnqueueHandler(Socket socket,int gui_client_id) {
		this.gui_client_id = gui_client_id;
		this.socket = socket;
		System.out.println("Created new Thread to handle client");
	}
	
	public void run()
	{
		MazewarPacket packet_from_client;
		
		try {
			
			ObjectInputStream in_from_client = new ObjectInputStream(socket.getInputStream());
			
			while(( packet_from_client = (MazewarPacket) in_from_client.readObject()) != null)
			{
					//update the queue
					ClientQueue.add_element(packet_from_client,packet_from_client.destination_clientID);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}