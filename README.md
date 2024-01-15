Explanation of my project:
When you run my program, you will start off with 3 options: Each one will lead to a different interface.
I implemented, the Customer online reservation access interface, Front-desk agent, and Housekeeping
For my customer interface: On the main menu, press 1 to go into the customer reservation interface:
Here you will choose one of the cities by typing it in, and that will lead you to your hotel. I only have 1 hotel per city
so the same city won't include multiple hotels. 
Then I will ask for check in and check out dates: Note here that they have to be in the specific format of (DD/Mon/YYYY)
All of my reservations are for 2023, so when testing, it would be best if you use 2023 as the year.
Also note that if you enter a check in date before the current date (For example May 3th today), you will be prompted to 
enter another date. I only allow for reservations for the present or future.  
For example a check in date would look like (07/May/2023) and the check out date could be (09/May/2023).
After this you will be presented with avaiable rooms, and prompted to pick a room by the number.
After this you will be asked if you are an existing customer or not and then you will be asked to enter the appropriate information. At the end I ask if you want to make a reservation or not, and if you say no you will be exited out.
If you say yes, I will book your reservation and then exit you from the program. 
Here are some test cases: 
1, Dallas, 19/May/2023, 21/May/2023, 27, N, Bobby, 14/Sep/1999, 123 hehehe, 1234567890, 1111111111111111, Y, Y
1, Houston, 22/May/2023, 23/May/2023, 27, Y, 10, Y

Front Desk:
This interface will allow for check in and check out. 
Once you enter this interface, you have to type in a customer name. Make sure that they exist in the database for it to work. Also make sure that their reservation check in date is the current date. Just to be safe, I would go to the customer reservation interface first, create a reservation for todays date (whichever date you are testing the program), and also make the checkout date the same. This will make it easier so that you can do the check in and check out with the same customer reseveration. After you enter the customer name, if their check in date is not today (the current date),
you will be exited out of the interface, as their is no point in them checking in today. If they do have a check in for today, I will make the room status to taken and update the cleaning status to show that the room is now occupied.
For checkout, the same process applies. Note that in my interface, I only allow customers to check out on their reservation date. After the customer checks out, the room will be cleaned and made avaiable, and that is reflected in my room_status and cleaning_status.

Housekeeping:
The housekeeping interface is very simple, as stated in the project description. You will start off by entering a hotel id depending on which hotel you want to go to. The hotel ids are between 10001 and 10010 (inclusive). After this my menu basically allows you to do anything you want in terms of the requirements. You can view all rooms, make a checked out room avaiable, clean a checked out room, change hotels, etc. 
