package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import com.formdev.flatlaf.FlatLightLaf;

public class PainelListasSalvas {

    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final Color COR_FUNDO_PAGINA = new Color(241, 245, 249);
    private static final Color COR_AZUL_PRIMARIO = new Color(55, 48, 224);
    private static final Color COR_TEXTO_ESCURO = new Color(15, 23, 42);
    private static final Color COR_TEXTO_MEDIO = new Color(51, 65, 85);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(100, 116, 139);
    private static final Color COR_BORDA = new Color(226, 232, 240);

    private final List<RegistroArquivoLista> todosRegistros = new ArrayList<>();
    private final List<RegistroArquivoLista> registrosFiltrados = new ArrayList<>();
    private String filtroTipoAtivo = "Todos";

    private DefaultTableModel modeloTabelaRegistros;
    private JTable tabelaRegistros;
    private JLabel lblContagemDisponiveis;

    // Abas de filtro
    private JButton btnAbaTodos;
    private JButton btnAbaModelos;
    private JButton btnAbaArquivados;

    // Componentes do painel lateral de detalhes
    private JLabel lblDetalheNomeArquivo;
    private JLabel lblDetalheTotalEst;
    private JLabel lblDetalheQtdItens;
    private JLabel lblDetalheDataHora;
    private JPanel painelLinhasPreview;
    private JLabel lblDetalheItensRestantes;

    private static class ItemPreviewArquivo {
        final String descricao;
        final double preco;

        ItemPreviewArquivo(String descricao, double preco) {
            this.descricao = descricao;
            this.preco = preco;
        }
    }

    private static class RegistroArquivoLista {
        String nomeArquivo;
        final String tipo; // "Modelo" ou "Arquivo TXT"
        final String data;
        final String hora;
        final int totalItens;
        final double totalEstimado;
        final List<ItemPreviewArquivo> itensPreview;

