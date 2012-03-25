import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;


public class MazewarServerHandlerThread extends Thread {
	private Socket socket = null;
	private Maze maze_server = null;
	private static int sequence_Num = 0;
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

				int client_id;

				/* process message */
				List<MazewarPacket> player_list = null;

				if(packetFromClient.type == MazewarPacket.CLIENT_REGISTRATION) 
				{	
					synchronized(this) {
						//add the packet to the list
						//assign a client identification number
						player_list = PlayersQueue.get_player_list();

						int i;

						if(player_list.size() == 0)
						{
							System.out.println("ARRAY EMPTY");
							player_list = new ArrayList<MazewarPacket>();
						};

						//fine the number 
						//of elements in the array list
						gui_client_id = player_list.size();
						client_id = player_list.size();

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
					
					//Wait for the number of players to get to 4
					int number_of_players = 0;
					Thread thread;
					while(number_of_players < 4) {
						number_of_players = 0;
						thread = new Thread(this);
						thread.sleep(10);
						//System.out.println("a");
						number_of_players = (PlayersQueue.get_player_list()).size();   
					}

					System.out.println("ALL 4 PLAYERS REGISTERED");


					MazewarPacket info_packet = new MazewarPacket();


					synchronized(this) {			

						int i;
						for(i = 0; i < 4; i++)
						{
							player_list = PlayersQueue.get_player_list();
							info_packet = player_list.get(i);

							System.out.println("INFO PACKET: NAME IS "+info_packet.client_name);
							System.out.println("INFO PACKET: CLIENT ID IS "+info_packet.client_id);

							//check if the client id is of the local GUI client connected to this thread
							if(info_packet.client_id == gui_client_id)
								info_packet.type = MazewarPacket.GUI_CLIENT_ACK;
							else
								info_packet.type = MazewarPacket.REMOTE_CLIENT_ACK;

							//Send the packet to the Mazewar client
							toClient.writeObject(info_packet);
						}
					}

					/* wait for next packet */
					continue;
				}
				else if(packetFromClient.type == MazewarPacket.CLIENT_PACKET)
				{
					sequence_Num++;
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
				};


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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}