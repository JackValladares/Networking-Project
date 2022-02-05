import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

//Jack Valladares
//Project 6
//TCP Server
//CSCI 3342
public class DIYAppController
{
    public static void main(String[] args) throws Exception {
        //Make sure a port is given
        if(args.length == 0)
        {
            System.err.println("Error, no port provided... \n");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);

        Driver driver = new Driver(new ServerSocket(port));
        System.out.println("Total Sum: "+driver.calculate());
        System.exit(0);
    }
}

class Driver
{
    public static BigDecimal finalSum = new BigDecimal(0);
    public static int threads = 0;
    public static ServerSocket sock;

    final File data = new File("test.dat");
    FileInputStream dataStream = new FileInputStream(data);
    InputStreamReader readStream = new InputStreamReader(dataStream);
    BufferedReader getData = new BufferedReader(readStream);


    Driver(ServerSocket s) throws FileNotFoundException {
        sock = s;
    }

    public BigDecimal calculate() throws Exception
    {
        incrementThreads();
        newThread(true);
        do{
            Thread.sleep(5000);
        }while(threads > 0);

        System.out.println("\nCalculation Complete!!! Socket Closed");
        return finalSum;
    }

    public void newThread(boolean p) throws IOException
    {
        Thread t = new Thread(new ServerThread(sock, this, p));
        t.start();
        System.out.println("Thread Number: "+threads);
    }

    public String getSlice() throws IOException {
        //get slice of data up to fifty numbers long
        String dataSlice = "";
        String line;
        for(int j = 0; j < 50; j++)
        {
            if((line=getData.readLine()) != null)
            {
                dataSlice += line + "X";
            }
        }
        return dataSlice;
    }

    public void decrementThreads() { threads--; }

    public void incrementThreads() { threads++; }

    public int getThreads() { return threads; }

    public void addToSum(BigDecimal n) { finalSum = finalSum.add(n); }

}


class ServerThread implements Runnable {

    private ServerSocket sock;
    private Socket newSock;
    public String data;
    BigDecimal sum;
    boolean parent;
    Driver driver;

    public ServerThread(ServerSocket s, Driver n, boolean p) throws IOException {
        driver = n;
        sock = s;
        parent = p;



        //Debug:
        //System.out.println("Data set: \n"+d);
        System.out.println("New Thread Created");
    }

    @Override
    public void run(){


        try {
            newSock = sock.accept();;
            data = driver.getSlice();
            driver.newThread(false);
            if(!parent)
            {
                driver.incrementThreads();
            }

            try {
                standby(newSock);
            } catch (Exception e) {
                System.out.println("Problem with initial join message");
            }

            do {dataExchange();}while(sum.compareTo(BigDecimal.ZERO) > 0);



            try {
                standby(newSock);
            } catch (Exception e) {
                System.out.println("Problem with exit message");
            }


            //Close the socket
            try {
                driver.decrementThreads();
                System.out.println("Socket Closing...Thread Count: "+driver.getThreads());
                newSock.close();
            } catch (Exception e) {
                System.out.println("Error Closing the Socket");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //an iteration of receiving and sending data back and forth with the client
    public void dataExchange()
    {
        //Sends the data String to the Client
        try {
            send(newSock, data);
        } catch (Exception e) {
            System.out.println("Data Send Error");
        }

        //fetch result
        try {
            sum = standbyForResult(newSock);
        } catch (Exception e) {
            System.out.println("Error Fetching the Result");
        }

        //wait for final acknowledgement
		/*try {
			standby(sock);
		} catch (Exception e) {
			System.out.println("End of loop message error");
		}*/

        driver.addToSum(sum);

        try {
            data = driver.getSlice();
        } catch (IOException e) {
            System.out.println("Unable to retrieve data slice :(");

        }
    }


    //receive a message
    public void standby(Socket sock) throws Exception
    {
        //Receive Data from given socket
        Scanner sc = new Scanner(sock.getInputStream());
        String msg = sc.nextLine();

        //display that data
        System.out.println("Received a message: "+msg);
    }

    //receive a the result
    public static BigDecimal standbyForResult(Socket sock) throws Exception
    {
        //Receive Data from given socket
        Scanner sc = new Scanner(sock.getInputStream());
        BigDecimal partialSum = sc.nextBigDecimal();

        //display that data
        System.out.println("Received a result: "+partialSum);

        //give the partial sum to the runnable to be sent to the server
        return partialSum;
    }

    //send a message
    public static void send(Socket sock, String msg) throws Exception
    {
        //create Printwriter through which to send the message
        PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
        pw.println(msg);
    }


}





