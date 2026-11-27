package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import com.formdev.flatlaf.FlatLightLaf;

public class PainelPrincipal {

    private final ConcurrentHashMap<String, ImageIcon> cacheImagens = new ConcurrentHashMap<>();
    private String categoriaFiltroAtiva = "Todas";
    private String termoBuscaAtivo = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            new PainelPrincipal().exibirJanela();
        });
    }

    private void exibirJanela() {

        Color corFundoPagina = new Color(241, 245, 249);
        Color corAzulPrimario = new Color(55, 48, 224);

        JFrame janelaPrincipal = new JFrame("Lista de Compras Inteligente — Java Swing");
        janelaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaPrincipal.setSize(1080, 760);
        janelaPrincipal.setLocationRelativeTo(null);

        // Conteúdo principal rolável
        JPanel painelConteudoPrincipal = new JPanel();
        painelConteudoPrincipal.setLayout(new BoxLayout(painelConteudoPrincipal, BoxLayout.Y_AXIS));
        painelConteudoPrincipal.setOpaque(true);
        painelConteudoPrincipal.setBackground(corFundoPagina);
        painelConteudoPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 4, 16, 14));

        // ==========================================
        // BARRA SUPERIOR: LOGO + IMPORTAR, EXPORTAR E MODELO
        // ==========================================

        JPanel painelBarraSuperior = new JPanel();
        painelBarraSuperior.setLayout(new BoxLayout(painelBarraSuperior, BoxLayout.LINE_AXIS));
        painelBarraSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarraSuperior.setBackground(Color.WHITE);
        painelBarraSuperior.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        // Logo pequena no canto superior esquerdo (ao lado de Importar Lista)
        JLabel lblLogoSistema = new JLabel();
        lblLogoSistema.setIcon(carregarLogoPequena("/images/logo-smartlist.png", 34, 34));
        lblLogoSistema.setToolTipText("smartList — Organização Inteligente");
        lblLogoSistema.setAlignmentY(Component.CENTER_ALIGNMENT);
        painelBarraSuperior.add(lblLogoSistema);

        FontIcon iconeImportar = FontIcon.of(FontAwesomeSolid.FILE_IMPORT, 11, new Color(51, 65, 85));
        JButton btnImportarLista = new JButton("Importar Lista (.txt)", iconeImportar);
        btnImportarLista.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnImportarLista.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnImportarLista.setBackground(Color.WHITE);
        btnImportarLista.setForeground(new Color(51, 65, 85));
        btnImportarLista.setFocusPainted(false);
        btnImportarLista.setContentAreaFilled(false);
        btnImportarLista.setOpaque(true);
        btnImportarLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportarLista.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnImportarLista.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Ação executada: Importar Lista (.txt)"));
        painelBarraSuperior.add(btnImportarLista);

        painelBarraSuperior.add(Box.createRigidArea(new Dimension(12, 20)));

        FontIcon iconeExportar = FontIcon.of(FontAwesomeSolid.FILE_EXPORT, 11, new Color(51, 65, 85));
        JButton btnExportarLista = new JButton("Exportar Lista (.txt)", iconeExportar);
        btnExportarLista.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnExportarLista.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnExportarLista.setBackground(Color.WHITE);
        btnExportarLista.setForeground(new Color(51, 65, 85));
        btnExportarLista.setFocusPainted(false);
        btnExportarLista.setContentAreaFilled(false);
        btnExportarLista.setOpaque(true);
        btnExportarLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportarLista.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnExportarLista.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Ação executada: Exportar Lista (.txt)"));
        painelBarraSuperior.add(btnExportarLista);

        painelBarraSuperior.add(Box.createRigidArea(new Dimension(12, 20)));

        FontIcon iconeSalvarModelo = FontIcon.of(FontAwesomeSolid.SAVE, 11, new Color(51, 65, 85));
        JButton btnSalvarModelo = new JButton("Salvar como Modelo", iconeSalvarModelo);
        btnSalvarModelo.setAlignmentY(Component.CENTER_ALIGNMENT);
        btnSalvarModelo.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnSalvarModelo.setBackground(Color.WHITE);
        btnSalvarModelo.setForeground(new Color(51, 65, 85));
        btnSalvarModelo.setFocusPainted(false);
        btnSalvarModelo.setContentAreaFilled(false);
        btnSalvarModelo.setOpaque(true);
        btnSalvarModelo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvarModelo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnSalvarModelo.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Ação executada: Salvar como Modelo"));
        painelBarraSuperior.add(btnSalvarModelo);

        // ==========================================
        // CARD: CONTROLE ORÇAMENTÁRIO
        // ==========================================

        JPanel painelControleOrcamento = new JPanel();
        painelControleOrcamento.setLayout(new BoxLayout(painelControleOrcamento, BoxLayout.Y_AXIS));
        painelControleOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelControleOrcamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 235));
        painelControleOrcamento.setBackground(Color.WHITE);
        painelControleOrcamento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        // Subseção 1 - Definir Orçamento
        JPanel painelDefinirOrcamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        painelDefinirOrcamento.setBackground(Color.WHITE);
        painelDefinirOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);

        FontIcon iconeCarteira = FontIcon.of(FontAwesomeSolid.WALLET, 15, corAzulPrimario);
        JLabel lblTituloOrcamento = new JLabel("Controle Orçamentário (Teto Mensal)", iconeCarteira, JLabel.LEFT);
        lblTituloOrcamento.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloOrcamento.setIconTextGap(8);
        painelDefinirOrcamento.add(lblTituloOrcamento);

        painelDefinirOrcamento.add(Box.createHorizontalStrut(24));

        JLabel lblValorMensal = new JLabel("Valor mensal");
        lblValorMensal.setFont(new Font("SansSerif", Font.BOLD, 12));

        JTextField campoValorOrcamento = new JTextField(16);

        // Botão Azul (mantido azul)
        JButton btnDefinirOrcamento = new JButton("Definir");
        btnDefinirOrcamento.setBackground(corAzulPrimario);
        btnDefinirOrcamento.setForeground(Color.WHITE);
        btnDefinirOrcamento.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDefinirOrcamento.setFocusPainted(false);
        btnDefinirOrcamento.setBorderPainted(false);
        btnDefinirOrcamento.setContentAreaFilled(false);
        btnDefinirOrcamento.setOpaque(true);
        btnDefinirOrcamento.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnDefinirOrcamento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDefinirOrcamento.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Ação executada: Definir Orçamento"));

        painelDefinirOrcamento.add(lblValorMensal);
        painelDefinirOrcamento.add(campoValorOrcamento);
        painelDefinirOrcamento.add(btnDefinirOrcamento);
        painelDefinirOrcamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        painelControleOrcamento.add(painelDefinirOrcamento);
        painelControleOrcamento.add(Box.createVerticalStrut(8));

        // Subseção 2 - Indicadores Orçamentários
        JPanel painelIndicadoresOrcamento = new JPanel(new GridLayout(1, 3, 12, 0));
        painelIndicadoresOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelIndicadoresOrcamento.setBackground(Color.WHITE);
        painelIndicadoresOrcamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));

        JPanel cardOrcamentoMensal = new JPanel();
        cardOrcamentoMensal.setLayout(new BoxLayout(cardOrcamentoMensal, BoxLayout.Y_AXIS));
        cardOrcamentoMensal.setBackground(Color.WHITE);
        cardOrcamentoMensal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel lblTituloOrcamentoMensal = new JLabel("Orçamento mensal");
        lblTituloOrcamentoMensal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel lblValorOrcamentoMensal = new JLabel("R$ 0,00");
        lblValorOrcamentoMensal.setFont(new Font("SansSerif", Font.BOLD, 18));
        cardOrcamentoMensal.add(lblTituloOrcamentoMensal);
        cardOrcamentoMensal.add(Box.createVerticalStrut(6));
        cardOrcamentoMensal.add(lblValorOrcamentoMensal);

        JPanel cardTotalGasto = new JPanel();
        cardTotalGasto.setLayout(new BoxLayout(cardTotalGasto, BoxLayout.Y_AXIS));
        cardTotalGasto.setBackground(Color.WHITE);
        cardTotalGasto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel lblTituloTotalGasto = new JLabel("Total gasto");
        lblTituloTotalGasto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel lblValorTotalGasto = new JLabel("R$ 0,00");
        lblValorTotalGasto.setFont(new Font("SansSerif", Font.BOLD, 18));
        cardTotalGasto.add(lblTituloTotalGasto);
        cardTotalGasto.add(Box.createVerticalStrut(6));
        cardTotalGasto.add(lblValorTotalGasto);

        JPanel cardSaldoDisponivel = new JPanel();
        cardSaldoDisponivel.setLayout(new BoxLayout(cardSaldoDisponivel, BoxLayout.Y_AXIS));
        cardSaldoDisponivel.setBackground(Color.WHITE);
        cardSaldoDisponivel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel lblTituloSaldoDisponivel = new JLabel("Saldo disponível");
        lblTituloSaldoDisponivel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel lblValorSaldoDisponivel = new JLabel("R$ 0,00");
        lblValorSaldoDisponivel.setFont(new Font("SansSerif", Font.BOLD, 18));
        cardSaldoDisponivel.add(lblTituloSaldoDisponivel);
        cardSaldoDisponivel.add(Box.createVerticalStrut(6));
        cardSaldoDisponivel.add(lblValorSaldoDisponivel);

        painelIndicadoresOrcamento.add(cardOrcamentoMensal);
        painelIndicadoresOrcamento.add(cardTotalGasto);
        painelIndicadoresOrcamento.add(cardSaldoDisponivel);
        painelControleOrcamento.add(painelIndicadoresOrcamento);
        painelControleOrcamento.add(Box.createVerticalStrut(12));

        // Subseção 3 - Barra de Progresso do Orçamento
        JPanel painelProgressoOrcamento = new JPanel();
        painelProgressoOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelProgressoOrcamento.setLayout(new BoxLayout(painelProgressoOrcamento, BoxLayout.Y_AXIS));
        painelProgressoOrcamento.setBackground(Color.WHITE);
        painelProgressoOrcamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel lblTituloProgressoOrcamento = new JLabel("Utilização do orçamento");
        lblTituloProgressoOrcamento.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTituloProgressoOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar barraProgressoOrcamento = new JProgressBar();
        barraProgressoOrcamento.setValue(10);
        barraProgressoOrcamento.setForeground(corAzulPrimario);
        barraProgressoOrcamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        barraProgressoOrcamento.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelProgressoOrcamento.add(lblTituloProgressoOrcamento);
        painelProgressoOrcamento.add(Box.createVerticalStrut(8));
        painelProgressoOrcamento.add(barraProgressoOrcamento);
        painelControleOrcamento.add(painelProgressoOrcamento);

        painelConteudoPrincipal.add(painelControleOrcamento);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // CARD: NOVO ITEM (FORMULÁRIO HORIZONTAL)
        // ==========================================

        JPanel painelNovoItem = new JPanel(new BorderLayout(0, 12));
        painelNovoItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelNovoItem.setBackground(Color.WHITE);
        painelNovoItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)));

        JPanel cabecalhoNovoItem = new JPanel(new BorderLayout());
        cabecalhoNovoItem.setOpaque(false);

        JPanel painelTituloNovoItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelTituloNovoItem.setOpaque(false);

        FontIcon iconeNovoItem = FontIcon.of(FontAwesomeSolid.PLUS_CIRCLE, 17, corAzulPrimario);
        JLabel lblTituloNovoItem = new JLabel("Novo Item", iconeNovoItem, JLabel.LEFT);
        lblTituloNovoItem.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTituloNovoItem.setIconTextGap(8);

        painelTituloNovoItem.add(lblTituloNovoItem);
        cabecalhoNovoItem.add(painelTituloNovoItem, BorderLayout.WEST);

        painelNovoItem.add(cabecalhoNovoItem, BorderLayout.NORTH);

        JPanel painelFormularioNovoItem = new JPanel(new GridBagLayout());
        painelFormularioNovoItem.setOpaque(false);

        Font fonteLabelCampo = new Font("SansSerif", Font.BOLD, 12);
        Color corLabelCampo = new Color(51, 65, 85);

        JLabel lblProdutoDescricao = new JLabel("Produto / Descrição");
        lblProdutoDescricao.setFont(fonteLabelCampo);
        lblProdutoDescricao.setForeground(corLabelCampo);
        GridBagConstraints gbcLblProdutoDescricao = new GridBagConstraints();
        gbcLblProdutoDescricao.anchor = GridBagConstraints.WEST;
        gbcLblProdutoDescricao.insets = new Insets(0, 0, 5, 10);
        gbcLblProdutoDescricao.gridx = 0;
        gbcLblProdutoDescricao.gridy = 0;
        painelFormularioNovoItem.add(lblProdutoDescricao, gbcLblProdutoDescricao);

        JLabel lblCategoria = new JLabel("Categoria");
        lblCategoria.setFont(fonteLabelCampo);
        lblCategoria.setForeground(corLabelCampo);
        GridBagConstraints gbcLblCategoria = new GridBagConstraints();
        gbcLblCategoria.anchor = GridBagConstraints.WEST;
        gbcLblCategoria.insets = new Insets(0, 0, 5, 10);
        gbcLblCategoria.gridx = 1;
        gbcLblCategoria.gridy = 0;
        painelFormularioNovoItem.add(lblCategoria, gbcLblCategoria);

        JLabel lblTipoUnidade = new JLabel("Tipo");
        lblTipoUnidade.setFont(fonteLabelCampo);
        lblTipoUnidade.setForeground(corLabelCampo);
        GridBagConstraints gbcLblTipoUnidade = new GridBagConstraints();
        gbcLblTipoUnidade.anchor = GridBagConstraints.WEST;
        gbcLblTipoUnidade.insets = new Insets(0, 0, 5, 10);
        gbcLblTipoUnidade.gridx = 2;
        gbcLblTipoUnidade.gridy = 0;
        painelFormularioNovoItem.add(lblTipoUnidade, gbcLblTipoUnidade);

        JLabel lblQuantidade = new JLabel("Qtd");
        lblQuantidade.setFont(fonteLabelCampo);
        lblQuantidade.setForeground(corLabelCampo);
        GridBagConstraints gbcLblQuantidade = new GridBagConstraints();
        gbcLblQuantidade.anchor = GridBagConstraints.WEST;
        gbcLblQuantidade.insets = new Insets(0, 0, 5, 10);
        gbcLblQuantidade.gridx = 3;
        gbcLblQuantidade.gridy = 0;
        painelFormularioNovoItem.add(lblQuantidade, gbcLblQuantidade);

        JLabel lblPrecoUnitario = new JLabel("Preço R$");
        lblPrecoUnitario.setFont(fonteLabelCampo);
        lblPrecoUnitario.setForeground(corLabelCampo);
        GridBagConstraints gbcLblPrecoUnitario = new GridBagConstraints();
        gbcLblPrecoUnitario.anchor = GridBagConstraints.WEST;
        gbcLblPrecoUnitario.insets = new Insets(0, 0, 5, 10);
        gbcLblPrecoUnitario.gridx = 4;
        gbcLblPrecoUnitario.gridy = 0;
        painelFormularioNovoItem.add(lblPrecoUnitario, gbcLblPrecoUnitario);

        // 1. Campo Produto / Descrição
        JTextField campoNomeProduto = new JTextField();
        campoNomeProduto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoNomeProduto.setPreferredSize(new Dimension(200, 34));
        campoNomeProduto.setBackground(Color.WHITE);
        campoNomeProduto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        aplicarPlaceholder(campoNomeProduto, "Ex: Queijo Parmesão Ralado 100g");

        GridBagConstraints gbcCampoNomeProduto = new GridBagConstraints();
        gbcCampoNomeProduto.gridx = 0;
        gbcCampoNomeProduto.gridy = 1;
        gbcCampoNomeProduto.weightx = 1.0;
        gbcCampoNomeProduto.fill = GridBagConstraints.BOTH;
        gbcCampoNomeProduto.insets = new Insets(0, 0, 0, 10);
        painelFormularioNovoItem.add(campoNomeProduto, gbcCampoNomeProduto);

        // 2. Combo Categoria
        JComboBox<String> comboCategoria = new JComboBox<>(new String[]{
                "Hortifruti", "Alimentos", "Bebidas", "Limpeza", "Higiene"
        });
        comboCategoria.setPreferredSize(new Dimension(140, 34));
        comboCategoria.setBackground(new Color(243, 244, 246));

        GridBagConstraints gbcComboCategoria = new GridBagConstraints();
        gbcComboCategoria.gridx = 1;
        gbcComboCategoria.gridy = 1;
        gbcComboCategoria.weightx = 0.25;
        gbcComboCategoria.fill = GridBagConstraints.BOTH;
        gbcComboCategoria.insets = new Insets(0, 0, 0, 10);
        painelFormularioNovoItem.add(comboCategoria, gbcComboCategoria);

        // 3. Seletor segmentado Tipo (un | kg)
        JPanel painelSeletorUnidade = new JPanel(new GridLayout(1, 2, 3, 0));
        painelSeletorUnidade.setBackground(new Color(243, 244, 246));
        painelSeletorUnidade.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        painelSeletorUnidade.setPreferredSize(new Dimension(116, 34));
        painelSeletorUnidade.setMinimumSize(new Dimension(116, 34));

        JToggleButton btnUnidadeUn = new JToggleButton("un", true);
        btnUnidadeUn.setFocusPainted(false);
        btnUnidadeUn.setBorderPainted(false);
        btnUnidadeUn.setContentAreaFilled(false);
        btnUnidadeUn.setOpaque(true);
        btnUnidadeUn.setHorizontalAlignment(SwingConstants.CENTER);
        btnUnidadeUn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        btnUnidadeUn.setMargin(new Insets(0, 0, 0, 0));
        btnUnidadeUn.setPreferredSize(new Dimension(52, 26));
        btnUnidadeUn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUnidadeUn.setBackground(Color.WHITE);
        btnUnidadeUn.setForeground(corAzulPrimario);
        btnUnidadeUn.setFont(new Font("SansSerif", Font.BOLD, 12));

        JToggleButton btnUnidadeKg = new JToggleButton("kg", false);
        btnUnidadeKg.setFocusPainted(false);
        btnUnidadeKg.setBorderPainted(false);
        btnUnidadeKg.setContentAreaFilled(false);
        btnUnidadeKg.setOpaque(true);
        btnUnidadeKg.setHorizontalAlignment(SwingConstants.CENTER);
        btnUnidadeKg.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        btnUnidadeKg.setMargin(new Insets(0, 0, 0, 0));
        btnUnidadeKg.setPreferredSize(new Dimension(52, 26));
        btnUnidadeKg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUnidadeKg.setBackground(new Color(243, 244, 246));
        btnUnidadeKg.setForeground(new Color(100, 116, 139));
        btnUnidadeKg.setFont(new Font("SansSerif", Font.PLAIN, 12));

        ButtonGroup grupoUnidadeMedida = new ButtonGroup();
        grupoUnidadeMedida.add(btnUnidadeUn);
        grupoUnidadeMedida.add(btnUnidadeKg);

        btnUnidadeUn.addActionListener(e -> {
            btnUnidadeUn.setBackground(Color.WHITE);
            btnUnidadeUn.setForeground(corAzulPrimario);
            btnUnidadeUn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnUnidadeKg.setBackground(new Color(243, 244, 246));
            btnUnidadeKg.setForeground(new Color(100, 116, 139));
            btnUnidadeKg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        });

        btnUnidadeKg.addActionListener(e -> {
            btnUnidadeKg.setBackground(Color.WHITE);
            btnUnidadeKg.setForeground(corAzulPrimario);
            btnUnidadeKg.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnUnidadeUn.setBackground(new Color(243, 244, 246));
            btnUnidadeUn.setForeground(new Color(100, 116, 139));
            btnUnidadeUn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        });

        painelSeletorUnidade.add(btnUnidadeUn);
        painelSeletorUnidade.add(btnUnidadeKg);

        GridBagConstraints gbcPainelSeletorUnidade = new GridBagConstraints();
        gbcPainelSeletorUnidade.gridx = 2;
        gbcPainelSeletorUnidade.gridy = 1;
        gbcPainelSeletorUnidade.weightx = 0.0;
        gbcPainelSeletorUnidade.ipadx = 16;
        gbcPainelSeletorUnidade.fill = GridBagConstraints.BOTH;
        gbcPainelSeletorUnidade.insets = new Insets(0, 0, 0, 10);
        painelFormularioNovoItem.add(painelSeletorUnidade, gbcPainelSeletorUnidade);

        // 4. Spinner Qtd
        JSpinner spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinnerQuantidade.setPreferredSize(new Dimension(68, 34));
        spinnerQuantidade.setMinimumSize(new Dimension(68, 34));
        ((JSpinner.DefaultEditor) spinnerQuantidade.getEditor()).getTextField().setBackground(new Color(243, 244, 246));

        GridBagConstraints gbcSpinnerQuantidade = new GridBagConstraints();
        gbcSpinnerQuantidade.gridx = 3;
        gbcSpinnerQuantidade.gridy = 1;
        gbcSpinnerQuantidade.weightx = 0.0;
        gbcSpinnerQuantidade.fill = GridBagConstraints.BOTH;
        gbcSpinnerQuantidade.insets = new Insets(0, 0, 0, 10);
        painelFormularioNovoItem.add(spinnerQuantidade, gbcSpinnerQuantidade);

        // 5. Spinner Preço R$
        JSpinner spinnerPrecoUnitario = new JSpinner(new SpinnerNumberModel(9.90, 0.00, 99999.99, 0.50));
        spinnerPrecoUnitario.setEditor(new JSpinner.NumberEditor(spinnerPrecoUnitario, "0.00"));
        spinnerPrecoUnitario.setPreferredSize(new Dimension(84, 34));
        spinnerPrecoUnitario.setMinimumSize(new Dimension(84, 34));
        ((JSpinner.DefaultEditor) spinnerPrecoUnitario.getEditor()).getTextField().setBackground(new Color(243, 244, 246));

        GridBagConstraints gbcSpinnerPrecoUnitario = new GridBagConstraints();
        gbcSpinnerPrecoUnitario.gridx = 4;
        gbcSpinnerPrecoUnitario.gridy = 1;
        gbcSpinnerPrecoUnitario.weightx = 0.0;
        gbcSpinnerPrecoUnitario.fill = GridBagConstraints.BOTH;
        gbcSpinnerPrecoUnitario.insets = new Insets(0, 0, 0, 10);
        painelFormularioNovoItem.add(spinnerPrecoUnitario, gbcSpinnerPrecoUnitario);

        // 6. Botão + Adicionar (Azul — mantido azul)
        JButton btnAdicionarNovoItem = new JButton("+ Adicionar");
        btnAdicionarNovoItem.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAdicionarNovoItem.setForeground(Color.WHITE);
        btnAdicionarNovoItem.setBackground(corAzulPrimario);
        btnAdicionarNovoItem.setFocusPainted(false);
        btnAdicionarNovoItem.setBorderPainted(false);
        btnAdicionarNovoItem.setContentAreaFilled(false);
        btnAdicionarNovoItem.setOpaque(true);
        btnAdicionarNovoItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdicionarNovoItem.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        btnAdicionarNovoItem.setMargin(new Insets(0, 0, 0, 0));
        btnAdicionarNovoItem.setPreferredSize(new Dimension(118, 34));
        btnAdicionarNovoItem.setMinimumSize(new Dimension(114, 34));

        btnAdicionarNovoItem.addActionListener(e -> {
            String unidade = btnUnidadeUn.isSelected() ? "un" : "kg";
            JOptionPane.showMessageDialog(janelaPrincipal,
                    "Adicionar: " + campoNomeProduto.getText() + " (" + spinnerQuantidade.getValue() + " " + unidade + ")");
        });

        campoNomeProduto.addActionListener(e -> btnAdicionarNovoItem.doClick());

        GridBagConstraints gbcBtnAdicionarNovoItem = new GridBagConstraints();
        gbcBtnAdicionarNovoItem.gridx = 5;
        gbcBtnAdicionarNovoItem.gridy = 1;
        gbcBtnAdicionarNovoItem.weightx = 0.0;
        gbcBtnAdicionarNovoItem.fill = GridBagConstraints.BOTH;
        gbcBtnAdicionarNovoItem.insets = new Insets(0, 0, 0, 0);
        painelFormularioNovoItem.add(btnAdicionarNovoItem, gbcBtnAdicionarNovoItem);

        painelNovoItem.add(painelFormularioNovoItem, BorderLayout.CENTER);
        painelNovoItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));

        painelConteudoPrincipal.add(painelNovoItem);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // CARD: LISTA DE ITENS (5 ITENS / 2 CATEGORIAS)
        // ==========================================

        JPanel painelListaCompras = new JPanel();
        painelListaCompras.setLayout(new BoxLayout(painelListaCompras, BoxLayout.Y_AXIS));
        painelListaCompras.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelListaCompras.setBackground(Color.WHITE);
        painelListaCompras.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        painelListaCompras.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // --- Barra Superior de Abas e Pesquisa ---
        JPanel painelBarraFiltros = new JPanel(new BorderLayout(10, 0));
        painelBarraFiltros.setBackground(new Color(248, 250, 252));
        painelBarraFiltros.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        painelBarraFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarraFiltros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel painelAbasCategorias = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        painelAbasCategorias.setOpaque(false);

        JButton btnAbaTodas = new JButton("Todas (5)");
        JButton btnAbaHortifruti = new JButton("Hortifruti (3)");
        JButton btnAbaAlimentos = new JButton("Alimentos (2)");

        estilizarAbaFiltro(btnAbaTodas, true, corAzulPrimario);
        estilizarAbaFiltro(btnAbaHortifruti, false, corAzulPrimario);
        estilizarAbaFiltro(btnAbaAlimentos, false, corAzulPrimario);

        painelAbasCategorias.add(btnAbaTodas);
        painelAbasCategorias.add(btnAbaHortifruti);
        painelAbasCategorias.add(btnAbaAlimentos);

        // Campo de busca com lupa à direita
        JPanel painelCampoBusca = new JPanel(new BorderLayout(8, 0));
        painelCampoBusca.setBackground(Color.WHITE);
        painelCampoBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        painelCampoBusca.setPreferredSize(new Dimension(215, 30));

        JLabel lblIconeLupa = new JLabel(FontIcon.of(FontAwesomeSolid.SEARCH, 12, new Color(148, 163, 184)));
        JTextField campoPesquisaLista = new JTextField();
        campoPesquisaLista.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoPesquisaLista.setBorder(null);
        campoPesquisaLista.setOpaque(false);
        aplicarPlaceholder(campoPesquisaLista, "Filtrar item na lista...");

        painelCampoBusca.add(lblIconeLupa, BorderLayout.WEST);
        painelCampoBusca.add(campoPesquisaLista, BorderLayout.CENTER);

        painelBarraFiltros.add(painelAbasCategorias, BorderLayout.CENTER);
        painelBarraFiltros.add(painelCampoBusca, BorderLayout.EAST);

        painelListaCompras.add(painelBarraFiltros);

        // --- Cabeçalho das Colunas da Tabela ---
        JPanel cabecalhoTabelaItens = new JPanel(new GridBagLayout());
        cabecalhoTabelaItens.setBackground(corFundoPagina);
        cabecalhoTabelaItens.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        cabecalhoTabelaItens.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalhoTabelaItens.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        Font fonteCabecalhoTabela = new Font("SansSerif", Font.BOLD, 10);
        Color corCabecalhoTabela = new Color(100, 116, 139);

        JLabel lblColunaStatus = new JLabel("STATUS", SwingConstants.CENTER);
        lblColunaStatus.setFont(fonteCabecalhoTabela);
        lblColunaStatus.setForeground(corCabecalhoTabela);
        lblColunaStatus.setPreferredSize(new Dimension(58, 16));
        lblColunaStatus.setMinimumSize(new Dimension(58, 16));
        GridBagConstraints gbcColHeader0 = new GridBagConstraints();
        gbcColHeader0.gridx = 0;
        gbcColHeader0.gridy = 0;
        gbcColHeader0.weightx = 0.0;
        gbcColHeader0.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader0.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaStatus, gbcColHeader0);

        JLabel lblColunaProduto = new JLabel("PRODUTO & ESPECIFICAÇÃO", SwingConstants.LEFT);
        lblColunaProduto.setFont(fonteCabecalhoTabela);
        lblColunaProduto.setForeground(corCabecalhoTabela);
        lblColunaProduto.setPreferredSize(new Dimension(240, 16));
        lblColunaProduto.setMinimumSize(new Dimension(240, 16));
        GridBagConstraints gbcColHeader1 = new GridBagConstraints();
        gbcColHeader1.gridx = 1;
        gbcColHeader1.gridy = 0;
        gbcColHeader1.weightx = 1.0;
        gbcColHeader1.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader1.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaProduto, gbcColHeader1);

        JLabel lblColunaCategoria = new JLabel("CATEGORIA", SwingConstants.CENTER);
        lblColunaCategoria.setFont(fonteCabecalhoTabela);
        lblColunaCategoria.setForeground(corCabecalhoTabela);
        lblColunaCategoria.setPreferredSize(new Dimension(105, 16));
        lblColunaCategoria.setMinimumSize(new Dimension(105, 16));
        GridBagConstraints gbcColHeader2 = new GridBagConstraints();
        gbcColHeader2.gridx = 2;
        gbcColHeader2.gridy = 0;
        gbcColHeader2.weightx = 0.0;
        gbcColHeader2.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader2.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaCategoria, gbcColHeader2);

        JLabel lblColunaQuantidade = new JLabel("QUANTIDADE", SwingConstants.CENTER);
        lblColunaQuantidade.setFont(fonteCabecalhoTabela);
        lblColunaQuantidade.setForeground(corCabecalhoTabela);
        lblColunaQuantidade.setPreferredSize(new Dimension(88, 16));
        lblColunaQuantidade.setMinimumSize(new Dimension(88, 16));
        GridBagConstraints gbcColHeader3 = new GridBagConstraints();
        gbcColHeader3.gridx = 3;
        gbcColHeader3.gridy = 0;
        gbcColHeader3.weightx = 0.0;
        gbcColHeader3.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader3.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaQuantidade, gbcColHeader3);

        JLabel lblColunaPrecoUnitario = new JLabel("PREÇO UNITÁRIO", SwingConstants.CENTER);
        lblColunaPrecoUnitario.setFont(fonteCabecalhoTabela);
        lblColunaPrecoUnitario.setForeground(corCabecalhoTabela);
        lblColunaPrecoUnitario.setPreferredSize(new Dimension(105, 16));
        lblColunaPrecoUnitario.setMinimumSize(new Dimension(105, 16));
        GridBagConstraints gbcColHeader4 = new GridBagConstraints();
        gbcColHeader4.gridx = 4;
        gbcColHeader4.gridy = 0;
        gbcColHeader4.weightx = 0.0;
        gbcColHeader4.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader4.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaPrecoUnitario, gbcColHeader4);

        JLabel lblColunaSubtotal = new JLabel("SUBTOTAL", SwingConstants.CENTER);
        lblColunaSubtotal.setFont(fonteCabecalhoTabela);
        lblColunaSubtotal.setForeground(corCabecalhoTabela);
        lblColunaSubtotal.setPreferredSize(new Dimension(90, 16));
        lblColunaSubtotal.setMinimumSize(new Dimension(90, 16));
        GridBagConstraints gbcColHeader5 = new GridBagConstraints();
        gbcColHeader5.gridx = 5;
        gbcColHeader5.gridy = 0;
        gbcColHeader5.weightx = 0.0;
        gbcColHeader5.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader5.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaSubtotal, gbcColHeader5);

        JLabel lblColunaAcoes = new JLabel("AÇÕES", SwingConstants.CENTER);
        lblColunaAcoes.setFont(fonteCabecalhoTabela);
        lblColunaAcoes.setForeground(corCabecalhoTabela);
        lblColunaAcoes.setPreferredSize(new Dimension(82, 16));
        lblColunaAcoes.setMinimumSize(new Dimension(82, 16));
        GridBagConstraints gbcColHeader6 = new GridBagConstraints();
        gbcColHeader6.gridx = 6;
        gbcColHeader6.gridy = 0;
        gbcColHeader6.weightx = 0.0;
        gbcColHeader6.fill = GridBagConstraints.HORIZONTAL;
        gbcColHeader6.insets = new Insets(0, 4, 0, 4);
        cabecalhoTabelaItens.add(lblColunaAcoes, gbcColHeader6);

        painelListaCompras.add(cabecalhoTabelaItens);

        // ==========================================
        // CATEGORIA 1: HORTIFRÚTI & FRESCOS (3 ITENS)
        // ==========================================

        JPanel painelGrupoHortifruti = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        painelGrupoHortifruti.setBackground(new Color(248, 250, 252));
        painelGrupoHortifruti.setBorder(new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        painelGrupoHortifruti.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelGrupoHortifruti.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel lblGrupoHortifruti = new JLabel("HORTIFRÚTI & FRESCOS (3 itens)",
                FontIcon.of(FontAwesomeSolid.APPLE_ALT, 12, new Color(21, 128, 61)), JLabel.LEFT);
        lblGrupoHortifruti.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblGrupoHortifruti.setForeground(new Color(21, 128, 61));
        lblGrupoHortifruti.setIconTextGap(6);
        painelGrupoHortifruti.add(lblGrupoHortifruti);
        painelListaCompras.add(painelGrupoHortifruti);

        // --- ITEM 1: Maçã Gala Nacional ---
        JPanel painelLinhaItem1 = new JPanel(new GridBagLayout());
        painelLinhaItem1.setBackground(new Color(248, 250, 252));
        painelLinhaItem1.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, corFundoPagina),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        painelLinhaItem1.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelLinhaItem1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JCheckBox checkItem1 = new JCheckBox();
        checkItem1.setSelected(true);
        checkItem1.setHorizontalAlignment(SwingConstants.CENTER);
        checkItem1.setOpaque(false);
        checkItem1.setFocusPainted(false);
        checkItem1.setPreferredSize(new Dimension(58, 24));
        GridBagConstraints gbcItem1Col0 = new GridBagConstraints();
        gbcItem1Col0.gridx = 0;
        gbcItem1Col0.gridy = 0;
        gbcItem1Col0.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col0.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(checkItem1, gbcItem1Col0);

        JPanel painelInfoItem1 = new JPanel();
        painelInfoItem1.setLayout(new BoxLayout(painelInfoItem1, BoxLayout.Y_AXIS));
        painelInfoItem1.setOpaque(false);
        painelInfoItem1.setPreferredSize(new Dimension(240, 36));
        JLabel lblNomeItem1 = new JLabel("<html><strike>Maçã Gala Nacional</strike></html>");
        lblNomeItem1.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNomeItem1.setForeground(new Color(100, 116, 139));
        JLabel lblDescItem1 = new JLabel("Frutas frescas para a semana");
        lblDescItem1.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescItem1.setForeground(new Color(100, 116, 139));
        painelInfoItem1.add(lblNomeItem1);
        painelInfoItem1.add(Box.createVerticalStrut(2));
        painelInfoItem1.add(lblDescItem1);
        GridBagConstraints gbcItem1Col1 = new GridBagConstraints();
        gbcItem1Col1.gridx = 1;
        gbcItem1Col1.gridy = 0;
        gbcItem1Col1.weightx = 1.0;
        gbcItem1Col1.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col1.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(painelInfoItem1, gbcItem1Col1);

        JPanel painelCatItem1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCatItem1.setOpaque(false);
        painelCatItem1.setPreferredSize(new Dimension(105, 24));
        JLabel lblCatItem1 = new JLabel("Hortifruti", SwingConstants.CENTER);
        lblCatItem1.setOpaque(true);
        lblCatItem1.setBackground(new Color(209, 250, 229));
        lblCatItem1.setForeground(new Color(6, 95, 70));
        lblCatItem1.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCatItem1.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        painelCatItem1.add(lblCatItem1);
        GridBagConstraints gbcItem1Col2 = new GridBagConstraints();
        gbcItem1Col2.gridx = 2;
        gbcItem1Col2.gridy = 0;
        gbcItem1Col2.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col2.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(painelCatItem1, gbcItem1Col2);

        JLabel lblQtdItem1 = new JLabel("1,8 kg", SwingConstants.CENTER);
        lblQtdItem1.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtdItem1.setForeground(new Color(51, 65, 85));
        lblQtdItem1.setPreferredSize(new Dimension(88, 20));
        GridBagConstraints gbcItem1Col3 = new GridBagConstraints();
        gbcItem1Col3.gridx = 3;
        gbcItem1Col3.gridy = 0;
        gbcItem1Col3.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col3.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(lblQtdItem1, gbcItem1Col3);

        JLabel lblPrecoItem1 = new JLabel("R$ 8,90/kg", SwingConstants.CENTER);
        lblPrecoItem1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPrecoItem1.setForeground(new Color(100, 116, 139));
        lblPrecoItem1.setPreferredSize(new Dimension(105, 20));
        GridBagConstraints gbcItem1Col4 = new GridBagConstraints();
        gbcItem1Col4.gridx = 4;
        gbcItem1Col4.gridy = 0;
        gbcItem1Col4.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col4.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(lblPrecoItem1, gbcItem1Col4);

        JLabel lblSubItem1 = new JLabel("R$ 16,02", SwingConstants.CENTER);
        lblSubItem1.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSubItem1.setForeground(new Color(15, 23, 42));
        lblSubItem1.setPreferredSize(new Dimension(90, 20));
        GridBagConstraints gbcItem1Col5 = new GridBagConstraints();
        gbcItem1Col5.gridx = 5;
        gbcItem1Col5.gridy = 0;
        gbcItem1Col5.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col5.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(lblSubItem1, gbcItem1Col5);

        JPanel painelAcoesItem1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelAcoesItem1.setOpaque(false);
        painelAcoesItem1.setPreferredSize(new Dimension(82, 26));
        JButton btnEditarItem1 = new JButton(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 11, new Color(51, 65, 85)));
        btnEditarItem1.setToolTipText("Editar item");
        btnEditarItem1.setBackground(Color.WHITE);
        btnEditarItem1.setFocusPainted(false);
        btnEditarItem1.setContentAreaFilled(false);
        btnEditarItem1.setOpaque(true);
        btnEditarItem1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarItem1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        JButton btnExcluirItem1 = new JButton(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(51, 65, 85)));
        btnExcluirItem1.setToolTipText("Excluir item");
        btnExcluirItem1.setBackground(Color.WHITE);
        btnExcluirItem1.setFocusPainted(false);
        btnExcluirItem1.setContentAreaFilled(false);
        btnExcluirItem1.setOpaque(true);
        btnExcluirItem1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirItem1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        painelAcoesItem1.add(btnEditarItem1);
        painelAcoesItem1.add(btnExcluirItem1);
        GridBagConstraints gbcItem1Col6 = new GridBagConstraints();
        gbcItem1Col6.gridx = 6;
        gbcItem1Col6.gridy = 0;
        gbcItem1Col6.fill = GridBagConstraints.HORIZONTAL;
        gbcItem1Col6.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem1.add(painelAcoesItem1, gbcItem1Col6);

        painelListaCompras.add(painelLinhaItem1);

        // --- ITEM 2: Banana Prata Climatizada ---
        JPanel painelLinhaItem2 = new JPanel(new GridBagLayout());
        painelLinhaItem2.setBackground(new Color(248, 250, 252));
        painelLinhaItem2.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, corFundoPagina),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        painelLinhaItem2.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelLinhaItem2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JCheckBox checkItem2 = new JCheckBox();
        checkItem2.setSelected(true);
        checkItem2.setHorizontalAlignment(SwingConstants.CENTER);
        checkItem2.setOpaque(false);
        checkItem2.setFocusPainted(false);
        checkItem2.setPreferredSize(new Dimension(58, 24));
        GridBagConstraints gbcItem2Col0 = new GridBagConstraints();
        gbcItem2Col0.gridx = 0;
        gbcItem2Col0.gridy = 0;
        gbcItem2Col0.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col0.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(checkItem2, gbcItem2Col0);

        JPanel painelInfoItem2 = new JPanel();
        painelInfoItem2.setLayout(new BoxLayout(painelInfoItem2, BoxLayout.Y_AXIS));
        painelInfoItem2.setOpaque(false);
        painelInfoItem2.setPreferredSize(new Dimension(240, 36));
        JLabel lblNomeItem2 = new JLabel("<html><strike>Banana Prata Climatizada</strike></html>");
        lblNomeItem2.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNomeItem2.setForeground(new Color(100, 116, 139));
        JLabel lblDescItem2 = new JLabel("Penca selecionada");
        lblDescItem2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescItem2.setForeground(new Color(100, 116, 139));
        painelInfoItem2.add(lblNomeItem2);
        painelInfoItem2.add(Box.createVerticalStrut(2));
        painelInfoItem2.add(lblDescItem2);
        GridBagConstraints gbcItem2Col1 = new GridBagConstraints();
        gbcItem2Col1.gridx = 1;
        gbcItem2Col1.gridy = 0;
        gbcItem2Col1.weightx = 1.0;
        gbcItem2Col1.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col1.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(painelInfoItem2, gbcItem2Col1);

        JPanel painelCatItem2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCatItem2.setOpaque(false);
        painelCatItem2.setPreferredSize(new Dimension(105, 24));
        JLabel lblCatItem2 = new JLabel("Hortifruti", SwingConstants.CENTER);
        lblCatItem2.setOpaque(true);
        lblCatItem2.setBackground(new Color(209, 250, 229));
        lblCatItem2.setForeground(new Color(6, 95, 70));
        lblCatItem2.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCatItem2.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        painelCatItem2.add(lblCatItem2);
        GridBagConstraints gbcItem2Col2 = new GridBagConstraints();
        gbcItem2Col2.gridx = 2;
        gbcItem2Col2.gridy = 0;
        gbcItem2Col2.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col2.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(painelCatItem2, gbcItem2Col2);

        JLabel lblQtdItem2 = new JLabel("1,2 kg", SwingConstants.CENTER);
        lblQtdItem2.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtdItem2.setForeground(new Color(51, 65, 85));
        lblQtdItem2.setPreferredSize(new Dimension(88, 20));
        GridBagConstraints gbcItem2Col3 = new GridBagConstraints();
        gbcItem2Col3.gridx = 3;
        gbcItem2Col3.gridy = 0;
        gbcItem2Col3.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col3.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(lblQtdItem2, gbcItem2Col3);

        JLabel lblPrecoItem2 = new JLabel("R$ 6,50/kg", SwingConstants.CENTER);
        lblPrecoItem2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPrecoItem2.setForeground(new Color(100, 116, 139));
        lblPrecoItem2.setPreferredSize(new Dimension(105, 20));
        GridBagConstraints gbcItem2Col4 = new GridBagConstraints();
        gbcItem2Col4.gridx = 4;
        gbcItem2Col4.gridy = 0;
        gbcItem2Col4.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col4.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(lblPrecoItem2, gbcItem2Col4);

        JLabel lblSubItem2 = new JLabel("R$ 7,80", SwingConstants.CENTER);
        lblSubItem2.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSubItem2.setForeground(new Color(15, 23, 42));
        lblSubItem2.setPreferredSize(new Dimension(90, 20));
        GridBagConstraints gbcItem2Col5 = new GridBagConstraints();
        gbcItem2Col5.gridx = 5;
        gbcItem2Col5.gridy = 0;
        gbcItem2Col5.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col5.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(lblSubItem2, gbcItem2Col5);

        JPanel painelAcoesItem2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelAcoesItem2.setOpaque(false);
        painelAcoesItem2.setPreferredSize(new Dimension(82, 26));
        JButton btnEditarItem2 = new JButton(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 11, new Color(51, 65, 85)));
        btnEditarItem2.setToolTipText("Editar item");
        btnEditarItem2.setBackground(Color.WHITE);
        btnEditarItem2.setFocusPainted(false);
        btnEditarItem2.setContentAreaFilled(false);
        btnEditarItem2.setOpaque(true);
        btnEditarItem2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarItem2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        JButton btnExcluirItem2 = new JButton(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(51, 65, 85)));
        btnExcluirItem2.setToolTipText("Excluir item");
        btnExcluirItem2.setBackground(Color.WHITE);
        btnExcluirItem2.setFocusPainted(false);
        btnExcluirItem2.setContentAreaFilled(false);
        btnExcluirItem2.setOpaque(true);
        btnExcluirItem2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirItem2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        painelAcoesItem2.add(btnEditarItem2);
        painelAcoesItem2.add(btnExcluirItem2);
        GridBagConstraints gbcItem2Col6 = new GridBagConstraints();
        gbcItem2Col6.gridx = 6;
        gbcItem2Col6.gridy = 0;
        gbcItem2Col6.fill = GridBagConstraints.HORIZONTAL;
        gbcItem2Col6.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem2.add(painelAcoesItem2, gbcItem2Col6);

        painelListaCompras.add(painelLinhaItem2);

        // --- ITEM 3: Tomate Italiano Maduro ---
        JPanel painelLinhaItem3 = new JPanel(new GridBagLayout());
        painelLinhaItem3.setBackground(Color.WHITE);
        painelLinhaItem3.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, corFundoPagina),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        painelLinhaItem3.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelLinhaItem3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JCheckBox checkItem3 = new JCheckBox();
        checkItem3.setSelected(false);
        checkItem3.setHorizontalAlignment(SwingConstants.CENTER);
        checkItem3.setOpaque(false);
        checkItem3.setFocusPainted(false);
        checkItem3.setPreferredSize(new Dimension(58, 24));
        GridBagConstraints gbcItem3Col0 = new GridBagConstraints();
        gbcItem3Col0.gridx = 0;
        gbcItem3Col0.gridy = 0;
        gbcItem3Col0.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col0.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(checkItem3, gbcItem3Col0);

        JPanel painelInfoItem3 = new JPanel();
        painelInfoItem3.setLayout(new BoxLayout(painelInfoItem3, BoxLayout.Y_AXIS));
        painelInfoItem3.setOpaque(false);
        painelInfoItem3.setPreferredSize(new Dimension(240, 36));
        JLabel lblNomeItem3 = new JLabel("Tomate Italiano Maduro");
        lblNomeItem3.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNomeItem3.setForeground(new Color(15, 23, 42));
        JLabel lblDescItem3 = new JLabel("Para molho e salada");
        lblDescItem3.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescItem3.setForeground(new Color(100, 116, 139));
        painelInfoItem3.add(lblNomeItem3);
        painelInfoItem3.add(Box.createVerticalStrut(2));
        painelInfoItem3.add(lblDescItem3);
        GridBagConstraints gbcItem3Col1 = new GridBagConstraints();
        gbcItem3Col1.gridx = 1;
        gbcItem3Col1.gridy = 0;
        gbcItem3Col1.weightx = 1.0;
        gbcItem3Col1.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col1.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(painelInfoItem3, gbcItem3Col1);

        JPanel painelCatItem3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCatItem3.setOpaque(false);
        painelCatItem3.setPreferredSize(new Dimension(105, 24));
        JLabel lblCatItem3 = new JLabel("Hortifruti", SwingConstants.CENTER);
        lblCatItem3.setOpaque(true);
        lblCatItem3.setBackground(new Color(209, 250, 229));
        lblCatItem3.setForeground(new Color(6, 95, 70));
        lblCatItem3.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCatItem3.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        painelCatItem3.add(lblCatItem3);
        GridBagConstraints gbcItem3Col2 = new GridBagConstraints();
        gbcItem3Col2.gridx = 2;
        gbcItem3Col2.gridy = 0;
        gbcItem3Col2.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col2.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(painelCatItem3, gbcItem3Col2);

        JLabel lblQtdItem3 = new JLabel("0,9 kg", SwingConstants.CENTER);
        lblQtdItem3.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtdItem3.setForeground(new Color(51, 65, 85));
        lblQtdItem3.setPreferredSize(new Dimension(88, 20));
        GridBagConstraints gbcItem3Col3 = new GridBagConstraints();
        gbcItem3Col3.gridx = 3;
        gbcItem3Col3.gridy = 0;
        gbcItem3Col3.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col3.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(lblQtdItem3, gbcItem3Col3);

        JLabel lblPrecoItem3 = new JLabel("R$ 9,20/kg", SwingConstants.CENTER);
        lblPrecoItem3.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPrecoItem3.setForeground(new Color(100, 116, 139));
        lblPrecoItem3.setPreferredSize(new Dimension(105, 20));
        GridBagConstraints gbcItem3Col4 = new GridBagConstraints();
        gbcItem3Col4.gridx = 4;
        gbcItem3Col4.gridy = 0;
        gbcItem3Col4.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col4.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(lblPrecoItem3, gbcItem3Col4);

        JLabel lblSubItem3 = new JLabel("R$ 8,28", SwingConstants.CENTER);
        lblSubItem3.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSubItem3.setForeground(new Color(15, 23, 42));
        lblSubItem3.setPreferredSize(new Dimension(90, 20));
        GridBagConstraints gbcItem3Col5 = new GridBagConstraints();
        gbcItem3Col5.gridx = 5;
        gbcItem3Col5.gridy = 0;
        gbcItem3Col5.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col5.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(lblSubItem3, gbcItem3Col5);

        JPanel painelAcoesItem3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelAcoesItem3.setOpaque(false);
        painelAcoesItem3.setPreferredSize(new Dimension(82, 26));
        JButton btnEditarItem3 = new JButton(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 11, new Color(51, 65, 85)));
        btnEditarItem3.setToolTipText("Editar item");
        btnEditarItem3.setBackground(Color.WHITE);
        btnEditarItem3.setFocusPainted(false);
        btnEditarItem3.setContentAreaFilled(false);
        btnEditarItem3.setOpaque(true);
        btnEditarItem3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarItem3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        JButton btnExcluirItem3 = new JButton(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(51, 65, 85)));
        btnExcluirItem3.setToolTipText("Excluir item");
        btnExcluirItem3.setBackground(Color.WHITE);
        btnExcluirItem3.setFocusPainted(false);
        btnExcluirItem3.setContentAreaFilled(false);
        btnExcluirItem3.setOpaque(true);
        btnExcluirItem3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirItem3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        painelAcoesItem3.add(btnEditarItem3);
        painelAcoesItem3.add(btnExcluirItem3);
        GridBagConstraints gbcItem3Col6 = new GridBagConstraints();
        gbcItem3Col6.gridx = 6;
        gbcItem3Col6.gridy = 0;
        gbcItem3Col6.fill = GridBagConstraints.HORIZONTAL;
        gbcItem3Col6.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem3.add(painelAcoesItem3, gbcItem3Col6);

        painelListaCompras.add(painelLinhaItem3);

        // ==========================================
        // CATEGORIA 2: ALIMENTOS & MERCEARIA (2 ITENS)
        // ==========================================

        JPanel painelGrupoAlimentos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 7));
        painelGrupoAlimentos.setBackground(new Color(248, 250, 252));
        painelGrupoAlimentos.setBorder(new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        painelGrupoAlimentos.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelGrupoAlimentos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        JLabel lblGrupoAlimentos = new JLabel("ALIMENTOS & MERCEARIA (2 itens)",
                FontIcon.of(FontAwesomeSolid.BOX_OPEN, 12, new Color(29, 78, 216)), JLabel.LEFT);
        lblGrupoAlimentos.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblGrupoAlimentos.setForeground(new Color(29, 78, 216));
        lblGrupoAlimentos.setIconTextGap(6);
        painelGrupoAlimentos.add(lblGrupoAlimentos);
        painelListaCompras.add(painelGrupoAlimentos);

        // --- ITEM 4: Arroz Branco Tipo 1 (Pacote 5kg) ---
        JPanel painelLinhaItem4 = new JPanel(new GridBagLayout());
        painelLinhaItem4.setBackground(new Color(248, 250, 252));
        painelLinhaItem4.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, corFundoPagina),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        painelLinhaItem4.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelLinhaItem4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JCheckBox checkItem4 = new JCheckBox();
        checkItem4.setSelected(true);
        checkItem4.setHorizontalAlignment(SwingConstants.CENTER);
        checkItem4.setOpaque(false);
        checkItem4.setFocusPainted(false);
        checkItem4.setPreferredSize(new Dimension(58, 24));
        GridBagConstraints gbcItem4Col0 = new GridBagConstraints();
        gbcItem4Col0.gridx = 0;
        gbcItem4Col0.gridy = 0;
        gbcItem4Col0.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col0.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(checkItem4, gbcItem4Col0);

        JPanel painelInfoItem4 = new JPanel();
        painelInfoItem4.setLayout(new BoxLayout(painelInfoItem4, BoxLayout.Y_AXIS));
        painelInfoItem4.setOpaque(false);
        painelInfoItem4.setPreferredSize(new Dimension(240, 36));
        JLabel lblNomeItem4 = new JLabel("<html><strike>Arroz Branco Tipo 1 (Pacote 5kg)</strike></html>");
        lblNomeItem4.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNomeItem4.setForeground(new Color(100, 116, 139));
        JLabel lblDescItem4 = new JLabel("Marca Tio João ou similar");
        lblDescItem4.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescItem4.setForeground(new Color(100, 116, 139));
        painelInfoItem4.add(lblNomeItem4);
        painelInfoItem4.add(Box.createVerticalStrut(2));
        painelInfoItem4.add(lblDescItem4);
        GridBagConstraints gbcItem4Col1 = new GridBagConstraints();
        gbcItem4Col1.gridx = 1;
        gbcItem4Col1.gridy = 0;
        gbcItem4Col1.weightx = 1.0;
        gbcItem4Col1.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col1.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(painelInfoItem4, gbcItem4Col1);

        JPanel painelCatItem4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCatItem4.setOpaque(false);
        painelCatItem4.setPreferredSize(new Dimension(105, 24));
        JLabel lblCatItem4 = new JLabel("Alimentos", SwingConstants.CENTER);
        lblCatItem4.setOpaque(true);
        lblCatItem4.setBackground(new Color(219, 234, 254));
        lblCatItem4.setForeground(new Color(30, 64, 175));
        lblCatItem4.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCatItem4.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        painelCatItem4.add(lblCatItem4);
        GridBagConstraints gbcItem4Col2 = new GridBagConstraints();
        gbcItem4Col2.gridx = 2;
        gbcItem4Col2.gridy = 0;
        gbcItem4Col2.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col2.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(painelCatItem4, gbcItem4Col2);

        JLabel lblQtdItem4 = new JLabel("2 un", SwingConstants.CENTER);
        lblQtdItem4.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtdItem4.setForeground(new Color(51, 65, 85));
        lblQtdItem4.setPreferredSize(new Dimension(88, 20));
        GridBagConstraints gbcItem4Col3 = new GridBagConstraints();
        gbcItem4Col3.gridx = 3;
        gbcItem4Col3.gridy = 0;
        gbcItem4Col3.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col3.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(lblQtdItem4, gbcItem4Col3);

        JLabel lblPrecoItem4 = new JLabel("R$ 28,90", SwingConstants.CENTER);
        lblPrecoItem4.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPrecoItem4.setForeground(new Color(100, 116, 139));
        lblPrecoItem4.setPreferredSize(new Dimension(105, 20));
        GridBagConstraints gbcItem4Col4 = new GridBagConstraints();
        gbcItem4Col4.gridx = 4;
        gbcItem4Col4.gridy = 0;
        gbcItem4Col4.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col4.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(lblPrecoItem4, gbcItem4Col4);

        JLabel lblSubItem4 = new JLabel("R$ 57,80", SwingConstants.CENTER);
        lblSubItem4.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSubItem4.setForeground(new Color(15, 23, 42));
        lblSubItem4.setPreferredSize(new Dimension(90, 20));
        GridBagConstraints gbcItem4Col5 = new GridBagConstraints();
        gbcItem4Col5.gridx = 5;
        gbcItem4Col5.gridy = 0;
        gbcItem4Col5.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col5.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(lblSubItem4, gbcItem4Col5);

        JPanel painelAcoesItem4 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelAcoesItem4.setOpaque(false);
        painelAcoesItem4.setPreferredSize(new Dimension(82, 26));
        JButton btnEditarItem4 = new JButton(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 11, new Color(51, 65, 85)));
        btnEditarItem4.setToolTipText("Editar item");
        btnEditarItem4.setBackground(Color.WHITE);
        btnEditarItem4.setFocusPainted(false);
        btnEditarItem4.setContentAreaFilled(false);
        btnEditarItem4.setOpaque(true);
        btnEditarItem4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarItem4.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        JButton btnExcluirItem4 = new JButton(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(51, 65, 85)));
        btnExcluirItem4.setToolTipText("Excluir item");
        btnExcluirItem4.setBackground(Color.WHITE);
        btnExcluirItem4.setFocusPainted(false);
        btnExcluirItem4.setContentAreaFilled(false);
        btnExcluirItem4.setOpaque(true);
        btnExcluirItem4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirItem4.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        painelAcoesItem4.add(btnEditarItem4);
        painelAcoesItem4.add(btnExcluirItem4);
        GridBagConstraints gbcItem4Col6 = new GridBagConstraints();
        gbcItem4Col6.gridx = 6;
        gbcItem4Col6.gridy = 0;
        gbcItem4Col6.fill = GridBagConstraints.HORIZONTAL;
        gbcItem4Col6.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem4.add(painelAcoesItem4, gbcItem4Col6);

        painelListaCompras.add(painelLinhaItem4);

        // --- ITEM 5: Azeite de Oliva Extra Virgem 500ml ---
        JPanel painelLinhaItem5 = new JPanel(new GridBagLayout());
        painelLinhaItem5.setBackground(Color.WHITE);
        painelLinhaItem5.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, corFundoPagina),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        painelLinhaItem5.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelLinhaItem5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JCheckBox checkItem5 = new JCheckBox();
        checkItem5.setSelected(false);
        checkItem5.setHorizontalAlignment(SwingConstants.CENTER);
        checkItem5.setOpaque(false);
        checkItem5.setFocusPainted(false);
        checkItem5.setPreferredSize(new Dimension(58, 24));
        GridBagConstraints gbcItem5Col0 = new GridBagConstraints();
        gbcItem5Col0.gridx = 0;
        gbcItem5Col0.gridy = 0;
        gbcItem5Col0.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col0.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(checkItem5, gbcItem5Col0);

        JPanel painelInfoItem5 = new JPanel();
        painelInfoItem5.setLayout(new BoxLayout(painelInfoItem5, BoxLayout.Y_AXIS));
        painelInfoItem5.setOpaque(false);
        painelInfoItem5.setPreferredSize(new Dimension(240, 36));
        JLabel lblNomeItem5 = new JLabel("Azeite de Oliva Extra Virgem 500ml");
        lblNomeItem5.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNomeItem5.setForeground(new Color(15, 23, 42));
        JLabel lblDescItem5 = new JLabel("Vidro escuro acidez máx. 0.5%");
        lblDescItem5.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescItem5.setForeground(new Color(100, 116, 139));
        painelInfoItem5.add(lblNomeItem5);
        painelInfoItem5.add(Box.createVerticalStrut(2));
        painelInfoItem5.add(lblDescItem5);
        GridBagConstraints gbcItem5Col1 = new GridBagConstraints();
        gbcItem5Col1.gridx = 1;
        gbcItem5Col1.gridy = 0;
        gbcItem5Col1.weightx = 1.0;
        gbcItem5Col1.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col1.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(painelInfoItem5, gbcItem5Col1);

        JPanel painelCatItem5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCatItem5.setOpaque(false);
        painelCatItem5.setPreferredSize(new Dimension(105, 24));
        JLabel lblCatItem5 = new JLabel("Alimentos", SwingConstants.CENTER);
        lblCatItem5.setOpaque(true);
        lblCatItem5.setBackground(new Color(219, 234, 254));
        lblCatItem5.setForeground(new Color(30, 64, 175));
        lblCatItem5.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblCatItem5.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        painelCatItem5.add(lblCatItem5);
        GridBagConstraints gbcItem5Col2 = new GridBagConstraints();
        gbcItem5Col2.gridx = 2;
        gbcItem5Col2.gridy = 0;
        gbcItem5Col2.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col2.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(painelCatItem5, gbcItem5Col2);

        JLabel lblQtdItem5 = new JLabel("1 un", SwingConstants.CENTER);
        lblQtdItem5.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblQtdItem5.setForeground(new Color(51, 65, 85));
        lblQtdItem5.setPreferredSize(new Dimension(88, 20));
        GridBagConstraints gbcItem5Col3 = new GridBagConstraints();
        gbcItem5Col3.gridx = 3;
        gbcItem5Col3.gridy = 0;
        gbcItem5Col3.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col3.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(lblQtdItem5, gbcItem5Col3);

        JLabel lblPrecoItem5 = new JLabel("R$ 42,50", SwingConstants.CENTER);
        lblPrecoItem5.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPrecoItem5.setForeground(new Color(100, 116, 139));
        lblPrecoItem5.setPreferredSize(new Dimension(105, 20));
        GridBagConstraints gbcItem5Col4 = new GridBagConstraints();
        gbcItem5Col4.gridx = 4;
        gbcItem5Col4.gridy = 0;
        gbcItem5Col4.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col4.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(lblPrecoItem5, gbcItem5Col4);

        JLabel lblSubItem5 = new JLabel("R$ 42,50", SwingConstants.CENTER);
        lblSubItem5.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSubItem5.setForeground(new Color(15, 23, 42));
        lblSubItem5.setPreferredSize(new Dimension(90, 20));
        GridBagConstraints gbcItem5Col5 = new GridBagConstraints();
        gbcItem5Col5.gridx = 5;
        gbcItem5Col5.gridy = 0;
        gbcItem5Col5.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col5.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(lblSubItem5, gbcItem5Col5);

        JPanel painelAcoesItem5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelAcoesItem5.setOpaque(false);
        painelAcoesItem5.setPreferredSize(new Dimension(82, 26));
        JButton btnEditarItem5 = new JButton(FontIcon.of(FontAwesomeSolid.PENCIL_ALT, 11, new Color(51, 65, 85)));
        btnEditarItem5.setToolTipText("Editar item");
        btnEditarItem5.setBackground(Color.WHITE);
        btnEditarItem5.setFocusPainted(false);
        btnEditarItem5.setContentAreaFilled(false);
        btnEditarItem5.setOpaque(true);
        btnEditarItem5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarItem5.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        JButton btnExcluirItem5 = new JButton(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(51, 65, 85)));
        btnExcluirItem5.setToolTipText("Excluir item");
        btnExcluirItem5.setBackground(Color.WHITE);
        btnExcluirItem5.setFocusPainted(false);
        btnExcluirItem5.setContentAreaFilled(false);
        btnExcluirItem5.setOpaque(true);
        btnExcluirItem5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirItem5.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)));
        painelAcoesItem5.add(btnEditarItem5);
        painelAcoesItem5.add(btnExcluirItem5);
        GridBagConstraints gbcItem5Col6 = new GridBagConstraints();
        gbcItem5Col6.gridx = 6;
        gbcItem5Col6.gridy = 0;
        gbcItem5Col6.fill = GridBagConstraints.HORIZONTAL;
        gbcItem5Col6.insets = new Insets(0, 4, 0, 4);
        painelLinhaItem5.add(painelAcoesItem5, gbcItem5Col6);

        painelListaCompras.add(painelLinhaItem5);

        // --- Rodapé da Lista: Progresso + Botões no estilo btnMarcarTodos ---
        JPanel painelRodapeLista = new JPanel(new BorderLayout(12, 0));
        painelRodapeLista.setBackground(new Color(248, 250, 252));
        painelRodapeLista.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        painelRodapeLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelRodapeLista.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel painelStatusProgresso = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        painelStatusProgresso.setOpaque(false);

        FontIcon iconeProgressoCheck = FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 14, new Color(21, 128, 61));
        JLabel lblResumoProgresso = new JLabel(
                "<html><b>Progresso:</b> 3 de 5 itens coletados (60%)</html>",
                iconeProgressoCheck, JLabel.LEFT);
        lblResumoProgresso.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblResumoProgresso.setForeground(new Color(51, 65, 85));
        lblResumoProgresso.setIconTextGap(6);

        JLabel lblLegendaRiscado = new JLabel("<html><span style='color:#15803D'>&#9679;</span> Riscado = No carrinho físico</html>");
        lblLegendaRiscado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblLegendaRiscado.setForeground(new Color(100, 116, 139));

        painelStatusProgresso.add(lblResumoProgresso);
        painelStatusProgresso.add(Box.createHorizontalStrut(6));
        painelStatusProgresso.add(lblLegendaRiscado);

        JPanel painelBotoesLote = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesLote.setOpaque(false);

        JButton btnMarcarTodos = new JButton("Marcar Todos");
        btnMarcarTodos.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMarcarTodos.setBackground(Color.WHITE);
        btnMarcarTodos.setForeground(new Color(51, 65, 85));
        btnMarcarTodos.setFocusPainted(false);
        btnMarcarTodos.setContentAreaFilled(false);
        btnMarcarTodos.setOpaque(true);
        btnMarcarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMarcarTodos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JButton btnDesmarcarTodos = new JButton("Desmarcar Todos");
        btnDesmarcarTodos.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnDesmarcarTodos.setBackground(Color.WHITE);
        btnDesmarcarTodos.setForeground(new Color(51, 65, 85));
        btnDesmarcarTodos.setFocusPainted(false);
        btnDesmarcarTodos.setContentAreaFilled(false);
        btnDesmarcarTodos.setOpaque(true);
        btnDesmarcarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDesmarcarTodos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JButton btnLimparColetados = new JButton("Limpar Coletados");
        btnLimparColetados.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnLimparColetados.setBackground(Color.WHITE);
        btnLimparColetados.setForeground(new Color(51, 65, 85));
        btnLimparColetados.setFocusPainted(false);
        btnLimparColetados.setContentAreaFilled(false);
        btnLimparColetados.setOpaque(true);
        btnLimparColetados.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimparColetados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnLimparColetados.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Ação executada: Limpar Coletados"));

        painelBotoesLote.add(btnMarcarTodos);
        painelBotoesLote.add(btnDesmarcarTodos);
        painelBotoesLote.add(btnLimparColetados);

        painelRodapeLista.add(painelStatusProgresso, BorderLayout.WEST);
        painelRodapeLista.add(painelBotoesLote, BorderLayout.EAST);

        painelListaCompras.add(painelRodapeLista);
        painelConteudoPrincipal.add(painelListaCompras);

        // ==========================================
        // INTERATIVIDADE DOS CHECKBOXES E FILTROS
        // ==========================================

        Runnable atualizarListaEProgresso = () -> {
            JCheckBox[] checks = {checkItem1, checkItem2, checkItem3, checkItem4, checkItem5};
            JPanel[] linhas = {painelLinhaItem1, painelLinhaItem2, painelLinhaItem3, painelLinhaItem4, painelLinhaItem5};
            JLabel[] labelsNome = {lblNomeItem1, lblNomeItem2, lblNomeItem3, lblNomeItem4, lblNomeItem5};
            String[] nomes = {
                    "Maçã Gala Nacional",
                    "Banana Prata Climatizada",
                    "Tomate Italiano Maduro",
                    "Arroz Branco Tipo 1 (Pacote 5kg)",
                    "Azeite de Oliva Extra Virgem 500ml"
            };
            String[] descricoes = {
                    "Frutas frescas para a semana",
                    "Penca selecionada",
                    "Para molho e salada",
                    "Marca Tio João ou similar",
                    "Vidro escuro acidez máx. 0.5%"
            };
            String[] categorias = {"Hortifruti", "Hortifruti", "Hortifruti", "Alimentos", "Alimentos"};

            int coletados = 0;
            boolean temHortifrutiVisivel = false;
            boolean temAlimentosVisivel = false;

            for (int i = 0; i < checks.length; i++) {
                boolean marcado = checks[i].isSelected();
                if (marcado) {
                    coletados++;
                }
                linhas[i].setBackground(marcado ? new Color(248, 250, 252) : Color.WHITE);
                labelsNome[i].setText(marcado ? "<html><strike>" + nomes[i] + "</strike></html>" : nomes[i]);
                labelsNome[i].setForeground(marcado ? new Color(100, 116, 139) : new Color(15, 23, 42));

                boolean bateCat = "Todas".equals(categoriaFiltroAtiva) || categorias[i].equals(categoriaFiltroAtiva);
                boolean bateBusca = termoBuscaAtivo.isEmpty()
                        || nomes[i].toLowerCase().contains(termoBuscaAtivo)
                        || descricoes[i].toLowerCase().contains(termoBuscaAtivo);
                boolean visivel = bateCat && bateBusca;
                linhas[i].setVisible(visivel);

                if (visivel && "Hortifruti".equals(categorias[i])) {
                    temHortifrutiVisivel = true;
                }
                if (visivel && "Alimentos".equals(categorias[i])) {
                    temAlimentosVisivel = true;
                }
            }

            painelGrupoHortifruti.setVisible(temHortifrutiVisivel);
            painelGrupoAlimentos.setVisible(temAlimentosVisivel);

            int percentual = (int) Math.round((coletados * 100.0) / checks.length);
            lblResumoProgresso.setText(String.format(
                    "<html><b>Progresso:</b> %d de %d itens coletados (%d%%)</html>",
                    coletados, checks.length, percentual));
        };

        checkItem1.addActionListener(e -> atualizarListaEProgresso.run());
        checkItem2.addActionListener(e -> atualizarListaEProgresso.run());
        checkItem3.addActionListener(e -> atualizarListaEProgresso.run());
        checkItem4.addActionListener(e -> atualizarListaEProgresso.run());
        checkItem5.addActionListener(e -> atualizarListaEProgresso.run());

        btnMarcarTodos.addActionListener(e -> {
            if (painelLinhaItem1.isVisible()) checkItem1.setSelected(true);
            if (painelLinhaItem2.isVisible()) checkItem2.setSelected(true);
            if (painelLinhaItem3.isVisible()) checkItem3.setSelected(true);
            if (painelLinhaItem4.isVisible()) checkItem4.setSelected(true);
            if (painelLinhaItem5.isVisible()) checkItem5.setSelected(true);
            atualizarListaEProgresso.run();
        });

        btnDesmarcarTodos.addActionListener(e -> {
            if (painelLinhaItem1.isVisible()) checkItem1.setSelected(false);
            if (painelLinhaItem2.isVisible()) checkItem2.setSelected(false);
            if (painelLinhaItem3.isVisible()) checkItem3.setSelected(false);
            if (painelLinhaItem4.isVisible()) checkItem4.setSelected(false);
            if (painelLinhaItem5.isVisible()) checkItem5.setSelected(false);
            atualizarListaEProgresso.run();
        });

        btnAbaTodas.addActionListener(e -> {
            categoriaFiltroAtiva = "Todas";
            estilizarAbaFiltro(btnAbaTodas, true, corAzulPrimario);
            estilizarAbaFiltro(btnAbaHortifruti, false, corAzulPrimario);
            estilizarAbaFiltro(btnAbaAlimentos, false, corAzulPrimario);
            atualizarListaEProgresso.run();
        });

        btnAbaHortifruti.addActionListener(e -> {
            categoriaFiltroAtiva = "Hortifruti";
            estilizarAbaFiltro(btnAbaTodas, false, corAzulPrimario);
            estilizarAbaFiltro(btnAbaHortifruti, true, corAzulPrimario);
            estilizarAbaFiltro(btnAbaAlimentos, false, corAzulPrimario);
            atualizarListaEProgresso.run();
        });

        btnAbaAlimentos.addActionListener(e -> {
            categoriaFiltroAtiva = "Alimentos";
            estilizarAbaFiltro(btnAbaTodas, false, corAzulPrimario);
            estilizarAbaFiltro(btnAbaHortifruti, false, corAzulPrimario);
            estilizarAbaFiltro(btnAbaAlimentos, true, corAzulPrimario);
            atualizarListaEProgresso.run();
        });

        campoPesquisaLista.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                termoBuscaAtivo = campoPesquisaLista.getText().trim().toLowerCase();
                atualizarListaEProgresso.run();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        // ==========================================
        // MENU LATERAL (COM ITEM ATIVO DESTACADO EM AZUL)
        // ==========================================

        JPanel painelMenuLateral = new JPanel();
        painelMenuLateral.setBackground(Color.WHITE);
        painelMenuLateral.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 0, 1, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(-25, 12, 10, 12)
        ));

        GridBagLayout layoutMenuLateral = new GridBagLayout();
        layoutMenuLateral.columnWidths = new int[]{165, 0};
        layoutMenuLateral.rowHeights = new int[]{0, 0, 28, 28, 28, 28, 0};
        layoutMenuLateral.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        layoutMenuLateral.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        painelMenuLateral.setLayout(layoutMenuLateral);

        FontIcon iconeMenuLista = FontIcon.of(FontAwesomeSolid.LIST, 11, Color.WHITE);
        FontIcon iconeMenuOrcamento = FontIcon.of(FontAwesomeSolid.WALLET, 11, new Color(51, 65, 85));
        FontIcon iconeMenuCatalogo = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 11, new Color(51, 65, 85));
        FontIcon iconeMenuListasSalvas = FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85));

        Component espacoTopoMenuLateral = Box.createVerticalStrut(8);
        GridBagConstraints gbcEspacoTopoMenuLateral = new GridBagConstraints();
        gbcEspacoTopoMenuLateral.anchor = GridBagConstraints.NORTH;
        gbcEspacoTopoMenuLateral.insets = new Insets(0, 0, 5, 0);
        gbcEspacoTopoMenuLateral.gridx = 0;
        gbcEspacoTopoMenuLateral.gridy = 1;
        painelMenuLateral.add(espacoTopoMenuLateral, gbcEspacoTopoMenuLateral);

        // Item ativo no menu lateral: Lista Atual
        JButton btnMenuListaAtual = new JButton("Lista Atual", iconeMenuLista);
        btnMenuListaAtual.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuListaAtual.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuListaAtual.setBackground(corAzulPrimario);
        btnMenuListaAtual.setForeground(Color.WHITE);
        btnMenuListaAtual.setFocusPainted(false);
        btnMenuListaAtual.setBorderPainted(false);
        btnMenuListaAtual.setContentAreaFilled(false);
        btnMenuListaAtual.setOpaque(true);
        btnMenuListaAtual.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuListaAtual.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        GridBagConstraints gbcBtnListaAtual = new GridBagConstraints();
        gbcBtnListaAtual.anchor = GridBagConstraints.NORTH;
        gbcBtnListaAtual.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnListaAtual.insets = new Insets(0, 0, 6, 0);
        gbcBtnListaAtual.gridx = 0;
        gbcBtnListaAtual.gridy = 2;
        painelMenuLateral.add(btnMenuListaAtual, gbcBtnListaAtual);

        JButton btnMenuOrcamento = new JButton("Orçamento & Custos", iconeMenuOrcamento);
        btnMenuOrcamento.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuOrcamento.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuOrcamento.setBackground(Color.WHITE);
        btnMenuOrcamento.setForeground(new Color(51, 65, 85));
        btnMenuOrcamento.setFocusPainted(false);
        btnMenuOrcamento.setContentAreaFilled(false);
        btnMenuOrcamento.setOpaque(true);
        btnMenuOrcamento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuOrcamento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnMenuOrcamento.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Navegar para: Orçamento & Custos"));
        GridBagConstraints gbcBtnOrcamento = new GridBagConstraints();
        gbcBtnOrcamento.anchor = GridBagConstraints.NORTH;
        gbcBtnOrcamento.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnOrcamento.insets = new Insets(0, 0, 6, 0);
        gbcBtnOrcamento.gridx = 0;
        gbcBtnOrcamento.gridy = 3;
        painelMenuLateral.add(btnMenuOrcamento, gbcBtnOrcamento);

        JButton btnMenuCatalogo = new JButton("Catálogo de Itens", iconeMenuCatalogo);
        btnMenuCatalogo.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuCatalogo.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuCatalogo.setBackground(Color.WHITE);
        btnMenuCatalogo.setForeground(new Color(51, 65, 85));
        btnMenuCatalogo.setFocusPainted(false);
        btnMenuCatalogo.setContentAreaFilled(false);
        btnMenuCatalogo.setOpaque(true);
        btnMenuCatalogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuCatalogo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnMenuCatalogo.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Navegar para: Catálogo de Itens"));
        GridBagConstraints gbcBtnCatalogo = new GridBagConstraints();
        gbcBtnCatalogo.anchor = GridBagConstraints.NORTH;
        gbcBtnCatalogo.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnCatalogo.insets = new Insets(0, 0, 6, 0);
        gbcBtnCatalogo.gridx = 0;
        gbcBtnCatalogo.gridy = 4;
        painelMenuLateral.add(btnMenuCatalogo, gbcBtnCatalogo);

        JButton btnMenuListasSalvas = new JButton("Listas Salvas", iconeMenuListasSalvas);
        btnMenuListasSalvas.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuListasSalvas.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuListasSalvas.setBackground(Color.WHITE);
        btnMenuListasSalvas.setForeground(new Color(51, 65, 85));
        btnMenuListasSalvas.setFocusPainted(false);
        btnMenuListasSalvas.setContentAreaFilled(false);
        btnMenuListasSalvas.setOpaque(true);
        btnMenuListasSalvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuListasSalvas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnMenuListasSalvas.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Navegar para: Listas Salvas"));
        GridBagConstraints gbcBtnListasSalvas = new GridBagConstraints();
        gbcBtnListasSalvas.anchor = GridBagConstraints.NORTH;
        gbcBtnListasSalvas.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnListasSalvas.gridx = 0;
        gbcBtnListasSalvas.gridy = 5;
        painelMenuLateral.add(btnMenuListasSalvas, gbcBtnListasSalvas);

        // ==========================================
        // SCROLL E MONTAGEM FINAL DA JANELA
        // ==========================================

        JScrollPane painelRolagemPrincipal = new JScrollPane(painelConteudoPrincipal);
        painelRolagemPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        painelRolagemPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        painelRolagemPrincipal.setOpaque(true);
        painelRolagemPrincipal.setBackground(corFundoPagina);
        painelRolagemPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        painelRolagemPrincipal.getViewport().setOpaque(true);
        painelRolagemPrincipal.getViewport().setBackground(corFundoPagina);

        JPanel painelAreaCentral = new JPanel(new BorderLayout());
        painelAreaCentral.setOpaque(true);
        painelAreaCentral.setBackground(corFundoPagina);
        painelAreaCentral.add(painelMenuLateral, BorderLayout.WEST);
        painelAreaCentral.add(painelRolagemPrincipal, BorderLayout.CENTER);

        janelaPrincipal.getContentPane().setLayout(new BorderLayout());
        janelaPrincipal.getContentPane().add(painelBarraSuperior, BorderLayout.NORTH);
        janelaPrincipal.getContentPane().add(painelAreaCentral, BorderLayout.CENTER);

        janelaPrincipal.setVisible(true);
    }

    private void estilizarAbaFiltro(JButton botao, boolean ativo, Color corAzulPrimario) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 11));
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (ativo) {
            botao.setBackground(corAzulPrimario);
            botao.setForeground(Color.WHITE);
            botao.setBorderPainted(false);
            botao.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        } else {
            botao.setBackground(Color.WHITE);
            botao.setForeground(new Color(51, 65, 85));
            botao.setBorderPainted(true);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        }
    }

    // ==========================================
    // CARREGAMENTO E REDIMENSIONAMENTO DA LOGO
    // ==========================================

    private ImageIcon carregarLogoPequena(String caminhoRecurso, int largura, int altura) {
        try {
            BufferedImage imagemOriginal = null;
            URL urlRecurso = getClass().getResource(caminhoRecurso);
            if (urlRecurso != null) {
                imagemOriginal = ImageIO.read(urlRecurso);
            } else {
                File arquivoLocal = new File("src" + caminhoRecurso);
                if (arquivoLocal.exists()) {
                    imagemOriginal = ImageIO.read(arquivoLocal);
                }
            }

            if (imagemOriginal != null) {
                BufferedImage redimensionada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = redimensionada.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(imagemOriginal, 0, 0, largura, altura, null);
                g2.dispose();
                return new ImageIcon(redimensionada);
            }
        } catch (Exception ignored) {
            // Usa o ícone vetorial da logo smartList caso o PNG ainda não esteja na pasta
        }

        // Fallback vetorial da logo smartList (prancheta checklist em gradiente azul-turquesa-verde)
        BufferedImage logoGerada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = logoGerada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradiente = new GradientPaint(
                0, 0, new Color(55, 48, 224),
                largura, altura, new Color(16, 185, 129));
        g2.setPaint(gradiente);
        g2.fill(new RoundRectangle2D.Double(1, 1, largura - 2, altura - 2, 10, 10));

        // Prancheta branca interna
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Double(8, 8, largura - 16, altura - 14, 4, 4));
        g2.fill(new RoundRectangle2D.Double(12, 5, largura - 24, 5, 3, 3));

        // Checkmark + linhas de lista
        g2.drawLine(12, 15, 14, 17);
        g2.drawLine(14, 17, 18, 13);
        g2.drawLine(20, 15, 23, 15);
        g2.drawLine(12, 21, 23, 21);

        g2.dispose();
        return new ImageIcon(logoGerada);
    }

    // ==========================================
    // PLACEHOLDER COMPATÍVEL COM QUALQUER L&F
    // ==========================================

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

    // ==========================================
    // CARREGAMENTO DE IMAGENS
    // ==========================================

    private void carregarImagemHotlinkAsync(JLabel destino, String urlBruta, int larguraMaxima) {

        ImageIcon emCache = cacheImagens.get(urlBruta);
        if (emCache != null) {
            destino.setText(null);
            destino.setIcon(emCache);
            return;
        }

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                URL url = URI.create(urlBruta).toURL();
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setInstanceFollowRedirects(true);
                conexao.setConnectTimeout(8000);
                conexao.setReadTimeout(12000);
                conexao.setRequestProperty("User-Agent", "Mozilla/5.0 (Java Swing Hotlink Client)");

                try (InputStream entrada = conexao.getInputStream()) {
                    BufferedImage imagem = ImageIO.read(entrada);
                    if (imagem == null) {
                        throw new IllegalArgumentException("Formato de imagem não suportado pelo ImageIO");
                    }

                    if (imagem.getWidth() > larguraMaxima) {
                        int alturaAlvo = (int) ((double) imagem.getHeight() * larguraMaxima / imagem.getWidth());
                        BufferedImage redimensionada = new BufferedImage(larguraMaxima, alturaAlvo, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = redimensionada.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.drawImage(imagem, 0, 0, larguraMaxima, alturaAlvo, null);
                        g2.dispose();
                        return new ImageIcon(redimensionada);
                    }
                    return new ImageIcon(imagem);
                } finally {
                    conexao.disconnect();
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icone = get();
                    cacheImagens.put(urlBruta, icone);
                    destino.setText(null);
                    destino.setIcon(icone);
                    destino.revalidate();
                } catch (Exception ex) {
                    destino.setText("Falha ao carregar hotlink: " + ex.getMessage());
                }
            }
        }.execute();
    }
}