

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.*;
import javax.swing.JOptionPane;

public class MsgSSLClientSocket {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {

			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 3343);
			

			// create BufferedReader for reading server response
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// create PrintWriter for sending login to server
			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			sendData(output);
			
			// read response from server
			String response = input.readLine();
			Boolean validation = false;
			// display response to user
			JOptionPane.showMessageDialog(null, response);
			
			if(response == "Welcome to the Server") {
				String msj = JOptionPane.showInputDialog(null, "Introduzca el mensaje: ");
				output.flush();

			// clean up streams and Socket
			output.close();
			input.close();
			socket.close();
			
			
				
			}

		} // end try

		// handle exception communicating with server
		catch (IOException ioException) {
			ioException.printStackTrace();
		}

		// exit application
		finally {
			System.exit(0);
		}
	}

	public static String[] checkUser(){
		String user = JOptionPane.showInputDialog(null, "Enter your user: ");
		String password = JOptionPane.showInputDialog(null, "Enter your password: ");
		//String msg = JOptionPane.showInputDialog(null, "Enter a message:");
		return new String[]{user, password}; //msg};
	}

	public static void sendData(PrintWriter output ){
		String [] userData = checkUser();
		// prompt user for user name
		// send user name to server
		output.println(userData[0]);
		output.println(userData[1]);
		//output.println(userData[2]);	
		output.flush();
	}
}
