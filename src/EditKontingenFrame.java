import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EditKontingenFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tas";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private int kontingenId;
    private JTextField namaField;
    private JRadioButton singleButton, gandaButton;
    private JFileChooser fileChooser;
    private JComboBox<String> caborComboBox;
    private JTextArea deskripsiArea;
    private JRadioButton priaButton, wanitaButton;
    private JCheckBox sakitCheckBox;
    private JComboBox<NegaraItem> negaraComboBox;

    // Inner class to hold Negara ID and Name
    private class NegaraItem {
        int id;
        String name;

        NegaraItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public EditKontingenFrame(int id) {
        this.kontingenId = id;
        setTitle("Input Kontingen");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nama field
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        namaField = new JTextField(20);
        mainPanel.add(namaField, gbc);

        // Single/Ganda radio buttons
        gbc.gridx = 0; gbc.gridy = 1;
        ButtonGroup jenisGroup = new ButtonGroup();
        singleButton = new JRadioButton("Single");
        gandaButton = new JRadioButton("Ganda");
        jenisGroup.add(singleButton);
        jenisGroup.add(gandaButton);
        JPanel jenisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jenisPanel.add(singleButton);
        jenisPanel.add(gandaButton);
        mainPanel.add(new JLabel("Jenis:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(jenisPanel, gbc);

        // Photo upload
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Foto:"), gbc);
        gbc.gridx = 1;
        JButton uploadButton = new JButton("Upload Foto");
        fileChooser = new JFileChooser();
        uploadButton.addActionListener(e -> fileChooser.showOpenDialog(this));
        mainPanel.add(uploadButton, gbc);

        // Cabor dropdown
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Cabor:"), gbc);
        gbc.gridx = 1;
        String[] caborOptions = {"Badminton", "Basket", "Voli", "Sepak Bola", "Tenis",
                "Renang", "Atletik", "Tinju", "Panahan", "Karate"};
        caborComboBox = new JComboBox<>(caborOptions);
        mainPanel.add(caborComboBox, gbc);

        // Deskripsi
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        deskripsiArea = new JTextArea(4, 20);
        deskripsiArea.setLineWrap(true);
        mainPanel.add(new JScrollPane(deskripsiArea), gbc);

        // Kelamin radio buttons
        gbc.gridx = 0; gbc.gridy = 5;
        ButtonGroup kelaminGroup = new ButtonGroup();
        priaButton = new JRadioButton("Pria");
        wanitaButton = new JRadioButton("Wanita");
        kelaminGroup.add(priaButton);
        kelaminGroup.add(wanitaButton);
        JPanel kelaminPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        kelaminPanel.add(priaButton);
        kelaminPanel.add(wanitaButton);
        mainPanel.add(new JLabel("Kelamin:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(kelaminPanel, gbc);

        // Sakit checkbox
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        sakitCheckBox = new JCheckBox("Sakit");
        mainPanel.add(sakitCheckBox, gbc);

        // Negara dropdown
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Negara:"), gbc);
        gbc.gridx = 1;
        negaraComboBox = new JComboBox<>();
        loadNegaraData();
        mainPanel.add(negaraComboBox, gbc);

        // Add back button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        JButton backButton = new JButton("Kembali");
        backButton.addActionListener(e -> {
            new ListKontingenFrame().setVisible(true);
            this.dispose();
        });
        mainPanel.add(backButton, gbc);

        // Adjust submit button position
        gbc.gridx = 1;
        gbc.gridy = 8;
        JButton submitButton = new JButton("Edit Kontingen");
        submitButton.addActionListener(e -> submitEdit());
        mainPanel.add(submitButton, gbc);

        // Add main panel to frame
        add(new JScrollPane(mainPanel));

        // Load existing data if editing
        if (kontingenId > 0) {
            loadExistingData();
        }
    }

    private void loadNegaraData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nama FROM negara")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int negaraId = rs.getInt("id");
                String negaraName = rs.getString("nama");
                negaraComboBox.addItem(new NegaraItem(negaraId, negaraName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading negara data: " + e.getMessage());
        }
    }

    private void loadExistingData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM kontingen WHERE id = ?")) {

            stmt.setInt(1, kontingenId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                namaField.setText(rs.getString("nama"));
                String jenisKontingen = rs.getString("kategori");
                if ("Single".equals(jenisKontingen)) {
                    singleButton.setSelected(true);
                } else {
                    gandaButton.setSelected(true);
                }

                caborComboBox.setSelectedItem(rs.getString("cabor"));
                deskripsiArea.setText(rs.getString("deskripsi"));

                String kelamin = rs.getString("kelamin");
                if ("Pria".equals(kelamin)) {
                    priaButton.setSelected(true);
                } else {
                    wanitaButton.setSelected(true);
                }

                sakitCheckBox.setSelected(rs.getBoolean("isSakit"));

                // Find and select the correct negara based on its ID
                int negaraId = rs.getInt("negaraId");
                for (int i = 0; i < negaraComboBox.getItemCount(); i++) {
                    NegaraItem item = negaraComboBox.getItemAt(i);
                    if (item.id == negaraId) {
                        negaraComboBox.setSelectedItem(item);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading kontingen data: " + e.getMessage());
        }
    }

    private void submitEdit() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE kontingen SET " +
                             "nama=?, kategori=?, cabor=?, deskripsi=?, kelamin=?, isSakit=?, negaraId=? " +
                             "WHERE id=?")) {

            stmt.setString(1, namaField.getText());
            stmt.setString(2, singleButton.isSelected() ? "Single" : "Ganda");
            stmt.setString(3, (String) caborComboBox.getSelectedItem());
            stmt.setString(4, deskripsiArea.getText());
            stmt.setString(5, priaButton.isSelected() ? "Pria" : "Wanita");
            stmt.setBoolean(6, sakitCheckBox.isSelected());

            // Get the selected Negara's ID
            NegaraItem selectedNegara = (NegaraItem) negaraComboBox.getSelectedItem();
            stmt.setInt(7, selectedNegara.id);
            stmt.setInt(8, kontingenId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil diperbarui");
                new ListKontingenFrame().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating kontingen: " + e.getMessage());
        }
    }
}