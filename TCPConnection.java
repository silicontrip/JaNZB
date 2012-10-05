
import java.io.*;
import java.net.*;
import java.util.*;


/**
 * a class to manage a tcp connection
 */
public class TCPConnection {

	transient private Socket network;
	String host;
	int port;
	
	BufferedOutputStream out;
	BufferedInputStream in;
	
	private boolean  debug = false;

	
	public TCPConnection() { ; } 
	
	/**
	 * Class Constructor supporting a hostname and port
	 *
	 * @param s Hostname
	 * @param i Port Number
	 */
	
	public TCPConnection(String s, int i)  {
		host = s;
		port = i;
	}
	/** 
	 * Enables printing of network messages
	 */
	
	public void enableDebug() { 
		this.debug = true; 
		//System.out.println ("Debug enabled.");
	}
	/** 
	 * Disables printing of network messages
	 */
	
	public void disableDebug() { this.debug = false; }
	
	/**
	 * Sets the hostname for the connection
	 *
	 * @param s Hostname
	 */
	
	public void setHost(String s)  { this.host = s; }
	
	/**
	 * Sets the Port number for the connection
	 *
	 * @param i Port Number
	 */
	
	public void setPort(int i)  { this.port = i; }

	/**
	 * Returns the Host name for the TCP connection
	 *
	 * @return The hostname
	 */
	
	public String getHost() { return host; }
	/**
	 * Returns the Port Number for the TCP connection
	 *
	 * @return The Port Number
	 */	
	public int getPort() { return port; }

	/**
	 * Returns a BufferedOutputStream for the TCP connection
	 *
	 * @return The Output stream
	 */	
	
	public BufferedOutputStream getOutputStream() {return out;}
	
	/**
	 * Returns a BufferedInputStream for the TCP connection
	 *
	 * @return The Input stream
	 */	
	
	
	public BufferedInputStream getInputStream() {return in;}

	/**
	 * Establishes the TCP connection
	 *
	 * @throws IOException if there is a network error
	 */	
	
	
	public void connect() throws IOException {
		network = new Socket(host,port);
		
		out =new BufferedOutputStream(network.getOutputStream());
		in =new BufferedInputStream(network.getInputStream());
	}
	/**
	 * Disconnects the TCP connection
	 *
	 * @throws IOException if there is a network error
	 */	
	
	public void disconnect() throws IOException {
		out.close();
		in.close();
		network.close();
	}
	
	/**
	 * Sends data down the TCP connection
	 *
	 * @param comm the String to send
	 * @throws IOException if there is a network error
	 */	
	
	
	public void sendCommand (String comm) throws IOException {
		try {
			sendCommand(comm.getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			; // US-ASCII is a known fixed value this should never happen
		}
	}
	
	/**
	 * Sends data down the TCP connection
	 *
	 * @param comm the byte array to send
	 * @throws IOException if there is a network error
	 */	
	
	public void sendCommand (byte[] comm) throws IOException {
	
		if (this.debug)
			System.out.print(">> " + (new String(comm)));
		
		out.write(comm);
		out.flush();
	}
	
	/**
	 * Receives data from the TCP connection
	 *
	 * @param comm the byte array to receive the data
	 * @param off the number of bytes to skip in the byte array 
	 * @param len the number of bytes to read
	 * @throws IOException if there is a network error
	 */	
	
	
	public int receiveResponse (byte[] comm,int off,int len) throws IOException {
		int i = in.read(comm,off,len);
		if (this.debug)
			System.out.print ("<< " + (new String (comm)));
		return i;
	}
	
	/**
	 * Receives data from the TCP connection
	 * Equivalent to receiveResponse(b, 0, b.length)
	 *
	 * @param comm the byte array to receive the data
	 * @throws IOException if there is a network error
	 */	
	
	
	public int receiveResponse (byte[] comm) throws IOException {
		int i = in.read(comm);
		if (this.debug)
			System.out.print ("<< " + (new String (comm)));
		return i;
	}
	
	
	
}