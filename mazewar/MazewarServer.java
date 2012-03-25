import java.net.*;
import java.io.*;
import java.util.*;


public class MazewarServer {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		int count = 0;
		boolean listening = true;

		try {
			if(args.length == 1) {
				serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			} else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}
		} catch (IOException e) {
			System.err.println("ERROR: Could not listen on port!");
			System.exit(-1);
		}

		//first wait for 4 clients to connect to the server
		while (listening) {

			Socket socket;
			socket = serverSocket.accept();
			System.out.println("socket bound "+socket.getLocalPort());
			if(socket != null)
			{
				count++;

				new MazewarServerHandlerThread(socket).start();
				System.out.println("COUNT IS "+count);
			};
		}

		System.out.println("DONE WITH THE LOOP");
		// serverSocket.close();

		Thread thread = null;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean sending = true;
		List<ObjectOutputStream> output_streams_list = PlayersQueue.get_out_streams();
		List<MazewarPacket> server_queue = ServerQueue.get_event_queue();

		while(sending)
		{		

			synchronized(ServerQueue.lock1) {

				//check if there is anything in the server queue
				if(server_queue.size() > 0)
				{
					//Broadcast the first element in the server queue to all the clients
					int i = 0;
					for(i = 0; i < 4; i++)
					{
						//make out_to_client to a stored ObjectOutputStream
						ObjectOutputStream out_to_client = output_streams_list.get(i);

						//packet to client - store it with the first element in the list
						MazewarPacket packet_to_client = server_queue.get(0);
					
						//set the type to SERVER_PACKET
						packet_to_client.type = MazewarPacket.SERVER_PACKET;

						//send the packet to the client
						out_to_client.writeObject(packet_to_client);
					}


					//now that you have sent it, remove the first element in that list
					server_queue.remove(0);

					//update the queue
					ServerQueue.set_event_queue(server_queue);
					server_queue = ServerQueue.get_event_queue();
				};

				//get the server queue
				//server_queue = ServerQueue.get_event_queue();
				
			}
		}

	}
}