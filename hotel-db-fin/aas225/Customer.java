import java.io.*;
import java.io.IOException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class Customer {

  public static int getInt() {
    Scanner scanner = new Scanner(System.in);
    int result = 0;
    boolean isValid = false;
    while (!isValid) {
      System.out.print("Enter an integer: ");

      if (scanner.hasNextInt()) {
        result = scanner.nextInt();
        isValid = true;
      } else {
        System.out.println("Invalid input. Please enter an integer.");
        scanner.next();
      }
    }
    return result;
  }

  public void onlineReserationInterface(Connection conn)
    throws Exception, SQLException, IOException, java.lang.ClassNotFoundException {
    Scanner scanner = new Scanner(System.in);

    ArrayList<Integer> customerIds = new ArrayList<>();
    ArrayList<Integer> reservationIds = new ArrayList<>();
    ArrayList<Integer> paymentIds = new ArrayList<>();

    String query3 = "SELECT customer_id FROM customer";
    PreparedStatement stmt3 = conn.prepareStatement(query3);
    ResultSet rs3 = stmt3.executeQuery();
    while (rs3.next()) {
      int id = rs3.getInt("customer_id");
      customerIds.add(id);
    }
    int customerId = 0;
    boolean idExists = true;
    while (idExists) {
      customerId = (int) (Math.random() * 900) + 100; // generate random 3 digit id
      idExists = customerIds.contains(customerId); // check if id exists in database
    }

    String query4 = "SELECT reservation_id FROM reservation";
    PreparedStatement stmt4 = conn.prepareStatement(query4);
    ResultSet rs4 = stmt4.executeQuery();
    while (rs4.next()) {
      int id = rs4.getInt("reservation_id");
      reservationIds.add(id);
    }

    int reservationId = 0;
    boolean idExists3 = true;
    while (idExists3) {
      reservationId = (int) (Math.random() * 900) + 100; // generate random 3 digit id
      idExists3 = reservationIds.contains(reservationId); // check if id exists in database
    }

    String query5 = "SELECT payment_id FROM payment";
    PreparedStatement stmt5 = conn.prepareStatement(query5);
    ResultSet rs5 = stmt5.executeQuery();
    while (rs5.next()) {
      int id = rs5.getInt("payment_id");
      paymentIds.add(id);
    }
    int paymentId = 0;
    boolean idExists2 = true;
    while (idExists2) {
      paymentId = (int) (Math.random() * 900) + 100; // generate random 3 digit id
      idExists2 = paymentIds.contains(paymentId); // check if id exists in database
    }

    int exCustomerId = -1;
    int roomNumber = 0;
    String roomType = "";
    double dollarCosts = 0;
    int pointCosts = 0;
    System.out.println("Welcome to the reservation system!");
    System.out.println(
      "These are all of the cities that we have hotels in: \n- New York\n- Los Angeles\n- Chicago\n- Houston\n- Phoenix\n- Philadelphia\n- San Antonio\n- San Diego\n- Dallas\n- San Francisco"
    );
    System.out.println("Please enter the city of your stay.");
    boolean cityExists = false;
    String city;
    do {
      city = scanner.nextLine().trim();
      // Capitalize the first letter of each word in the city input
      String[] cityWords = city.split("\\s+");
      StringBuilder sb = new StringBuilder();
      for (String word : cityWords) {
        sb.append(Character.toUpperCase(word.charAt(0)));
        sb.append(word.substring(1).toLowerCase());
        sb.append(" ");
      }
      city = sb.toString().trim();
      try {
        String query = "SELECT * FROM hotel WHERE city = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, city);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          cityExists = true;
        } else {
          System.out.println(
            "We do not have any hotels in the city you just entered"
          );
        }
      } catch (SQLException e) {
        System.out.println(
          "Error checking if city exists in database: " + e.getMessage()
        );
        return;
      }
    } while (!cityExists);
    
    // Get check-in and check_out date input
    System.out.print("Check-in date (DD/Mon/YYYY): ");
    String checkinDate = scanner.nextLine();

    // Keep prompting the user to enter a valid date format and check that the date is not before today
    LocalDate checkinLocalDate = null;
    while (
      checkinLocalDate == null || checkinLocalDate.isBefore(LocalDate.now())
    ) {
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
          "dd/MMM/yyyy"
        );
        checkinLocalDate = LocalDate.parse(checkinDate, formatter);
        if (checkinLocalDate.isBefore(LocalDate.now())) {
          System.out.println("Check-in date cannot be before today's date.");
          System.out.print("Check-in date (DD/Mon/YYYY): ");
          checkinDate = scanner.nextLine();
          checkinLocalDate = null;
        }
      } catch (DateTimeParseException e) {
        System.out.println(
          "Invalid date format. Please enter in the format DD/Mon/YYYY."
        );
        System.out.print("Check-in date (DD/Mon/YYYY): ");
        checkinDate = scanner.nextLine();
      }
    }

    // Get check-out date input
    System.out.print("Check-out date (DD/Mon/YYYY): ");
    String checkoutDate = scanner.nextLine();

    // Keep prompting the user to enter a valid date format and check that the date
    // is not before the check-in date
    LocalDate checkoutLocalDate = null;
    while (
      checkoutLocalDate == null || checkoutLocalDate.isBefore(checkinLocalDate)
    ) {
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
          "dd/MMM/yyyy"
        );
        checkoutLocalDate = LocalDate.parse(checkoutDate, formatter);
        if (checkoutLocalDate.isBefore(checkinLocalDate)) {
          System.out.println(
            "Check-out date cannot be before the check-in date."
          );
          System.out.print("Check-out date (DD/Mon/YYYY): ");
          checkoutDate = scanner.nextLine();
          checkoutLocalDate = null;
        }
      } catch (DateTimeParseException e) {
        System.out.println(
          "Invalid date format. Please enter in the format DD/Mon/YYYY."
        );
        System.out.print("Check-out date (DD/Mon/YYYY): ");
        checkoutDate = scanner.nextLine();
      }
    }
    // Calculate number of nights
    long numNights = ChronoUnit.DAYS.between(
      checkinLocalDate,
      checkoutLocalDate
    );
    if (numNights == 0) numNights = 1;

    String hotelQuery = "SELECT * FROM hotel WHERE city = ?";
    PreparedStatement hotelStmt = conn.prepareStatement(hotelQuery);
    hotelStmt.setString(1, city);
    ResultSet hotelRs = hotelStmt.executeQuery();

    while (hotelRs.next()) {
      int hotelId = hotelRs.getInt("hotel_id");
      String hotelName = hotelRs.getString("hotel_name");
      String hotelAddress = hotelRs.getString("hotel_address");
      System.out.printf(
        "Hotel ID: %-10d\tHotel Name: %-20s\tAddress: %-25s%n",
        hotelId,
        hotelName,
        hotelAddress
      );

      // Check if the hotel has available rooms for the specified dates
      String roomQuery =
        "SELECT room.*, room_type.room_type_name, room_type.dollar_costs, room_type.point_costs FROM room JOIN room_type ON room.room_type_id = room_type.room_type_id WHERE room.hotel_id = ? AND room.room_status = 'Available' AND room.room_number NOT IN (SELECT reservation.room_number FROM reservation WHERE (reservation.check_in_date <= ? AND reservation.check_out_date >= ?) OR (reservation.check_in_date <= ? AND reservation.check_out_date >= ?) OR (reservation.check_in_date >= ? AND reservation.check_out_date <= ?))";
      PreparedStatement roomStmt = conn.prepareStatement(roomQuery);
      roomStmt.setInt(1, hotelId);
      roomStmt.setString(2, checkinDate);
      roomStmt.setString(3, checkinDate);
      roomStmt.setString(4, checkoutDate);
      roomStmt.setString(5, checkoutDate);
      roomStmt.setString(6, checkinDate);
      roomStmt.setString(7, checkoutDate);
      ResultSet roomRs = roomStmt.executeQuery();

      if (!roomRs.next()) {
        System.out.println(
          "No rooms available at " + hotelName + " during the specified dates."
        );
      } else {
        // Display available rooms for the hotel
        List<Integer> availableRooms = new ArrayList<>();
        System.out.println("Available rooms:");
        while (roomRs.next()) {
          roomNumber = roomRs.getInt("room_number");
          roomType = roomRs.getString("room_type_name");
          dollarCosts = roomRs.getDouble("dollar_costs");
          pointCosts = roomRs.getInt("point_costs");
          availableRooms.add(roomNumber);
          System.out.printf(
            "Room Number: %-10d\tRoom Type: %-20s\tBase Dollar Costs: $%.2f\tBase Point Costs: %d%n",
            roomNumber,
            roomType,
            dollarCosts,
            pointCosts
          );
        }

        int selectedRoomNumber = 0;
        String name = "";
        String dateOfBirthStr1 = "23-Jul-1988";
        LocalDate dateOfBirth = LocalDate.parse(
          dateOfBirthStr1,
          DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        );
        String address = "";
        String phoneNumber = "";
        String creditCardNumber = "";
        int totalPoints = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
          "dd/MMM/yyyy"
        );
        String dateOfBirthStr;

        while (true) {
          System.out.print(
            "Which room would you like to select in " + hotelName + "?: \n"
          );
          selectedRoomNumber = getInt();
          if (availableRooms.contains(selectedRoomNumber)) {
            break;
          } else {
            System.out.println("Invalid room number. Please try again.");
          }
        }

        // Check if customer is new or existing
        boolean existingCustomer = false;
        while (true) {
          System.out.print("Are you an existing customer? (Y/N): ");
          String existingCustomerStr = scanner.nextLine();
          if (existingCustomerStr.equalsIgnoreCase("Y")) {
            existingCustomer = true;
            break;
          } else if (existingCustomerStr.equalsIgnoreCase("N")) {
            break;
          } else {
            System.out.println("Invalid input. Please enter Y or N.");
          }
        }
        int custCheck = 3;
        if (existingCustomer && custCheck > 0) {
          while (custCheck > 0) {
            System.out.print("Please enter your customer ID: ");
            String exCustomerIdStr = scanner.nextLine().trim();
            try {
              exCustomerId = Integer.parseInt(exCustomerIdStr);
              // Check if customer exists in database
              PreparedStatement checkCustomerStmt = conn.prepareStatement(
                "SELECT * FROM Customer WHERE Customer_id = ?"
              );
              checkCustomerStmt.setInt(1, exCustomerId);
              ResultSet customerRs = checkCustomerStmt.executeQuery();

              if (!customerRs.next()) {
                custCheck -= 1;
                System.out.println("Customer not found. Please try again. Only " + custCheck + " tries left");
              } else {
                // If customer exists, get their information from the database
                name = customerRs.getString("c_name");
                address = customerRs.getString("address");
                phoneNumber = customerRs.getString("Phone_Number");
                creditCardNumber = customerRs.getString("credit_card_number");
                dateOfBirthStr = customerRs.getString("Date_of_birth");
                break;
              }
            } catch (NumberFormatException e) {
              custCheck -= 1;
              System.out.println("Invalid customer ID. Please enter a number. Only " + custCheck+ " tries left");
            } catch (SQLException e) {
              System.out.println(
                "Error checking customer ID: " + e.getMessage()
              );
              return;
            }
          }
        }
        if(custCheck == 0){
          existingCustomer = false;
         System.out.print("You will now be checked in as a new customer: ");
        }
        
        if (!existingCustomer) {
          while (true) {
            System.out.print("Enter your name: ");
            name = scanner.nextLine();
            if (name.length() > 0 && name.length() <= 30) {
              break;
            } else {
              System.out.println(
                "Invalid input. Name must be between 1 and 30 characters."
              );
            }
          }

          while (true) {
            System.out.print("Enter your date of birth (DD/Mon/YYYY): ");
            dateOfBirthStr = scanner.nextLine();
            try {
              dateOfBirth = LocalDate.parse(dateOfBirthStr, formatter);
              break;
            } catch (DateTimeParseException e) {
              System.out.println(
                "Invalid date format. Please enter in the format DD/Mon/YYYY."
              );
            }
          }

          while (true) {
            System.out.print("Enter your address: ");
            address = scanner.nextLine();
            if (address.length() > 0 && address.length() <= 30) {
              break;
            } else {
              System.out.println(
                "Invalid input. Address must be between 1 and 30 characters."
              );
            }
          }

          while (true) {
            System.out.print("Enter your phone number (10 digits): ");
            phoneNumber = scanner.nextLine();
            if (phoneNumber.matches("\\d{10}")) {
              break;
            } else {
              System.out.println(
                "Invalid input. Phone number must be 10 digits."
              );
            }
          }

          while (true) {
            System.out.print("Enter your credit card number (16 digits): ");
            creditCardNumber = scanner.nextLine();
            if (creditCardNumber.matches("\\d{16}")) {
              break;
            } else {
              System.out.println(
                "Invalid input. Credit card number must be 16 digits."
              );
            }
          }
          boolean freqCustomer = false;
          while (true) {
            System.out.print(
              "Would you like to enroll in the Frequent Customer Program? You will receive 200 points for signing up. (Y/N): "
            );
            String freqCust = scanner.nextLine();
            if (freqCust.equalsIgnoreCase("Y")) {
              freqCustomer = true;
              break;
            } else if (freqCust.equalsIgnoreCase("N")) {
              break;
            } else {
              System.out.println("Invalid input. Please enter Y or N.");
            }
          }

          if (freqCustomer == true) {
            totalPoints = 200;
          }
        }

        // Parse checkinDate and checkoutDate into LocalDate objects
        LocalDate checkinDateObj = LocalDate.parse(
          checkinDate,
          DateTimeFormatter.ofPattern("dd/MMM/yyyy")
        );
        LocalDate checkoutDateObj = LocalDate.parse(
          checkoutDate,
          DateTimeFormatter.ofPattern("dd/MMM/yyyy")
        );

        // Check if rate calculator should be used
        LocalDate rateCalculatorStartDate = LocalDate.parse("2023-12-20");
        LocalDate rateCalculatorEndDate = LocalDate.parse("2023-12-31");
        double roomCost = dollarCosts * numNights;
        if (
          (
            checkinDateObj.isAfter(rateCalculatorStartDate) ||
            checkinDateObj.isEqual(rateCalculatorStartDate)
          ) &&
          (
            checkoutDateObj.isBefore(rateCalculatorEndDate) ||
            checkoutDateObj.isEqual(rateCalculatorEndDate)
          )
        ) {
          roomCost = roomCost * 1.5;
        }

        System.out.println("Here are your reservation details: ");
        System.out.println("Room Number: " + selectedRoomNumber);
        System.out.println("Room Type: " + roomType);
        System.out.println("Check-in Date: " + checkinDate);
        System.out.println("Check-out Date: " + checkoutDate);
        System.out.println("Total Cost: $" + roomCost);
        // Ask if the customer wants to book a reservation
        while (true) {
          System.out.print("\nWould you like to book a reservation? (Y/N): ");
          String bookReservation = scanner.nextLine().trim().toUpperCase();

          if (bookReservation.equals("Y")) {
            if (!existingCustomer) {
              customerId++;
              PreparedStatement insertCustomerStmt = conn.prepareStatement(
                "INSERT INTO Customer (Customer_id, c_name, Date_of_birth, address, Phone_Number, credit_card_number, Total_points) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
              );
              LocalDate dateOfBirthLocalDate = LocalDate.parse(
                dateOfBirthStr1,
                DateTimeFormatter.ofPattern("dd-MMM-yyyy")
              );
              java.sql.Date dateOfBirthSqlDate = java.sql.Date.valueOf(
                dateOfBirthLocalDate
              );
              insertCustomerStmt.setInt(1, customerId);
              insertCustomerStmt.setString(2, name);
              insertCustomerStmt.setDate(3, dateOfBirthSqlDate);
              insertCustomerStmt.setString(4, address);
              insertCustomerStmt.setString(5, phoneNumber);
              insertCustomerStmt.setLong(5, Long.parseLong(phoneNumber));
              insertCustomerStmt.setString(6, creditCardNumber);
              insertCustomerStmt.setInt(7, totalPoints);
              insertCustomerStmt.executeUpdate();
            }
            reservationId++;
            paymentId++;

            int cancellationFee = (int) (Math.random() * 101) + 50;
            // Insert reservation data
            PreparedStatement insertReservationStmt = conn.prepareStatement(
              "INSERT INTO Reservation (Reservation_id, Check_in_date, Check_out_date, Points_cost, Dollars_cost, Cancellation_fee, Customer_id, Room_number, Hotel_id) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            insertReservationStmt.setInt(1, reservationId);
            insertReservationStmt.setString(2, checkinDate);
            insertReservationStmt.setString(3, checkoutDate);
            insertReservationStmt.setNull(4, java.sql.Types.INTEGER);
            insertReservationStmt.setNull(5, java.sql.Types.DOUBLE);
            insertReservationStmt.setDouble(6, cancellationFee);
            if (existingCustomer) insertReservationStmt.setInt(
              7,
              exCustomerId
            ); else insertReservationStmt.setInt(7, customerId);
            insertReservationStmt.setInt(8, selectedRoomNumber);
            insertReservationStmt.setInt(9, hotelId);
            insertReservationStmt.executeUpdate();

            // Insert payment data
            PreparedStatement insertPaymentStmt = conn.prepareStatement(
              "INSERT INTO Payment (Payment_id, Dollar_amount, Points_amount, Total_cost, Customer_id, Reservation_id) " +
              "VALUES (?, ?, ?, ?, ?, ?)"
            );
            insertPaymentStmt.setInt(1, paymentId);
            insertPaymentStmt.setDouble(2, roomCost);
            insertPaymentStmt.setInt(3, 0);
            insertPaymentStmt.setDouble(4, roomCost);
            if (existingCustomer) insertPaymentStmt.setInt(
              5,
              exCustomerId
            ); else insertPaymentStmt.setInt(5, customerId);
            insertPaymentStmt.setInt(6, reservationId);
            insertPaymentStmt.executeUpdate();
            System.out.println("Congrats! Your reservation has been booked and your payment was successful");
            break;
          } else if (bookReservation.equals("N")) {
            System.out.println("Thank you for considering our hotel. Goodbye!");
            break;
          } else {
            System.out.println("Invalid input. Please enter 'Y' or 'N'.");
          }
        }
      }
    }
  }
}
