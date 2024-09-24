package HospitalManagementSystem;

import javax.print.Doc;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Kumar@123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctor");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter Your Choice");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1 :
                        //Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2 :
                        //View Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3 :
                        //View Doctor
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4 :
                        //Book Appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5 :
                        System.out.println("Thank You For Using Hospital Management System");
                        return;
                    default:System.out.println("Invalid Choice");

                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
        System.out.print("Enter Patient Id : ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id : ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YY-MM-DD) : ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                String appointmentQuery = "INSERT INTO appointment(patients_id, doctors_id, appointment_date) VALUES(?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,patientId);
                    preparedStatement.setInt(2,doctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.print("Appointment Booked!!");
                    }else{
                        System.out.print("Failed To Booked Appointment!!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.print("Doctor Not Available On This Date!!");
            }
        }else{
            System.out.print("Either Doctor Or Patient Does Not Exit!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointment WHERE doctors_id = ? and appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
