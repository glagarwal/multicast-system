import java.net.*;
import java.io.*;
import java.util.*;

class Participant extends Thread {
private static final String UNEXPECTED_ERROR = "Unexpected error occured";
private static DataInputStream dis;
private static DataOutputStream dos;
private static Scanner sc = new Scanner(System.in);
private static Socket socket;
private ServerSocket messageServerSocket;
private Socket messageSocket;
private DataInputStream dis_message;
private DataOutputStream dos_message;
private String cmd;
private int participantId;
private String logFilePath;
private String coordinatorIp;
private int coordinatorPort;
private static boolean disconnectFlag = true;

public Participant(int participantId, String logFilePath, String coordinatorIp, int coordinatorPort){
								try{
																this.participantId = participantId;
																this.logFilePath = logFilePath;
																this.coordinatorIp = coordinatorIp;
																this.coordinatorPort = coordinatorPort;
																dis = new DataInputStream(socket.getInputStream());
																dos = new DataOutputStream(socket.getOutputStream());
								} catch (Exception e) {
																//e.printStackTrace();
								}
}
//----------------------Constructor to instantiate myftp child thread (Created to make client multi-threaded)-----------
public Participant(int myPort, String logFilePath) {
								try{
																this.messageServerSocket = new ServerSocket(myPort);
																this.logFilePath = logFilePath;
								} catch (Exception e) {
																//e.printStackTrace();
								}
}
public Participant(){

}
public static String getMyIpAddress(){
								try{
																InetAddress ip;
																ip = InetAddress.getLocalHost();
																String temp = String.valueOf(ip);
																return temp.split("/")[1];
								}catch(Exception e) {
																//e.printStackTrace();
								}
								return null;
}

public void run() {
								try {
																boolean temp = true;
																//System.out.println("Thread started");
																this.messageSocket = this.messageServerSocket.accept();
																//System.out.println("Connection made");
																this.dis_message = new DataInputStream(messageSocket.getInputStream());
																this.dos_message = new DataOutputStream(messageSocket.getOutputStream());
																if(!new File(this.logFilePath).exists()) {
																								Writer tempWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFilePath), "utf-8"));
																								tempWriter.close();
																}
																while(temp) {
																								if(disconnectFlag) {
																																temp = false;
																								}else{
																																List<String> messages = new ArrayList<String>();

																																File file = new File(this.logFilePath);
																																Scanner scanner = new Scanner(file);
																																String message = this.dis_message.readUTF();
																																//System.out.println("Message is "+message+" and log file path is "+this.logFilePath);

																																while(scanner.hasNext()) {
																																								messages.add(scanner.nextLine());
																																}
																																Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFilePath), "utf-8"));
																																for(String s : messages) {
																																								writer.write(s+"\n");
																																}
																																writer.write(message);
																																writer.close();
																								}
																}
								} catch (Exception e) {
																//e.printStackTrace();
								}
}
public static void main(String args[]) {
								try {
																File file = new File(args[0]);
																Scanner scanner = new Scanner(file);
																int participantId = Integer.parseInt(scanner.nextLine());
																String logFilePath = scanner.nextLine();
																String thirdLine = scanner.nextLine();
																String coordinatorIp = thirdLine.split(" ")[0];
																int coordinatorPort = Integer.parseInt(thirdLine.split(" ")[1]);

																socket = new Socket(coordinatorIp, coordinatorPort);
																Participant participant = new Participant(participantId, logFilePath, coordinatorIp, coordinatorPort);
																String command = "Chat started!";

																while (true) {
																								System.out.print("Participant> ");
																								command = sc.nextLine();

																								if (command.split(" ")[0].equalsIgnoreCase("register")) {
																																//System.out.println("In register");
																																disconnectFlag = false;
																																//String myIp = command.split(" ")[1];
																																String myIp = new Participant().getMyIpAddress();
																																int myPort = Integer.parseInt(command.split(" ")[1]);
																																Participant messageThread = new Participant(myPort, logFilePath);
																																messageThread.start();
																																dos.writeUTF("register "+participantId+" "+myIp+" "+String.valueOf(myPort));
																																String ret = dis.readUTF();
																																do {

																																} while(!ret.equalsIgnoreCase("connection made"));
																								}
																								else if(command.contains("msend") && disconnectFlag == false) {
																																dos.writeUTF(command);
																								}
																								else if(command.contains("disconnect") && disconnectFlag == false) {
																																dos.writeUTF(command);
																																String res = dis.readUTF();
																																if(res.equalsIgnoreCase("ok")) {
																																								disconnectFlag = true;
																																}

																								}
																								else if(command.equalsIgnoreCase("deregister") && disconnectFlag == false) {
																																dos.writeUTF(command);
																																String res = dis.readUTF();
																																if(res.equalsIgnoreCase("ok")) {
																																								disconnectFlag = true;
																																}
																								}
																								else if(command.contains("reconnect")) {
																																disconnectFlag = false;
																																String myIp = new Participant().getMyIpAddress();
																																int myPort = Integer.parseInt(command.split(" ")[1]);
																																Participant messageThread = new Participant(myPort, logFilePath);
																																messageThread.start();
																																dos.writeUTF("reconnect "+participantId+" "+myIp+" "+String.valueOf(myPort));
																																String ret = dis.readUTF();
																																do {

																																} while(!ret.equalsIgnoreCase("connection made again"));
																								}
																								else{

																								}
																}
								} catch (Exception e) {
																//e.printStackTrace();
																System.out.println(UNEXPECTED_ERROR + ": " + e);
								}
}
}
