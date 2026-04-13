package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Color;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField; // IDINAGDAG PARA SA FORGOT PASSWORD

public class stafLogIn extends javax.swing.JFrame {

    public stafLogIn() {
        initComponents();
        // Ito ang nagpapagana sa "Enter Key" kapag tapos na mag-type
        this.getRootPane().setDefaultButton(signInBtn);
        setLogoImage();
    }

    // Method para i-load ang Logo mula sa files mo (Top Left)
    private void setLogoImage() {
        try {
            // Siguraduhing nasa loob ng src/ folder mo ang bulsu_logo.jpg
            ImageIcon icon = new ImageIcon(getClass().getResource("/bulsu_logo.jpg"));
            Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
            logoLabel.setText(""); // Tatanggalin nito ang text placeholder at ipapakita na ang image
        } catch (Exception e) {
            System.out.println("Walang makitang logo sa resource folder: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        cardPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        passLabel = new javax.swing.JLabel();
        passField = new javax.swing.JPasswordField();
        forgotPassLabel = new javax.swing.JLabel();
        signInBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Staff Log In");
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(248, 250, 252));

        logoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        logoLabel.setForeground(new java.awt.Color(43, 70, 100));
        logoLabel.setText("BulSU Portal");

        cardPanel.setBackground(new java.awt.Color(255, 255, 255));

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); 
        titleLabel.setForeground(new java.awt.Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("STAFF LOGIN");

        userLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
        userLabel.setForeground(new java.awt.Color(100, 85, 71));
        userLabel.setText("Username");

        userField.setFont(new java.awt.Font("Segoe UI", 0, 14)); 

        passLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
        passLabel.setForeground(new java.awt.Color(100, 85, 71));
        passLabel.setText("Password");

        passField.setFont(new java.awt.Font("Segoe UI", 0, 14)); 

        forgotPassLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); 
        forgotPassLabel.setForeground(new java.awt.Color(37, 99, 235));
        forgotPassLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        forgotPassLabel.setText("Forgot Password?");
        forgotPassLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // NAAYOS: Idinagdag ang mouseClicked event para gumana kapag pinindot!
        forgotPassLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                forgotPassLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                forgotPassLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                forgotPassLabelMouseExited(evt);
            }
        });

        signInBtn.setBackground(new java.awt.Color(37, 99, 235));
        signInBtn.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        signInBtn.setForeground(new java.awt.Color(255, 255, 255));
        signInBtn.setText("SIGN IN");
        signInBtn.setBorderPainted(false);
        signInBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signInBtn.setFocusPainted(false);
        signInBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signInBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signInBtnMouseExited(evt);
            }
        });
        signInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signInBtnActionPerformed(evt);
            }
        });

        backBtn.setBackground(new java.awt.Color(248, 245, 241));
        backBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
        backBtn.setForeground(new java.awt.Color(100, 85, 71));
        backBtn.setText("← Back");
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backBtn.setFocusPainted(false);
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtnMouseExited(evt);
            }
        });

        org.jdesktop.layout.GroupLayout cardPanelLayout = new org.jdesktop.layout.GroupLayout(cardPanel);
        cardPanel.setLayout(cardPanelLayout);
        cardPanelLayout.setHorizontalGroup(
            cardPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardPanelLayout.createSequentialGroup()
                .add(50, 50, 50)
                .add(cardPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, userLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, userField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, passLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, passField)
                    .add(forgotPassLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, signInBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, backBtn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .add(50, 50, 50))
        );
        cardPanelLayout.setVerticalGroup(
            cardPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardPanelLayout.createSequentialGroup()
                .add(40, 40, 40)
                .add(titleLabel)
                .add(30, 30, 30)
                .add(userLabel)
                .add(5, 5, 5)
                .add(userField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(passLabel)
                .add(5, 5, 5)
                .add(passField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(forgotPassLabel)
                .add(30, 30, 30)
                .add(signInBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(backBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(40, 40, 40))
        );

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(logoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 130, Short.MAX_VALUE)
                .add(cardPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(300, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(logoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .add(cardPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    // NAAYOS: ITO ANG STAFF DATABASE LOGIN LOGIC NA NAG-OOPPEN NG DASHBOARD
    private void signInBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password.", "Empty Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String url = "jdbc:mysql://localhost:3306/school_management_system";
        String user = "root";
        String pass = "";

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            
            // QUERY: Kinukuha natin ang fullname para ipasa sa Dashboard!
            String sql = "SELECT u.username, s.full_name FROM users u " +
                         "LEFT JOIN staffs s ON u.staff_id = s.staff_id " +
                         "WHERE u.username = ? AND u.password = ? AND u.role = 'staff'";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String fullName = rs.getString("full_name") != null ? rs.getString("full_name") : username;
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + fullName, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // NAAYOS NA! Tatanggalin ko na sa comment ang dashboard. 
                // Gagawa tayo ng instance ng StaffDashboard kung meron ka na.
                try {
                    // Pansamantala, kung paano sa Student, ganito rin dito.
                    // Tiyakin mo lang na naka-create na yung StaffDashboard na may parameter!
                    /* StaffDashboard dashboard = new StaffDashboard(username, fullName);
                    dashboard.setLocationRelativeTo(null);
                    dashboard.setVisible(true);
                    this.dispose(); // I-sasara ang login frame
                    */
                    
                    // NOTE: Dahil hindi ko alam kung nagawa mo na ang StaffDashboard class, i-uncomment mo na lang 
                    // ang code block sa itaas 'pag ready na ang JFrame class ng StaffDashboard mo!
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Staff Dashboard not yet created or found.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                         

    // NAAYOS: IDINAGDAG ANG LOGIC PARA SA FORGOT PASSWORD NG STAFF
    private void forgotPassLabelMouseClicked(java.awt.event.MouseEvent evt) {                                             
        String username = JOptionPane.showInputDialog(this, "Enter your Staff Username:", "Forgot Password", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.trim().isEmpty()) return;

        String email = JOptionPane.showInputDialog(this, "Enter your registered Email:", "Forgot Password", JOptionPane.QUESTION_MESSAGE);
        if (email == null || email.trim().isEmpty()) return;

        String url = "jdbc:mysql://localhost:3306/school_management_system";
        String dbUser = "root";
        String dbPass = "";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            // I-verify sa database kung nagma-match ang username at email as 'staff'
            String verifySql = "SELECT * FROM users WHERE username = ? AND email = ? AND role = 'staff'";
            PreparedStatement verifyPst = conn.prepareStatement(verifySql);
            verifyPst.setString(1, username);
            verifyPst.setString(2, email);
            ResultSet rs = verifyPst.executeQuery();

            if (rs.next()) {
                JPasswordField newPasswordField = new JPasswordField();
                Object[] message = {
                    "Verification Successful!\nEnter your New Password:", newPasswordField
                };
                
                int option = JOptionPane.showConfirmDialog(this, message, "Reset Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (option == JOptionPane.OK_OPTION) {
                    String newPassword = new String(newPasswordField.getPassword());
                    
                    if(newPassword.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String updateSql = "UPDATE users SET password = ? WHERE username = ? AND role = 'staff'";
                    PreparedStatement updatePst = conn.prepareStatement(updateSql);
                    updatePst.setString(1, newPassword);
                    updatePst.setString(2, username);
                    updatePst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Password reset successful! You can now log in using your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username and Email do not match our records.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                            

    private void signInBtnMouseEntered(java.awt.event.MouseEvent evt) {                                       
        signInBtn.setBackground(new Color(29, 78, 216)); 
    }                                      

    private void signInBtnMouseExited(java.awt.event.MouseEvent evt) {                                      
        signInBtn.setBackground(new Color(37, 99, 235)); 
    }                                     

    private void backBtnMouseEntered(java.awt.event.MouseEvent evt) {                                     
        backBtn.setBackground(new Color(226, 232, 240)); 
    }                                    

    private void backBtnMouseExited(java.awt.event.MouseEvent evt) {                                    
        backBtn.setBackground(new Color(241, 245, 248)); 
    }                                   

    private void forgotPassLabelMouseEntered(java.awt.event.MouseEvent evt) {                                             
        forgotPassLabel.setForeground(new Color(29, 78, 216)); 
    }                                            

    private void forgotPassLabelMouseExited(java.awt.event.MouseEvent evt) {                                            
        forgotPassLabel.setForeground(new Color(37, 99, 235)); 
    }                                           

    private void backBtnMouseClicked(java.awt.event.MouseEvent evt) {                                     
        // Pansamantalang naka-comment para hindi mag-error habang ginagawa. I-uncomment na lang!
        // SignIn sign = new SignIn();
        // sign.setVisible(true);
        // this.dispose(); 
    }                                    

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(stafLogIn.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new stafLogIn().setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton backBtn;
    private javax.swing.JPanel cardPanel;
    private javax.swing.JLabel forgotPassLabel;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPasswordField passField;
    private javax.swing.JLabel passLabel;
    private javax.swing.JButton signInBtn;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField userField;
    private javax.swing.JLabel userLabel;
    // End of variables declaration                   
}