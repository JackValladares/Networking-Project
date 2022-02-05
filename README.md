# Networking-Project
A networking project built using the TCP paradigm utilizing multi-threading through multiple worker machines.

**The Components** 🧩️
 - dataset.py: Generates a massive list of random decimal numbers and exports the data set to test.dat
 - DIYAppController: Acts as the network controller. The DIYAppController waits to make a connection with each of the workers, and, through a driver class,
 reads and iterates through the data set, sends the data out to be processed in parallel, and calculates the overall sum
 - DIYAppWorker: Acts as each of the workers on the network. Each instance of DIYAppWorker establishes a connection with the controller and performs calculations in parallel 
  

*How it works** 🔧
 - The controller class is launched through the console with the port number as a parameter, then waits for workers to connect to it
 - Each worker class is run with the controller's ip address, the port number, and optionally the worker's name (repsectively) as parameters.
 - Once the controller makes a connection with at least one worker, it iterates through the data set and sends data to the workers in chunks of fifty items
 - Each worker, upon receiving a chunk of data, calculates the sum of the numbers and sends the result back to the controller
 - The controller adds this sum to the grand total, and sends another chunk to the worker
 - After all of the data has been processed, the workers close the socket and the controller prints the final sum
 
**To-Do** ✔️
 - Add a User-Interface to make the program more user-friendly
 - Make it so that the controller doesn't begin sending data until the user activates a trigger, so that it's easier to use multiple workers at once
 - Tweak the algorithm to minimize data loss, as several packets can be dropped and hurt the accuracy of the overall result
