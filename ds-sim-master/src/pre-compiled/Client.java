package comp3100wk3;
//A Java program for a Client
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    //Generation of all static strings
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String QUIT = "QUIT";
    private static String GET = "GETS All";
    private static String GETC = "GETS CAPABLE";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String JOBN = "JOBN";
    private static String JOBP = "JOBP";
    private static String JCPL = "JCPL";
    private static String DOT = ".";
    private static String DATA = "DATA";
    private static String ERR = "ERR";
    private static String name = System.getProperty("user.name");
    
    
    
    
    //Method that accepts a list of strings and separates them into smaller segments based on present whitespace and stores data to custom class
    public static ArrayList<Storage> Separate(ArrayList<String> Servers){
        String [] Info;
        //Temp ArrayList 
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        //Loop through a string and divide into segments
        //Store segemnts into temp string array in function
        for (int i = 0; i<Servers.size(); i++){
            Storage cur = new Storage();
            Info = Servers.get(i).split("\\s+");
            cur.ID = Info[0];
            cur.type = Integer.parseInt(Info[1]);
            cur.core = Integer.parseInt(Info[4]);
            cur.memory = Integer.parseInt(Info[5]);
            cur.disk = Integer.parseInt(Info[6]);
            ServerInfo.add(cur);
        }
        return ServerInfo;
    }
    
    //Determines largest server based on comparison of cores
     public static Storage getLargest(ArrayList<Storage> ServerInfo){
        Storage curLargest = new Storage();
        if(ServerInfo.size() == 1){
            curLargest = ServerInfo.get(0);
        }
        for(int i = 0;i<ServerInfo.size();i++){
            Storage cur = ServerInfo.get(i);
            for(int j = i;j<ServerInfo.size();j++){
                Storage cur2 = ServerInfo.get(j);
                if(cur2.core > cur.core){
                    curLargest = cur2;
                    break;
                }
            }
        }
        return curLargest;
    }
    
    //Implementation of a basic allocation to largest server method, improves code readability
    public static void allToLargest(Storage LargestServer, String JobID, PrintWriter pw){
        write(pw, SCHD + " " + JobID + " " + LargestServer.ID + " " + LargestServer.type);
    }
    
    //Accepts string in order to determine job ID as recevied from server
    public static String CurJobID(String s){
        String [] JobInfo;
        String JobID;
        JobInfo = s.split("\\s+");
        JobID = JobInfo[2];
        System.out.println(JobID);
        return JobID;
    }
  
    public static void ScheduleJob(BufferedReader bf, PrintWriter pw , String S) throws IOException, SocketException{
    	String [] JobInfo;
    	ArrayList<String> ServerInfo = new ArrayList<String>();
    	String strCur;
    	ArrayList<Storage> FirstSer = new ArrayList<Storage>();
    	JobInfo = S.split("\\s+");
    	pw.println(GETC + "" + JobInfo[4] + JobInfo[5] + "" + JobInfo[6]);
    	pw.flush();
    	strCur = bf.readLine();
    	System.out.println(strCur);
    	pw.println(OK);
    	pw.flush();
    	while(!strCur.contentEquals(DOT)) {
    		strCur = bf.readLine();
    		System.out.println("Server : "+strCur);
    		pw.println(OK);
    		pw.flush();
    		if(!strCur.equals(DOT)&&!strCur.contains(DATA)) {
    			ServerInfo.add(strCur);    			
    		}
    	}
    	
    	pw.println(OK);
    	pw.flush();
    	String [] temp = ServerInfo.get(1).split("\\s+");
    	pw.println(SCHD + "" + JobInfo[2] + "" +temp[0] + "" +temp[1]);
    	pw.flush();
    	
    }

    //writes the parameter String s to output socket using a PrintWriter pw
    public static void write(PrintWriter pw, String s) {
        pw.println(s);
        pw.flush();
    }
    
   public static void main(String[] args) throws IOException, SocketException{
        //Connection to specified socket
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //PrintWriter to circumvent byte issue
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String str = "";
        String Largest = "";
        String Job = "";
        String JobID = "";
        ArrayList<String> Servers = new ArrayList<String>();
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        Storage LargestServer = new Storage();

        //General handshake procedures
        write(pw, HELO);

        str = bf.readLine();
        System.out.println("server : " + str);
       //takes current system username and uses it for AUTH
        write(pw, AUTH + " " + name);

        str = bf.readLine();
        System.out.println("server : " + str);

        write(pw, REDY);

        str = bf.readLine();
        Job = str;
        System.out.println("server : " + str);

//        write(pw, GET);
        
       //Loop to read and store all received server information to a string arraylist 
       //Excepts the ending . and initial DATA message
//        while(!str.equals(DOT)){
//            str = bf.readLine();
//            System.out.println("Server : " + str);
//            write(pw, OK);
//            if(!str.equals(DOT)&&!str.contains(DATA)){
//                Servers.add(str);
//            }
//        } 
       //Calls upon Separate method to separate server data into readable information
//       ServerInfo = Separate(Servers);
       //Determines largest server based on server information
//       LargestServer = getLargest(ServerInfo);

    pw.println(OK);
    pw.flush();

       //Switches current read point to previously send Job
        str = Job;
        System.out.println(str);
        while(!str.contains(NONE)){
            //Conditions for server messages
            if(str.contains(JCPL)){
                write(pw, REDY);
            }
            else if(str.equals(OK)||str.contains(JCPL)){
                write(pw, REDY);
            }
            else if(str.contains(JOBN)){
                JobID = CurJobID(str);
                allToLargest(LargestServer,JobID, pw);
            }
            else if(str.contains(JOBP)){
                JobID = CurJobID(str);
                allToLargest(LargestServer,JobID, pw);
            }
            if(str.equals(NONE)){
                pw.flush();
                break;
            }
            str = bf.readLine();
            pw.flush();
        }
       //Quit procedures
        write(pw, QUIT);
        str = bf.readLine();
        System.out.println("Server : " + str);
       //Closing of all connections
        in.close();
        pw.close();
        s.close();
   }
}

