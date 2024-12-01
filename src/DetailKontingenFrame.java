import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class DetailKontingenFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tas";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private int kontingenId;
    private JTable detailTable;
    private DefaultTableModel tableModel;

    public DetailKontingenFrame(int id) {
        this.kontingenId = id;
        setTitle("Detail Data Kontingen");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        String[] columns = {"Field", "Value"};
        tableModel = new DefaultTableModel(columns, 0);
        detailTable = new JTable(tableModel);
        detailTable.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer()); // Set custom renderer for the image column
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Set preferred width for the photo column
        JScrollPane scrollPane = new JScrollPane(detailTable);

        // Create back button
        JButton backButton = new JButton("Kembali");
        backButton.addActionListener(e -> {
            new ListKontingenFrame().setVisible(true);
            this.dispose();
        });

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Load detail data
        loadDetailData();
    }

    private void loadDetailData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT k.*, n.nama AS nama_negara, foto " +
                             "FROM kontingen k " +
                             "LEFT JOIN negara n ON k.negaraId = n.id " +
                             "WHERE k.id = ?")) {

            stmt.setInt(1, kontingenId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Display country or ID if not available
                String negaraDisplay = rs.getString("nama_negara");
                if (negaraDisplay == null || negaraDisplay.trim().isEmpty()) {
                    negaraDisplay = rs.getString("negaraId");
                }

                // Handle 'Ganda' category names
                String nama = rs.getString("nama");
                String jenis = rs.getString("kategori");
                String namaDisplay = nama;
                if ("Ganda".equals(jenis)) {
                    String[] namaAnggota = nama.split("[,\\s]+dan[,\\s]+|,");
                    namaDisplay = String.join(" dan ", namaAnggota);
                }

                // Handle image path from database
                String photoPath = rs.getString("foto");
                ImageIcon photoIcon = null;

                if (photoPath != null && !photoPath.isEmpty()) {
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        photoIcon = new ImageIcon(photoPath); // Load image from the local path
                    }
                }

                // Add details to table
                Object[][] details = {
                        {"ID", rs.getInt("id")},
                        {"Nama", namaDisplay},
                        {"Jenis", jenis},
                        {"Cabang Olahraga", rs.getString("cabor")},
                        {"Deskripsi", rs.getString("deskripsi")},
                        {"Kelamin", rs.getString("kelamin")},
                        {"Status", rs.getBoolean("isSakit") ? "Sakit" : "Sehat"},
                        {"Negara", negaraDisplay},
                        {"Foto", photoIcon} // Display image in the table
                };

                for (Object[] detail : details) {
                    tableModel.addRow(detail);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data kontingen tidak ditemukan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading detail data: " + e.getMessage());
        }
    }

    // Custom TableCellRenderer to render images in the table
    class ImageRenderer extends JLabel implements TableCellRenderer {
        public ImageRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
            } else {
                setIcon(null);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int kontingenId = 1; // Sample ID, replace it with real ID
            new DetailKontingenFrame(kontingenId).setVisible(true);
        });
    }
}