import java.io.*;
import java.net.*;
import java.util.List;
import java.lang.*;


public class ClientEnqueueHandler extends Thread{
	private Boolean receiving = true;
	ObjectInputStream in;
	int client_id;
	
	public ClientEnqueueHandler(ObjectInputStream in,int client_id)
	{
		this.in = in;
		this.client_id = client_id;
	}

	public void run()
	{
		MazewarPacket packet_from_server;
		try {

			while(( packet_from_server = (MazewarPacket) in.readObject()) != null)
			{
				//synchronized(ClientQueue.lock1) {
					//System.out.println("[CLIENTENQUEUE HANDLER] CLIENT ID"+packet_from_server.client_id);
					//System.out.println("[CLIENTENQUEUE HANDLER] CLIENT NAME"+packet_from_server.client_name);
					//System.out.println("[CLIENTENQUEUE HANDLER] CLIENT Seq#"+packet_from_server.sequence_Num);
					
					//store it in the queue
					//List<MazewarPacket> client_queue_list = ClientQueue.get_event_queue(client_id);

					//just store the packet in the queue
					//client_queue_list.add(packet_from_server);

					//update the queue
					ClientQueue.add_element(packet_from_server,client_id);
				//}

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