import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private static final boolean DEBUG = false;
    //Generation of all static strings
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String QUIT = "QUIT";
    private static String GET = "GETS";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String JOBN = "JOBN";
    private static String JOBP = "JOBP";
    private static String JCPL = "JCPL";
    private static String DOT = ".";
    private static String DATA = "DATA";
    private static String ERR = "ERR";
    private static String EJWT = "EJWT";
    private static String CNTJ = "CNTJ";
    private static String LSTJ = "LSTJ";
    private static String name = System.getProperty("user.name");

    //Determines largest server based on comparison of cores
    public static Storage getLargest(ArrayList<Storage> ServerInfo) {
        Storage curLargest = ServerInfo.get(0);
        for (Storage nextServer: ServerInfo) {
            if(nextServer.core > curLargest.core) {
                curLargest = nextServer;
            }
        }

        return curLargest;
    }

    //Implementation of a basic allocation to largest server method, improves code readability
    public static void schedule_job(Storage server, Job job, PrintWriter pw) {
        write(pw, SCHD + " " + job.jobID + " " + server.type + " " + server.ID);
    }

    //Accepts string in order to determine job ID as recevied from server
    public static String CurJobID(String s) {
        String[] JobInfo;
        String JobID;
        JobInfo = s.split(" ");
        JobID = JobInfo[2];
        return JobID;
    }

    public static String read(BufferedReader bf) throws IOException {
        String str = bf.readLine();
        if (DEBUG) System.out.printf("< %s%n", str);
        return str;
    }

    //writes the parameter String s to output socket using a PrintWriter pw
    public static void write(PrintWriter pw, String s) {
        if (DEBUG) System.out.printf("> %s%n", s);
        pw.write(s);
        pw.write("\n");
        pw.flush();
    }

    //General handshake procedures
    public static void handshake(PrintWriter pw, BufferedReader bf) throws IOException {
        String str = "";
        write(pw, HELO);
        str = read(bf);

        write(pw, AUTH + " " + name);
        str = read(bf);

        write(pw, REDY);
    }

    public static void main(String[] args) throws IOException, SocketException {
        //Connection to specified socket
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //PrintWriter to circumvent byte issue
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String Largest = "";
        String str;
        String Job = "";
        String JobID = "";
        ArrayList<String> Servers = new ArrayList<String>();
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();

        handshake(pw, bf);

        str = read(bf);
        Job = str;

        // Build list of servers
        write(pw, String.format("%s %s", GET, "All"));
        str = read(bf);
        // DATA LINES (read all capable servers of doing job and put into arraylist)
        int lines = Integer.parseInt(str.split(" ")[1]);
        write(pw, OK);
        for (int i = 0; i < lines; ++i) {
            str = read(bf);
            ServerInfo.add(new Storage(str));
        }
        Comparator<Storage> comp = (Storage a, Storage b) -> {
            return Integer.compare(a.core, b.core);
        };
        ServerInfo.sort(comp);
        write(pw, OK);
        read(bf);

        //Switches current read point to previously send Job
        str = Job;


        while (!str.contains(NONE)) {
            //Conditions for server messages
            if (str.contains(JCPL)) {
                // TODO handle job removal
                write(pw, REDY);
            } else if (str.equals(OK)) {
                write(pw, REDY);
            } else if (str.contains(JOBN) || str.contains(JOBP)) {
                Job job = new Job(str);
                write(pw, String.format("%s %s %d %d %d", GET, "Capable", job.core, job.memory, job.disk));
                str = read(bf);
                // DATA LINES (read all capable servers of doing job and put into arraylist)
                lines = Integer.parseInt(str.split(" ")[1]);
                write(pw, OK);
                ArrayList<Storage> capable = new ArrayList<>();
                for (int i = 0; i < lines; ++i) {
                    str = read(bf);
                    capable.add(new Storage(str));
                }
                write(pw, OK);
                read(bf);
                Comparator<Storage> compwait = (Storage a, Storage b) -> {
                    float ratioA = a.waitingJobs / (float) a.core;
                    if (a.Status.equals("inactive") || a.Status.equals("booting")) ratioA += 1 / (float) a.core;
                    float ratioB = b.waitingJobs / (float) b.core;
                    if (b.Status.equals("inactive") || b.Status.equals("booting")) ratioB += 1 / (float) b.core;
                    if (ratioA == ratioB) return Integer.compare(b.core, a.core);
                    return Float.compare(ratioA, ratioB);
                };
                capable.sort(compwait);
                Storage server = capable.get(0);
                schedule_job(server, job, pw);
            }
            str = read(bf);
        }

        //Quit procedures
        write(pw, QUIT);
        read(bf);

        //Closing of all connections
        in.close();
        pw.close();
        s.close();
    }
}
