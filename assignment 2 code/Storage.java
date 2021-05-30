import java.lang.reflect.Array;
import java.util.ArrayList;

public class Storage{
    public int ID;
    public String type;
    public String Status;
    public int BootupTime;
    public int core;
    public int memory;
    public int disk;
    public int waitingJobs;
    public int runningJobs;
    public float avgWait;

    public Storage(String s){
        String[] Info = s.split(" ");
        ID = Integer.parseInt(Info[1]);
        type = Info[0];
        Status = Info[2];
        BootupTime = Integer.parseInt(Info[3]);
        core = Integer.parseInt(Info[4]);
        memory = Integer.parseInt(Info[5]);
        disk = Integer.parseInt(Info[6]);
        waitingJobs = Integer.parseInt(Info[7]);
        runningJobs = Integer.parseInt(Info[8]);
        avgWait = 0;
    }

}