import java.util.*;
import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.io.IOException;
import java.sql.*;
import java.io.*;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Housekeeping{
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
    //For this interface it is important to note that the Hotel Ids range from 10001 to 10010, meaning I have 10 hotels total
    public void housekeepingInterface(Connection conn)throws Exception, SQLException, IOException, java.lang.ClassNotFoundException{
        int userChoice = 0;
        int hotelId  = 0;
        int optionChoice = 0;
        int roomChoice = 0;
        boolean keepId = false;
        System.out.println("\nWelcome to the housekeeping interface:");
        do{
            if(keepId == false){
            System.out.println("Enter the Hotel ID of the Hotel that you would like to access\nThe Hotel IDs range from 10001 to 10010");
            hotelId = getInt();
            }
            keepId = true;
            System.out.println("Select one of the following: \n1. View all of the rooms in this hotel\n2. View all rooms that have been checked out, but aren't updated\n3. Update room status of all checked out rooms to available\n4. Update the status of an individual checked out room to Available\n5. Update the status of an individual checked out room to Cleaned \n6. Change Hotel ID\n7. Exit\n");
            userChoice = getInt();
            if(userChoice == 1){
           String sql = "SELECT * FROM room WHERE hotel_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
            while (rs.next()) {
                int roomNumber = rs.getInt("Room_number");
                String roomStatus = rs.getString("Room_status");
                String cleaningStatus = rs.getString("Cleaning_status");
                int hotelId1 = rs.getInt("Hotel_id");
                System.out.printf("Room Number: %-10d\tRoom Status: %-13s\tCleaning Status: %-10s\tHotel ID: %-10d%n", roomNumber, roomStatus, cleaningStatus, hotelId1);
                }
                System.out.println("\n");
                rs.close();
            }
            else{
                System.out.println("This hotel doesn't exist");
            }
            }
            else if(userChoice == 2){
                displayRoomsForCleaning(conn, hotelId);
                System.out.println("\n");
            }
            else if(userChoice == 3){
                updateAllRoomStatus(conn, hotelId);
                System.out.println("\n");
            }
            else if(userChoice == 4){
                System.out.println("Which room would you like to update?");
                roomChoice = getInt();
                updateRoomStatusForOneRoom(conn, hotelId, roomChoice);
                System.out.println("\n");
            }
            else if(userChoice == 5){
                System.out.println("Which room's cleaning status would you like to update?");
                roomChoice = getInt();
                updateCleaningStatus(conn, hotelId, roomChoice);
                System.out.println("\n");
            }
            else if(userChoice == 6){
                keepId = false;
            }
        }while(userChoice !=7);
    }
    public void displayRoomsForCleaning(Connection conn, int hotelId) throws SQLException{
        System.out.println("HIII");
        System.out.println(hotelId);
        String query = "SELECT room.room_number, room.room_status, room.cleaning_status, room.hotel_id, reservation.check_out_date "
            + "FROM room JOIN reservation ON room.room_number = reservation.room_number AND room.hotel_id = reservation.hotel_id "
            + "WHERE reservation.check_out_date < ? AND room.room_status = 'Taken' AND room.hotel_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, new java.sql.Date(new java.util.Date().getTime())); // current date
            stmt.setInt(2, hotelId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
            while (rs.next()) {
            System.out.printf("Room Number: %-10d Room Status: %-10s Cleaning Status: %-10s "
            + "Hotel ID: %-10d Check Out Date: %-10s\n", rs.getInt("room_number"), rs.getString("room_status"),
            rs.getString("cleaning_status"), rs.getInt("hotel_id"), rs.getString("check_out_date").substring(0, 10));
                }
            }
            else{
                System.out.println("All past reservation check outs have been updated for this hotel");
            }
    }
public void updateAllRoomStatus(Connection conn, int hotelId) {
    try {
        String sql = "UPDATE room SET room_status = 'Available' WHERE room_status = 'Taken' AND hotel_id = ? AND room_number IN (SELECT room_number FROM reservation WHERE check_out_date < ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, hotelId);
        stmt.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
        int rowsAffected = stmt.executeUpdate();
        System.out.printf("%d rooms have been updated to 'Available' status.%n", rowsAffected);
    } catch (SQLException e) {
        System.out.println("Error updating room status: " + e.getMessage());
    }
}
    public void updateRoomStatusForOneRoom(Connection conn, int hotelId, int roomNumber) throws SQLException {
    String query = "UPDATE room SET room_status = 'Available' " +
                   "WHERE room_status = 'Taken' " +
                   "AND room_number = ? " +
                   "AND hotel_id = ? " +
                   "AND room_number IN (SELECT room_number FROM reservation WHERE hotel_id = ? AND check_out_date < SYSDATE)";
    PreparedStatement stmt = conn.prepareStatement(query);
    stmt.setInt(1, roomNumber);
    stmt.setInt(2, hotelId);
    stmt.setInt(3, hotelId);
    int rowsAffected = stmt.executeUpdate();
    if (rowsAffected > 0) {
        System.out.println("Room status updated to 'Available' for room number " + roomNumber);
    } else {
        System.out.println("No rooms were updated.");
        }
    } 
    public void updateCleaningStatus(Connection conn, int hotelId, int roomNumber) throws SQLException {
        String query = "UPDATE room SET cleaning_status = 'Clean' WHERE room_number = ? AND hotel_id = ? AND room_status = 'Available' AND room_number IN (SELECT room_number FROM reservation WHERE check_out_date < CURRENT_DATE)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, roomNumber);
        stmt.setInt(2, hotelId);
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Cleaning status updated for room number " + roomNumber);
        } else {
            System.out.println("No rows updated for room number " + roomNumber);
        }
}

}
    