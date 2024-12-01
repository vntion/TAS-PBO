import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ShowNegara {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tas";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void showNegaraGui() {
        // Membuat frame dengan modern look
        JFrame frame = new JFrame("List Negara");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Judul
        JLabel titleLabel = new JLabel("List Negara", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 123, 255)); // Stylish color for the title
        frame.add(titleLabel, BorderLayout.NORTH);

        // Tabel
        String[] columnNames = {"ID", "Nama"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(0, 123, 255)); // Highlight color for selected row
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Load data dari database
        loadData(tableModel);

        // Panel bawah dengan tombol
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addButton = createModernButton("Tambah Negara");
        JButton editButton = createModernButton("Edit Negara");
        JButton deleteButton = createModernButton("Hapus Negara");
        JButton tambahKontingenButton = createModernButton("Tambah Kontingen");
        JButton lihatKontingenButton = createModernButton("Lihat Kontingen");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(tambahKontingenButton);
        buttonPanel.add(lihatKontingenButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Listener untuk tombol Tambah Negara
        addButton.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(frame, "Masukkan ID Negara:");
            String nama = JOptionPane.showInputDialog(frame, "Masukkan Nama Negara:");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO negara (id, nama) VALUES (?, ?)")) {

                int id = Integer.parseInt(idStr);
                stmt.setInt(1, id);
                stmt.setString(2, nama);
                stmt.executeUpdate();
                tableModel.addRow(new Object[]{id, nama});

            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listener untuk tombol Edit Negara
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String newNama = JOptionPane.showInputDialog(frame, "Masukkan Nama Baru Negara:", tableModel.getValueAt(selectedRow, 1));
                if (newNama != null && !newNama.trim().isEmpty()) {
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         PreparedStatement stmt = conn.prepareStatement("UPDATE negara SET nama = ? WHERE id = ?")) {

                        int id = (int) tableModel.getValueAt(selectedRow, 0);
                        stmt.setString(1, newNama);
                        stmt.setInt(2, id);
                        stmt.executeUpdate();
                        tableModel.setValueAt(newNama, selectedRow, 1);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Pilih baris yang ingin diedit!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener untuk tombol Hapus Negara
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM negara WHERE id = ?")) {

                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    tableModel.removeRow(selectedRow);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Pilih baris yang ingin dihapus!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener untuk tombol Tambah Kontingen
        tambahKontingenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InputKontingen().setVisible(true);
            }
        });

        // Listener untuk tombol Lihat Kontingen
        lihatKontingenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ListKontingenFrame().setVisible(true);
            }
        });

        // Menampilkan frame
        frame.setVisible(true);
    }

    private static void loadData(DefaultTableModel tableModel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM negara")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                tableModel.addRow(new Object[]{id, nama});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    public static void main(String[] args) {
        showNegaraGui();
    }
}
