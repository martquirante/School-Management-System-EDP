package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableModel;

public class StudentDashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StudentDashboard.class.getName());
    private String loggedInStudentId;
    private String loggedInFullName; 

    // NAAYOS NA CONSTRUCTOR: Kinukuha ang ID at tinatawag ang Load function
    public StudentDashboard(String studentNumber) {
        this.loggedInStudentId = studentNumber;
        this.loggedInFullName = "Student"; // Default kapag walang pangalan
        initComponents();
        styleComponents();
        loadDashboardData(); // DITO ANG MAGIC! Tinatawag na siya automatically
    }

    public StudentDashboard(String studentId, String fullName) {
        this.loggedInStudentId = studentId;
        this.loggedInFullName = fullName;
        initComponents();
        styleComponents();
        loadDashboardData(); 
    }

    private void loadDashboardData() {
        welcomeLabel.setText("Welcome, " + loggedInFullName + " (ID: " + loggedInStudentId + ")");
        lblProfileTitle.setText("Overview: " + loggedInFullName);

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String url = "jdbc:mysql://localhost:3306/school_management_system";
        String user = "root";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            
            String sql = "SELECT sub.subject_name, p.last_name, g.grade_value " +
                         "FROM users u " +
                         "JOIN enrollments e ON u.student_id = e.student_id " +
                         "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                         "LEFT JOIN professors p ON sub.professor_id = p.professor_id " +
                         "LEFT JOIN grades g ON e.enrollment_id = g.enrollment_id " +
                         "WHERE u.username = ?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, loggedInStudentId);
            
            ResultSet rs = pst.executeQuery();

            boolean hasSubjects = false;

            while(rs.next()) {
                hasSubjects = true;
                String subjectName = rs.getString("subject_name");
                String profName = rs.getString("last_name") != null ? "Prof. " + rs.getString("last_name") : "TBA";
                String grade = rs.getString("grade_value") != null ? rs.getString("grade_value") : "N/A";
                
                model.addRow(new Object[]{subjectName, profName, grade});
            }

            if (!hasSubjects) {
                model.addRow(new Object[]{"No Subjects Enrolled", "-", "-"});
            }

        } catch (Exception e) {
            System.err.println("Database Error loading dashboard: " + e.getMessage());
            model.addRow(new Object[]{"Error loading data", "-", "-"});
        }
    }

    private void styleComponents() {
        JButton[] sidebarBtns = {btnDashboard, btnProfile, btnSubjects, btnGrades, btnLogout};
        
        for (JButton btn : sidebarBtns) {
            btn.setBackground(new Color(248, 250, 252)); 
            btn.setForeground(new Color(100, 116, 139)); 
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(226, 232, 240)); 
                    btn.setForeground(new Color(37, 99, 235));   
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(248, 250, 252));
                    btn.setForeground(new Color(100, 116, 139));
                }
            });
        }
        
        btnDashboard.setBackground(new Color(226, 232, 240));
        btnDashboard.setForeground(new Color(37, 99, 235));

        profileCard.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        subjectsCard.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scheduleCard.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        
        jTable1.setRowHeight(35);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jTable1.getTableHeader().setBackground(new Color(248, 250, 252));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnProfile = new javax.swing.JButton();
        btnSubjects = new javax.swing.JButton();
        btnGrades = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        rightPanel = new javax.swing.JPanel();
        headerPanel = new javax.swing.JPanel();
        welcomeLabel = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        profileCard = new javax.swing.JPanel();
        lblProfileTitle = new javax.swing.JLabel();
        subjectsCard = new javax.swing.JPanel();
        lblSubjectsTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        scheduleCard = new javax.swing.JPanel();
        lblScheduleTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("School Management System - Student Dashboard");
        setMinimumSize(new java.awt.Dimension(1000, 700));

        sidebarPanel.setBackground(new java.awt.Color(240, 244, 248));
        sidebarPanel.setPreferredSize(new java.awt.Dimension(220, 700));

        logoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        logoLabel.setForeground(new java.awt.Color(43, 70, 100));
        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setText("BulSU Portal");

        btnDashboard.setText("   Dashboard");
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnProfile.setText("   My Profile");
        btnProfile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnSubjects.setText("   My Subjects");
        btnSubjects.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnGrades.setText("   My Grades");
        btnGrades.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnLogout.setText("   Logout");
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
            .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSubjects, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnGrades, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnSubjects, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnGrades, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 246, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.WEST);

        rightPanel.setLayout(new java.awt.BorderLayout());

        headerPanel.setBackground(new java.awt.Color(255, 255, 255));
        headerPanel.setPreferredSize(new java.awt.Dimension(780, 60));

        welcomeLabel.setFont(new java.awt.Font("Segoe UI", 1, 20)); 
        welcomeLabel.setForeground(new java.awt.Color(32, 48, 64));
        welcomeLabel.setText("Welcome, Student");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(welcomeLabel)
                .addContainerGap(694, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(welcomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        rightPanel.add(headerPanel, java.awt.BorderLayout.NORTH);

        contentPanel.setBackground(new java.awt.Color(245, 247, 250));

        profileCard.setBackground(new java.awt.Color(255, 255, 255));

        lblProfileTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); 
        lblProfileTitle.setText("Student Overview");

        javax.swing.GroupLayout profileCardLayout = new javax.swing.GroupLayout(profileCard);
        profileCard.setLayout(profileCardLayout);
        profileCardLayout.setHorizontalGroup(
            profileCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblProfileTitle))
        );
        profileCardLayout.setVerticalGroup(
            profileCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblProfileTitle))
        );

        subjectsCard.setBackground(new java.awt.Color(255, 255, 255));

        lblSubjectsTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        lblSubjectsTitle.setText("Enrolled Subjects");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "Professor", "Grade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout subjectsCardLayout = new javax.swing.GroupLayout(subjectsCard);
        subjectsCard.setLayout(subjectsCardLayout);
        subjectsCardLayout.setHorizontalGroup(
            subjectsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subjectsCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblSubjectsTitle))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );
        subjectsCardLayout.setVerticalGroup(
            subjectsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subjectsCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblSubjectsTitle)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
        );

        scheduleCard.setBackground(new java.awt.Color(255, 255, 255));

        lblScheduleTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); 
        lblScheduleTitle.setText("Today's Schedule");

        javax.swing.GroupLayout scheduleCardLayout = new javax.swing.GroupLayout(scheduleCard);
        scheduleCard.setLayout(scheduleCardLayout);
        scheduleCardLayout.setHorizontalGroup(
            scheduleCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblScheduleTitle))
        );
        scheduleCardLayout.setVerticalGroup(
            scheduleCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scheduleCardLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblScheduleTitle))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profileCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(subjectsCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20)
                        .addComponent(scheduleCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(30, 30, 30))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(profileCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(subjectsCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scheduleCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );

        rightPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(rightPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // In-comment out ko muna kung wala ka pang SignIn frame. Alisin mo lang yung comment kung meron na.
        // SignIn sign = new SignIn();
        // sign.setVisible(true);
        // this.dispose();
    }                                         

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> new StudentDashboard("2024500022").setVisible(true));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnGrades;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProfile;
    private javax.swing.JButton btnSubjects;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblProfileTitle;
    private javax.swing.JLabel lblScheduleTitle;
    private javax.swing.JLabel lblSubjectsTitle;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JPanel profileCard;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel scheduleCard;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JPanel subjectsCard;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration                   
}