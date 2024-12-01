import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ListKontingenFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tas";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";


    private JTable kontingenTable;
    private JLabel totalLabel;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;

    public ListKontingenFrame() {
        setTitle("List Kontingen");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"ID", "Nama Kontingen"};
        tableModel = new DefaultTableModel(columns, 0);
        kontingenTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(kontingenTable);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterComboBox = new JComboBox<>(new String[]{"Semua", "Single", "Ganda"});
        filterPanel.add(new JLabel("Filter Kategori: "));
        filterPanel.add(filterComboBox);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton detailButton = new JButton("Lihat Detail");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton back = new JButton("List negara");
        buttonPanel.add(detailButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(back);

        // Create a panel to hold the button panel and total label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        totalLabel = new JLabel("Total Kontingen: 0");
        bottomPanel.add(totalLabel, BorderLayout.SOUTH);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add button listeners
        detailButton.addActionListener(e -> showDetailPage());
        editButton.addActionListener(e -> showEditPage());
        deleteButton.addActionListener(e -> deleteKontingen());
        back.addActionListener(e -> back());

        // Add filter listener
        filterComboBox.addActionListener(e -> loadKontingenData());

        // Load initial data
        loadKontingenData();
    }

    private void back(){
        this.dispose();
        ShowNegara.showNegaraGui();
    }

    private void loadKontingenData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nama, kategori FROM kontingen")) {

            // Clear existing rows
            tableModel.setRowCount(0);

            int totalCount = 0;
            int singleCount = 0;
            int gandaCount = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                String jenis = rs.getString("kategori");

                // Handle Ganda category
                if ("Ganda".equals(jenis)) {
                    // Split the name if it contains comma or 'dan'
                    String[] namaAnggota = nama.split("[,\\s]+dan[,\\s]+|,");

                    // Use all names for Ganda category
                    String namaDisplay = String.join(" dan ", namaAnggota);

                    tableModel.addRow(new Object[]{id, namaDisplay});
                    gandaCount++;
                } else {
                    tableModel.addRow(new Object[]{id, nama});
                    singleCount++;
                }
                totalCount++;
            }

            // Update total label with more detailed information
            String totalText = String.format(
                    "Total Kontingen: %d (Single: %d, Ganda: %d)",
                    totalCount, singleCount, gandaCount
            );
            totalLabel.setText(totalText);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading kontingen data: " + e.getMessage());
        }
    }

    private void showDetailPage() {
        int selectedRow = kontingenTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) kontingenTable.getValueAt(selectedRow, 0);
            new DetailKontingenFrame(id).setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontingen terlebih dahulu");
        }
    }

    private void showEditPage() {
        int selectedRow = kontingenTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) kontingenTable.getValueAt(selectedRow, 0);
            new EditKontingenFrame(id).setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontingen terlebih dahulu");
        }
    }

    private void deleteKontingen() {
        int selectedRow = kontingenTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) kontingenTable.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin menghapus kontingen ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM kontingen WHERE id = ?")) {

                    stmt.setInt(1, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        loadKontingenData(); // Refresh the table
                        JOptionPane.showMessageDialog(this, "Kontingen berhasil dihapus");
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus kontingen");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error menghapus kontingen: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih kontingen terlebih dahulu");
        }
    }
}