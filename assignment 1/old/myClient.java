
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
public class myClient {
	public static void main(String args[]) throws Exception {
		System.out.println("Connecting to server...");
		Socket sock = new Socket("localhost", 50000);
		
		DataInputStream dataIn = new DataInputStream(sock.getInputStream());
		DataOutputStream dataOut = new DataOutputStream(sock.getOutputStream());
		
		//make string, convert to array of bytes then send bytestream to server
		String myString = "HELO";
		dataOut.write(myString.getBytes());
		dataOut.flush();
		
		System.out.println(" Client has sent \"" + myString + "\" to the server ...") ;
		
		//read the bytestream recieved
		byte[] byteArray = new byte [dataIn.available()];
		dataIn.read(byteArray);
		//convert bytes to string and print it out
		myString = new String(byteArray, StandardCharsets.UTF_8);
		System.out.println(myString+ "recieved from server");
		
		//authentication
		myString = "AUTH Dan";
		byteArray = myString.getBytes();
		dataOut.write(byteArray);
		dataOut.flush();
		
		//read bytestream from server(expecting greeting)
		byte[] byteArray0 = new byte[2];
		dataIn.read(byteArray0);
		//convert to string and print
		myString = new String(byteArray0, StandardCharsets.UTF_8);
		System.out.println("second OK from server: + myString");
		
		//tell server that client is ready
		myString = "REDY" ;
		byteArray = myString.getBytes();
		dataOut.write(byteArray0);
		dataOut.flush();
		
		//read bytestream from server
		//expecting GREETING
		byte[] byteArray1 = new byte[5];
		dataIn.read(byteArray1);
		//convert to string and print
		myString = new String(byteArray1, StandardCharsets.UTF_8);
		System.out.println("String: " + myString);
		
		//tell server that client is ready
		myString = "GETS ALL";
		byteArray = myString.getBytes();
		dataOut.write(byteArray);
		dataOut.flush();
		
		//read bytestream from server
		//expect to recieve GREETING
		byte[] byteArray2 = new byte[12+2];
		dataIn.read(byteArray2);
		//convert to string and print
		myString = new String(byteArray2, StandardCharsets.UTF_8);
		System.out.println("Gets all reply: " + myString);
		
		myString = "OK";
		dataOut.write(myString.getBytes());
		dataOut.flush();
		
		//expecting GREETING
		byte[] byteArray3 = new byte[184*124];
		dataIn.read(byteArray3);
		//convert to string and print
		myString= new String(byteArray3, StandardCharsets.UTF_8);
		System.out.println("Gets all reply: \n" + myString);
		
		myString ="OK";
		dataOut.write(myString.getBytes());
		dataOut.flush();
		
		//tell server that client wants to quit
		if(myString =="" || myString =="NONE") {
			myString = "QUIT";
			byteArray = myString.getBytes();
			dataOut.write(byteArray);
			dataOut.flush();

			sock.close();
			System.out.println("Disconnected");
		}
		
		
	}
}
