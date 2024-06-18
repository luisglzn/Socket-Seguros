import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;


import javax.net.ssl.*;


public class MsgSSLServerSocket {
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public static void main(String[] args) throws IOException, InterruptedException {
		try{
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(3343);
			Logger logger = Logger.getLogger(MsgSSLClientSocket.class.getName());
			Handler fileHandler = new FileHandler("logfile.log", true);
			logConfiguration(logger, fileHandler);
			ciphersuites(serverSocket, logger);
			
		}catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
	}

	public static void connection(SSLServerSocket serverSocket, Logger logger) {
		try {		
			
			// wait for client connection and check login information
			logger.log(Level.INFO, "Servidor esperando la conexión");
			System.err.println("Waiting for connection...");

      		SSLSocket socket = (SSLSocket) serverSocket.accept();

			// open BufferedReader for reading data from client
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String user = input.readLine();
			String password = input.readLine();
			String msg = input.readLine();

			//usuario valido si se encuentra en la base de datos
			UserInfo receivedUser = UserInfo.of(user, password);
			Boolean validation = UserValidation(receivedUser);
			System.out.println(receivedUser);

			// open PrintWriter for writing data to client
			PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			if (validation) {
				output.println("Welcome to the Server");
				logger.log(Level.INFO, "Usuario dentro del servidor");
				
				logger.log(Level.INFO, "Mensaje recibido ");
				System.out.println("Mensaje recibido de" + user + msg);

			} else {
				output.println("Incorrect message.");
				logger.log(Level.INFO, "Credenciales incorrectas");

			}
			output.close();
			input.close();
			socket.close();

		} // handle exception communicating with client
		catch (IOException ioException) {
			ioException.printStackTrace();
			logger.log(Level.WARNING, "El CipherSuite elegido no es v�lido!");
		}
	}

	public static void ciphersuites(SSLServerSocket serverSocket,  Logger logger) {
		// Vemos los ciphersuites que soporta 
		List<String> enCiphersuite = Arrays.asList(serverSocket.getEnabledCipherSuites());
		System.out.println("\n Los ciphersuites soportados son: \n" + enCiphersuite + "\n");

		//optamos por un ciphersuite seguro
		String[] selCipherSuite ={"TLS_AES_128_GCM_SHA256"};
		System.out.println("Ciphersuite seleccionada: " + selCipherSuite[0]);

		//comprueba que el ciphersuite seleccionado sea uno de los soportados
		if(enCiphersuite.contains(selCipherSuite[0])) {
			serverSocket.setEnabledCipherSuites(selCipherSuite);
			connection(serverSocket, logger);
		}else System.out.println("El CipherSuite seleccionado no forma parte de la lista de los soportados, no se puede asegurar una conexion> segura.");
	}

	public static void logConfiguration(Logger logger, Handler fileHandler){
		// Configuración LOG
		logger.setLevel(Level.INFO); // Configura el nivel de registro a INFO o superior
		SimpleFormatter sformatter = new SimpleFormatter();
		fileHandler.setFormatter(sformatter);
		logger.addHandler(fileHandler);
	}

	//Devuelve true si el usuario y la contraseña son correctos en la base de datos
	public static Boolean UserValidation(UserInfo receivedUser){
		Boolean res = false;
		
		String fileName = "users.txt";
		Path filePath = Paths.get(fileName);
		if (!Files.exists(filePath)) {
            System.out.println("File not found: " + filePath);
        }
		try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while (((line = br.readLine()) != null) && !res) {
                String [] userReadInfo = line.split(",");
				UserInfo checkUser = new UserInfo(userReadInfo[0], userReadInfo[1]);
				System.out.println(checkUser);
				res = checkUser.equals(receivedUser); //si existe usuario y contraseña --> devuelve true
				//cuando res = true --> break
			}
            br.close();
        } catch (IOException e) {
			e.printStackTrace();
        }
		return res;
	}
}
