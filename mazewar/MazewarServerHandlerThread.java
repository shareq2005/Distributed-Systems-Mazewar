import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;


public class MazewarServerHandlerThread extends Thread {
	private Socket socket = null;
	private static int sequence_Num = 0;
	public static int number_of_clients = 0;
	public int gui_client_id;

	public MazewarServerHandlerThread(Socket socket) {
		super("MazewarServerHandlerThread");
		this.socket = socket;
		System.out.println("Created new Thread to handle client");
	}

	public void run() {

		boolean gotByePacket = false;

		try {
			/* stream to read from client */
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			MazewarPacket packetFromClient;

			/* stream to write back to client */
			ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

			List<ObjectOutputStream> list = null;

			synchronized(this) {
				list = PlayersQueue.get_out_streams();
				list.add(toClient);
				PlayersQueue.set_out_streams(list);
			}

			while (( packetFromClient = (MazewarPacket) fromClient.readObject()) != null) {
				/* create a packet to send reply back to client */
				MazewarPacket packetToClient = new MazewarPacket();
				packetToClient.type = MazewarPacket.SERVER_PACKET;

				/* process message */
				List<MazewarPacket> player_list = null;

				if(packetFromClient.type == MazewarPacket.CLIENT_REGISTRATION) 
				{	
					synchronized(this) {
						//add the packet to the list
						//assign a client identification number
						player_list = PlayersQueue.get_player_list();

						if(player_list.size() == 0)
						{
							System.out.println("ARRAY EMPTY");
							player_list = new ArrayList<MazewarPacket>();
						};

						//fine the number 
						//of elements in the array list
						gui_client_id = player_list.size();
						//Generate a random number that hasn't been generated before
						Random randomGen = new Random();
						int x =  randomGen.nextInt(packetFromClient.mazeWidth);
						int y =  randomGen.nextInt(packetFromClient.mazeHeight);

						System.out.println("X IS "+x);
						System.out.println("Y IS "+ y);

						int m, size;
						size = player_list.size();
						for(m = 0; m < size;m++)
						{
							if(((player_list.get(m)).x_coordinate == x) && ((player_list.get(m)).y_coordinate == y))
							{
								m = 0;
								x =  randomGen.nextInt(packetFromClient.mazeWidth); 
								y =  randomGen.nextInt(packetFromClient.mazeHeight);
							};					
						}

						System.out.println("X after loop IS "+x);
						System.out.println("Y after loop IS "+y);

						//Store the unique x and y coordinates in the packet
						packetFromClient.x_coordinate = x;
						packetFromClient.y_coordinate = y;					
						packetFromClient.client_id = gui_client_id;
						System.out.println("THE CLIENT ID IS "+gui_client_id);

						//Add it back to the list+
						player_list.add(gui_client_id, packetFromClient);
						PlayersQueue.set_player_list(player_list);

					}

					synchronized(this) {			
						if(player_list.size() == 4) {
							MazewarPacket info_packet = new MazewarPacket();
							System.out.println("ALL 4 PLAYERS REGISTERED");

							int i;
							for(i = 0; i < 4; i++)
							{
								player_list = PlayersQueue.get_player_list();
								info_packet = player_list.get(i);

								System.out.println("INFO PACKET: NAME IS "+info_packet.client_name);
								System.out.println("INFO PACKET: CLIENT ID IS "+info_packet.client_id);

								//Send the packet to all the Mazewar clients
								int j = 0;
								for(j = 0; j < 4; j++)
								{
									//check if the client id is of the local GUI client connected to this thread
									if(info_packet.client_id == j)
										info_packet.type = MazewarPacket.GUI_CLIENT_ACK;
									else
										info_packet.type = MazewarPacket.REMOTE_CLIENT_ACK;
									
									ObjectOutputStream temp_stream = (PlayersQueue.out_streams_list).get(j);
									temp_stream.writeObject(info_packet);
								}
							}
						}
					}

					/* wait for next packet */
					continue;
				}
				else if(packetFromClient.type == MazewarPacket.CLIENT_PACKET)
				{
					MazewarPacket packet_queue = new MazewarPacket();
					packet_queue.type = MazewarPacket.SERVER_PACKET;
					packet_queue.x_coordinate = packetFromClient.x_coordinate;
					packet_queue.y_coordinate = packetFromClient.y_coordinate;
					packet_queue.action = packetFromClient.action;
					packet_queue.client_id = packetFromClient.client_id;
					packet_queue.client_name = packetFromClient.client_name;

					packet_queue.sequence_Num = sequence_Num;
					List<MazewarPacket> server_queue = ServerQueue.get_event_queue();

					synchronized(ServerQueue.lock1) {
						//store it in the queue
						server_queue = ServerQueue.get_event_queue();

						//just store the packet in the queue
						server_queue.add(packet_queue);

						//update the queue
						ServerQueue.set_event_queue(server_queue);
					}

					continue;
				}
				else if(packetFromClient.type == MazewarPacket.SEQUENCE_REQUEST)
				{
					//send back a sequence number
					MazewarPacket packet_queue = new MazewarPacket();
					packet_queue.type = MazewarPacket.SEQUENCE_RETURN;
					
					//extract a sequence number
					int sequence_number = SynchronizedCounter.grant_sequence();
					
					//pass in the sequence number
					packet_queue.sequence_number = sequence_number;

					//send back a sequence number to the client which requested it
					ObjectOutputStream temp_stream = (PlayersQueue.out_streams_list).get(packet_queue.client_id);
					temp_stream.writeObject(packet_queue);
					
				}


				/* Sending an ECHO_NULL || ECHO_BYE means quit */
				if (packetFromClient.type == MazewarPacket.MAZEWAR_NULL || packetFromClient.type == MazewarPacket.MAZEWAR_BYE) {
					gotByePacket = true;
					packetToClient = new MazewarPacket();
					packetToClient.type = MazewarPacket.MAZEWAR_BYE;
					toClient.writeObject(packetToClient);
					break;
				}				

				/* if code comes here, there is an error in the packet */
				System.err.println("ERROR: Unknown ECHO_* packet!!");
				System.exit(-1);
			}

			/* cleanup when client exits */
			fromClient.close();
			toClient.close();
			socket.close();

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
	}

}