        RegistroArquivoLista(String nomeArquivo, String tipo, String data, String hora,
                             int totalItens, double totalEstimado, List<ItemPreviewArquivo> itensPreview) {
            this.nomeArquivo = nomeArquivo;
            this.tipo = tipo;
            this.data = data;
            this.hora = hora;
            this.totalItens = totalItens;
            this.totalEstimado = totalEstimado;
            this.itensPreview = itensPreview;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            new PainelListasSalvas().exibirJanela();
        });
    }

    public void exibirJanela() {
        carregarRegistrosIniciais();

        JFrame janelaPrincipal = new JFrame("Lista de Compras Inteligente — Listas Salvas");
        janelaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaPrincipal.setSize(1180, 820);
        janelaPrincipal.setLocationRelativeTo(null);

        // ==========================================
        // BARRA SUPERIOR: LOGO + AÇÕES DE ARQUIVO
        // ==========================================

        JPanel painelBarraSuperior = new JPanel();
        painelBarraSuperior.setLayout(new BoxLayout(painelBarraSuperior, BoxLayout.LINE_AXIS));
        painelBarraSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarraSuperior.setBackground(Color.WHITE);
        painelBarraSuperior.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel lblLogoSistema = new JLabel();
        lblLogoSistema.setIcon(carregarLogoPequena("/images/logo-smartlist.png", 34, 34));
        lblLogoSistema.setToolTipText("smartList — Organização Inteligente");
        lblLogoSistema.setAlignmentY(Component.CENTER_ALIGNMENT);
        painelBarraSuperior.add(lblLogoSistema);

        painelBarraSuperior.add(Box.createRigidArea(new Dimension(14, 20)));

        FontIcon iconeImportarLista = FontIcon.of(FontAwesomeSolid.FILE_IMPORT, 11, new Color(51, 65, 85));
        JButton btnImportarLista = new JButton("Importar Lista (.txt)", iconeImportarLista);
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

        FontIcon iconeExportarLista = FontIcon.of(FontAwesomeSolid.FILE_EXPORT, 11, new Color(51, 65, 85));
        JButton btnExportarLista = new JButton("Exportar Lista (.txt)", iconeExportarLista);
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
        // CONTEÚDO PRINCIPAL ROLÁVEL
        // ==========================================

        JPanel painelConteudoPrincipal = new JPanel();
        painelConteudoPrincipal.setLayout(new BoxLayout(painelConteudoPrincipal, BoxLayout.Y_AXIS));
        painelConteudoPrincipal.setOpaque(true);
        painelConteudoPrincipal.setBackground(COR_FUNDO_PAGINA);
        painelConteudoPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 4, 18, 14));

        // ==========================================
        // 1. CARD SUPERIOR: DIRETÓRIO, AÇÕES E ABAS
        // ==========================================

        JPanel painelCardTopoListas = new JPanel();
        painelCardTopoListas.setLayout(new BoxLayout(painelCardTopoListas, BoxLayout.Y_AXIS));
        painelCardTopoListas.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCardTopoListas.setBackground(Color.WHITE);
        painelCardTopoListas.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        painelCardTopoListas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));

        // Linha 1: Título + Diretório + Botões (+ Novo Modelo, Importar, Atualizar)
        JPanel linhaCabecalhoDiretorio = new JPanel(new BorderLayout(12, 0));
        linhaCabecalhoDiretorio.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaCabecalhoDiretorio.setBackground(Color.WHITE);
        linhaCabecalhoDiretorio.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        linhaCabecalhoDiretorio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JPanel infoDiretorioEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        infoDiretorioEsquerda.setOpaque(false);

        JLabel lblTituloModelos = new JLabel("Modelos & Listas Salvas",
                FontIcon.of(FontAwesomeSolid.BOOKMARK, 14, COR_AZUL_PRIMARIO), JLabel.LEFT);
        lblTituloModelos.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloModelos.setForeground(COR_TEXTO_ESCURO);
        lblTituloModelos.setIconTextGap(8);

        JLabel lblSeparadorTopo = new JLabel("|");
        lblSeparadorTopo.setForeground(new Color(203, 213, 225));

        infoDiretorioEsquerda.add(lblTituloModelos);
        infoDiretorioEsquerda.add(lblSeparadorTopo);

        JPanel acoesDiretorioDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoesDiretorioDireita.setOpaque(false);

        FontIcon iconeNovoModelo = FontIcon.of(FontAwesomeSolid.PLUS, 11, Color.WHITE);

        FontIcon iconeImportarArquivo = FontIcon.of(FontAwesomeSolid.FILE_IMPORT, 11, new Color(51, 65, 85));
        JButton btnImportarArquivo = new JButton("Importar", iconeImportarArquivo);
        btnImportarArquivo.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnImportarArquivo.setBackground(Color.WHITE);
        btnImportarArquivo.setForeground(new Color(51, 65, 85));
        btnImportarArquivo.setFocusPainted(false);
        btnImportarArquivo.setContentAreaFilled(false);
        btnImportarArquivo.setOpaque(true);
        btnImportarArquivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportarArquivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnImportarArquivo.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Selecionar arquivo .txt do diretório local..."));

        FontIcon iconeAtualizarDiretorio = FontIcon.of(FontAwesomeSolid.SYNC_ALT, 11, new Color(51, 65, 85));
        JButton btnAtualizarDiretorio = new JButton(iconeAtualizarDiretorio);
        btnAtualizarDiretorio.setToolTipText("Atualizar arquivos do diretório");
        btnAtualizarDiretorio.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAtualizarDiretorio.setBackground(Color.WHITE);
        btnAtualizarDiretorio.setForeground(new Color(51, 65, 85));
        btnAtualizarDiretorio.setFocusPainted(false);
        btnAtualizarDiretorio.setContentAreaFilled(false);
        btnAtualizarDiretorio.setOpaque(true);
        btnAtualizarDiretorio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtualizarDiretorio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        acoesDiretorioDireita.add(btnImportarArquivo);
        acoesDiretorioDireita.add(btnAtualizarDiretorio);

        linhaCabecalhoDiretorio.add(infoDiretorioEsquerda, BorderLayout.WEST);
        linhaCabecalhoDiretorio.add(acoesDiretorioDireita, BorderLayout.EAST);

        // Linha 2: Abas de filtro + Campo Filtrar arquivos...
        JPanel linhaAbasEBusca = new JPanel(new BorderLayout(12, 0));
        linhaAbasEBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaAbasEBusca.setBackground(new Color(248, 250, 252));
        linhaAbasEBusca.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        linhaAbasEBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel painelAbasTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelAbasTipo.setOpaque(false);

        FontIcon iconeAbaTodos = FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 11, Color.WHITE);
        btnAbaTodos = new JButton("Todos os Registros (7)", iconeAbaTodos);
        btnAbaTodos.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAbaTodos.setBackground(new Color(55, 48, 224));
        btnAbaTodos.setForeground(Color.WHITE);
        btnAbaTodos.setFocusPainted(false);
        btnAbaTodos.setBorderPainted(false);
        btnAbaTodos.setContentAreaFilled(false);
        btnAbaTodos.setOpaque(true);
        btnAbaTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbaTodos.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));

        FontIcon iconeAbaModelos = FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85));
        btnAbaModelos = new JButton("Modelos (4)", iconeAbaModelos);
        btnAbaModelos.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAbaModelos.setBackground(Color.WHITE);
        btnAbaModelos.setForeground(new Color(51, 65, 85));
        btnAbaModelos.setFocusPainted(false);
        btnAbaModelos.setContentAreaFilled(false);
        btnAbaModelos.setOpaque(true);
        btnAbaModelos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbaModelos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        FontIcon iconeAbaArquivados = FontIcon.of(FontAwesomeSolid.HISTORY, 11, new Color(51, 65, 85));
        btnAbaArquivados = new JButton("Arquivados TXT (3)", iconeAbaArquivados);
        btnAbaArquivados.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAbaArquivados.setBackground(Color.WHITE);
        btnAbaArquivados.setForeground(new Color(51, 65, 85));
        btnAbaArquivados.setFocusPainted(false);
        btnAbaArquivados.setContentAreaFilled(false);
        btnAbaArquivados.setOpaque(true);
        btnAbaArquivados.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAbaArquivados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        painelAbasTipo.add(btnAbaTodos);
        painelAbasTipo.add(btnAbaModelos);
        painelAbasTipo.add(btnAbaArquivados);

        JPanel boxFiltroArquivos = new JPanel(new BorderLayout(8, 0));
        boxFiltroArquivos.setBackground(Color.WHITE);
        boxFiltroArquivos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        boxFiltroArquivos.setPreferredSize(new Dimension(235, 30));

        JLabel lblIconeLupaArquivo = new JLabel(FontIcon.of(FontAwesomeSolid.SEARCH, 12, COR_TEXTO_SECUNDARIO));
        JTextField campoFiltroArquivos = new JTextField();
        campoFiltroArquivos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoFiltroArquivos.setBorder(null);
        campoFiltroArquivos.setOpaque(false);
        aplicarPlaceholder(campoFiltroArquivos, "Filtrar arquivos...");

        boxFiltroArquivos.add(lblIconeLupaArquivo, BorderLayout.WEST);
        boxFiltroArquivos.add(campoFiltroArquivos, BorderLayout.CENTER);

        linhaAbasEBusca.add(painelAbasTipo, BorderLayout.WEST);
        linhaAbasEBusca.add(boxFiltroArquivos, BorderLayout.EAST);

        painelCardTopoListas.add(linhaCabecalhoDiretorio);
        painelCardTopoListas.add(linhaAbasEBusca);

        painelConteudoPrincipal.add(painelCardTopoListas);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 2. CORPO CENTRAL (TABELA + DETALHES DO REGISTRO)
        // ==========================================

        JPanel painelSplitCentral = new JPanel(new BorderLayout(14, 0));
        painelSplitCentral.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSplitCentral.setOpaque(false);

        // --- COLUNA ESQUERDA: LISTA DE MODELOS E ARQUIVOS TXT ---
        JPanel cardListaArquivos = new JPanel(new BorderLayout());
        cardListaArquivos.setBackground(Color.WHITE);
        cardListaArquivos.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        JPanel cabecalhoTabelaArquivos = new JPanel(new BorderLayout());
        cabecalhoTabelaArquivos.setBackground(new Color(248, 250, 252));
        cabecalhoTabelaArquivos.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(11, 16, 11, 16)));

        JLabel lblTituloTabelaArquivos = new JLabel("Lista de Modelos e Arquivos TXT");
        lblTituloTabelaArquivos.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTituloTabelaArquivos.setForeground(COR_TEXTO_ESCURO);

        lblContagemDisponiveis = new JLabel("7 itens disponíveis");
        lblContagemDisponiveis.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblContagemDisponiveis.setForeground(COR_TEXTO_SECUNDARIO);

        cabecalhoTabelaArquivos.add(lblTituloTabelaArquivos, BorderLayout.WEST);
        cabecalhoTabelaArquivos.add(lblContagemDisponiveis, BorderLayout.EAST);

        modeloTabelaRegistros = new DefaultTableModel(
                new String[]{"Nome", "Tipo", "Data", "Itens", "Total Est."},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaRegistros = new JTable(modeloTabelaRegistros);
        tabelaRegistros.setRowHeight(46);
        tabelaRegistros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaRegistros.setShowVerticalLines(false);
        tabelaRegistros.setShowHorizontalLines(true);
        tabelaRegistros.setGridColor(COR_FUNDO_PAGINA);
        tabelaRegistros.setIntercellSpacing(new Dimension(0, 1));
        tabelaRegistros.setSelectionBackground(new Color(238, 242, 255));
        tabelaRegistros.setSelectionForeground(COR_TEXTO_ESCURO);

        JTableHeader headerTabela = tabelaRegistros.getTableHeader();
        headerTabela.setReorderingAllowed(false);
        headerTabela.setPreferredSize(new Dimension(100, 32));
        headerTabela.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COR_FUNDO_PAGINA);
                lbl.setForeground(COR_TEXTO_SECUNDARIO);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, COR_BORDA),
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)));

                if (column == 1 || column == 2 || column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 4) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        });

        tabelaRegistros.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Color fundoLinha = isSelected ? new Color(238, 242, 255) : Color.WHITE;
                RegistroArquivoLista reg = row < registrosFiltrados.size() ? registrosFiltrados.get(row) : null;

                // Coluna 0: Ícone + Nome do arquivo .txt
                if (column == 0 && reg != null) {
                    JPanel celulaArquivo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 13));
                    celulaArquivo.setBackground(fundoLinha);
                    Color corIcone = isSelected ? COR_AZUL_PRIMARIO : COR_TEXTO_SECUNDARIO;
                    JLabel iconeDoc = new JLabel(FontIcon.of(FontAwesomeSolid.FILE_ALT, 13, corIcone));
                    JLabel lblNome = new JLabel(reg.nomeArquivo);
                    lblNome.setFont(new Font("Monospaced", isSelected ? Font.BOLD : Font.PLAIN, 12));
                    lblNome.setForeground(COR_TEXTO_ESCURO);
                    celulaArquivo.add(iconeDoc);
                    celulaArquivo.add(lblNome);
                    return celulaArquivo;
                }

                // Coluna 1: Badge Tipo ("Modelo" ou "Arquivo TXT")
                if (column == 1 && reg != null) {
                    JPanel wrapperTipo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
                    wrapperTipo.setBackground(fundoLinha);
                    JLabel badgeTipo = new JLabel(reg.tipo, SwingConstants.CENTER);
                    badgeTipo.setOpaque(true);
                    badgeTipo.setFont(new Font("SansSerif", Font.BOLD, 10));
                    badgeTipo.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
                    if ("Modelo".equalsIgnoreCase(reg.tipo)) {
                        badgeTipo.setBackground(new Color(224, 231, 255));
                        badgeTipo.setForeground(COR_AZUL_PRIMARIO);
                    } else {
                        badgeTipo.setBackground(COR_FUNDO_PAGINA);
                        badgeTipo.setForeground(COR_TEXTO_MEDIO);
                    }
                    wrapperTipo.add(badgeTipo);
                    return wrapperTipo;
                }

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(fundoLinha);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

                if (column == 2) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setForeground(COR_TEXTO_MEDIO);
                } else if (column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
                    lbl.setForeground(COR_TEXTO_ESCURO);
                } else if (column == 4) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
                    lbl.setForeground(COR_TEXTO_ESCURO);
                }
                return lbl;
            }
        });

        tabelaRegistros.getColumnModel().getColumn(0).setPreferredWidth(255);
        tabelaRegistros.getColumnModel().getColumn(1).setPreferredWidth(105);
        tabelaRegistros.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaRegistros.getColumnModel().getColumn(3).setPreferredWidth(62);
        tabelaRegistros.getColumnModel().getColumn(4).setPreferredWidth(105);

        JScrollPane scrollTabelaArquivos = new JScrollPane(tabelaRegistros);
        scrollTabelaArquivos.setBorder(null);
        scrollTabelaArquivos.getViewport().setBackground(Color.WHITE);

        JPanel rodapeTabelaArquivos = new JPanel(new BorderLayout());
        rodapeTabelaArquivos.setBackground(new Color(248, 250, 252));
        rodapeTabelaArquivos.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        cardListaArquivos.add(cabecalhoTabelaArquivos, BorderLayout.NORTH);
        cardListaArquivos.add(scrollTabelaArquivos, BorderLayout.CENTER);
        cardListaArquivos.add(rodapeTabelaArquivos, BorderLayout.SOUTH);

        // --- COLUNA DIREITA: DETALHES DO REGISTRO ---
        JPanel cardDetalhesRegistro = new JPanel(new BorderLayout(0, 12));
        cardDetalhesRegistro.setBackground(Color.WHITE);
        cardDetalhesRegistro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)));
        cardDetalhesRegistro.setPreferredSize(new Dimension(345, 520));

        JPanel topoDetalhesPainel = new JPanel();
        topoDetalhesPainel.setLayout(new BoxLayout(topoDetalhesPainel, BoxLayout.Y_AXIS));
        topoDetalhesPainel.setOpaque(false);

        JPanel linhaTituloPrecoDetalhe = new JPanel(new BorderLayout(8, 0));
        linhaTituloPrecoDetalhe.setOpaque(false);
        linhaTituloPrecoDetalhe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel blocoEsquerdaDetalhe = new JPanel();
        blocoEsquerdaDetalhe.setLayout(new BoxLayout(blocoEsquerdaDetalhe, BoxLayout.Y_AXIS));
        blocoEsquerdaDetalhe.setOpaque(false);

        JLabel lblSubtituloDetalhe = new JLabel("DETALHES DO REGISTRO");
        lblSubtituloDetalhe.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblSubtituloDetalhe.setForeground(COR_AZUL_PRIMARIO);

        lblDetalheNomeArquivo = new JLabel("compra_mensal_completa.txt");
        lblDetalheNomeArquivo.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblDetalheNomeArquivo.setForeground(COR_TEXTO_ESCURO);

        blocoEsquerdaDetalhe.add(lblSubtituloDetalhe);
        blocoEsquerdaDetalhe.add(Box.createVerticalStrut(3));
        blocoEsquerdaDetalhe.add(lblDetalheNomeArquivo);

        JPanel blocoDireitaDetalhe = new JPanel();
        blocoDireitaDetalhe.setLayout(new BoxLayout(blocoDireitaDetalhe, BoxLayout.Y_AXIS));
        blocoDireitaDetalhe.setOpaque(false);

        lblDetalheTotalEst = new JLabel("R$ 650,00");
        lblDetalheTotalEst.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblDetalheTotalEst.setForeground(COR_AZUL_PRIMARIO);
        lblDetalheTotalEst.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblDetalheQtdItens = new JLabel("48 itens registrados");
        lblDetalheQtdItens.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDetalheQtdItens.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalheQtdItens.setAlignmentX(Component.RIGHT_ALIGNMENT);

        blocoDireitaDetalhe.add(lblDetalheTotalEst);
        blocoDireitaDetalhe.add(Box.createVerticalStrut(2));
        blocoDireitaDetalhe.add(lblDetalheQtdItens);

        linhaTituloPrecoDetalhe.add(blocoEsquerdaDetalhe, BorderLayout.CENTER);
        linhaTituloPrecoDetalhe.add(blocoDireitaDetalhe, BorderLayout.EAST);

        lblDetalheDataHora = new JLabel("14/10/2024 - 10:15");
        lblDetalheDataHora.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDetalheDataHora.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalheDataHora.setAlignmentX(Component.LEFT_ALIGNMENT);

        topoDetalhesPainel.add(linhaTituloPrecoDetalhe);
        topoDetalhesPainel.add(Box.createVerticalStrut(4));
        topoDetalhesPainel.add(lblDetalheDataHora);

        // Caixa Central de Pré-visualização dos Itens do Arquivo
        JPanel boxPreviewConteudo = new JPanel(new BorderLayout(0, 8));
        boxPreviewConteudo.setBackground(COR_FUNDO_PAGINA);
        boxPreviewConteudo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        painelLinhasPreview = new JPanel();
        painelLinhasPreview.setLayout(new BoxLayout(painelLinhasPreview, BoxLayout.Y_AXIS));
        painelLinhasPreview.setOpaque(false);

        lblDetalheItensRestantes = new JLabel("... e mais 42 itens salvos no arquivo.");
        lblDetalheItensRestantes.setFont(new Font("Monospaced", Font.ITALIC, 11));
        lblDetalheItensRestantes.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalheItensRestantes.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, COR_BORDA),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)));

        boxPreviewConteudo.add(painelLinhasPreview, BorderLayout.NORTH);
        boxPreviewConteudo.add(lblDetalheItensRestantes, BorderLayout.SOUTH);

        // Botões Inferiores do Card Detalhes
        JPanel painelBotoesDetalhe = new JPanel();
        painelBotoesDetalhe.setLayout(new BoxLayout(painelBotoesDetalhe, BoxLayout.Y_AXIS));
        painelBotoesDetalhe.setOpaque(false);

        FontIcon iconeCarregarNaLista = FontIcon.of(FontAwesomeSolid.PLAY, 11, Color.WHITE);
        JButton btnCarregarNaLista = new JButton("Carregar na Lista Atual", iconeCarregarNaLista);
        btnCarregarNaLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCarregarNaLista.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCarregarNaLista.setBackground(new Color(55, 48, 224));
        btnCarregarNaLista.setForeground(Color.WHITE);
        btnCarregarNaLista.setFocusPainted(false);
        btnCarregarNaLista.setBorderPainted(false);
        btnCarregarNaLista.setContentAreaFilled(false);
        btnCarregarNaLista.setOpaque(true);
        btnCarregarNaLista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCarregarNaLista.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        btnCarregarNaLista.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnCarregarNaLista.setPreferredSize(new Dimension(310, 36));

        JPanel linhaBotoesSecundarios = new JPanel(new GridBagLayout());
        linhaBotoesSecundarios.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotoesSecundarios.setOpaque(false);
        linhaBotoesSecundarios.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        FontIcon iconeEditarRegistro = FontIcon.of(FontAwesomeSolid.EDIT, 11, new Color(51, 65, 85));

        FontIcon iconeCopiarRegistro = FontIcon.of(FontAwesomeSolid.COPY, 11, new Color(51, 65, 85));
        JButton btnCopiarRegistro = new JButton("Copiar", iconeCopiarRegistro);
        btnCopiarRegistro.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCopiarRegistro.setBackground(Color.WHITE);
        btnCopiarRegistro.setForeground(new Color(51, 65, 85));
        btnCopiarRegistro.setFocusPainted(false);
        btnCopiarRegistro.setContentAreaFilled(false);
        btnCopiarRegistro.setOpaque(true);
        btnCopiarRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCopiarRegistro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        FontIcon iconeExcluirRegistro = FontIcon.of(FontAwesomeSolid.TRASH_ALT, 11, new Color(185, 28, 28));
        JButton btnExcluirRegistro = new JButton(iconeExcluirRegistro);
        btnExcluirRegistro.setToolTipText("Excluir registro");
        btnExcluirRegistro.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnExcluirRegistro.setBackground(new Color(254, 242, 242));
        btnExcluirRegistro.setForeground(new Color(185, 28, 28));
        btnExcluirRegistro.setFocusPainted(false);
        btnExcluirRegistro.setContentAreaFilled(false);
        btnExcluirRegistro.setOpaque(true);
        btnExcluirRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExcluirRegistro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        GridBagConstraints gbcBtnCopiar = new GridBagConstraints();
        gbcBtnCopiar.gridx = 1;
        gbcBtnCopiar.gridy = 0;
        gbcBtnCopiar.weightx = 1.0;
        gbcBtnCopiar.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnCopiar.insets = new Insets(0, 0, 0, 6);
        linhaBotoesSecundarios.add(btnCopiarRegistro, gbcBtnCopiar);

        GridBagConstraints gbcBtnExcluir = new GridBagConstraints();
        gbcBtnExcluir.gridx = 2;
        gbcBtnExcluir.gridy = 0;
        gbcBtnExcluir.weightx = 0.0;
        gbcBtnExcluir.fill = GridBagConstraints.BOTH;
        linhaBotoesSecundarios.add(btnExcluirRegistro, gbcBtnExcluir);

        painelBotoesDetalhe.add(btnCarregarNaLista);
        painelBotoesDetalhe.add(Box.createVerticalStrut(8));
        painelBotoesDetalhe.add(linhaBotoesSecundarios);

        cardDetalhesRegistro.add(topoDetalhesPainel, BorderLayout.NORTH);
        cardDetalhesRegistro.add(boxPreviewConteudo, BorderLayout.CENTER);
        cardDetalhesRegistro.add(painelBotoesDetalhe, BorderLayout.SOUTH);

        painelSplitCentral.add(cardListaArquivos, BorderLayout.CENTER);
        painelSplitCentral.add(cardDetalhesRegistro, BorderLayout.EAST);

        painelConteudoPrincipal.add(painelSplitCentral);

        // ==========================================
        // INTERATIVIDADE, FILTROS E AÇÕES
        // ==========================================

        Runnable atualizarTabelaEAbas = () -> {
            int qtdModelos = 0;
            int qtdArquivados = 0;
            for (RegistroArquivoLista r : todosRegistros) {
                if ("Modelo".equalsIgnoreCase(r.tipo)) {
                    qtdModelos++;
                } else {
                    qtdArquivados++;
                }
            }

            btnAbaTodos.setText("Todos os Registros (" + todosRegistros.size() + ")");
            btnAbaModelos.setText("Modelos (" + qtdModelos + ")");
            btnAbaArquivados.setText("Arquivados TXT (" + qtdArquivados + ")");

            String termo = campoFiltroArquivos.getText().trim().toLowerCase();
            registrosFiltrados.clear();
            modeloTabelaRegistros.setRowCount(0);

            NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BR);
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);

            for (RegistroArquivoLista r : todosRegistros) {
                boolean bateTipo = "Todos".equals(filtroTipoAtivo) || r.tipo.equalsIgnoreCase(filtroTipoAtivo);
                boolean bateBusca = termo.isEmpty() || r.nomeArquivo.toLowerCase().contains(termo);
                if (bateTipo && bateBusca) {
                    registrosFiltrados.add(r);
                    modeloTabelaRegistros.addRow(new Object[]{
                            r.nomeArquivo,
                            r.tipo,
                            r.data,
                            r.totalItens,
                            "R$ " + nf.format(r.totalEstimado)
                    });
                }
            }

            lblContagemDisponiveis.setText(registrosFiltrados.size() + " itens disponíveis");
            if (!registrosFiltrados.isEmpty()) {
                tabelaRegistros.setRowSelectionInterval(0, 0);
                atualizarPainelDetalhes(registrosFiltrados.get(0));
            }
        };

        tabelaRegistros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabelaRegistros.getSelectedRow();
                if (linha >= 0 && linha < registrosFiltrados.size()) {
                    atualizarPainelDetalhes(registrosFiltrados.get(linha));
                }
            }
        });

        btnAbaTodos.addActionListener(e -> {
            filtroTipoAtivo = "Todos";
            btnAbaTodos.setBackground(new Color(55, 48, 224));
            btnAbaTodos.setForeground(Color.WHITE);
            btnAbaTodos.setBorderPainted(false);
            btnAbaTodos.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
            btnAbaTodos.setIcon(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 11, Color.WHITE));

            btnAbaModelos.setBackground(Color.WHITE);
            btnAbaModelos.setForeground(new Color(51, 65, 85));
            btnAbaModelos.setBorderPainted(true);
            btnAbaModelos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaModelos.setIcon(FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85)));

            btnAbaArquivados.setBackground(Color.WHITE);
            btnAbaArquivados.setForeground(new Color(51, 65, 85));
            btnAbaArquivados.setBorderPainted(true);
            btnAbaArquivados.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaArquivados.setIcon(FontIcon.of(FontAwesomeSolid.HISTORY, 11, new Color(51, 65, 85)));

            atualizarTabelaEAbas.run();
        });

        btnAbaModelos.addActionListener(e -> {
            filtroTipoAtivo = "Modelo";
            btnAbaTodos.setBackground(Color.WHITE);
            btnAbaTodos.setForeground(new Color(51, 65, 85));
            btnAbaTodos.setBorderPainted(true);
            btnAbaTodos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaTodos.setIcon(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 11, new Color(51, 65, 85)));

            btnAbaModelos.setBackground(new Color(55, 48, 224));
            btnAbaModelos.setForeground(Color.WHITE);
            btnAbaModelos.setBorderPainted(false);
            btnAbaModelos.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
            btnAbaModelos.setIcon(FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, Color.WHITE));

            btnAbaArquivados.setBackground(Color.WHITE);
            btnAbaArquivados.setForeground(new Color(51, 65, 85));
            btnAbaArquivados.setBorderPainted(true);
            btnAbaArquivados.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaArquivados.setIcon(FontIcon.of(FontAwesomeSolid.HISTORY, 11, new Color(51, 65, 85)));

            atualizarTabelaEAbas.run();
        });

        btnAbaArquivados.addActionListener(e -> {
            filtroTipoAtivo = "Arquivo TXT";
            btnAbaTodos.setBackground(Color.WHITE);
            btnAbaTodos.setForeground(new Color(51, 65, 85));
            btnAbaTodos.setBorderPainted(true);
            btnAbaTodos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaTodos.setIcon(FontIcon.of(FontAwesomeSolid.FOLDER_OPEN, 11, new Color(51, 65, 85)));

            btnAbaModelos.setBackground(Color.WHITE);
            btnAbaModelos.setForeground(new Color(51, 65, 85));
            btnAbaModelos.setBorderPainted(true);
            btnAbaModelos.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225)),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)));
            btnAbaModelos.setIcon(FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85)));

            btnAbaArquivados.setBackground(new Color(55, 48, 224));
            btnAbaArquivados.setForeground(Color.WHITE);
            btnAbaArquivados.setBorderPainted(false);
            btnAbaArquivados.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
            btnAbaArquivados.setIcon(FontIcon.of(FontAwesomeSolid.HISTORY, 11, Color.WHITE));

            atualizarTabelaEAbas.run();
        });

        campoFiltroArquivos.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabelaEAbas.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabelaEAbas.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabelaEAbas.run();
            }
        });

        btnAtualizarDiretorio.addActionListener(e -> atualizarTabelaEAbas.run());

        btnCarregarNaLista.addActionListener(e -> {
            int linha = tabelaRegistros.getSelectedRow();
            if (linha >= 0 && linha < registrosFiltrados.size()) {
                RegistroArquivoLista r = registrosFiltrados.get(linha);
                JOptionPane.showMessageDialog(janelaPrincipal,
                        "Registro \"" + r.nomeArquivo + "\" (" + r.totalItens + " itens) carregado na Lista Atual!");
            }
        });

        btnCopiarRegistro.addActionListener(e -> {
            int linha = tabelaRegistros.getSelectedRow();
            if (linha >= 0 && linha < registrosFiltrados.size()) {
                RegistroArquivoLista r = registrosFiltrados.get(linha);
                String nomeCopia = r.nomeArquivo.replace(".txt", "_copia.txt");
                todosRegistros.add(0, new RegistroArquivoLista(
                        nomeCopia, r.tipo, "26/11/2026", "23:15", r.totalItens, r.totalEstimado, r.itensPreview));
                atualizarTabelaEAbas.run();
            }
        });

        btnExcluirRegistro.addActionListener(e -> {
            int linha = tabelaRegistros.getSelectedRow();
            if (linha >= 0 && linha < registrosFiltrados.size()) {
                RegistroArquivoLista r = registrosFiltrados.get(linha);
                int confirm = JOptionPane.showConfirmDialog(janelaPrincipal,
                        "Deseja excluir permanentemente \"" + r.nomeArquivo + "\"?",
                        "Excluir Registro", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    todosRegistros.remove(r);
                    atualizarTabelaEAbas.run();
                }
            }
        });

        // Atalho Ctrl+N para Novo Modelo
        JRootPane rootPane = janelaPrincipal.getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "novoModelo");
        am.put("novoModelo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNovoModelo.doClick();
            }
        });

        // Inicializa tabela e painel de detalhes
        atualizarTabelaEAbas.run();

        // ==========================================
        // MENU LATERAL (COM "LISTAS SALVAS" ATIVO)
        // ==========================================

        JPanel painelMenuLateral = new JPanel();
        painelMenuLateral.setBackground(Color.WHITE);
        painelMenuLateral.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 0, 1, COR_BORDA),
                BorderFactory.createEmptyBorder(-25, 12, 10, 12)
        ));

        GridBagLayout layoutMenuLateral = new GridBagLayout();
        layoutMenuLateral.columnWidths = new int[]{165, 0};
        layoutMenuLateral.rowHeights = new int[]{0, 0, 28, 28, 28, 28, 0};
        layoutMenuLateral.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        layoutMenuLateral.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        painelMenuLateral.setLayout(layoutMenuLateral);

        Component espacoTopoMenuLateral = Box.createVerticalStrut(8);
        GridBagConstraints gbcEspacoTopo = new GridBagConstraints();
        gbcEspacoTopo.anchor = GridBagConstraints.NORTH;
        gbcEspacoTopo.insets = new Insets(0, 0, 5, 0);
        gbcEspacoTopo.gridx = 0;
        gbcEspacoTopo.gridy = 1;
        painelMenuLateral.add(espacoTopoMenuLateral, gbcEspacoTopo);

        FontIcon iconeMenuListaAtual = FontIcon.of(FontAwesomeSolid.LIST, 11, new Color(51, 65, 85));
        JButton btnMenuListaAtual = new JButton("Lista Atual", iconeMenuListaAtual);
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
        GridBagConstraints gbcBtn1 = new GridBagConstraints();
        gbcBtn1.anchor = GridBagConstraints.NORTH;
        gbcBtn1.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn1.insets = new Insets(0, 0, 6, 0);
        gbcBtn1.gridx = 0;
        gbcBtn1.gridy = 2;
        painelMenuLateral.add(btnMenuListaAtual, gbcBtn1);

        FontIcon iconeMenuOrcamento = FontIcon.of(FontAwesomeSolid.WALLET, 11, new Color(51, 65, 85));
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
        GridBagConstraints gbcBtn2 = new GridBagConstraints();
        gbcBtn2.anchor = GridBagConstraints.NORTH;
        gbcBtn2.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn2.insets = new Insets(0, 0, 6, 0);
        gbcBtn2.gridx = 0;
        gbcBtn2.gridy = 3;
        painelMenuLateral.add(btnMenuOrcamento, gbcBtn2);

        FontIcon iconeMenuCatalogo = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 11, new Color(51, 65, 85));
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
        GridBagConstraints gbcBtn3 = new GridBagConstraints();
        gbcBtn3.anchor = GridBagConstraints.NORTH;
        gbcBtn3.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn3.insets = new Insets(0, 0, 6, 0);
        gbcBtn3.gridx = 0;
        gbcBtn3.gridy = 4;
        painelMenuLateral.add(btnMenuCatalogo, gbcBtn3);

        // Item ativo no menu lateral: Listas Salvas
        FontIcon iconeMenuListasSalvas = FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, Color.WHITE);
        JButton btnMenuListasSalvas = new JButton("Listas Salvas", iconeMenuListasSalvas);
        btnMenuListasSalvas.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuListasSalvas.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuListasSalvas.setBackground(new Color(55, 48, 224));
        btnMenuListasSalvas.setForeground(Color.WHITE);
        btnMenuListasSalvas.setFocusPainted(false);
        btnMenuListasSalvas.setBorderPainted(false);
        btnMenuListasSalvas.setContentAreaFilled(false);
        btnMenuListasSalvas.setOpaque(true);
        btnMenuListasSalvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuListasSalvas.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        GridBagConstraints gbcBtn4 = new GridBagConstraints();
        gbcBtn4.anchor = GridBagConstraints.NORTH;
        gbcBtn4.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn4.gridx = 0;
        gbcBtn4.gridy = 5;
        painelMenuLateral.add(btnMenuListasSalvas, gbcBtn4);

        // ==========================================
        // SCROLL E MONTAGEM FINAL DA JANELA
        // ==========================================

        JScrollPane painelRolagemPrincipal = new JScrollPane(painelConteudoPrincipal);
        painelRolagemPrincipal.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        painelRolagemPrincipal.getVerticalScrollBar().setUnitIncrement(16);
        painelRolagemPrincipal.setOpaque(true);
        painelRolagemPrincipal.setBackground(COR_FUNDO_PAGINA);
        painelRolagemPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        painelRolagemPrincipal.getViewport().setOpaque(true);
        painelRolagemPrincipal.getViewport().setBackground(COR_FUNDO_PAGINA);

        JPanel painelAreaCentral = new JPanel(new BorderLayout());
        painelAreaCentral.setOpaque(true);
        painelAreaCentral.setBackground(COR_FUNDO_PAGINA);
        painelAreaCentral.add(painelMenuLateral, BorderLayout.WEST);
        painelAreaCentral.add(painelRolagemPrincipal, BorderLayout.CENTER);

        janelaPrincipal.getContentPane().setLayout(new BorderLayout());
        janelaPrincipal.getContentPane().add(painelBarraSuperior, BorderLayout.NORTH);
        janelaPrincipal.getContentPane().add(painelAreaCentral, BorderLayout.CENTER);

        janelaPrincipal.setVisible(true);
    }

    private void atualizarPainelDetalhes(RegistroArquivoLista reg) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BR);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        lblDetalheNomeArquivo.setText(reg.nomeArquivo);
        lblDetalheTotalEst.setText("R$ " + nf.format(reg.totalEstimado));
        lblDetalheQtdItens.setText(reg.totalItens + " itens registrados");
        lblDetalheDataHora.setText(reg.data + " - " + reg.hora);

        painelLinhasPreview.removeAll();
        for (int i = 0; i < reg.itensPreview.size(); i++) {
            ItemPreviewArquivo item = reg.itensPreview.get(i);
            JPanel linhaItem = new JPanel(new BorderLayout(8, 0));
            linhaItem.setOpaque(false);
            linhaItem.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 0));
            linhaItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

            JLabel lblDesc = new JLabel((i + 1) + ". " + item.descricao);
            lblDesc.setFont(new Font("Monospaced", Font.PLAIN, 11));
            lblDesc.setForeground(COR_TEXTO_MEDIO);

            JLabel lblPreco = new JLabel("R$ " + nf.format(item.preco));
            lblPreco.setFont(new Font("Monospaced", Font.BOLD, 11));
            lblPreco.setForeground(COR_TEXTO_ESCURO);

            linhaItem.add(lblDesc, BorderLayout.CENTER);
            linhaItem.add(lblPreco, BorderLayout.EAST);
            painelLinhasPreview.add(linhaItem);
        }

        int restantes = Math.max(0, reg.totalItens - reg.itensPreview.size());
        if (restantes > 0) {
            lblDetalheItensRestantes.setVisible(true);
            lblDetalheItensRestantes.setText("... e mais " + restantes + " itens salvos no arquivo.");
        } else {
            lblDetalheItensRestantes.setVisible(false);
        }

        painelLinhasPreview.revalidate();
        painelLinhasPreview.repaint();
    }

    private void carregarRegistrosIniciais() {
        todosRegistros.add(new RegistroArquivoLista(
                "compra_mensal_completa.txt", "Modelo", "14/10/2024", "10:15", 48, 650.00,
                List.of(
                        new ItemPreviewArquivo("Arroz Integral Tio João 5kg", 32.90),
                        new ItemPreviewArquivo("Feijão Preto Camil 1kg (2x)", 17.80),
                        new ItemPreviewArquivo("Azeite Extra Virgem Borges 500ml", 44.90),
                        new ItemPreviewArquivo("Filé de Peito Frango Sadia 1kg", 23.50),
                        new ItemPreviewArquivo("Café Torrado Especial 500g", 21.90),
                        new ItemPreviewArquivo("Sabão Líquido Ariel 3L", 49.90)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "hortifruti_semanal.txt", "Modelo", "10/10/2024", "08:40", 14, 125.00,
                List.of(
                        new ItemPreviewArquivo("Maçã Gala Nacional (1,5kg)", 13.35),
                        new ItemPreviewArquivo("Banana Prata Climatizada (2kg)", 13.00),
                        new ItemPreviewArquivo("Tomate Italiano Maduro (1,2kg)", 11.04),
                        new ItemPreviewArquivo("Alface Crespa Higienizada (2x)", 7.80),
                        new ItemPreviewArquivo("Batata Inglesa Lavada (2kg)", 11.60),
                        new ItemPreviewArquivo("Laranja Pêra Rio (3kg)", 14.70)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "churrasco_fim_de_semana.txt", "Modelo", "28/09/2024", "17:20", 18, 380.00,
                List.of(
                        new ItemPreviewArquivo("Picanha Bovina Resfriada (1,8kg)", 143.82),
                        new ItemPreviewArquivo("Linguiça Toscana Sadia (1,5kg)", 34.35),
                        new ItemPreviewArquivo("Coração de Frango Temperado 1kg", 29.90),
                        new ItemPreviewArquivo("Pão de Alho Tradicional (2x)", 23.80),
                        new ItemPreviewArquivo("Carvão Vegetal Eucalipto 5kg", 24.90),
                        new ItemPreviewArquivo("Refrigerante Guaraná 2L (3x)", 26.70)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "limpeza_lavanderia.txt", "Modelo", "15/09/2024", "14:05", 12, 160.00,
                List.of(
                        new ItemPreviewArquivo("Sabão Líquido OMO 3L", 44.90),
                        new ItemPreviewArquivo("Amaciante Concentrado Downy 1,5L", 26.90),
                        new ItemPreviewArquivo("Desinfetante Veja Lavanda 2L", 16.50),
                        new ItemPreviewArquivo("Detergente Neutro Ypê (4x)", 11.60),
                        new ItemPreviewArquivo("Água Sanitária Qboa 2L", 8.90),
                        new ItemPreviewArquivo("Esponja Multiuso Scotch-Brite (4un)", 9.80)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "lista_compras_outubro_2024.txt", "Arquivo TXT", "14/10/2024", "19:12", 28, 735.40,
                List.of(
                        new ItemPreviewArquivo("Arroz Branco Tipo 1 5kg (2x)", 57.80),
                        new ItemPreviewArquivo("Contra Filé Bovino (2,2kg)", 98.78),
                        new ItemPreviewArquivo("Leite Integral Piracanjuba (12x)", 65.88),
                        new ItemPreviewArquivo("Queijo Mussarela Fatiado 500g", 24.90),
                        new ItemPreviewArquivo("Azeite Extra Virgem Gallo 500ml", 42.50),
                        new ItemPreviewArquivo("Papel Higiênico Neve 16 rolos", 31.90)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "lista_feira_semana41.txt", "Arquivo TXT", "10/10/2024", "09:30", 12, 98.20,
                List.of(
                        new ItemPreviewArquivo("Ovos Brancos Grandes (30 un)", 22.90),
                        new ItemPreviewArquivo("Mamão Formosa Unidade", 8.50),
                        new ItemPreviewArquivo("Cebola Roxa Selecionada 1kg", 7.90),
                        new ItemPreviewArquivo("Cenoura Fresca 1kg", 5.80),
                        new ItemPreviewArquivo("Cheiro Verde Maço (2x)", 5.00),
                        new ItemPreviewArquivo("Abacate Manteiga (1,5kg)", 12.60)
                )));

        todosRegistros.add(new RegistroArquivoLista(
                "estoque_emergencial_setembro.txt", "Arquivo TXT", "22/09/2024", "16:48", 35, 510.00,
                List.of(
                        new ItemPreviewArquivo("Água Mineral sem Gás 5L (4x)", 39.60),
                        new ItemPreviewArquivo("Atum Sólido Gomes da Costa (4x)", 39.60),
                        new ItemPreviewArquivo("Macarrão Espaguete Barilla (5x)", 34.50),
                        new ItemPreviewArquivo("Molho de Tomate Heinz (6x)", 23.40),
                        new ItemPreviewArquivo("Biscoito Cream Cracker (4x)", 19.60),
                        new ItemPreviewArquivo("Leite em Pó Integral Ninho 800g", 36.90)
                )));
    }

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
                0, 0, COR_AZUL_PRIMARIO,
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