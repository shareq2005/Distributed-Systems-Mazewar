import java.io.*;
import java.net.*;

public class ServerSocketThread extends Thread{

	int local_port;
	int gui_client_id;
	
	public ServerSocketThread(int local_port)
	{
		this.local_port = local_port;
	}
	
	public void setGuiClientID(int gui_client_id)
	{
		this.gui_client_id = gui_client_id;
	}

	public void run() 
	{
		//Set up the ServerSocket of the local port
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(local_port);
		} catch (IOException e) {
			System.err.println("ERROR: Could not listen on port!");
			System.exit(-1);
		}

		while (listening) {
			
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(socket != null)
			{
				new ClientEnqueueHandler(socket,gui_client_id).start();
			};
		}
	}
}