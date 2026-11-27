package view;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import com.formdev.flatlaf.FlatLightLaf;

public class ModalCadastrarItemCompra extends JDialog {

    private static final Color COR_FUNDO_PAGINA = new Color(241, 245, 249);
    private static final Color COR_AZUL_PRIMARIO = new Color(55, 48, 224);
    private static final Color COR_TEXTO_ESCURO = new Color(15, 23, 42);
    private static final Color COR_TEXTO_MEDIO = new Color(51, 65, 85);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(100, 116, 139);
    private static final Color COR_BORDA = new Color(226, 232, 240);
    private static final Color COR_ERRO = new Color(220, 38, 38);
    private static final Color COR_SUCESSO = new Color(21, 128, 61);

    public static class DadosItemCompra {
        public final String nome;
        public final String categoria;
        public final double precoUnitario;
        public final boolean coletado;

        public DadosItemCompra(String nome, String categoria, double precoUnitario, boolean coletado) {
            this.nome = nome;
            this.categoria = categoria;
            this.precoUnitario = precoUnitario;
            this.coletado = coletado;
        }
    }

    private JTextField campoNome;
    private JComboBox<String> comboCategoria;
    private JSpinner spinnerPrecoUnitario;

    private JLabel lblValidacaoNome;
    private JLabel lblValidacaoCategoria;
    private JLabel lblValidacaoPreco;

