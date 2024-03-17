# Project Overview

This project features a comprehensive hotel reservation system with three distinct interfaces for different users: Customers, Front Desk Agents, and Housekeeping staff. Each interface caters to specific functionalities within the hotel management ecosystem.

## Customer Online Reservation Access

### How It Works:
- After starting the program, you're presented with 3 options. Selecting `1` will take you to the Customer Reservation Interface.
- You'll then choose a city by typing it in. Each city corresponds to one hotel, so there are no duplicates within the same city.
- Next, you'll be prompted to enter check-in and check-out dates in the format `DD/Mon/YYYY`. Please note that all reservations are for the year 2023, and dates must be current or future dates.
- After entering valid dates, available rooms will be displayed. You'll select a room by entering its number.
- You'll then indicate whether you're an existing customer and provide relevant information.
- Finally, you'll be asked to confirm your reservation. Responding `no` will exit the program, while `yes` will book your reservation and then exit.

### Test Cases:
- `1, Dallas, 19/May/2023, 21/May/2023, 27, N, Bobby, 14/Sep/1999, 123 hehehe, 1234567890, 1111111111111111, Y, Y`
- `1, Houston, 22/May/2023, 23/May/2023, 27, Y, 10, Y`

## Front Desk Interface

### Functionality:
- This interface facilitates check-ins and check-outs.
- Upon access, you'll input a customer name. The system checks if the customer exists and has a reservation for the current date.
- For check-ins, the room status is updated to 'taken', and the cleaning status reflects occupancy.
- Check-outs are permitted only on the reservation end date. Following check-out, the room is marked for cleaning and becomes available.

### Usage Note:
For testing purposes, it's advisable to first create a reservation for the current day through the Customer Reservation Interface. This setup simplifies the check-in and check-out process.

## Housekeeping Interface

### Overview:
- The interface starts by prompting for a hotel ID, which ranges from 10001 to 10010.
- The menu offers various options, including viewing all rooms, making a checked-out room available, cleaning rooms, and changing hotels, aligning with housekeeping requirements.
