import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Coordinator {

    public static DatagramSocket server;
    public static DatagramPacket recivingVoteFromClient1;
    public static DatagramPacket recivingVoteFromClient2;
    public static DatagramPacket recivingVoteFromClient3;
    public static DatagramPacket recivingVoteForGlobalCommitFromNode1;
    public static DatagramPacket recivingVoteForGlobalCommitFromNode2;
    public static DatagramPacket recivingVoteForGlobalCommitFromNode3;
    public static int indicator;
    public static int yesCount = 0;
    public static int noCount = 0;
    public static Boolean flag;
    public static int[] arr; //  Contains the Timming for the Crashe's and No response time out.






    public Coordinator() throws SocketException {
    }


    public static void crashTime(int n) throws InterruptedException {
        int k = n * 1000; // converting milliseconds to Seconds
        Thread.sleep(k);
    }

    public static void noResponseTime(int n) throws SocketException {
        int k = n * 1000; // converting milliseconds to Seconds
        server.setSoTimeout(k); // 1sec = 1000 milliseconds --> 10 sec
    }

    public static void sendResponseTime( DatagramSocket server, int i, InetAddress add,int portID) throws IOException {
//      Sending the Crash and No response Time to the Nodes.
        String crashTimeForNodeValue = String.valueOf(i);
        byte timeBuf[] = crashTimeForNodeValue.getBytes(StandardCharsets.UTF_8);
        DatagramPacket setTimeFortheNode = new DatagramPacket(timeBuf, timeBuf.length,add,portID);
        server.send(setTimeFortheNode);
    }

    public static void reciveMsgFromNodes(DatagramPacket msg) throws IOException {
        try{
            flag = true;
            server.receive(msg);
            String nodeDataFromClient =new String(msg.getData());
            String removeExtraSpace = nodeDataFromClient.replaceAll("\\s+"," ").trim();
            System.out.println("Node "+ msg.getPort() +" voted: "+ removeExtraSpace);
            if(removeExtraSpace.equalsIgnoreCase("Yes")){
                yesCount = yesCount + 1;
            }
            else if (removeExtraSpace.contains("No")){
                noCount = noCount + 1;
            }

            if(noCount > 0){
                System.out.println("After the Prepare message the Nodes are Voted 'No' ");
                System.out.println("Aborting the Whole Transaction");
                System.out.println("\n\n\n--------Transaction Aborted--------\n\n\n ");
                System.exit(0);
            }
        }
        catch (IOException e){
            flag = false;
            indicator = 4;
            System.err.println("Node "+msg.getPort()+" haven't responded with in the setOut time limit" + e.getMessage());
            System.out.println("So globaly Aborting the Commit");
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {


        System.out.println("In this Project we have one Co-ordinator and three partispantes/Nodes\n");
        System.out.println( "1. There is No Crash of the Co-ordinator as well as the nodes \n" +
                            "2. The Co-ordinator sends the 'Prepare' message after the maximum waiting time of the Node/Nodes --> " +
                                "Node/Nodes send the vote as 'No' as response \n" +
                            "3. The Co-ordinator sends the 'Preapare' message in right interval But the Node/Nodes('Crashes') send the vote 'Yes' " +
                                "after the time out for No response in Co-ordinator \n" +
                            "4. The Co-ordinator crashes after the voting 'Yes' is done\n" +
                            "5. Both Nodes are crashed after the Votiing 'Yes' and Nodes are Done before the Co-ordinator say's about the " +
                                "Global Commit\n");
        System.out.println("Enter the 'Number' To which senario need to perform");

        Scanner scan = new Scanner(System.in);
        int input = scan.nextInt();
        switch (input) {
            case 1:
                arr = new int[]{0,0,0,0,0,0,0,0,0};
                break;
            case 2:
                arr = new int[]{20,150,0,0,10,0,10,0,10};
                break;
            case 3:
                arr = new int[]{0,15,0,20,20,0,20,0,20};
                break;
            case 4:
                arr = new int[]{0,150,25,0,200,0,200,0,200};
                break;
            case 5:
                arr = new int[]{0,150,25,20,200,20,200,20,200};
                break;
            default:
                System.out.println("Enter only the Above Senaiors:  :-) ");
        }


        server = new DatagramSocket(4160);
        InetAddress add = InetAddress.getByName("localhost");
//      set the Crash and Response to the Nodes
        sendResponseTime(server,arr[3],add,4161);
        sendResponseTime(server,arr[4],add,4161);
        sendResponseTime(server,arr[5],add,4162);
        sendResponseTime(server,arr[6],add,4162);
        sendResponseTime(server,arr[7],add,4163);
        sendResponseTime(server,arr[8],add,4163);


        System.out.println("\n\n\n----------Transaction Start----------\n\n\n ");

        String m1 = "Prepare";
        byte buf[] = m1.getBytes();
        byte buf1[] = new byte[3];


        DatagramPacket sendingCommitMsgToClient1 = new DatagramPacket(buf, buf.length,add,4161);
        DatagramPacket sendingCommitMsgToClient2 = new DatagramPacket(buf, buf.length,add,4162);
        DatagramPacket sendingCommitMsgToClient3 = new DatagramPacket(buf, buf.length,add,4163);

//       Reciving Votes from the Nodes.
        crashTime(arr[0]); // Coordinator Crashes for the 0 sec.
        noResponseTime(arr[1]); // Coordinator Waits for 10 sec.

        server.send(sendingCommitMsgToClient1);
        server.send(sendingCommitMsgToClient2);
        server.send(sendingCommitMsgToClient3);

        System.out.println("Connected to Node 1");
        System.out.println("Connected to Node 2");
        System.out.println("Connected to Node 3");
        System.out.println("Coordinator is asking Nodes that they are ready for Voting ?");
        System.out.println("Sent Prepare Command to Node 1");
        System.out.println("Sent Prepare Command to Node 2");
        System.out.println("Sent Prepare Command to Node 3");

        recivingVoteFromClient1 = new DatagramPacket(buf1,buf1.length);
        reciveMsgFromNodes(recivingVoteFromClient1);
        recivingVoteFromClient2 = new DatagramPacket(buf1,buf1.length);
        reciveMsgFromNodes(recivingVoteFromClient2);
        recivingVoteFromClient3 = new DatagramPacket(buf1,buf1.length);
        reciveMsgFromNodes(recivingVoteFromClient3);


        if(yesCount == 3 && flag == true){
            String m2 = "Initialize Global Commit";
            byte buf2[] = m2.getBytes();
            DatagramPacket sendingGlobleCommitMsgToClient1 = new DatagramPacket(buf2, buf2.length,add,4161);
            DatagramPacket sendingGlobleCommitMsgToClient2 = new DatagramPacket(buf2, buf2.length,add,4162);
            DatagramPacket sendingGlobleCommitMsgToClient3 = new DatagramPacket(buf2, buf2.length,add,4163);

            crashTime(arr[3]);

            server.send(sendingGlobleCommitMsgToClient1);
            server.send(sendingGlobleCommitMsgToClient2);
            server.send(sendingGlobleCommitMsgToClient3);

            System.out.println("Continuing Transaction...");
            System.out.println("Sent Initialize global Commit Command to Node 1");
            System.out.println("Sent Initialize global Commit Command to Node 2");
            System.out.println("Sent Initialize global Commit Command to Node 3");

            recivingVoteForGlobalCommitFromNode1 = new DatagramPacket(buf1, buf1.length);
            reciveMsgFromNodes(recivingVoteForGlobalCommitFromNode1);

            recivingVoteForGlobalCommitFromNode2 = new DatagramPacket(buf1, buf1.length);
            reciveMsgFromNodes(recivingVoteForGlobalCommitFromNode2);

            recivingVoteForGlobalCommitFromNode3 = new DatagramPacket(buf1, buf1.length);
            reciveMsgFromNodes(recivingVoteForGlobalCommitFromNode3);

            if(yesCount == 6 && flag == true){
                System.out.println("Global Commit is Done Sucessfully");
                System.out.println("\n\n\n-------Transaction Successful-------\n\n\n ");
            }
            else if(flag == false && indicator == 4){
                System.out.println("Aborting the Whole Transaction");
                System.out.println("\n\n\n--------Transaction Aborted--------\n\n\n ");
                System.exit(0);
            }
            else{
                System.out.println("Aborting the Whole Transaction");
                System.out.println("\n\n\n--------Transaction Aborted--------\n\n\n ");
                System.exit(0);
            }
        }
        else if (flag == false){
            System.out.println("Aborting the Whole Transaction");
            System.out.println("\n\n\n--------Transaction Aborted--------\n\n\n ");
            System.exit(0);
        }
    }
}
