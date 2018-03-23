import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

class coordinator {
	public static final String REG_CMD = "register ";
	public static final String DEREG_CMD = "deregister";
	public static final String DISCON_CMD = "disconnect";
	public static final String RECON_CMD = "reconnect ";
	public static final String MULTICAST_CMD = "msend ";

	public ServerSocket nportServer;
	public static final String UNEXPECTED_ERROR = "Unexpected error occured";
	public DataInputStream in;
	public DataOutputStream out;
	public static CopyOnWriteArrayList participants = new CopyOnWriteArrayList();

	public void coordinator(DataInputStream dis, DataOutputStream dos) {
		this.in = dis;
		this.out = dos;
	}

	public void sendACK(boolean b){
		String ack;
		if(b)
			ack = "1";
		else
			ack = "0";
		this.out.writeUTF(ack);
	}

	/**
	 * Sending multicast message
	 */
	public void msend(){

	}

	public void run() {
		String command = "";
		try {
			while (true) {
				command = this.in.readUTF();
				if(command.substring(0, 9).equalsIgnoreCase(REG_CMD)){
					// call register method
					this.sendACK(true);
				}
				else if(command.equalsIgnoreCase(DEREG_CMD)){
					// call dergister method
					this.sendACK(true);
				}
				else if(command.equalsIgnoreCase(DISCON_CMD)){
					// call the disconnect method
					this.sendACK(true);
				}
				else if(command.substring(0, 10).equalsIgnoreCase(RECON_CMD)){
					// call reconnect method
					this.sendACK(true);
				}
				else if(command.substring(0, 6).equalsIgnoreCase(MULTICAST_CMD)){
					// call to multicast method
					this.sendACK(true);
				}
				else{
					this.sendACK(false);
				}
			}
		} catch (Exception e) {
			//TODO: handle exception
			this.sendACK(false);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerSocket co_ordinator = new ServerSocket();
		Socket p;
		try {
			while (true) {
				p = co_ordinator.accept();
				DataInputStream in = new DataInputStream(p.getInputStream());
				DataOutputStream out = new DataOutputStream(p.getOutputStream());
				Thread t = new Thread(new coordinator(in, out));
			}
		} catch (Exception e) {
			//TODO: handle exception
			e.printStackTrace();
		}
	}
}