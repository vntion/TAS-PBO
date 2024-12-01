import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class InputKontingen extends JFrame {
    private JTextField txtNama;
    private ButtonGroup bgTipe, bgKelamin;
    private JRadioButton rbSingle, rbGanda, rbPria, rbWanita;
    private JComboBox<String> cbCabor;
    private JComboBox<NegaraItem> cbNegara; // Changed to store NegaraItem
    private JTextArea txtDeskripsi;
    private JCheckBox chkSakit;
    private JButton btnPilihFoto, btnTambah;
    private JLabel lblFotoPath;
    private String selectedFotoPath;

    // Database connection settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tas";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public InputKontingen() {
        setTitle("Input Kontingen");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nama
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nama:"), gbc);

        txtNama = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtNama, gbc);

        // Tipe (Single/Ganda)
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Tipe:"), gbc);

        JPanel tipePanel = new JPanel();
        bgTipe = new ButtonGroup();
        rbSingle = new JRadioButton("Single");
        rbGanda = new JRadioButton("Ganda");
        bgTipe.add(rbSingle);
        bgTipe.add(rbGanda);
        tipePanel.add(rbSingle);
        tipePanel.add(rbGanda);
        gbc.gridx = 1;
        mainPanel.add(tipePanel, gbc);

        // Foto
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Foto:"), gbc);

        JPanel fotoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPilihFoto = new JButton("Pilih Foto");
        lblFotoPath = new JLabel("Belum ada foto dipilih");
        fotoPanel.add(btnPilihFoto);
        fotoPanel.add(lblFotoPath);
        gbc.gridx = 1;
        mainPanel.add(fotoPanel, gbc);

        // Cabor
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Cabang Olahraga:"), gbc);

        String[] caborList = {"Sepak Bola", "Bulu Tangkis", "Bola Basket", "Voli",
                "Tenis Meja", "Renang", "Atletik", "Tinju",
                "Pencak Silat", "Karate"};
        cbCabor = new JComboBox<>(caborList);
        gbc.gridx = 1;
        mainPanel.add(cbCabor, gbc);

        // Deskripsi
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Deskripsi:"), gbc);

        txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setLineWrap(true);
        JScrollPane scrollDeskripsi = new JScrollPane(txtDeskripsi);
        gbc.gridx = 1;
        mainPanel.add(scrollDeskripsi, gbc);

        // Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Jenis Kelamin:"), gbc);

        JPanel kelaminPanel = new JPanel();
        bgKelamin = new ButtonGroup();
        rbPria = new JRadioButton("Pria");
        rbWanita = new JRadioButton("Wanita");
        bgKelamin.add(rbPria);
        bgKelamin.add(rbWanita);
        kelaminPanel.add(rbPria);
        kelaminPanel.add(rbWanita);
        gbc.gridx = 1;
        mainPanel.add(kelaminPanel, gbc);

        // Status Sakit
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Status:"), gbc);

        chkSakit = new JCheckBox("Sakit");
        gbc.gridx = 1;
        mainPanel.add(chkSakit, gbc);

        // Negara
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Negara:"), gbc);

        cbNegara = new JComboBox<>(); // Changed to store NegaraItem
        loadNegara();
        gbc.gridx = 1;
        mainPanel.add(cbNegara, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        btnTambah = new JButton("Tambah Kontingen");
        buttonPanel.add(btnTambah);

        // Add panels to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add event listeners
        btnPilihFoto.addActionListener(e -> pilihFoto());
        btnTambah.addActionListener(e -> tambahKontingen());

        setLocationRelativeTo(null);
    }

    private void loadNegara() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT id, nama FROM negara ORDER BY nama";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                NegaraItem negara = new NegaraItem(
                        rs.getInt("id"),
                        rs.getString("nama")
                );
                cbNegara.addItem(negara); // Add NegaraItem object directly
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading negara: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pilihFoto() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFotoPath = selectedFile.getAbsolutePath();
            lblFotoPath.setText(selectedFile.getName());
        }
    }

    private void tambahKontingen() {
        // Validate input
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama harus diisi!");
            return;
        }

        if (!rbSingle.isSelected() && !rbGanda.isSelected()) {
            JOptionPane.showMessageDialog(this, "Pilih tipe kontingen!");
            return;
        }

        if (!rbPria.isSelected() && !rbWanita.isSelected()) {
            JOptionPane.showMessageDialog(this, "Pilih jenis kelamin!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "INSERT INTO kontingen (nama, kategori, foto, cabor, " +
                    "deskripsi, kelamin, isSakit, negaraId) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, txtNama.getText().trim());
            pstmt.setString(2, rbSingle.isSelected() ? "Single" : "Ganda");
            pstmt.setString(3, selectedFotoPath);
            pstmt.setString(4, cbCabor.getSelectedItem().toString());
            pstmt.setString(5, txtDeskripsi.getText().trim());
            pstmt.setString(6, rbPria.isSelected() ? "Pria" : "Wanita");
            pstmt.setBoolean(7, chkSakit.isSelected());

            NegaraItem selectedNegara = (NegaraItem) cbNegara.getSelectedItem();
            pstmt.setInt(8, selectedNegara.getId());

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Kontingen berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            new InputKontingen().setVisible(false);
            ShowNegara.showNegaraGui();
            clearForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error menambahkan kontingen: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtNama.setText("");
        bgTipe.clearSelection();
        selectedFotoPath = null;
        lblFotoPath.setText("Belum ada foto dipilih");
        cbCabor.setSelectedIndex(0);
        txtDeskripsi.setText("");
        bgKelamin.clearSelection();
        chkSakit.setSelected(false);
        cbNegara.setSelectedIndex(0);
    }

    // Helper class for Negara ComboBox
    private class NegaraItem {
        private int id;
        private String nama;

        public NegaraItem(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        public int getId() { return id; }

        @Override
        public String toString() {
            return nama;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set Look and Feel
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new InputKontingen().setVisible(true);
        });
    }
}