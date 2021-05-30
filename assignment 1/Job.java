public class Job {
    public int submitTime;
    public int jobID;
    public int estRuntime;
    public int core;
    public int memory;
    public int disk;

    // JOBN SUBMITTIME ID RUNTIME CORES MEM DISK
    public Job(String s) {
        String[] jobSplit = s.split(" ");
        submitTime = Integer.parseInt(jobSplit[1]);
        jobID = Integer.parseInt(jobSplit[2]);
        estRuntime = Integer.parseInt(jobSplit[3]);
        core = Integer.parseInt(jobSplit[4]);
        memory = Integer.parseInt(jobSplit[5]);
        disk = Integer.parseInt(jobSplit[6]);
    }
}
