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
				new MazewarServerHandlerThread(socket).start();
			};
		}

		System.out.println("DONE WITH THE LOOP");

	}
}