import java.io.*;
import java.io.IOException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class FrontDesk {

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

  public void frontDeskInterface(Connection conn)
    throws Exception, SQLException, IOException, java.lang.ClassNotFoundException {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("What would you like to do?");
      System.out.println("1. Check in a customer");
      System.out.println("2. Check out a customer");
      System.out.println("3. Quit");

      int choice = -1;
      while (true) {
        try {
          choice = getInt();
          if (choice >= 1 && choice <= 3) {
            break;
          } else {
            System.out.println(
              "Invalid choice, please enter a number between 1 and 3"
            );
          }
        } catch (NumberFormatException e) {
          System.out.println("Invalid choice, please enter a number");
        }
      }

      if (choice == 1) {
        String customerName = "";
        String phoneNumber = "";
        System.out.println("Please enter the customer's name:");
        while (customerName.isEmpty()) {
          System.out.print("Enter customer name: ");
          customerName = scanner.nextLine().trim();
        }

        PreparedStatement stmt = conn.prepareStatement(
          "SELECT * FROM customer WHERE c_name=?",
          ResultSet.TYPE_SCROLL_SENSITIVE,
          ResultSet.CONCUR_READ_ONLY
        );
        stmt.setString(1, customerName);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
          // No matches found
          System.out.println("No customers found with name: " + customerName);
        } else {
          // Single match found
          System.out.println("Welcome " + rs.getString("c_name") + "!");
          int customerId = rs.getInt("customer_id");
          PreparedStatement stmt2 = conn.prepareStatement(
            "SELECT * FROM reservation WHERE customer_id=? AND check_in_date<=? AND check_out_date>=?"
          );

          stmt2.setInt(1, customerId);

          DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
          Date currentDate = new Date(System.currentTimeMillis());

          String formattedDate = df.format(currentDate);
          java.util.Date utilDate = df.parse(formattedDate);
          java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

          stmt2.setDate(2, sqlDate);
          stmt2.setDate(3, sqlDate);

          ResultSet rs2 = stmt2.executeQuery();

          if (!rs2.next()) {
            // No reservations found for today
            System.out.println(
              "No reservations found for today for customer: " + customerName
            );
            return;
          } else {
            // Reservation found
            int reservationId = rs2.getInt("reservation_id");
            int roomNum = rs2.getInt("room_number");
            int hotelId = rs2.getInt("hotel_id");

            // Update room and cleaning status
            PreparedStatement stmt3 = conn.prepareStatement(
              "UPDATE room SET room_status='Taken', cleaning_status='Dirty' WHERE room_number=? AND hotel_id=?"
            );
            stmt3.setInt(1, roomNum);
            stmt3.setInt(2, hotelId);
            stmt3.executeUpdate();

            // Get hotel name
            PreparedStatement stmt4 = conn.prepareStatement(
              "SELECT hotel_name FROM hotel WHERE hotel_id=?"
            );
            stmt4.setInt(1, hotelId);
            ResultSet rs4 = stmt4.executeQuery();
            rs4.next();
            String hotelName = rs4.getString("hotel_name");

            // Print reservation details
            System.out.println(
              "Room " +
              roomNum +
              " in hotel " +
              hotelName +
              " is ready for you!"
            );
            System.exit(0);
          }
        }
      } else if (choice == 2) {
        // Ask for customer information
        System.out.println("Please enter the customer's name:");
        String customerName = scanner.nextLine().trim();

        // Query the database to find the reservation
        PreparedStatement stmt = conn.prepareStatement(
          "SELECT * FROM customer WHERE c_name=?"
        );
        stmt.setString(1, customerName);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
          // No customer found
          System.out.println("No customer found with name: " + customerName);
        } else {
          int customerId = rs.getInt("customer_id");
          String phoneNumber = rs.getString("phone_number");
          PreparedStatement stmt2 = conn.prepareStatement(
            "SELECT * FROM reservation WHERE customer_id=? AND check_out_date = TO_DATE(TO_CHAR(CURRENT_DATE, 'DD/Mon/YYYY'), 'DD/Mon/YYYY')"
          );
          stmt2.setInt(1, customerId);
          ResultSet rs2 = stmt2.executeQuery();
          if (!rs2.next()) {
            // No reservation found
            System.out.println(
              "No reservation found for customer: " + customerName
            );
          } else {
            // Reservation found
            int reservationId = rs2.getInt("reservation_id");
            int roomNum = rs2.getInt("room_number");
            int hotelId = rs2.getInt("hotel_id");
            PreparedStatement stmt3 = conn.prepareStatement(
              "UPDATE room rm SET rm.Room_status = 'Available' WHERE rm.Room_number = (SELECT Room_number FROM reservation WHERE reservation_id = ?) AND rm.Hotel_id = (SELECT Hotel_id FROM reservation WHERE reservation_id = ?)"
            );
            stmt3.setInt(1, reservationId);
            stmt3.setInt(2, reservationId);
            stmt3.executeUpdate();
            System.out.println(
              "Check-out complete for room " + roomNum + " in hotel " + hotelId
            );
            System.exit(0);
          }
        }
      } else if (choice == 3) {
        System.out.println("You have exited the front desk");
        System.exit(0);
      }
    }
  }
}
