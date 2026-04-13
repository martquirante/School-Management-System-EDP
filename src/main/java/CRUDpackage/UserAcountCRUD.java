package CRUDpackage;

import java.sql.*;
import java.util.Scanner;

public class UserAcountCRUD {

    // Database Credentials
    static String url = "jdbc:mysql://localhost:3306/school_management_system";
    static String user = "root";
    static String password = "";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            clearScreen();
            System.out.println("==============================");
            System.out.println("   USER ACCOUNT CRUD SYSTEM   ");
            System.out.println("==============================");
            System.out.println("[1] Create Account");
            System.out.println("[2] View Accounts");
            System.out.println("[3] Update Account");
            System.out.println("[4] Delete Account");
            System.out.println("[0] Exit");
            System.out.println("------------------------------");
            System.out.print("Reply with number: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1": createMenu(); break;
                case "2": viewMenu(); break;
                case "3": updateMenu(); break;
                case "4": deleteMenu(); break;
                case "0": 
                    clearScreen();
                    System.out.println("Exiting program... Thank you!");
                    return;
                default:
                    System.out.println("\nInvalid choice!");
                    pressEnterToContinue();
            }
        }
    }

    static void createMenu() {
        clearScreen();
        System.out.println("--- CREATE ACCOUNT ---");
        System.out.println("[1] Staff");
        System.out.println("[2] Student");
        System.out.println("[3] Professor");
        System.out.println("[4] Admin");
        System.out.println("[0] Back to Main Menu");
        System.out.println("----------------------");
        System.out.print("Reply with role number: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1": createUser("staff"); break;
            case "2": createUser("student"); break;
            case "3": createUser("professor"); break;
            case "4": createUser("admin"); break;
            case "0": return;
            default:
                System.out.println("\nInvalid choice!");
                pressEnterToContinue();
        }
    }

    static void viewMenu() {
        clearScreen();
        System.out.println("--- VIEW ACCOUNTS ---");
        System.out.println("[1] View All");
        System.out.println("[2] View Staff");
        System.out.println("[3] View Students");
        System.out.println("[4] View Professors");
        System.out.println("[5] View Admins");
        System.out.println("[0] Back to Main Menu");
        System.out.println("---------------------");
        System.out.print("Reply with option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1": viewUsers(null); break;
            case "2": viewUsers("staff"); break;
            case "3": viewUsers("student"); break;
            case "4": viewUsers("professor"); break;
            case "5": viewUsers("admin"); break;
            case "0": return;
            default:
                System.out.println("\nInvalid choice!");
                pressEnterToContinue();
        }
    }

    static void updateMenu() {
        clearScreen();
        System.out.println("--- UPDATE ACCOUNT ---");
        System.out.println("[1] Update Staff");
        System.out.println("[2] Update Student");
        System.out.println("[3] Update Professor");
        System.out.println("[4] Update Admin");
        System.out.println("[0] Back to Main Menu");
        System.out.println("----------------------");
        System.out.print("Reply with role number: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1": updateUser("staff"); break;
            case "2": updateUser("student"); break;
            case "3": updateUser("professor"); break;
            case "4": updateUser("admin"); break;
            case "0": return;
            default:
                System.out.println("\nInvalid choice!");
                pressEnterToContinue();
        }
    }

    static void deleteMenu() {
        clearScreen();
        System.out.println("--- DELETE ACCOUNT ---");
        System.out.println("[1] Delete Staff");
        System.out.println("[2] Delete Student");
        System.out.println("[3] Delete Professor");
        System.out.println("[4] Delete Admin");
        System.out.println("[0] Back to Main Menu");
        System.out.println("----------------------");
        System.out.print("Reply with role number: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1": deleteUser("staff"); break;
            case "2": deleteUser("student"); break;
            case "3": deleteUser("professor"); break;
            case "4": deleteUser("admin"); break;
            case "0": return;
            default:
                System.out.println("\nInvalid choice!");
                pressEnterToContinue();
        }
    }

    static void createUser(String role) {
        clearScreen();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("--- REGISTER " + role.toUpperCase() + " ---");

            String username;
            if (role.equals("student")) {
                System.out.print("Enter Student Number: ");
            } else if (role.equals("professor")) {
                System.out.print("Enter Professor ID: ");
            } else if (role.equals("staff")) {
                System.out.print("Enter Staff ID: ");
            } else {
                System.out.print("Enter Admin Username: ");
            }
            username = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setString(4, role);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[SUCCESS] " + role.toUpperCase() + " account created!");
            } else {
                System.out.println("\n[FAILED] Could not create account.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("\n[ERROR] That ID/Username or Email is already taken!");
            } else {
                System.out.println("\n[ERROR] Database error: " + e.getMessage());
            }
        }
        pressEnterToContinue();
    }

    static void viewUsers(String roleFilter) {
        clearScreen();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql;
            PreparedStatement pst;

            if (roleFilter == null) {
                System.out.println("--- VIEWING ALL ACCOUNTS ---");
                sql = "SELECT user_id, username, email, role FROM users ORDER BY user_id ASC";
                pst = conn.prepareStatement(sql);
            } else {
                System.out.println("--- VIEWING " + roleFilter.toUpperCase() + " ACCOUNTS ---");
                sql = "SELECT user_id, username, email, role FROM users WHERE role = ? ORDER BY user_id ASC";
                pst = conn.prepareStatement(sql);
                pst.setString(1, roleFilter);
            }

            ResultSet rs = pst.executeQuery();

            System.out.printf("%-5s | %-20s | %-30s | %-10s\n", "ID", "Username/No.", "Email", "Role");
            System.out.println("-------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d | %-20s | %-30s | %-10s\n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role").toUpperCase());
            }

            if (!found) {
                System.out.println("No records found.");
            }
            System.out.println("-------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        pressEnterToContinue();
    }

    static void updateUser(String role) {
        clearScreen();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("--- UPDATE " + role.toUpperCase() + " ACCOUNT ---");
            System.out.print("Enter Database User ID to update: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter New Login ID / Username: ");
            String username = sc.nextLine();

            System.out.print("Enter New Email: ");
            String email = sc.nextLine();

            System.out.print("Enter New Password: ");
            String pass = sc.nextLine();

            String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE user_id = ? AND role = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, pass);
            pst.setInt(4, id);
            pst.setString(5, role);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[SUCCESS] Account updated!");
            } else {
                System.out.println("\n[WARNING] No " + role.toUpperCase() + " found with that Database ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Please enter a valid numerical ID.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        pressEnterToContinue();
    }

    static void deleteUser(String role) {
        clearScreen();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("--- DELETE " + role.toUpperCase() + " ACCOUNT ---");
            System.out.print("Enter Database User ID to delete: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Confirm deletion? (Y/N): ");
            String confirm = sc.nextLine();

            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("\nDeletion cancelled.");
                pressEnterToContinue();
                return;
            }

            String sql = "DELETE FROM users WHERE user_id = ? AND role = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, role);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("\n[SUCCESS] Account deleted!");
            } else {
                System.out.println("\n[WARNING] No " + role.toUpperCase() + " found with that Database ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Please enter a valid numerical ID.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        pressEnterToContinue();
    }

    static void pressEnterToContinue() {
        System.out.println("\n(Press Enter to go back)");
        sc.nextLine();
    }

    // USSD-Style Screen Clearer
    static void clearScreen() {
        try {
            // Para sa mismong Command Prompt
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Walang gagawin dito
        }
        // FALLBACK PARA SA NETBEANS IDE (Para laging fresh ang screen tulad ng USSD)
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}