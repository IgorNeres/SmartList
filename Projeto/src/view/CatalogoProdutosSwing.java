package view;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import com.formdev.flatlaf.FlatLightLaf;

public class CatalogoProdutosSwing {

    private final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            new CatalogoProdutosSwing().showWindow();
        });
    }

    private void showWindow() {
        JFrame frame = new JFrame("Catálogo de Equipamentos — Java Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(860, 600);
        frame.setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBackground(new Color(248, 250, 252));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel cardPanel1 = new JPanel();
        cardPanel1.setLayout(new BoxLayout(cardPanel1, BoxLayout.Y_AXIS));
        cardPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel1.setBackground(new Color(255, 255, 255));
        cardPanel1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JButton btn2 = new JButton("Carregar lista 📄 Importar (.txt)");
        btn2.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn2.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: Carregar lista 📄 Importar (.txt)"));
        cardPanel1.add(btn2);
        cardPanel1.add(Box.createVerticalStrut(8));
        JButton btn3 = new JButton("💾 Salvar Lista (.txt)");
        btn3.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn3.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: 💾 Salvar Lista (.txt)"));
        cardPanel1.add(btn3);
        cardPanel1.add(Box.createVerticalStrut(8));
        rootPanel.add(cardPanel1);
        rootPanel.add(Box.createVerticalStrut(14));

        JButton btn4 = new JButton("🔖 Salvar como Modelo");
        btn4.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn4.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: 🔖 Salvar como Modelo"));
        rootPanel.add(btn4);
        rootPanel.add(Box.createVerticalStrut(8));
        JLabel fieldLabel5 = new JLabel("Campo de texto");
        fieldLabel5.setFont(new Font("SansSerif", Font.BOLD, 12));
        fieldLabel5.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(fieldLabel5);
        rootPanel.add(Box.createVerticalStrut(4));
        JTextField field6 = new JTextField(28);
        field6.setAlignmentX(Component.LEFT_ALIGNMENT);
        field6.setMaximumSize(new Dimension(420, 34));
        rootPanel.add(field6);
        rootPanel.add(Box.createVerticalStrut(10));
        JPanel cardPanel7 = new JPanel();
        cardPanel7.setLayout(new BoxLayout(cardPanel7, BoxLayout.Y_AXIS));
        cardPanel7.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel7.setBackground(new Color(255, 255, 255));
        cardPanel7.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel heading8 = new JLabel("<html><body style='width: 720px'>▣ Controle Orçamentário (Teto Mensal)</body></html>");
        heading8.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading8.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel7.add(heading8);
        cardPanel7.add(Box.createVerticalStrut(6));
        JLabel fieldLabel9 = new JLabel("Campo de texto");
        fieldLabel9.setFont(new Font("SansSerif", Font.BOLD, 12));
        fieldLabel9.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel7.add(fieldLabel9);
        cardPanel7.add(Box.createVerticalStrut(4));
        JTextField field10 = new JTextField(28);
        field10.setAlignmentX(Component.LEFT_ALIGNMENT);
        field10.setMaximumSize(new Dimension(420, 34));
        cardPanel7.add(field10);
        cardPanel7.add(Box.createVerticalStrut(10));
        JButton btn11 = new JButton("Definir");
        btn11.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn11.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: Definir"));
        cardPanel7.add(btn11);
        cardPanel7.add(Box.createVerticalStrut(8));
        JButton btn12 = new JButton("⟳");
        btn12.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn12.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: ⟳"));
        cardPanel7.add(btn12);
        cardPanel7.add(Box.createVerticalStrut(8));
        rootPanel.add(cardPanel7);
        rootPanel.add(Box.createVerticalStrut(14));

        JPanel cardPanel13 = new JPanel();
        cardPanel13.setLayout(new BoxLayout(cardPanel13, BoxLayout.Y_AXIS));
        cardPanel13.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel13.setBackground(new Color(255, 255, 255));
        cardPanel13.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel heading14 = new JLabel("<html><body style='width: 720px'>⊕ Novo Item F2</body></html>");
        heading14.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading14.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel13.add(heading14);
        cardPanel13.add(Box.createVerticalStrut(6));
        rootPanel.add(cardPanel13);
        rootPanel.add(Box.createVerticalStrut(14));

        JButton btn15 = new JButton("+ Adicionar");
        btn15.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn15.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: + Adicionar"));
        rootPanel.add(btn15);
        rootPanel.add(Box.createVerticalStrut(8));
        JButton btn16 = new JButton("Marcar Todos");
        btn16.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn16.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: Marcar Todos"));
        rootPanel.add(btn16);
        rootPanel.add(Box.createVerticalStrut(8));
        JButton btn17 = new JButton("Desmarcar Todos");
        btn17.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn17.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: Desmarcar Todos"));
        rootPanel.add(btn17);
        rootPanel.add(Box.createVerticalStrut(8));
        JButton btn18 = new JButton("Limpar Coletados");
        btn18.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn18.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Ação executada: Limpar Coletados"));
        rootPanel.add(btn18);
        rootPanel.add(Box.createVerticalStrut(8));
        JScrollPane scrollPane = new JScrollPane(rootPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void loadHotlinkImageAsync(JLabel target, String rawUrl, int maxWidth) {
        ImageIcon cached = imageCache.get(rawUrl);
        if (cached != null) {
            target.setText(null);
            target.setIcon(cached);
            return;
        }
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL url = URI.create(rawUrl).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(12000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Java Swing Hotlink Client)");
                try (InputStream in = conn.getInputStream()) {
                    BufferedImage img = ImageIO.read(in);
                    if (img == null) {
                        throw new IllegalArgumentException("Formato de imagem não suportado pelo ImageIO");
                    }
                    if (img.getWidth() > maxWidth) {
                        int targetHeight = (int) ((double) img.getHeight() * maxWidth / img.getWidth());
                        BufferedImage scaled = new BufferedImage(maxWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = scaled.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.drawImage(img, 0, 0, maxWidth, targetHeight, null);
                        g2.dispose();
                        return new ImageIcon(scaled);
                    }
                    return new ImageIcon(img);
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    imageCache.put(rawUrl, icon);
                    target.setText(null);
                    target.setIcon(icon);
                    target.revalidate();
                } catch (Exception ex) {
                    target.setText("Falha ao carregar hotlink: " + ex.getMessage());
                }
            }
        }.execute();
    }
}