    private DadosItemCompra itemConfirmado = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            ModalCadastrarItemCompra modal = new ModalCadastrarItemCompra(null);
            modal.setVisible(true);
        });
    }

    public ModalCadastrarItemCompra(Window janelaPai) {
        super(janelaPai, "Cadastrar Item de Compra [ItemCompra]", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 485);
        setResizable(false);
        setLocationRelativeTo(janelaPai);

        JPanel painelRaiz = new JPanel(new BorderLayout());
        painelRaiz.setBackground(Color.WHITE);
        painelRaiz.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        // ==========================================
        // 1. CABEÇALHO DO MODAL
        // ==========================================

        JPanel painelCabecalho = new JPanel(new BorderLayout(10, 0));
        painelCabecalho.setBackground(COR_FUNDO_PAGINA);
        painelCabecalho.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(12, 18, 12, 14)));

        FontIcon iconeTituloModal = FontIcon.of(FontAwesomeSolid.ARCHIVE, 14, COR_AZUL_PRIMARIO);
        JLabel lblTituloModal = new JLabel("Cadastrar Item de Compra [ItemCompra]", iconeTituloModal, JLabel.LEFT);
        lblTituloModal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTituloModal.setForeground(COR_TEXTO_ESCURO);
        lblTituloModal.setIconTextGap(9);

        FontIcon iconeFechar = FontIcon.of(FontAwesomeSolid.TIMES, 13, COR_TEXTO_SECUNDARIO);
        JButton btnFecharModal = new JButton(iconeFechar);
        btnFecharModal.setToolTipText("Fechar [Esc]");
        btnFecharModal.setBackground(COR_FUNDO_PAGINA);
        btnFecharModal.setFocusPainted(false);
        btnFecharModal.setBorderPainted(false);
        btnFecharModal.setContentAreaFilled(false);
        btnFecharModal.setOpaque(true);
        btnFecharModal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFecharModal.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btnFecharModal.addActionListener(e -> dispose());

        painelCabecalho.add(lblTituloModal, BorderLayout.WEST);
        painelCabecalho.add(btnFecharModal, BorderLayout.EAST);

        // ==========================================
        // 2. CORPO DO FORMULÁRIO
        // ==========================================

        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

  

        // --- CAMPO 1: NOME DO ITEM / PRODUTO (nome) ---
        JPanel linhaLabelNome = new JPanel(new BorderLayout());
        linhaLabelNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaLabelNome.setOpaque(false);
        linhaLabelNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel lblTituloNome = new JLabel(
                "<html>Nome do Item / Produto (nome) <span style='color:#DC2626;'>*</span></html>");
        lblTituloNome.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTituloNome.setForeground(COR_TEXTO_ESCURO);

        JLabel lblTipoNome = new JLabel("String (obrigatório)");
        lblTipoNome.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblTipoNome.setForeground(COR_TEXTO_SECUNDARIO);

        linhaLabelNome.add(lblTituloNome, BorderLayout.WEST);
        linhaLabelNome.add(lblTipoNome, BorderLayout.EAST);

        JPanel boxCampoNome = new JPanel(new BorderLayout());
        boxCampoNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxCampoNome.setBackground(new Color(248, 250, 252));
        boxCampoNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        boxCampoNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        campoNome = new JTextField();
        campoNome.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoNome.setForeground(COR_TEXTO_ESCURO);
        campoNome.setOpaque(false);
        campoNome.setBorder(null);
        aplicarPlaceholder(campoNome, "Ex: Azeite de Oliva Extra Virgem 500ml");
        boxCampoNome.add(campoNome, BorderLayout.CENTER);

        lblValidacaoNome = new JLabel("Validação: \"O nome não pode ser vazio.\"",
                FontIcon.of(FontAwesomeSolid.INFO_CIRCLE, 11, COR_TEXTO_SECUNDARIO), JLabel.LEFT);
        lblValidacaoNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblValidacaoNome.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblValidacaoNome.setForeground(COR_TEXTO_SECUNDARIO);
        lblValidacaoNome.setIconTextGap(5);

        painelFormulario.add(linhaLabelNome);
        painelFormulario.add(Box.createVerticalStrut(4));
        painelFormulario.add(boxCampoNome);
        painelFormulario.add(Box.createVerticalStrut(4));
        painelFormulario.add(lblValidacaoNome);
        painelFormulario.add(Box.createVerticalStrut(12));

        // --- CAMPOS 2 E 3: CATEGORIA + PREÇO UNITÁRIO (2 COLUNAS) ---
        JPanel gridCategoriaPreco = new JPanel(new GridLayout(1, 2, 14, 0));
        gridCategoriaPreco.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridCategoriaPreco.setOpaque(false);
        gridCategoriaPreco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        // Coluna Esquerda: Categoria (categoria)
        JPanel colCategoria = new JPanel();
        colCategoria.setLayout(new BoxLayout(colCategoria, BoxLayout.Y_AXIS));
        colCategoria.setOpaque(false);

        JPanel linhaLabelCat = new JPanel(new BorderLayout());
        linhaLabelCat.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaLabelCat.setOpaque(false);
        linhaLabelCat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel lblTituloCat = new JLabel(
                "<html>Categoria (categoria) <span style='color:#DC2626;'>*</span></html>");
        lblTituloCat.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTituloCat.setForeground(COR_TEXTO_ESCURO);

        JLabel lblTipoCat = new JLabel("String");
        lblTipoCat.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblTipoCat.setForeground(COR_TEXTO_SECUNDARIO);

        linhaLabelCat.add(lblTituloCat, BorderLayout.WEST);
        linhaLabelCat.add(lblTipoCat, BorderLayout.EAST);

        comboCategoria = new JComboBox<>(new String[]{
                "Hortifrúti", "Alimentos", "Açougue", "Limpeza", "Bebidas", "Padaria & Frios"
        });
        comboCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboCategoria.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboCategoria.setForeground(COR_TEXTO_ESCURO);
        comboCategoria.setBackground(new Color(248, 250, 252));
        comboCategoria.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        comboCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        lblValidacaoCategoria = new JLabel("Validação: \"A categoria não pode ser vazia.\"",
                FontIcon.of(FontAwesomeSolid.INFO_CIRCLE, 11, COR_TEXTO_SECUNDARIO), JLabel.LEFT);
        lblValidacaoCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblValidacaoCategoria.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblValidacaoCategoria.setForeground(COR_TEXTO_SECUNDARIO);
        lblValidacaoCategoria.setIconTextGap(5);

        colCategoria.add(linhaLabelCat);
        colCategoria.add(Box.createVerticalStrut(4));
        colCategoria.add(comboCategoria);
        colCategoria.add(Box.createVerticalStrut(4));
        colCategoria.add(lblValidacaoCategoria);

        // Coluna Direita: Preço Unitário R$ (precoUnitario)
        JPanel colPreco = new JPanel();
        colPreco.setLayout(new BoxLayout(colPreco, BoxLayout.Y_AXIS));
        colPreco.setOpaque(false);

        JPanel linhaLabelPreco = new JPanel(new BorderLayout());
        linhaLabelPreco.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaLabelPreco.setOpaque(false);
        linhaLabelPreco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel lblTituloPreco = new JLabel(
                "<html>Preço Unitário R$ (precoUnitario) <span style='color:#DC2626;'>*</span></html>");
        lblTituloPreco.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTituloPreco.setForeground(COR_TEXTO_ESCURO);

        JLabel lblTipoPreco = new JLabel("double (> 0)");
        lblTipoPreco.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblTipoPreco.setForeground(COR_TEXTO_SECUNDARIO);

        linhaLabelPreco.add(lblTituloPreco, BorderLayout.WEST);
        linhaLabelPreco.add(lblTipoPreco, BorderLayout.EAST);

        JPanel boxSpinnerPreco = new JPanel(new BorderLayout(6, 0));
        boxSpinnerPreco.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxSpinnerPreco.setBackground(new Color(248, 250, 252));
        boxSpinnerPreco.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(2, 10, 2, 4)));
        boxSpinnerPreco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lblCifrao = new JLabel("$");
        lblCifrao.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblCifrao.setForeground(COR_TEXTO_SECUNDARIO);

        SpinnerNumberModel modeloPreco = new SpinnerNumberModel(0.00, 0.00, 99999.99, 0.50);
        spinnerPrecoUnitario = new JSpinner(modeloPreco);
        spinnerPrecoUnitario.setFont(new Font("Monospaced", Font.PLAIN, 12));
        spinnerPrecoUnitario.setBorder(null);
        spinnerPrecoUnitario.setOpaque(false);

        JSpinner.NumberEditor editorPreco = new JSpinner.NumberEditor(spinnerPrecoUnitario, "0.00");
        editorPreco.getFormat().setDecimalFormatSymbols(
                java.text.DecimalFormatSymbols.getInstance(Locale.US));
        editorPreco.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        editorPreco.getTextField().setBackground(new Color(248, 250, 252));
        editorPreco.getTextField().setForeground(COR_TEXTO_ESCURO);
        editorPreco.getTextField().setBorder(null);
        spinnerPrecoUnitario.setEditor(editorPreco);

        boxSpinnerPreco.add(lblCifrao, BorderLayout.WEST);
        boxSpinnerPreco.add(spinnerPrecoUnitario, BorderLayout.CENTER);

        lblValidacaoPreco = new JLabel("Regra: \"O preço deve ser maior que zero.\"",
                FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO), JLabel.LEFT);
        lblValidacaoPreco.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblValidacaoPreco.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblValidacaoPreco.setForeground(COR_ERRO);
        lblValidacaoPreco.setIconTextGap(5);

        colPreco.add(linhaLabelPreco);
        colPreco.add(Box.createVerticalStrut(4));
        colPreco.add(boxSpinnerPreco);
        colPreco.add(Box.createVerticalStrut(4));
        colPreco.add(lblValidacaoPreco);

        gridCategoriaPreco.add(colCategoria);
        gridCategoriaPreco.add(colPreco);

        painelFormulario.add(gridCategoriaPreco);
        painelFormulario.add(Box.createVerticalStrut(12));

     

        // ==========================================
        // 3. RODAPÉ DO MODAL (INSTÂNCIA + BOTÕES)
        // ==========================================

        JPanel painelRodape = new JPanel();
        painelRodape.setLayout(new BoxLayout(painelRodape, BoxLayout.Y_AXIS));
        painelRodape.setBackground(new Color(248, 250, 252));
        painelRodape.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(11, 18, 14, 18)));

        JPanel linhaBotoesRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linhaBotoesRodape.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotoesRodape.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCancelar.setBackground(new Color(226, 232, 240));
        btnCancelar.setForeground(new Color(51, 65, 85));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnCancelar.addActionListener(e -> dispose());

        FontIcon iconeSalvarItem = FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 11, Color.WHITE);
        JButton btnSalvarItem = new JButton("Salvar Item", iconeSalvarItem);
        btnSalvarItem.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnSalvarItem.setBackground(new Color(55, 48, 224));
        btnSalvarItem.setForeground(Color.WHITE);
        btnSalvarItem.setFocusPainted(false);
        btnSalvarItem.setBorderPainted(false);
        btnSalvarItem.setContentAreaFilled(false);
        btnSalvarItem.setOpaque(true);
        btnSalvarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvarItem.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        linhaBotoesRodape.add(btnCancelar);
        linhaBotoesRodape.add(btnSalvarItem);
        painelRodape.add(Box.createVerticalStrut(10));
        painelRodape.add(linhaBotoesRodape);

        // ==========================================
        // VALIDAÇÃO EM TEMPO REAL E EVENTOS
        // ==========================================

        campoNome.getDocument().addDocumentListener(new DocumentListener() {
            private void validarNomeTempoReal() {
                String texto = campoNome.getText().trim();
                if (texto.isEmpty()) {
                    lblValidacaoNome.setText("Validação: \"O nome não pode ser vazio.\"");
                    lblValidacaoNome.setForeground(COR_ERRO);
                    lblValidacaoNome.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO));
                } else {
                    lblValidacaoNome.setText("Nome válido (" + texto.length() + " caracteres).");
                    lblValidacaoNome.setForeground(COR_SUCESSO);
                    lblValidacaoNome.setIcon(FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 11, COR_SUCESSO));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validarNomeTempoReal();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validarNomeTempoReal();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validarNomeTempoReal();
            }
        });

        spinnerPrecoUnitario.addChangeListener(e -> {
            double valor = ((Number) spinnerPrecoUnitario.getValue()).doubleValue();
            if (valor <= 0.0) {
                lblValidacaoPreco.setText("Regra: \"O preço deve ser maior que zero.\"");
                lblValidacaoPreco.setForeground(COR_ERRO);
                lblValidacaoPreco.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO));
            } else {
                lblValidacaoPreco.setText("Preço unitário válido (> 0,00).");
                lblValidacaoPreco.setForeground(COR_SUCESSO);
                lblValidacaoPreco.setIcon(FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 11, COR_SUCESSO));
            }
        });

        btnSalvarItem.addActionListener(e -> {
            try {
                spinnerPrecoUnitario.commitEdit();
            } catch (Exception ignored) {
                // Mantém o último valor válido do spinner
            }

            String nomeDigitado = campoNome.getText().trim();
            Object catObj = comboCategoria.getSelectedItem();
            String categoriaSelecionada = catObj != null ? catObj.toString().trim() : "";
            double precoDigitado = ((Number) spinnerPrecoUnitario.getValue()).doubleValue();
            boolean jaColetado = checkColetado.isSelected();

            boolean valido = true;

            if (nomeDigitado.isEmpty()) {
                lblValidacaoNome.setText("Validação: \"O nome não pode ser vazio.\"");
                lblValidacaoNome.setForeground(COR_ERRO);
                lblValidacaoNome.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO));
                valido = false;
            }

            if (categoriaSelecionada.isEmpty()) {
                lblValidacaoCategoria.setText("Validação: \"A categoria não pode ser vazia.\"");
                lblValidacaoCategoria.setForeground(COR_ERRO);
                lblValidacaoCategoria.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO));
                valido = false;
            }

            if (precoDigitado <= 0.0) {
                lblValidacaoPreco.setText("Regra: \"O preço deve ser maior que zero.\"");
                lblValidacaoPreco.setForeground(COR_ERRO);
                lblValidacaoPreco.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_CIRCLE, 11, COR_ERRO));
                valido = false;
            }

            if (!valido) {
                JOptionPane.showMessageDialog(this,
                        "Preencha corretamente os campos obrigatórios:\n"
                                + "• Nome não pode ser vazio\n"
                                + "• Categoria não pode ser vazia\n"
                                + "• Preço unitário deve ser maior que 0,00",
                        "Validação de Domínio (ItemCompra)", JOptionPane.WARNING_MESSAGE);
                return;
            }

            itemConfirmado = new DadosItemCompra(nomeDigitado, categoriaSelecionada, precoDigitado, jaColetado);
            dispose();
        });

        // Atalhos de Teclado: [Esc] Cancelar e [Enter] Salvar Item
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelarModal");
        am.put("cancelarModal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelar.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "salvarModal");
        am.put("salvarModal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSalvarItem.doClick();
            }
        });

        painelRaiz.add(painelCabecalho, BorderLayout.NORTH);
        painelRaiz.add(painelFormulario, BorderLayout.CENTER);
        painelRaiz.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelRaiz);
    }

    public DadosItemCompra getItemConfirmado() {
        return itemConfirmado;
    }

    private void aplicarPlaceholder(JTextField campo, String textoPlaceholder) {
        campo.setLayout(new BorderLayout());

        JLabel lblPlaceholder = new JLabel(textoPlaceholder);
        lblPlaceholder.setFont(campo.getFont());
        lblPlaceholder.setForeground(new Color(148, 163, 184));
        campo.add(lblPlaceholder, BorderLayout.CENTER);

        campo.getDocument().addDocumentListener(new DocumentListener() {
            private void atualizarVisibilidade() {
                lblPlaceholder.setVisible(campo.getText().isEmpty());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarVisibilidade();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarVisibilidade();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarVisibilidade();
            }
        });
    }
}