import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.PrintWriter;

//Jack Valladares
//Project 6
//TCP Worker
//CSCI 3342
public class DIYAppWorker
{

    public static void main(String[] args) throws Exception
    {
        //sum of the given payload
        BigDecimal sum;
        //the socket
        Socket sock;

        //Client Name
        String name = "Worker";

        //makes sure proper parameters are passed
        if(args.length < 2)
        {
            System.err.println("Usage: TCPClient Server Port");
            System.exit(0);
        }

        //get server data
        InetAddress server = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        //set name based on console input
        if(args[2] != null)
        {
            name = args[2];
        }

        sock = new Socket(server, port);
        sendMsg(sock, "Worker ["+name+"] Joined");
        do
        {

            //Wait for the payload
            sum = standby(sock);

            //send calculated sub-sum to the server
            sendBigDecimal(sock, sum);

            //if(sum != 0) {sendMsg(sock, "Calculation done on worker [" + name + "]...awaiting payload data");}


        }while(sum.compareTo(BigDecimal.ZERO) > 0);

        sendMsg(sock, "Connection closed with worker ["+name+"]");

        System.out.println(name + " is finished processing data");
        sock.close();

    }

    //puts the program on standby to listen for a message
    public static BigDecimal standby(Socket sock) throws Exception
    {
        DecimalFormat decimalFormat = new DecimalFormat("0.###############################################################");
        BigDecimal sum = new BigDecimal(0);
        //Scanner and string for message receiving

        Scanner sc = new Scanner(sock.getInputStream());
        String numbers = sc.nextLine();
        String[] dataSet = numbers.split("X");

        //reads the slice of the Data Stream and converts it to doubles in an array
        for(int x = 0; x < dataSet.length-1; x++)
        {
            //Debug:
            //System.out.println("Adding data piece number "+(x+1));
            BigDecimal bd = new BigDecimal(dataSet[x]);
            sum = sum.add(bd);
        }

        //display the message
        System.out.println("Payload received of size " + (dataSet.length));
        System.out.println("Sum is: "+sum);

        //return payload size...tells the program if the payload is empty or not
        return sum;
    }

    //sends a number to the server
    public static void sendBigDecimal(Socket sock, BigDecimal sum) throws Exception
    {
        //create Printwriter through which to send the message
        PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);

        //send the message
        pw.println(sum);
    }


    //sends a message to the server
    public static void sendMsg(Socket sock, String sum) throws Exception
    {
        //create Printwriter through which to send the message
        PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);

        //send the message
        pw.println(sum);
    }


}

