import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import Connection.Koneksi;

public class LoginGUI {
    private static Connection conn = null;

    public static void main(String[] args) {
        try {
            // Mendapatkan koneksi dari class Koneksi
            conn = Koneksi.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi database gagal: " + e.getMessage());
            System.exit(1);
        }

        // Membuat frame
        JFrame frame = new JFrame("Login Form");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Mengatur warna background untuk frame
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        // Membuat panel utama dengan layout GridBagLayout untuk lebih fleksibel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245)); // Background color for the panel
        frame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for elements

        // Title Label
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 123, 255)); // Stylish color for the title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Menambahkan label dan input email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); // Rounded border
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Menambahkan label dan input password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); // Rounded border
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Menambahkan tombol login
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 123, 255)); // Button color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside the button
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        // Menambahkan action listener pada tombol login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Email dan Password tidak boleh kosong!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proses login dengan database
                try {
                    String query = "SELECT * FROM user WHERE email = ? AND password = ?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, email);
                    pst.setString(2, password);

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        frame.dispose();
                        ShowNegara.showNegaraGui();
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Email atau Password salah!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    rs.close();
                    pst.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Error database: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Menampilkan frame
        frame.setVisible(true);
    }
}
