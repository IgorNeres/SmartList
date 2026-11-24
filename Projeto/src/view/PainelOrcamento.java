package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import com.formdev.flatlaf.FlatLightLaf;

public class PainelOrcamento {

    private double tetoMensalAtual = 1800.00;
    private final double totalGastoAtual = 1485.20;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            new PainelOrcamento().exibirJanela();
        });
    }

    public void exibirJanela() {

        Color corFundoPagina = new Color(241, 245, 249);

        JFrame janelaPrincipal = new JFrame("Lista de Compras Inteligente — Orçamento & Custos");
        janelaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaPrincipal.setSize(1120, 820);
        janelaPrincipal.setLocationRelativeTo(null);

        // Conteúdo principal rolável
        JPanel painelConteudoPrincipal = new JPanel();
        painelConteudoPrincipal.setLayout(new BoxLayout(painelConteudoPrincipal, BoxLayout.Y_AXIS));
        painelConteudoPrincipal.setOpaque(true);
        painelConteudoPrincipal.setBackground(corFundoPagina);
        painelConteudoPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 4, 18, 14));

        // ==========================================
        // BARRA SUPERIOR: LOGO + AÇÕES DE ARQUIVO
        // ==========================================

        JPanel painelBarraSuperior = new JPanel();
        painelBarraSuperior.setLayout(new BoxLayout(painelBarraSuperior, BoxLayout.LINE_AXIS));
        painelBarraSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarraSuperior.setBackground(Color.WHITE);
        painelBarraSuperior.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

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
        // 1. CABEÇALHO DO PAINEL ANALÍTICO
        // ==========================================

        JPanel painelCabecalhoAnalitico = new JPanel(new BorderLayout(16, 0));
        painelCabecalhoAnalitico.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCabecalhoAnalitico.setBackground(Color.WHITE);
        painelCabecalhoAnalitico.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        painelCabecalhoAnalitico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));

        // Coluna Esquerda: Subtítulo + Título Principal
        JPanel painelTitulosAnalitico = new JPanel();
        painelTitulosAnalitico.setLayout(new BoxLayout(painelTitulosAnalitico, BoxLayout.Y_AXIS));
        painelTitulosAnalitico.setOpaque(false);

        JLabel lblSubtituloAnalitico = new JLabel(
                "<html><span style='color:#3730E0;font-weight:bold;'>PAINEL ANALÍTICO</span>"
                        + " &nbsp;<span style='color:#CBD5E1;'>&#9679;</span>&nbsp; "
                        + "<span style='color:#64748B;'>Competência Fiscal</span></html>");
        lblSubtituloAnalitico.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubtituloAnalitico.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTituloAnalitico = new JLabel("Controle Orçamentário e Análise de Custos");
        lblTituloAnalitico.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblTituloAnalitico.setForeground(new Color(15, 23, 42));
        lblTituloAnalitico.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelTitulosAnalitico.add(lblSubtituloAnalitico);
        painelTitulosAnalitico.add(Box.createVerticalStrut(6));
        painelTitulosAnalitico.add(lblTituloAnalitico);

        // Coluna Direita: Competência + Exportar (.pdf / .csv) e Configurar Metas
        JPanel painelAcoesAnalitico = new JPanel();
        painelAcoesAnalitico.setLayout(new BoxLayout(painelAcoesAnalitico, BoxLayout.Y_AXIS));
        painelAcoesAnalitico.setOpaque(false);

        JPanel painelLinhaControlesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelLinhaControlesTopo.setOpaque(false);
        painelLinhaControlesTopo.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel painelSeletorCompetencia = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        painelSeletorCompetencia.setBackground(new Color(241, 245, 249));
        painelSeletorCompetencia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)));

        JLabel lblIconeCalendario = new JLabel(FontIcon.of(FontAwesomeSolid.CALENDAR_ALT, 12, new Color(71, 85, 105)));
        JComboBox<String> comboCompetencia = new JComboBox<>(new String[]{
                "Mês Atual - Outubro/2024",
                "Setembro/2024",
                "Agosto/2024"
        });
        comboCompetencia.setFont(new Font("SansSerif", Font.BOLD, 11));
        comboCompetencia.setForeground(new Color(51, 65, 85));
        comboCompetencia.setBackground(new Color(241, 245, 249));
        comboCompetencia.setBorder(null);

        painelSeletorCompetencia.add(lblIconeCalendario);
        painelSeletorCompetencia.add(comboCompetencia);

        JButton btnExportarRelatorio = new JButton("Exportar (.pdf / .csv)",
                FontIcon.of(FontAwesomeSolid.DOWNLOAD, 11, new Color(51, 65, 85)));
        btnExportarRelatorio.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnExportarRelatorio.setBackground(Color.WHITE);
        btnExportarRelatorio.setForeground(new Color(51, 65, 85));
        btnExportarRelatorio.setFocusPainted(false);
        btnExportarRelatorio.setContentAreaFilled(false);
        btnExportarRelatorio.setOpaque(true);
        btnExportarRelatorio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportarRelatorio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnExportarRelatorio.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Exportando relatório de " + comboCompetencia.getSelectedItem() + "..."));

        painelLinhaControlesTopo.add(painelSeletorCompetencia);
        painelLinhaControlesTopo.add(btnExportarRelatorio);

        JPanel painelLinhaBotaoMetas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelLinhaBotaoMetas.setOpaque(false);

        JButton btnConfigurarMetas = new JButton("Configurar Metas",
                FontIcon.of(FontAwesomeSolid.SLIDERS_H, 11, Color.WHITE));
        btnConfigurarMetas.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnConfigurarMetas.setBackground(new Color(55, 48, 224));
        btnConfigurarMetas.setForeground(Color.WHITE);
        btnConfigurarMetas.setFocusPainted(false);
        btnConfigurarMetas.setBorderPainted(false);
        btnConfigurarMetas.setContentAreaFilled(false);
        btnConfigurarMetas.setOpaque(true);
        btnConfigurarMetas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfigurarMetas.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));

        painelLinhaBotaoMetas.add(btnConfigurarMetas);

        painelAcoesAnalitico.add(painelLinhaControlesTopo);
        painelAcoesAnalitico.add(Box.createVerticalStrut(8));
        painelAcoesAnalitico.add(painelLinhaBotaoMetas);

        painelCabecalhoAnalitico.add(painelTitulosAnalitico, BorderLayout.CENTER);
        painelCabecalhoAnalitico.add(painelAcoesAnalitico, BorderLayout.EAST);

        painelConteudoPrincipal.add(painelCabecalhoAnalitico);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 2. CARDS DE INDICADORES (3 COLUNAS)
        // ==========================================

        JPanel painelCardsKpi = new JPanel(new GridLayout(1, 3, 14, 0));
        painelCardsKpi.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCardsKpi.setOpaque(false);
        painelCardsKpi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 146));

        // --- CARD 1: TETO MENSAL (ORÇADO) ---
        JPanel cardTetoMensal = new JPanel(new BorderLayout(0, 8));
        cardTetoMensal.setBackground(Color.WHITE);
        cardTetoMensal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 12, 16)));

        JPanel topoCardTeto = new JPanel(new BorderLayout());
        topoCardTeto.setOpaque(false);
        JLabel lblTituloTeto = new JLabel("TETO MENSAL (ORÇADO)");
        lblTituloTeto.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTituloTeto.setForeground(new Color(100, 116, 139));
        JLabel lblIconeTeto = new JLabel(FontIcon.of(FontAwesomeSolid.LANDMARK, 14, new Color(71, 85, 105)));
        topoCardTeto.add(lblTituloTeto, BorderLayout.WEST);
        topoCardTeto.add(lblIconeTeto, BorderLayout.EAST);

        JPanel centroCardTeto = new JPanel();
        centroCardTeto.setLayout(new BoxLayout(centroCardTeto, BoxLayout.Y_AXIS));
        centroCardTeto.setOpaque(false);

        JPanel linhaValorTeto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaValorTeto.setOpaque(false);
        linhaValorTeto.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblMoedaTeto = new JLabel("R$ ");
        lblMoedaTeto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMoedaTeto.setForeground(new Color(100, 116, 139));
        JLabel lblValorTeto = new JLabel("1.800,00");
        lblValorTeto.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValorTeto.setForeground(new Color(15, 23, 42));
        linhaValorTeto.add(lblMoedaTeto);
        linhaValorTeto.add(lblValorTeto);

        JLabel lblSubTeto = new JLabel("Limite definido para Outubro");
        lblSubTeto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubTeto.setForeground(new Color(100, 116, 139));
        lblSubTeto.setAlignmentX(Component.LEFT_ALIGNMENT);

        centroCardTeto.add(linhaValorTeto);
        centroCardTeto.add(Box.createVerticalStrut(4));
        centroCardTeto.add(lblSubTeto);

        JPanel rodapeCardTeto = new JPanel(new BorderLayout());
        rodapeCardTeto.setOpaque(false);
        rodapeCardTeto.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)));
        JLabel lblBaseCalculoTexto = new JLabel("Base de cálculo");
        lblBaseCalculoTexto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblBaseCalculoTexto.setForeground(new Color(100, 116, 139));
        JLabel lblBaseCalculoValor = new JLabel("31 dias");
        lblBaseCalculoValor.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBaseCalculoValor.setForeground(new Color(51, 65, 85));
        rodapeCardTeto.add(lblBaseCalculoTexto, BorderLayout.WEST);
        rodapeCardTeto.add(lblBaseCalculoValor, BorderLayout.EAST);

        cardTetoMensal.add(topoCardTeto, BorderLayout.NORTH);
        cardTetoMensal.add(centroCardTeto, BorderLayout.CENTER);
        cardTetoMensal.add(rodapeCardTeto, BorderLayout.SOUTH);

        // --- CARD 2: TOTAL GASTO ATÉ O MOMENTO ---
        JPanel cardTotalGasto = new JPanel(new BorderLayout(0, 8));
        cardTotalGasto.setBackground(Color.WHITE);
        cardTotalGasto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 12, 16)));

        JPanel topoCardGasto = new JPanel(new BorderLayout());
        topoCardGasto.setOpaque(false);
        JLabel lblTituloGasto = new JLabel("TOTAL GASTO ATÉ O MOMENTO");
        lblTituloGasto.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTituloGasto.setForeground(new Color(100, 116, 139));
        JLabel lblIconeGasto = new JLabel(FontIcon.of(FontAwesomeSolid.MONEY_BILL_WAVE, 14, new Color(55, 48, 224)));
        topoCardGasto.add(lblTituloGasto, BorderLayout.WEST);
        topoCardGasto.add(lblIconeGasto, BorderLayout.EAST);

        JPanel centroCardGasto = new JPanel();
        centroCardGasto.setLayout(new BoxLayout(centroCardGasto, BoxLayout.Y_AXIS));
        centroCardGasto.setOpaque(false);

        JPanel linhaValorGasto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaValorGasto.setOpaque(false);
        linhaValorGasto.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblMoedaGasto = new JLabel("R$ ");
        lblMoedaGasto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMoedaGasto.setForeground(new Color(100, 116, 139));
        JLabel lblValorGasto = new JLabel("1.485,20");
        lblValorGasto.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValorGasto.setForeground(new Color(55, 48, 224));
        linhaValorGasto.add(lblMoedaGasto);
        linhaValorGasto.add(lblValorGasto);

        JPanel linhaConsumoPercentual = new JPanel(new BorderLayout());
        linhaConsumoPercentual.setOpaque(false);
        linhaConsumoPercentual.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaConsumoPercentual.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        JLabel lblConsumoTexto = new JLabel("Consumo do orçamento");
        lblConsumoTexto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblConsumoTexto.setForeground(new Color(100, 116, 139));
        JLabel lblConsumoPercentual = new JLabel("82,5%");
        lblConsumoPercentual.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblConsumoPercentual.setForeground(new Color(15, 23, 42));
        linhaConsumoPercentual.add(lblConsumoTexto, BorderLayout.WEST);
        linhaConsumoPercentual.add(lblConsumoPercentual, BorderLayout.EAST);

        centroCardGasto.add(linhaValorGasto);
        centroCardGasto.add(Box.createVerticalStrut(4));
        centroCardGasto.add(linhaConsumoPercentual);

        JPanel rodapeCardGasto = new JPanel(new BorderLayout());
        rodapeCardGasto.setOpaque(false);
        rodapeCardGasto.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));

        JProgressBar barraConsumoOrcamento = new JProgressBar(0, 100);
        barraConsumoOrcamento.setValue(83);
        barraConsumoOrcamento.setForeground(new Color(55, 48, 224));
        barraConsumoOrcamento.setBackground(new Color(226, 232, 240));
        barraConsumoOrcamento.setBorderPainted(false);
        barraConsumoOrcamento.setPreferredSize(new Dimension(100, 7));
        rodapeCardGasto.add(barraConsumoOrcamento, BorderLayout.CENTER);

        cardTotalGasto.add(topoCardGasto, BorderLayout.NORTH);
        cardTotalGasto.add(centroCardGasto, BorderLayout.CENTER);
        cardTotalGasto.add(rodapeCardGasto, BorderLayout.SOUTH);

        // --- CARD 3: SALDO RESTANTE DISPONÍVEL ---
        JPanel cardSaldoRestante = new JPanel(new BorderLayout(0, 8));
        cardSaldoRestante.setBackground(Color.WHITE);
        cardSaldoRestante.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 12, 16)));

        JPanel topoCardSaldo = new JPanel(new BorderLayout());
        topoCardSaldo.setOpaque(false);
        JLabel lblTituloSaldo = new JLabel("SALDO RESTANTE DISPONÍVEL");
        lblTituloSaldo.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTituloSaldo.setForeground(new Color(100, 116, 139));
        JLabel lblIconeSaldo = new JLabel(FontIcon.of(FontAwesomeSolid.WALLET, 14, new Color(21, 128, 61)));
        topoCardSaldo.add(lblTituloSaldo, BorderLayout.WEST);
        topoCardSaldo.add(lblIconeSaldo, BorderLayout.EAST);

        JPanel centroCardSaldo = new JPanel();
        centroCardSaldo.setLayout(new BoxLayout(centroCardSaldo, BoxLayout.Y_AXIS));
        centroCardSaldo.setOpaque(false);

        JPanel linhaValorSaldo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaValorSaldo.setOpaque(false);
        linhaValorSaldo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblMoedaSaldo = new JLabel("R$ ");
        lblMoedaSaldo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMoedaSaldo.setForeground(new Color(100, 116, 139));
        JLabel lblValorSaldo = new JLabel("314,80");
        lblValorSaldo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValorSaldo.setForeground(new Color(21, 128, 61));
        linhaValorSaldo.add(lblMoedaSaldo);
        linhaValorSaldo.add(lblValorSaldo);

        JLabel lblSubSaldo = new JLabel("Margem para fechamento do mês");
        lblSubSaldo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSubSaldo.setForeground(new Color(100, 116, 139));
        lblSubSaldo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centroCardSaldo.add(linhaValorSaldo);
        centroCardSaldo.add(Box.createVerticalStrut(4));
        centroCardSaldo.add(lblSubSaldo);

        JPanel rodapeCardSaldo = new JPanel(new BorderLayout());
        rodapeCardSaldo.setOpaque(false);
        rodapeCardSaldo.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)));
        JLabel lblStatusSaldoTexto = new JLabel("Status");
        lblStatusSaldoTexto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusSaldoTexto.setForeground(new Color(100, 116, 139));
        JLabel lblStatusSaldoValor = new JLabel("Dentro do teto previsto");
        lblStatusSaldoValor.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStatusSaldoValor.setForeground(new Color(21, 128, 61));
        rodapeCardSaldo.add(lblStatusSaldoTexto, BorderLayout.WEST);
        rodapeCardSaldo.add(lblStatusSaldoValor, BorderLayout.EAST);

        cardSaldoRestante.add(topoCardSaldo, BorderLayout.NORTH);
        cardSaldoRestante.add(centroCardSaldo, BorderLayout.CENTER);
        cardSaldoRestante.add(rodapeCardSaldo, BorderLayout.SOUTH);

        painelCardsKpi.add(cardTetoMensal);
        painelCardsKpi.add(cardTotalGasto);
        painelCardsKpi.add(cardSaldoRestante);

        painelConteudoPrincipal.add(painelCardsKpi);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 3. CARD: GASTOS POR CATEGORIA (JTable)
        // ==========================================

        JPanel painelGastosCategoria = new JPanel(new BorderLayout());
        painelGastosCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelGastosCategoria.setBackground(Color.WHITE);
        painelGastosCategoria.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        painelGastosCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 315));

        JPanel cabecalhoGastosCategoria = new JPanel(new BorderLayout());
        cabecalhoGastosCategoria.setBackground(Color.WHITE);
        cabecalhoGastosCategoria.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        JLabel lblTituloGastosCategoria = new JLabel("Gastos por Categoria (JTable)",
                FontIcon.of(FontAwesomeSolid.TABLE, 14, new Color(55, 48, 224)), JLabel.LEFT);
        lblTituloGastosCategoria.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloGastosCategoria.setForeground(new Color(15, 23, 42));
        lblTituloGastosCategoria.setIconTextGap(8);

        JLabel lblQtdCategorias = new JLabel("5 categorias cadastradas");
        lblQtdCategorias.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblQtdCategorias.setForeground(new Color(100, 116, 139));

        cabecalhoGastosCategoria.add(lblTituloGastosCategoria, BorderLayout.WEST);
        cabecalhoGastosCategoria.add(lblQtdCategorias, BorderLayout.EAST);

        DefaultTableModel modeloGastosCategoria = new DefaultTableModel(
                new Object[][]{
                        {"Alimentos & Mercearia", "R$ 620,00", "41,7%", "Normal"},
                        {"Açougue & Proteínas", "R$ 380,00", "25,6%", "Atenção"},
                        {"Hortifrúti & Frescos", "R$ 245,20", "16,5%", "Normal"},
                        {"Limpeza & Higiene", "R$ 160,00", "10,8%", "Normal"},
                        {"Outros & Bebidas", "R$ 80,00", "5,4%", "Normal"}
                },
                new String[]{"CATEGORIA", "VALOR GASTO", "% DO TOTAL", "SITUAÇÃO"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaGastosCategoria = new JTable(modeloGastosCategoria);
        tabelaGastosCategoria.setRowHeight(38);
        tabelaGastosCategoria.setShowVerticalLines(false);
        tabelaGastosCategoria.setShowHorizontalLines(true);
        tabelaGastosCategoria.setGridColor(new Color(241, 245, 249));
        tabelaGastosCategoria.setIntercellSpacing(new Dimension(0, 1));
        tabelaGastosCategoria.setSelectionBackground(new Color(248, 250, 252));
        tabelaGastosCategoria.setSelectionForeground(new Color(15, 23, 42));
        tabelaGastosCategoria.setFocusable(false);

        JTableHeader headerGastos = tabelaGastosCategoria.getTableHeader();
        headerGastos.setReorderingAllowed(false);
        headerGastos.setPreferredSize(new Dimension(100, 32));
        headerGastos.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(241, 245, 249));
                lbl.setForeground(new Color(100, 116, 139));
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                if (column == 1 || column == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        });

        tabelaGastosCategoria.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                String texto = value == null ? "" : value.toString();

                if (column == 3) {
                    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
                    wrapper.setBackground(Color.WHITE);
                    JLabel badge = new JLabel(texto, SwingConstants.CENTER);
                    badge.setOpaque(true);
                    badge.setFont(new Font("SansSerif", Font.BOLD, 10));
                    badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                    if ("Atenção".equalsIgnoreCase(texto)) {
                        badge.setBackground(new Color(254, 226, 226));
                        badge.setForeground(new Color(185, 28, 28));
                    } else {
                        badge.setBackground(new Color(241, 245, 249));
                        badge.setForeground(new Color(71, 85, 105));
                    }
                    wrapper.add(badge);
                    return wrapper;
                }

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.WHITE);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

                if (column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                    lbl.setForeground(new Color(30, 41, 59));
                } else if (column == 1) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    lbl.setForeground(new Color(51, 65, 85));
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setForeground(new Color(71, 85, 105));
                }
                return lbl;
            }
        });

        tabelaGastosCategoria.getColumnModel().getColumn(0).setPreferredWidth(340);
        tabelaGastosCategoria.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabelaGastosCategoria.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabelaGastosCategoria.getColumnModel().getColumn(3).setPreferredWidth(130);

        JPanel painelTabelaGastosWrapper = new JPanel(new BorderLayout());
        painelTabelaGastosWrapper.setOpaque(false);
        painelTabelaGastosWrapper.add(headerGastos, BorderLayout.NORTH);
        painelTabelaGastosWrapper.add(tabelaGastosCategoria, BorderLayout.CENTER);

        // Linha Total Geral
        JPanel painelTotalGeralCategorias = new JPanel(new GridBagLayout());
        painelTotalGeralCategorias.setBackground(new Color(241, 245, 249));
        painelTotalGeralCategorias.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel lblTotalGeralTexto = new JLabel("Total Geral");
        lblTotalGeralTexto.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalGeralTexto.setForeground(new Color(15, 23, 42));
        GridBagConstraints gbcTotal0 = new GridBagConstraints();
        gbcTotal0.gridx = 0;
        gbcTotal0.gridy = 0;
        gbcTotal0.weightx = 0.44;
        gbcTotal0.fill = GridBagConstraints.HORIZONTAL;
        painelTotalGeralCategorias.add(lblTotalGeralTexto, gbcTotal0);

        JLabel lblTotalGeralValor = new JLabel("R$ 1.485,20", SwingConstants.RIGHT);
        lblTotalGeralValor.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblTotalGeralValor.setForeground(new Color(55, 48, 224));
        GridBagConstraints gbcTotal1 = new GridBagConstraints();
        gbcTotal1.gridx = 1;
        gbcTotal1.gridy = 0;
        gbcTotal1.weightx = 0.21;
        gbcTotal1.fill = GridBagConstraints.HORIZONTAL;
        painelTotalGeralCategorias.add(lblTotalGeralValor, gbcTotal1);

        JLabel lblTotalGeralPercentual = new JLabel("100,0%", SwingConstants.RIGHT);
        lblTotalGeralPercentual.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalGeralPercentual.setForeground(new Color(15, 23, 42));
        GridBagConstraints gbcTotal2 = new GridBagConstraints();
        gbcTotal2.gridx = 2;
        gbcTotal2.gridy = 0;
        gbcTotal2.weightx = 0.18;
        gbcTotal2.fill = GridBagConstraints.HORIZONTAL;
        painelTotalGeralCategorias.add(lblTotalGeralPercentual, gbcTotal2);

        JLabel lblTotalGeralEspaco = new JLabel("");
        GridBagConstraints gbcTotal3 = new GridBagConstraints();
        gbcTotal3.gridx = 3;
        gbcTotal3.gridy = 0;
        gbcTotal3.weightx = 0.17;
        gbcTotal3.fill = GridBagConstraints.HORIZONTAL;
        painelTotalGeralCategorias.add(lblTotalGeralEspaco, gbcTotal3);

        painelGastosCategoria.add(cabecalhoGastosCategoria, BorderLayout.NORTH);
        painelGastosCategoria.add(painelTabelaGastosWrapper, BorderLayout.CENTER);
        painelGastosCategoria.add(painelTotalGeralCategorias, BorderLayout.SOUTH);

        painelConteudoPrincipal.add(painelGastosCategoria);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 4. CARD: HISTÓRICO DE COMPRAS REALIZADAS
        // ==========================================

        JPanel painelHistoricoCompras = new JPanel(new BorderLayout());
        painelHistoricoCompras.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelHistoricoCompras.setBackground(Color.WHITE);
        painelHistoricoCompras.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        painelHistoricoCompras.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JPanel cabecalhoHistorico = new JPanel(new BorderLayout(12, 0));
        cabecalhoHistorico.setBackground(Color.WHITE);
        cabecalhoHistorico.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        JLabel lblTituloHistorico = new JLabel("Histórico de Compras Realizadas",
                FontIcon.of(FontAwesomeSolid.RECEIPT, 14, new Color(55, 48, 224)), JLabel.LEFT);
        lblTituloHistorico.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloHistorico.setForeground(new Color(15, 23, 42));
        lblTituloHistorico.setIconTextGap(8);

        JPanel painelFiltroHistorico = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelFiltroHistorico.setOpaque(false);

        JTextField campoFiltroCompras = new JTextField();
        campoFiltroCompras.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoFiltroCompras.setPreferredSize(new Dimension(175, 30));
        campoFiltroCompras.setBackground(new Color(241, 245, 249));
        campoFiltroCompras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        aplicarPlaceholder(campoFiltroCompras, "Filtrar compras...");

        JButton btnFiltrarCompras = new JButton("Filtrar",
                FontIcon.of(FontAwesomeSolid.FILTER, 11, new Color(51, 65, 85)));
        btnFiltrarCompras.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnFiltrarCompras.setBackground(Color.WHITE);
        btnFiltrarCompras.setForeground(new Color(51, 65, 85));
        btnFiltrarCompras.setFocusPainted(false);
        btnFiltrarCompras.setContentAreaFilled(false);
        btnFiltrarCompras.setOpaque(true);
        btnFiltrarCompras.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFiltrarCompras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        painelFiltroHistorico.add(campoFiltroCompras);
        painelFiltroHistorico.add(btnFiltrarCompras);

        cabecalhoHistorico.add(lblTituloHistorico, BorderLayout.WEST);
        cabecalhoHistorico.add(painelFiltroHistorico, BorderLayout.EAST);

        DefaultTableModel modeloHistoricoCompras = new DefaultTableModel(
                new Object[][]{
                        {"14/10/2024", "Supermercado Pão de Açúcar", "26", "R$ 398,50", "No Teto"},
                        {"07/10/2024", "Atacadão Distribuidora", "44", "R$ 589,00", "Excedido"},
                        {"03/10/2024", "Feira do Produtor Local", "15", "R$ 165,00", "No Teto"}
                },
                new String[]{"DATA", "LOCAL / ESTABELECIMENTO", "QTD. ITENS", "VALOR TOTAL", "STATUS"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaHistoricoCompras = new JTable(modeloHistoricoCompras);
        tabelaHistoricoCompras.setRowHeight(40);
        tabelaHistoricoCompras.setShowVerticalLines(false);
        tabelaHistoricoCompras.setShowHorizontalLines(true);
        tabelaHistoricoCompras.setGridColor(new Color(241, 245, 249));
        tabelaHistoricoCompras.setIntercellSpacing(new Dimension(0, 1));
        tabelaHistoricoCompras.setSelectionBackground(new Color(248, 250, 252));
        tabelaHistoricoCompras.setSelectionForeground(new Color(15, 23, 42));
        tabelaHistoricoCompras.setFocusable(false);

        TableRowSorter<DefaultTableModel> ordenadorHistorico = new TableRowSorter<>(modeloHistoricoCompras);
        tabelaHistoricoCompras.setRowSorter(ordenadorHistorico);

        JTableHeader headerHistorico = tabelaHistoricoCompras.getTableHeader();
        headerHistorico.setReorderingAllowed(false);
        headerHistorico.setPreferredSize(new Dimension(100, 32));
        headerHistorico.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(241, 245, 249));
                lbl.setForeground(new Color(100, 116, 139));
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(1, 0, 1, 0, new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(0, 16, 0, 16)));
                if (column == 2 || column == 4) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        });

        tabelaHistoricoCompras.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                String texto = value == null ? "" : value.toString();

                if (column == 4) {
                    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 9));
                    wrapper.setBackground(Color.WHITE);
                    JLabel badge = new JLabel(texto, SwingConstants.CENTER);
                    badge.setOpaque(true);
                    badge.setFont(new Font("SansSerif", Font.BOLD, 10));
                    badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                    if ("Excedido".equalsIgnoreCase(texto)) {
                        badge.setBackground(new Color(254, 226, 226));
                        badge.setForeground(new Color(185, 28, 28));
                    } else {
                        badge.setBackground(new Color(209, 250, 229));
                        badge.setForeground(new Color(6, 95, 70));
                    }
                    wrapper.add(badge);
                    return wrapper;
                }

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.WHITE);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

                if (column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setForeground(new Color(71, 85, 105));
                } else if (column == 1) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                    lbl.setForeground(new Color(30, 41, 59));
                } else if (column == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setForeground(new Color(71, 85, 105));
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
                    lbl.setForeground(new Color(15, 23, 42));
                }
                return lbl;
            }
        });

        tabelaHistoricoCompras.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabelaHistoricoCompras.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabelaHistoricoCompras.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaHistoricoCompras.getColumnModel().getColumn(3).setPreferredWidth(140);
        tabelaHistoricoCompras.getColumnModel().getColumn(4).setPreferredWidth(120);

        JPanel painelTabelaHistoricoWrapper = new JPanel(new BorderLayout());
        painelTabelaHistoricoWrapper.setOpaque(false);
        painelTabelaHistoricoWrapper.add(headerHistorico, BorderLayout.NORTH);
        painelTabelaHistoricoWrapper.add(tabelaHistoricoCompras, BorderLayout.CENTER);

        // Rodapé do Histórico
        JPanel painelRodapeHistorico = new JPanel(new BorderLayout());
        painelRodapeHistorico.setBackground(Color.WHITE);
        painelRodapeHistorico.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(11, 16, 11, 16)));

        JLabel lblResumoRegistros = new JLabel("Exibindo 3 registros auditados em Outubro");
        lblResumoRegistros.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblResumoRegistros.setForeground(new Color(100, 116, 139));

        JButton btnVerHistoricoCompleto = new JButton("Ver histórico completo →");
        btnVerHistoricoCompleto.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVerHistoricoCompleto.setForeground(new Color(55, 48, 224));
        btnVerHistoricoCompleto.setBorderPainted(false);
        btnVerHistoricoCompleto.setContentAreaFilled(false);
        btnVerHistoricoCompleto.setFocusPainted(false);
        btnVerHistoricoCompleto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerHistoricoCompleto.setMargin(new Insets(0, 0, 0, 0));
        btnVerHistoricoCompleto.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Exibindo histórico completo de compras."));

        painelRodapeHistorico.add(lblResumoRegistros, BorderLayout.WEST);
        painelRodapeHistorico.add(btnVerHistoricoCompleto, BorderLayout.EAST);

        painelHistoricoCompras.add(cabecalhoHistorico, BorderLayout.NORTH);
        painelHistoricoCompras.add(painelTabelaHistoricoWrapper, BorderLayout.CENTER);
        painelHistoricoCompras.add(painelRodapeHistorico, BorderLayout.SOUTH);

        painelConteudoPrincipal.add(painelHistoricoCompras);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 5. RODAPÉ INFORMATIVO DE ATALHOS
        // ==========================================

        JPanel painelAtalhosRodape = new JPanel();
        painelAtalhosRodape.setLayout(new BoxLayout(painelAtalhosRodape, BoxLayout.Y_AXIS));
        painelAtalhosRodape.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelAtalhosRodape.setBackground(new Color(248, 250, 252));
        painelAtalhosRodape.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        painelAtalhosRodape.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JLabel lblAtalhosTeclado = new JLabel(
                "<html>"
                        + "<span style='background:#E2E8F0;color:#334155;font-weight:bold;'>&nbsp;F3&nbsp;</span> Nova Meta Orçamentária &nbsp;&nbsp;&nbsp;"
                        + "<span style='background:#E2E8F0;color:#334155;font-weight:bold;'>&nbsp;Ctrl+P&nbsp;</span> Imprimir / Exportar Relatório &nbsp;&nbsp;&nbsp;"
                        + "<span style='background:#E2E8F0;color:#334155;font-weight:bold;'>&nbsp;F5&nbsp;</span> Recalcular Indicadores &nbsp;&nbsp;&nbsp;"
                        + "<span style='background:#E2E8F0;color:#334155;font-weight:bold;'>&nbsp;Alt+D&nbsp;</span> Detalhamento por Item"
                        + "</html>");
        lblAtalhosTeclado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblAtalhosTeclado.setForeground(new Color(71, 85, 105));
        lblAtalhosTeclado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfoAmbiente = new JLabel("Ambiente Desktop Swing FlatLaf • Modo Integrado",
                FontIcon.of(FontAwesomeSolid.DESKTOP, 11, new Color(100, 116, 139)), JLabel.LEFT);
        lblInfoAmbiente.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfoAmbiente.setForeground(new Color(100, 116, 139));
        lblInfoAmbiente.setIconTextGap(6);
        lblInfoAmbiente.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelAtalhosRodape.add(lblAtalhosTeclado);
        painelAtalhosRodape.add(Box.createVerticalStrut(6));
        painelAtalhosRodape.add(lblInfoAmbiente);

        painelConteudoPrincipal.add(painelAtalhosRodape);

        // ==========================================
        // INTERATIVIDADE (METAS E FILTRO DE COMPRAS)
        // ==========================================

        NumberFormat formatoMoeda = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setMaximumFractionDigits(2);

        NumberFormat formatoPercentual = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
        formatoPercentual.setMinimumFractionDigits(1);
        formatoPercentual.setMaximumFractionDigits(1);

        btnConfigurarMetas.addActionListener(e -> {
            ModalConfigurarMetas modal = ModalConfigurarMetas.exibir(janelaPrincipal, tetoMensalAtual);

            if (modal.isConfirmado()) {
                tetoMensalAtual = modal.getTetoGlobalSalvo();
                double saldo = tetoMensalAtual - totalGastoAtual;
                double consumo = (totalGastoAtual / tetoMensalAtual) * 100.0;

                lblValorTeto.setText(formatoMoeda.format(tetoMensalAtual));
                lblConsumoPercentual.setText(formatoPercentual.format(consumo) + "%");
                barraConsumoOrcamento.setValue((int) Math.min(100, Math.round(consumo)));

                if (saldo >= 0) {
                    lblValorSaldo.setText(formatoMoeda.format(saldo));
                    lblValorSaldo.setForeground(new Color(21, 128, 61));
                    lblStatusSaldoValor.setText("Dentro do teto previsto");
                    lblStatusSaldoValor.setForeground(new Color(21, 128, 61));
                    barraConsumoOrcamento.setForeground(new Color(55, 48, 224));
                } else {
                    lblValorSaldo.setText("-" + formatoMoeda.format(Math.abs(saldo)));
                    lblValorSaldo.setForeground(new Color(185, 28, 28));
                    lblStatusSaldoValor.setText("Teto mensal excedido");
                    lblStatusSaldoValor.setForeground(new Color(185, 28, 28));
                    barraConsumoOrcamento.setForeground(new Color(185, 28, 28));
                }
            }
        });

        Runnable aplicarFiltroHistorico = () -> {
            String termo = campoFiltroCompras.getText().trim();
            if (termo.isEmpty()) {
                ordenadorHistorico.setRowFilter(null);
            } else {
                ordenadorHistorico.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(termo)));
            }
            lblResumoRegistros.setText("Exibindo " + tabelaHistoricoCompras.getRowCount() + " registros auditados em Outubro");
        };

        btnFiltrarCompras.addActionListener(e -> aplicarFiltroHistorico.run());
        campoFiltroCompras.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltroHistorico.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltroHistorico.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltroHistorico.run();
            }
        });

        // ==========================================
        // MENU LATERAL (BOTÕES NO PADRÃO ANTERIOR)
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

        FontIcon iconeMenuLista = FontIcon.of(FontAwesomeSolid.LIST, 11, new Color(51, 65, 85));
        FontIcon iconeMenuOrcamento = FontIcon.of(FontAwesomeSolid.WALLET, 11, Color.WHITE);
        FontIcon iconeMenuCatalogo = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 11, new Color(51, 65, 85));
        FontIcon iconeMenuListasSalvas = FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85));

        Component espacoTopoMenuLateral = Box.createVerticalStrut(8);
        GridBagConstraints gbcEspacoTopoMenuLateral = new GridBagConstraints();
        gbcEspacoTopoMenuLateral.anchor = GridBagConstraints.NORTH;
        gbcEspacoTopoMenuLateral.insets = new Insets(0, 0, 5, 0);
        gbcEspacoTopoMenuLateral.gridx = 0;
        gbcEspacoTopoMenuLateral.gridy = 1;
        painelMenuLateral.add(espacoTopoMenuLateral, gbcEspacoTopoMenuLateral);

        JButton btnMenuListaAtual = new JButton("Lista Atual", iconeMenuLista);
        btnMenuListaAtual.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuListaAtual.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuListaAtual.setBackground(Color.WHITE);
        btnMenuListaAtual.setForeground(new Color(51, 65, 85));
        btnMenuListaAtual.setFocusPainted(false);
        btnMenuListaAtual.setContentAreaFilled(false);
        btnMenuListaAtual.setOpaque(true);
        btnMenuListaAtual.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuListaAtual.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnMenuListaAtual.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Navegar para: Lista Atual"));
        GridBagConstraints gbcBtnListaAtual = new GridBagConstraints();
        gbcBtnListaAtual.anchor = GridBagConstraints.NORTH;
        gbcBtnListaAtual.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnListaAtual.insets = new Insets(0, 0, 6, 0);
        gbcBtnListaAtual.gridx = 0;
        gbcBtnListaAtual.gridy = 2;
        painelMenuLateral.add(btnMenuListaAtual, gbcBtnListaAtual);

        // Item ativo no menu lateral: Orçamento & Custos
        JButton btnMenuOrcamento = new JButton("Orçamento & Custos", iconeMenuOrcamento);
        btnMenuOrcamento.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuOrcamento.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuOrcamento.setBackground(new Color(55, 48, 224));
        btnMenuOrcamento.setForeground(Color.WHITE);
        btnMenuOrcamento.setFocusPainted(false);
        btnMenuOrcamento.setBorderPainted(false);
        btnMenuOrcamento.setContentAreaFilled(false);
        btnMenuOrcamento.setOpaque(true);
        btnMenuOrcamento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuOrcamento.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
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
            // Usa o ícone vetorial caso o PNG não esteja na pasta
        }

        BufferedImage logoGerada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = logoGerada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradiente = new GradientPaint(
                0, 0, new Color(55, 48, 224),
                largura, altura, new Color(16, 185, 129));
        g2.setPaint(gradiente);
        g2.fill(new RoundRectangle2D.Double(1, 1, largura - 2, altura - 2, 10, 10));

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Double(8, 8, largura - 16, altura - 14, 4, 4));
        g2.fill(new RoundRectangle2D.Double(12, 5, largura - 24, 5, 3, 3));

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
}