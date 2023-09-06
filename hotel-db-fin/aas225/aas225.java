import java.io.*;
import java.sql.*;
import java.sql.ResultSet;
import java.util.*;

public class aas225 {

  static String getString(BufferedReader buff, String message) {
    String s1;
    try {
      System.out.print(message + " :");
      s1 = buff.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
    return s1;
  }

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

  public static void main(String[] args)
    throws Exception, SQLException, IOException, java.lang.ClassNotFoundException {
    Scanner scanner = new Scanner(System.in);
    BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
    String buildingName = "";
    int roomNumber = -1;
    int userChoice = 0;
    boolean runQuery = false;
    String id = getString(buff, "Enter username");
    String password = getString(buff, "Enter password");
    boolean allTrue = false;
    try (
      Connection conn = DriverManager.getConnection(
        "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241",
        id,
        password
      )
    ) {
      System.out.println("connection successfully made.");

      System.out.println(
        "Hello, welcome to Hotel California. Are you a customer, housekeeping-staff member, or a front-desk agent? \n1. Customer\n2. Staff Member\n3. Front-Desk Agent\n4. Exit"
      );
      while (userChoice < 1 || userChoice > 4) {
        userChoice = getInt();
        if (userChoice == 1) {
          Customer customer = new Customer();
          customer.onlineReserationInterface(conn);
        } else if (userChoice == 2) {
          Housekeeping housekeeping = new Housekeeping();
          housekeeping.housekeepingInterface(conn);
        } else if (userChoice == 3) {
          FrontDesk desk = new FrontDesk();
          desk.frontDeskInterface(conn);
        } else {
          System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }
}
