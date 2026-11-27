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

public class PainelCatalogoItens {

    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final Color COR_FUNDO_PAGINA = new Color(241, 245, 249);
    private static final Color COR_AZUL_PRIMARIO = new Color(55, 48, 224);
    private static final Color COR_TEXTO_ESCURO = new Color(15, 23, 42);
    private static final Color COR_TEXTO_MEDIO = new Color(51, 65, 85);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(100, 116, 139);
    private static final Color COR_BORDA = new Color(226, 232, 240);

    private final List<ProdutoCatalogo> todosProdutos = new ArrayList<>();
    private final List<ProdutoCatalogo> produtosFiltrados = new ArrayList<>();

    private DefaultTableModel modeloTabelaProdutos;
    private JTable tabelaProdutos;
    private JLabel lblContagemExibidos;

    // Componentes do painel lateral de detalhes
    private JLabel lblDetalheNome;
    private JLabel lblDetalheBadgeCategoria;
    private JLabel lblDetalheMeta;
    private JLabel lblDetalheUltimoPreco;
    private JLabel lblDetalheVariacaoVsMedia;
    private JLabel lblDetalhePrecoMedio;
    private JLabel lblDetalheQtdRegistros;
    private DefaultTableModel modeloHistoricoItem;

    private static class RegistroCompra {
        final String data;
        final String estabelecimento;
        final double valor;

        RegistroCompra(String data, String estabelecimento, double valor) {
            this.data = data;
            this.estabelecimento = estabelecimento;
            this.valor = valor;
        }
    }

    private static class ProdutoCatalogo {
        final String codigo;
        final String nome;
        final String marca;
        final String categoria;
        final String unidade;
        final double ultimoPreco;
        final double precoMedio;
        final double variacaoPercentual;
        final String frequencia;
        final double menorPreco;
        final String localMenorPreco;
        final String qtdPadrao;
        final List<RegistroCompra> historico;

        ProdutoCatalogo(String codigo, String nome, String marca, String categoria, String unidade,
                        double ultimoPreco, double precoMedio, double variacaoPercentual,
                        String frequencia, double menorPreco, String localMenorPreco,
                        String qtdPadrao, List<RegistroCompra> historico) {
            this.codigo = codigo;
            this.nome = nome;
            this.marca = marca;
            this.categoria = categoria;
            this.unidade = unidade;
            this.ultimoPreco = ultimoPreco;
            this.precoMedio = precoMedio;
            this.variacaoPercentual = variacaoPercentual;
            this.frequencia = frequencia;
            this.menorPreco = menorPreco;
            this.localMenorPreco = localMenorPreco;
            this.qtdPadrao = qtdPadrao;
            this.historico = historico;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
            } catch (Exception ignored) {
                // Mantém o Look & Feel padrão da JVM
            }
            new PainelCatalogoItens().exibirJanela();
        });
    }

    public void exibirJanela() {
        carregarDadosIniciais();

        JFrame janelaPrincipal = new JFrame("Lista de Compras Inteligente — Catálogo de Itens");
        janelaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaPrincipal.setSize(1180, 840);
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
        // 1. CARD SUPERIOR: BUSCA, FILTROS E RESUMO
        // ==========================================

        JPanel painelCardBusca = new JPanel();
        painelCardBusca.setLayout(new BoxLayout(painelCardBusca, BoxLayout.Y_AXIS));
        painelCardBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCardBusca.setBackground(Color.WHITE);
        painelCardBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        painelCardBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 148));

        // Linha 1: Campo de busca com atalho Ctrl + F
        JPanel boxBarraBusca = new JPanel(new BorderLayout(10, 0));
        boxBarraBusca.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxBarraBusca.setBackground(COR_FUNDO_PAGINA);
        boxBarraBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 12, 6, 10)));
        boxBarraBusca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lblIconeBusca = new JLabel(FontIcon.of(FontAwesomeSolid.SEARCH, 13, COR_TEXTO_SECUNDARIO));
        JTextField campoBusca = new JTextField();
        campoBusca.setFont(new Font("SansSerif", Font.PLAIN, 12));
        campoBusca.setOpaque(false);
        campoBusca.setBorder(null);
        aplicarPlaceholder(campoBusca, "Buscar por nome, marca ou código de barras... (ex: Maçã, Tio João, Limpeza)");

        boxBarraBusca.add(lblIconeBusca, BorderLayout.WEST);
        boxBarraBusca.add(campoBusca, BorderLayout.CENTER);

        // Linha 2: Filtros (Categoria, Frequência) + Botões (Importar .csv, Novo Produto)
        JPanel linhaFiltrosAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linhaFiltrosAcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaFiltrosAcoes.setOpaque(false);
        linhaFiltrosAcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel boxFiltroCategoria = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        boxFiltroCategoria.setBackground(COR_FUNDO_PAGINA);
        boxFiltroCategoria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)));
        JLabel lblIconeFiltroCategoria = new JLabel(FontIcon.of(FontAwesomeSolid.SLIDERS_H, 11, COR_TEXTO_MEDIO));
        boxFiltroCategoria.add(lblIconeFiltroCategoria);

        JComboBox<String> comboFiltroCategoria = new JComboBox<>(new String[]{
                "Todas as Categorias", "Hortifrúti", "Alimentos", "Açougue", "Limpeza"
        });
        comboFiltroCategoria.setFont(new Font("SansSerif", Font.BOLD, 11));
        comboFiltroCategoria.setForeground(COR_TEXTO_MEDIO);
        comboFiltroCategoria.setBackground(COR_FUNDO_PAGINA);
        comboFiltroCategoria.setBorder(null);
        boxFiltroCategoria.add(comboFiltroCategoria);

        FontIcon iconeImportarCsv = FontIcon.of(FontAwesomeSolid.UPLOAD, 11, new Color(51, 65, 85));
        JButton btnImportarCsv = new JButton("Importar (.csv)", iconeImportarCsv);
        btnImportarCsv.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnImportarCsv.setBackground(Color.WHITE);
        btnImportarCsv.setForeground(new Color(51, 65, 85));
        btnImportarCsv.setFocusPainted(false);
        btnImportarCsv.setContentAreaFilled(false);
        btnImportarCsv.setOpaque(true);
        btnImportarCsv.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportarCsv.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btnImportarCsv.addActionListener(e ->
                JOptionPane.showMessageDialog(janelaPrincipal, "Importando catálogo via arquivo CSV..."));

        FontIcon iconeNovoProduto = FontIcon.of(FontAwesomeSolid.PLUS, 11, Color.WHITE);
        JButton btnNovoProduto = new JButton("Novo Produto", iconeNovoProduto);
        btnNovoProduto.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnNovoProduto.setBackground(new Color(55, 48, 224));
        btnNovoProduto.setForeground(Color.WHITE);
        btnNovoProduto.setFocusPainted(false);
        btnNovoProduto.setBorderPainted(false);
        btnNovoProduto.setContentAreaFilled(false);
        btnNovoProduto.setOpaque(true);
        btnNovoProduto.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNovoProduto.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));

        linhaFiltrosAcoes.add(boxFiltroCategoria);
        linhaFiltrosAcoes.add(btnImportarCsv);
        linhaFiltrosAcoes.add(btnNovoProduto);

        // Linha 3: Pills de Métricas
        JPanel linhaMetricasCatalogo = new JPanel(new BorderLayout());
        linhaMetricasCatalogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaMetricasCatalogo.setOpaque(false);
        linhaMetricasCatalogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel painelPillsEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelPillsEsquerda.setOpaque(false);

        JPanel pillTotalCadastrado = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        estilizarPillStatus(pillTotalCadastrado);
        JLabel lblIconePillTotal = new JLabel(FontIcon.of(FontAwesomeSolid.ARCHIVE, 11, COR_AZUL_PRIMARIO));
        JLabel lblTextoPillTotal = new JLabel("<html>Total Cadastrado: <b>148 produtos</b></html>");
        lblTextoPillTotal.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTextoPillTotal.setForeground(COR_TEXTO_MEDIO);
        pillTotalCadastrado.add(lblIconePillTotal);
        pillTotalCadastrado.add(lblTextoPillTotal);

        JPanel pillCategoriasAtivas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        estilizarPillStatus(pillCategoriasAtivas);
        JLabel lblIconePillCat = new JLabel(FontIcon.of(FontAwesomeSolid.SITEMAP, 11, new Color(37, 99, 235)));
        JLabel lblTextoPillCat = new JLabel("<html>Categorias Ativas: <b>6 departamentos</b></html>");
        lblTextoPillCat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTextoPillCat.setForeground(COR_TEXTO_MEDIO);
        pillCategoriasAtivas.add(lblIconePillCat);
        pillCategoriasAtivas.add(lblTextoPillCat);

        painelPillsEsquerda.add(pillTotalCadastrado);
        painelPillsEsquerda.add(pillCategoriasAtivas);

        linhaMetricasCatalogo.add(painelPillsEsquerda, BorderLayout.WEST);

        painelCardBusca.add(boxBarraBusca);
        painelCardBusca.add(Box.createVerticalStrut(10));
        painelCardBusca.add(linhaFiltrosAcoes);
        painelCardBusca.add(Box.createVerticalStrut(10));
        painelCardBusca.add(linhaMetricasCatalogo);

        painelConteudoPrincipal.add(painelCardBusca);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

        // ==========================================
        // 2. CORPO CENTRAL (2 COLUNAS: TABELA + DETALHES)
        // ==========================================

        JPanel painelSplitCentral = new JPanel(new BorderLayout(14, 0));
        painelSplitCentral.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSplitCentral.setOpaque(false);

        // --- COLUNA ESQUERDA: BASE DE ITENS CADASTRADOS ---
        JPanel cardTabelaCatalogo = new JPanel(new BorderLayout());
        cardTabelaCatalogo.setBackground(Color.WHITE);
        cardTabelaCatalogo.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        JPanel cabecalhoCardTabela = new JPanel(new BorderLayout());
        cabecalhoCardTabela.setBackground(Color.WHITE);
        cabecalhoCardTabela.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        JPanel tituloEsquerdaTabela = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tituloEsquerdaTabela.setOpaque(false);

        JLabel lblTituloBaseItens = new JLabel("Base de Itens Cadastrados");
        lblTituloBaseItens.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTituloBaseItens.setForeground(COR_TEXTO_ESCURO);

        lblContagemExibidos = new JLabel("8 de 148 exibidos");
        lblContagemExibidos.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblContagemExibidos.setForeground(COR_TEXTO_SECUNDARIO);
        lblContagemExibidos.setOpaque(true);
        lblContagemExibidos.setBackground(COR_FUNDO_PAGINA);
        lblContagemExibidos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));

        tituloEsquerdaTabela.add(lblTituloBaseItens);
        tituloEsquerdaTabela.add(lblContagemExibidos);

        JPanel acoesDireitaTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoesDireitaTabela.setOpaque(false);

        FontIcon iconeOrdenarTabela = FontIcon.of(FontAwesomeSolid.SORT, 11, new Color(51, 65, 85));
        JButton btnOrdenarTabela = new JButton(iconeOrdenarTabela);
        btnOrdenarTabela.setToolTipText("Ordenar itens");
        btnOrdenarTabela.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnOrdenarTabela.setBackground(Color.WHITE);
        btnOrdenarTabela.setForeground(new Color(51, 65, 85));
        btnOrdenarTabela.setFocusPainted(false);
        btnOrdenarTabela.setContentAreaFilled(false);
        btnOrdenarTabela.setOpaque(true);
        btnOrdenarTabela.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOrdenarTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        FontIcon iconeAtualizarTabela = FontIcon.of(FontAwesomeSolid.SYNC_ALT, 11, new Color(51, 65, 85));
        JButton btnAtualizarTabela = new JButton(iconeAtualizarTabela);
        btnAtualizarTabela.setToolTipText("Atualizar catálogo");
        btnAtualizarTabela.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAtualizarTabela.setBackground(Color.WHITE);
        btnAtualizarTabela.setForeground(new Color(51, 65, 85));
        btnAtualizarTabela.setFocusPainted(false);
        btnAtualizarTabela.setContentAreaFilled(false);
        btnAtualizarTabela.setOpaque(true);
        btnAtualizarTabela.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAtualizarTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        acoesDireitaTabela.add(btnOrdenarTabela);
        acoesDireitaTabela.add(btnAtualizarTabela);

        cabecalhoCardTabela.add(tituloEsquerdaTabela, BorderLayout.WEST);
        cabecalhoCardTabela.add(acoesDireitaTabela, BorderLayout.EAST);

        // Modelo e JTable do Catálogo
        modeloTabelaProdutos = new DefaultTableModel(
                new String[]{"CÓD.", "PRODUTO & MARCA", "CATEGORIA", "UN.", "ÚLTIMO", "MÉDIO", "VARIAÇÃO", "FREQUÊNCIA"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaProdutos = new JTable(modeloTabelaProdutos);
        tabelaProdutos.setRowHeight(52);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setShowVerticalLines(false);
        tabelaProdutos.setShowHorizontalLines(true);
        tabelaProdutos.setGridColor(COR_FUNDO_PAGINA);
        tabelaProdutos.setIntercellSpacing(new Dimension(0, 1));
        tabelaProdutos.setSelectionBackground(new Color(248, 250, 252));
        tabelaProdutos.setSelectionForeground(COR_TEXTO_ESCURO);

        JTableHeader headerTabela = tabelaProdutos.getTableHeader();
        headerTabela.setReorderingAllowed(false);
        headerTabela.setPreferredSize(new Dimension(100, 32));
        headerTabela.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COR_FUNDO_PAGINA);
                lbl.setForeground(COR_TEXTO_SECUNDARIO);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(1, 0, 1, 0, COR_BORDA),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));

                if (column == 2 || column == 3 || column == 6 || column == 7) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 4 || column == 5) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        });

        tabelaProdutos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Color fundoLinha = isSelected ? new Color(248, 250, 252) : Color.WHITE;
                ProdutoCatalogo prod = row < produtosFiltrados.size() ? produtosFiltrados.get(row) : null;

                // Coluna 1: PRODUTO & MARCA
                if (column == 1 && prod != null) {
                    JPanel celulaProduto = new JPanel();
                    celulaProduto.setLayout(new BoxLayout(celulaProduto, BoxLayout.Y_AXIS));
                    celulaProduto.setBackground(fundoLinha);
                    celulaProduto.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

                    String pontoSelecao = isSelected ? " <span style='color:#3730E0;'>&#9679;</span>" : "";
                    JLabel lblNome = new JLabel("<html><b>" + prod.nome + "</b>" + pontoSelecao + "</html>");
                    lblNome.setFont(new Font("SansSerif", Font.BOLD, 12));
                    lblNome.setForeground(COR_TEXTO_ESCURO);

                    JLabel lblMarca = new JLabel(prod.marca);
                    lblMarca.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    lblMarca.setForeground(COR_TEXTO_SECUNDARIO);

                    celulaProduto.add(lblNome);
                    celulaProduto.add(Box.createVerticalStrut(2));
                    celulaProduto.add(lblMarca);
                    return celulaProduto;
                }

                // Coluna 2: Badge CATEGORIA
                if (column == 2 && prod != null) {
                    JPanel wrapperCat = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
                    wrapperCat.setBackground(fundoLinha);
                    JLabel badgeCat = new JLabel(prod.categoria, SwingConstants.CENTER);
                    estilizarBadgeCategoria(badgeCat, prod.categoria);
                    wrapperCat.add(badgeCat);
                    return wrapperCat;
                }

                // Coluna 6: Badge VARIAÇÃO
                if (column == 6 && prod != null) {
                    JPanel wrapperVar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
                    wrapperVar.setBackground(fundoLinha);
                    boolean subiu = prod.variacaoPercentual > 0;
                    String sinal = subiu ? "▲ +" : "▼ ";
                    String textoVar = sinal + String.format(LOCALE_BR, "%.1f%%", prod.variacaoPercentual);

                    JLabel badgeVar = new JLabel(textoVar, SwingConstants.CENTER);
                    badgeVar.setOpaque(true);
                    badgeVar.setFont(new Font("SansSerif", Font.BOLD, 10));
                    badgeVar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                    if (subiu) {
                        badgeVar.setBackground(new Color(254, 226, 226));
                        badgeVar.setForeground(new Color(185, 28, 28));
                    } else {
                        badgeVar.setBackground(new Color(209, 250, 229));
                        badgeVar.setForeground(new Color(6, 95, 70));
                    }
                    wrapperVar.add(badgeVar);
                    return wrapperVar;
                }

                // Coluna 7: Badge FREQUÊNCIA
                if (column == 7 && prod != null) {
                    JPanel wrapperFreq = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
                    wrapperFreq.setBackground(fundoLinha);
                    JLabel badgeFreq = new JLabel(prod.frequencia, SwingConstants.CENTER);
                    badgeFreq.setOpaque(true);
                    badgeFreq.setFont(new Font("SansSerif", Font.BOLD, 10));
                    badgeFreq.setBackground(COR_FUNDO_PAGINA);
                    badgeFreq.setForeground(COR_TEXTO_MEDIO);
                    badgeFreq.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                    wrapperFreq.add(badgeFreq);
                    return wrapperFreq;
                }

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(fundoLinha);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                if (column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
                    lbl.setForeground(COR_TEXTO_SECUNDARIO);
                } else if (column == 3) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    lbl.setForeground(COR_TEXTO_MEDIO);
                } else if (column == 4) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
                    lbl.setForeground(COR_TEXTO_ESCURO);
                } else if (column == 5) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    lbl.setForeground(COR_TEXTO_SECUNDARIO);
                }
                return lbl;
            }
        });

        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(56);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(210);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(88);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(42);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(72);
        tabelaProdutos.getColumnModel().getColumn(5).setPreferredWidth(72);
        tabelaProdutos.getColumnModel().getColumn(6).setPreferredWidth(76);
        tabelaProdutos.getColumnModel().getColumn(7).setPreferredWidth(82);

        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setBorder(null);
        scrollTabela.getViewport().setBackground(Color.WHITE);

        // Rodapé de Paginação da Tabela
        JPanel rodapeTabela = new JPanel(new BorderLayout());
        rodapeTabela.setBackground(Color.WHITE);
        rodapeTabela.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, COR_FUNDO_PAGINA),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JPanel painelPaginacao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelPaginacao.setOpaque(false);

        JButton btnPaginaAnterior = new JButton("Anterior");
        btnPaginaAnterior.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPaginaAnterior.setBackground(Color.WHITE);
        btnPaginaAnterior.setForeground(new Color(51, 65, 85));
        btnPaginaAnterior.setFocusPainted(false);
        btnPaginaAnterior.setContentAreaFilled(false);
        btnPaginaAnterior.setOpaque(true);
        btnPaginaAnterior.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPaginaAnterior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JButton btnPagina1 = new JButton("1");
        btnPagina1.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPagina1.setBackground(new Color(55, 48, 224));
        btnPagina1.setForeground(Color.WHITE);
        btnPagina1.setFocusPainted(false);
        btnPagina1.setBorderPainted(false);
        btnPagina1.setContentAreaFilled(false);
        btnPagina1.setOpaque(true);
        btnPagina1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPagina1.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));

        JButton btnPagina2 = new JButton("2");
        btnPagina2.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPagina2.setBackground(Color.WHITE);
        btnPagina2.setForeground(new Color(51, 65, 85));
        btnPagina2.setFocusPainted(false);
        btnPagina2.setContentAreaFilled(false);
        btnPagina2.setOpaque(true);
        btnPagina2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPagina2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JButton btnPagina3 = new JButton("3");
        btnPagina3.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPagina3.setBackground(Color.WHITE);
        btnPagina3.setForeground(new Color(51, 65, 85));
        btnPagina3.setFocusPainted(false);
        btnPagina3.setContentAreaFilled(false);
        btnPagina3.setOpaque(true);
        btnPagina3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPagina3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        JButton btnPaginaProxima = new JButton("Próxima");
        btnPaginaProxima.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPaginaProxima.setBackground(Color.WHITE);
        btnPaginaProxima.setForeground(new Color(51, 65, 85));
        btnPaginaProxima.setFocusPainted(false);
        btnPaginaProxima.setContentAreaFilled(false);
        btnPaginaProxima.setOpaque(true);
        btnPaginaProxima.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPaginaProxima.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        painelPaginacao.add(btnPaginaAnterior);
        painelPaginacao.add(btnPagina1);
        painelPaginacao.add(btnPagina2);
        painelPaginacao.add(btnPagina3);
        painelPaginacao.add(btnPaginaProxima);
        rodapeTabela.add(painelPaginacao, BorderLayout.EAST);

        cardTabelaCatalogo.add(cabecalhoCardTabela, BorderLayout.NORTH);
        cardTabelaCatalogo.add(scrollTabela, BorderLayout.CENTER);
        cardTabelaCatalogo.add(rodapeTabela, BorderLayout.SOUTH);

        // --- COLUNA DIREITA: DETALHES DO ITEM ---
        JPanel colunaDireita = new JPanel();
        colunaDireita.setLayout(new BoxLayout(colunaDireita, BoxLayout.Y_AXIS));
        colunaDireita.setOpaque(false);
        colunaDireita.setPreferredSize(new Dimension(320, 620));
        colunaDireita.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));

        JPanel cardDetalhesItem = new JPanel();
        cardDetalhesItem.setLayout(new BoxLayout(cardDetalhesItem, BoxLayout.Y_AXIS));
        cardDetalhesItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardDetalhesItem.setBackground(Color.WHITE);
        cardDetalhesItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        // Topo Detalhes: Subtítulo + Badge Categoria
        JPanel topoDetalhes = new JPanel(new BorderLayout(8, 0));
        topoDetalhes.setOpaque(false);
        topoDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        topoDetalhes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel blocoTituloDetalhe = new JPanel();
        blocoTituloDetalhe.setLayout(new BoxLayout(blocoTituloDetalhe, BoxLayout.Y_AXIS));
        blocoTituloDetalhe.setOpaque(false);

        JLabel lblSubDetalhes = new JLabel("DETALHES DO ITEM");
        lblSubDetalhes.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblSubDetalhes.setForeground(COR_AZUL_PRIMARIO);

        lblDetalheNome = new JLabel("Maçã Gala Nacional");
        lblDetalheNome.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDetalheNome.setForeground(COR_TEXTO_ESCURO);

        blocoTituloDetalhe.add(lblSubDetalhes);
        blocoTituloDetalhe.add(Box.createVerticalStrut(2));
        blocoTituloDetalhe.add(lblDetalheNome);

        lblDetalheBadgeCategoria = new JLabel("Hortifrúti", SwingConstants.CENTER);
        estilizarBadgeCategoria(lblDetalheBadgeCategoria, "Hortifrúti");

        topoDetalhes.add(blocoTituloDetalhe, BorderLayout.CENTER);
        topoDetalhes.add(lblDetalheBadgeCategoria, BorderLayout.EAST);

        lblDetalheMeta = new JLabel("Código: #1021 • Categoria: Hortifrúti • Unidade: kg");
        lblDetalheMeta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDetalheMeta.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalheMeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Box Cinza de Indicadores de Preço
        JPanel boxPrecosDetalhe = new JPanel();
        boxPrecosDetalhe.setLayout(new BoxLayout(boxPrecosDetalhe, BoxLayout.Y_AXIS));
        boxPrecosDetalhe.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxPrecosDetalhe.setBackground(COR_FUNDO_PAGINA);
        boxPrecosDetalhe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        boxPrecosDetalhe.setMaximumSize(new Dimension(Integer.MAX_VALUE, 134));

        JPanel gridDoisPrecos = new JPanel(new GridLayout(1, 2, 10, 0));
        gridDoisPrecos.setOpaque(false);
        gridDoisPrecos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel colUltimoPreco = new JPanel();
        colUltimoPreco.setLayout(new BoxLayout(colUltimoPreco, BoxLayout.Y_AXIS));
        colUltimoPreco.setOpaque(false);
        JLabel lblTitUltimo = new JLabel("Último Preço");
        lblTitUltimo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitUltimo.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalheUltimoPreco = new JLabel("R$ 8,90");
        lblDetalheUltimoPreco.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblDetalheUltimoPreco.setForeground(COR_TEXTO_ESCURO);
        lblDetalheVariacaoVsMedia = new JLabel("+4,7% vs. média");
        lblDetalheVariacaoVsMedia.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblDetalheVariacaoVsMedia.setForeground(new Color(185, 28, 28));
        colUltimoPreco.add(lblTitUltimo);
        colUltimoPreco.add(lblDetalheUltimoPreco);
        colUltimoPreco.add(lblDetalheVariacaoVsMedia);

        JPanel colPrecoMedio = new JPanel();
        colPrecoMedio.setLayout(new BoxLayout(colPrecoMedio, BoxLayout.Y_AXIS));
        colPrecoMedio.setOpaque(false);
        JLabel lblTitMedio = new JLabel("Preço Médio");
        lblTitMedio.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitMedio.setForeground(COR_TEXTO_SECUNDARIO);
        lblDetalhePrecoMedio = new JLabel("R$ 8,50");
        lblDetalhePrecoMedio.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblDetalhePrecoMedio.setForeground(COR_TEXTO_ESCURO);
        JLabel lblBase6Meses = new JLabel("Base de 6 meses");
        lblBase6Meses.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblBase6Meses.setForeground(COR_TEXTO_SECUNDARIO);
        colPrecoMedio.add(lblTitMedio);
        colPrecoMedio.add(lblDetalhePrecoMedio);
        colPrecoMedio.add(lblBase6Meses);

        gridDoisPrecos.add(colUltimoPreco);
        gridDoisPrecos.add(colPrecoMedio);

        boxPrecosDetalhe.add(gridDoisPrecos);

        // Mini-tabela: Últimas Compras Registradas
        JPanel cabecalhoHistoricoItem = new JPanel(new BorderLayout());
        cabecalhoHistoricoItem.setOpaque(false);
        cabecalhoHistoricoItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalhoHistoricoItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lblTitUltimasCompras = new JLabel("Últimas Compras Registradas",
                FontIcon.of(FontAwesomeSolid.RECEIPT, 12, COR_AZUL_PRIMARIO), JLabel.LEFT);
        lblTitUltimasCompras.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitUltimasCompras.setForeground(COR_TEXTO_ESCURO);
        lblTitUltimasCompras.setIconTextGap(6);

        lblDetalheQtdRegistros = new JLabel("3 registros");
        lblDetalheQtdRegistros.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDetalheQtdRegistros.setForeground(COR_TEXTO_SECUNDARIO);

        cabecalhoHistoricoItem.add(lblTitUltimasCompras, BorderLayout.WEST);
        cabecalhoHistoricoItem.add(lblDetalheQtdRegistros, BorderLayout.EAST);

        modeloHistoricoItem = new DefaultTableModel(new String[]{"DATA", "ESTABELECIMENTO", "VALOR"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaHistoricoItem = new JTable(modeloHistoricoItem);
        tabelaHistoricoItem.setRowHeight(28);
        tabelaHistoricoItem.setShowVerticalLines(false);
        tabelaHistoricoItem.setShowHorizontalLines(true);
        tabelaHistoricoItem.setGridColor(COR_FUNDO_PAGINA);
        tabelaHistoricoItem.setFocusable(false);
        tabelaHistoricoItem.setRowSelectionAllowed(false);

        JTableHeader headerHistItem = tabelaHistoricoItem.getTableHeader();
        headerHistItem.setReorderingAllowed(false);
        headerHistItem.setPreferredSize(new Dimension(100, 24));
        headerHistItem.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COR_FUNDO_PAGINA);
                lbl.setForeground(COR_TEXTO_SECUNDARIO);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                lbl.setHorizontalAlignment(column == 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return lbl;
            }
        });

        tabelaHistoricoItem.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.WHITE);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                if (column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    lbl.setForeground(COR_TEXTO_SECUNDARIO);
                } else if (column == 1) {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                    lbl.setForeground(COR_TEXTO_ESCURO);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
                    lbl.setForeground(row == 1 ? new Color(6, 95, 70) : COR_TEXTO_ESCURO);
                }
                return lbl;
            }
        });

        tabelaHistoricoItem.getColumnModel().getColumn(0).setPreferredWidth(46);
        tabelaHistoricoItem.getColumnModel().getColumn(1).setPreferredWidth(155);
        tabelaHistoricoItem.getColumnModel().getColumn(2).setPreferredWidth(68);

        JPanel boxTabelaHistoricoItem = new JPanel(new BorderLayout());
        boxTabelaHistoricoItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxTabelaHistoricoItem.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        boxTabelaHistoricoItem.add(headerHistItem, BorderLayout.NORTH);
        boxTabelaHistoricoItem.add(tabelaHistoricoItem, BorderLayout.CENTER);
        boxTabelaHistoricoItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        // Botão Azul: Adicionar à Lista [Enter]
        FontIcon iconeAdicionarALista = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 11, Color.WHITE);
        JButton btnAdicionarALista = new JButton("Adicionar à Lista [Enter]", iconeAdicionarALista);
        btnAdicionarALista.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdicionarALista.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnAdicionarALista.setBackground(new Color(55, 48, 224));
        btnAdicionarALista.setForeground(Color.WHITE);
        btnAdicionarALista.setFocusPainted(false);
        btnAdicionarALista.setBorderPainted(false);
        btnAdicionarALista.setContentAreaFilled(false);
        btnAdicionarALista.setOpaque(true);
        btnAdicionarALista.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdicionarALista.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        btnAdicionarALista.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnAdicionarALista.setPreferredSize(new Dimension(280, 36));

        cardDetalhesItem.add(topoDetalhes);
        cardDetalhesItem.add(Box.createVerticalStrut(4));
        cardDetalhesItem.add(lblDetalheMeta);
        cardDetalhesItem.add(Box.createVerticalStrut(12));
        cardDetalhesItem.add(boxPrecosDetalhe);
        cardDetalhesItem.add(Box.createVerticalStrut(12));
        cardDetalhesItem.add(cabecalhoHistoricoItem);
        cardDetalhesItem.add(Box.createVerticalStrut(6));
        cardDetalhesItem.add(boxTabelaHistoricoItem);
        cardDetalhesItem.add(Box.createVerticalStrut(12));
        cardDetalhesItem.add(btnAdicionarALista);
        cardDetalhesItem.add(Box.createVerticalStrut(8));

        colunaDireita.add(cardDetalhesItem);

        painelSplitCentral.add(cardTabelaCatalogo, BorderLayout.CENTER);
        painelSplitCentral.add(colunaDireita, BorderLayout.EAST);

        painelConteudoPrincipal.add(painelSplitCentral);
        painelConteudoPrincipal.add(Box.createVerticalStrut(14));

   

        // ==========================================
        // INTERATIVIDADE, FILTROS E ATALHOS
        // ==========================================

        Runnable aplicarFiltros = () -> {
            String termo = campoBusca.getText().trim().toLowerCase();
            String catSel = (String) comboFiltroCategoria.getSelectedItem();
            String freqSel = (String) comboFiltroFrequencia.getSelectedItem();

            produtosFiltrados.clear();
            modeloTabelaProdutos.setRowCount(0);

            NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BR);
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);

            for (ProdutoCatalogo p : todosProdutos) {
                boolean bateBusca = termo.isEmpty()
                        || p.nome.toLowerCase().contains(termo)
                        || p.marca.toLowerCase().contains(termo)
                        || p.codigo.toLowerCase().contains(termo);
                boolean bateCat = "Todas as Categorias".equals(catSel) || p.categoria.equalsIgnoreCase(catSel);
                boolean bateFreq = "Qualquer Frequência".equals(freqSel) || p.frequencia.equalsIgnoreCase(freqSel);

                if (bateBusca && bateCat && bateFreq) {
                    produtosFiltrados.add(p);
                    modeloTabelaProdutos.addRow(new Object[]{
                            p.codigo,
                            p.nome,
                            p.categoria,
                            p.unidade,
                            "R$ " + nf.format(p.ultimoPreco),
                            "R$ " + nf.format(p.precoMedio),
                            p.variacaoPercentual,
                            p.frequencia
                    });
                }
            }

            lblContagemExibidos.setText(produtosFiltrados.size() + " de 148 exibidos");
            if (!produtosFiltrados.isEmpty()) {
                tabelaProdutos.setRowSelectionInterval(0, 0);
                atualizarPainelDetalhes(produtosFiltrados.get(0));
            }
        };

        tabelaProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabelaProdutos.getSelectedRow();
                if (linha >= 0 && linha < produtosFiltrados.size()) {
                    atualizarPainelDetalhes(produtosFiltrados.get(linha));
                }
            }
        });

        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltros.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltros.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltros.run();
            }
        });

        comboFiltroCategoria.addActionListener(e -> aplicarFiltros.run());

        btnAdicionarALista.addActionListener(e -> {
            int linha = tabelaProdutos.getSelectedRow();
            if (linha >= 0 && linha < produtosFiltrados.size()) {
                ProdutoCatalogo p = produtosFiltrados.get(linha);
                JOptionPane.showMessageDialog(janelaPrincipal,
                        "Item adicionado à Lista Atual:\n" + p.nome + " (" + p.qtdPadrao + ")");
            }
        });

        btnNovoProduto.addActionListener(e -> {
            String novoNome = JOptionPane.showInputDialog(janelaPrincipal, "Nome do novo produto para o catálogo:");
            if (novoNome != null && !novoNome.trim().isEmpty()) {
                String novoCodigo = "#" + (1021 + todosProdutos.size());
                List<RegistroCompra> hist = List.of(
                        new RegistroCompra("Hoje", "Cadastro Manual", 10.00)
                );
                todosProdutos.add(0, new ProdutoCatalogo(
                        novoCodigo, novoNome.trim(), "Marca Padrão", "Alimentos", "un",
                        10.00, 10.00, 0.0, "Mensal", 10.00, "Cadastro Manual", "1 un", hist));
                aplicarFiltros.run();
            }
        });

        // Atalhos globais na janela (Ctrl+F, F2, Enter, Del, Esc)
        JRootPane rootPane = janelaPrincipal.getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "focarBusca");
        am.put("focarBusca", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                campoBusca.requestFocusInWindow();
                campoBusca.selectAll();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "novoProduto");
        am.put("novoProduto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNovoProduto.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "adicionarSelecionado");
        am.put("adicionarSelecionado", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!campoBusca.hasFocus()) {
                    btnAdicionarALista.doClick();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "excluirSelecionado");
        am.put("excluirSelecionado", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linha = tabelaProdutos.getSelectedRow();
                if (linha >= 0 && linha < produtosFiltrados.size()) {
                    ProdutoCatalogo p = produtosFiltrados.get(linha);
                    int op = JOptionPane.showConfirmDialog(janelaPrincipal,
                            "Excluir \"" + p.nome + "\" do catálogo?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.YES_OPTION) {
                        todosProdutos.remove(p);
                        aplicarFiltros.run();
                    }
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "limparFiltros");
        am.put("limparFiltros", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                campoBusca.setText("");
                comboFiltroCategoria.setSelectedIndex(0);
                comboFiltroFrequencia.setSelectedIndex(0);
                aplicarFiltros.run();
            }
        });

        // Carrega os 8 itens iniciais
        aplicarFiltros.run();

        // ==========================================
        // MENU LATERAL (COM "CATÁLOGO DE ITENS" ATIVO)
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

        // Item ativo no menu lateral: Catálogo de Itens
        FontIcon iconeMenuCatalogo = FontIcon.of(FontAwesomeSolid.SHOPPING_CART, 11, Color.WHITE);
        JButton btnMenuCatalogo = new JButton("Catálogo de Itens", iconeMenuCatalogo);
        btnMenuCatalogo.setHorizontalAlignment(SwingConstants.LEFT);
        btnMenuCatalogo.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMenuCatalogo.setBackground(new Color(55, 48, 224));
        btnMenuCatalogo.setForeground(Color.WHITE);
        btnMenuCatalogo.setFocusPainted(false);
        btnMenuCatalogo.setBorderPainted(false);
        btnMenuCatalogo.setContentAreaFilled(false);
        btnMenuCatalogo.setOpaque(true);
        btnMenuCatalogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMenuCatalogo.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));
        GridBagConstraints gbcBtn3 = new GridBagConstraints();
        gbcBtn3.anchor = GridBagConstraints.NORTH;
        gbcBtn3.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn3.insets = new Insets(0, 0, 6, 0);
        gbcBtn3.gridx = 0;
        gbcBtn3.gridy = 4;
        painelMenuLateral.add(btnMenuCatalogo, gbcBtn3);

        FontIcon iconeMenuListasSalvas = FontIcon.of(FontAwesomeSolid.BOOKMARK, 11, new Color(51, 65, 85));
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

    private void atualizarPainelDetalhes(ProdutoCatalogo p) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BR);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        lblDetalheNome.setText(p.nome);
        estilizarBadgeCategoria(lblDetalheBadgeCategoria, p.categoria);
        lblDetalheMeta.setText("Código: " + p.codigo + " • Categoria: " + p.categoria + " • Unidade: " + p.unidade);

        lblDetalheUltimoPreco.setText("R$ " + nf.format(p.ultimoPreco));
        boolean subiu = p.variacaoPercentual > 0;
        String prefixo = subiu ? "+" : "";
        lblDetalheVariacaoVsMedia.setText(prefixo + String.format(LOCALE_BR, "%.1f%% vs. média", p.variacaoPercentual));
        lblDetalheVariacaoVsMedia.setForeground(subiu ? new Color(185, 28, 28) : new Color(6, 95, 70));

        lblDetalhePrecoMedio.setText("R$ " + nf.format(p.precoMedio));
        lblDetalheMenorPreco.setText("R$ " + nf.format(p.menorPreco));
        lblDetalheMenorLocal.setText("(" + p.localMenorPreco + ")");

        modeloHistoricoItem.setRowCount(0);
        for (RegistroCompra reg : p.historico) {
            modeloHistoricoItem.addRow(new Object[]{reg.data, reg.estabelecimento, "R$ " + nf.format(reg.valor)});
        }
        lblDetalheQtdRegistros.setText(p.historico.size() + " registros");

        lblDetalheFrequencia.setText("<html>Frequência sugerida: <b>" + p.frequencia + "</b></html>");
        lblDetalheQtdPadrao.setText("<html>Qtd padrão: <b>" + p.qtdPadrao + "</b></html>");
    }

    private void carregarDadosIniciais() {
        todosProdutos.add(new ProdutoCatalogo(
                "#1021", "Maçã Gala Nacional", "Hortifrúti Selecionado / Granel", "Hortifrúti", "kg",
                8.90, 8.50, 4.7, "Semanal", 8.20, "Supermercado Alvorada", "1,5 kg",
                List.of(
                        new RegistroCompra("18/10", "Mercado Bairro Nobre", 8.90),
                        new RegistroCompra("14/10", "Supermercado Alvorada", 8.20),
                        new RegistroCompra("06/10", "Hipermercado Central", 8.65)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1022", "Arroz Branco Tipo 1 5kg", "Marca: Tio João", "Alimentos", "un",
                28.90, 29.50, -2.0, "Mensal", 27.90, "Atacadão Distribuidora", "2 un",
                List.of(
                        new RegistroCompra("15/10", "Supermercado Pão de Açúcar", 28.90),
                        new RegistroCompra("20/09", "Atacadão Distribuidora", 27.90),
                        new RegistroCompra("18/08", "Mercado Bairro Nobre", 31.70)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1023", "Filé de Frango Sassami 1kg", "Marca: Seara Congelados", "Açougue", "kg",
                21.90, 22.90, -4.3, "Semanal", 20.50, "Açougue Boi Nobre", "2 kg",
                List.of(
                        new RegistroCompra("17/10", "Supermercado Alvorada", 21.90),
                        new RegistroCompra("10/10", "Açougue Boi Nobre", 20.50),
                        new RegistroCompra("02/10", "Hipermercado Central", 26.30)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1024", "Azeite de Oliva Extra Virgem 500ml", "Marca: Gallo Vidro", "Alimentos", "un",
                42.50, 38.90, 9.2, "Mensal", 36.90, "Hipermercado Central", "1 un",
                List.of(
                        new RegistroCompra("12/10", "Mercado Bairro Nobre", 42.50),
                        new RegistroCompra("14/09", "Hipermercado Central", 36.90),
                        new RegistroCompra("10/08", "Supermercado Alvorada", 37.30)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1025", "Sabão Líquido Concentrado 3L", "Marca: OMO Lavagem Perfeita", "Limpeza", "un",
                44.90, 46.00, -2.4, "Quinzenal", 42.90, "Atacadão Distribuidora", "1 un",
                List.of(
                        new RegistroCompra("16/10", "Atacadão Distribuidora", 44.90),
                        new RegistroCompra("01/10", "Atacadão Distribuidora", 42.90),
                        new RegistroCompra("15/09", "Hipermercado Central", 50.20)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1026", "Café Torrado e Moído 500g", "Marca: Pilão Tradicional", "Alimentos", "un",
                21.90, 21.50, 1.8, "Quinzenal", 19.90, "Supermercado Alvorada", "2 un",
                List.of(
                        new RegistroCompra("18/10", "Mercado Bairro Nobre", 21.90),
                        new RegistroCompra("04/10", "Supermercado Alvorada", 19.90),
                        new RegistroCompra("19/09", "Hipermercado Central", 22.70)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1027", "Leite Integral UHT 1L", "Marca: Piracanjuba", "Alimentos", "un",
                5.49, 5.20, 5.5, "Semanal", 4.89, "Atacadão Distribuidora", "6 un",
                List.of(
                        new RegistroCompra("18/10", "Mercado Bairro Nobre", 5.49),
                        new RegistroCompra("11/10", "Atacadão Distribuidora", 4.89),
                        new RegistroCompra("04/10", "Supermercado Alvorada", 5.22)
                )));

        todosProdutos.add(new ProdutoCatalogo(
                "#1028", "Desinfetante Multiuso 500ml", "Marca: Veja Lavanda", "Limpeza", "un",
                7.90, 8.10, -2.5, "Quinzenal", 7.49, "Supermercado Alvorada", "2 un",
                List.of(
                        new RegistroCompra("15/10", "Supermercado Alvorada", 7.90),
                        new RegistroCompra("30/09", "Supermercado Alvorada", 7.49),
                        new RegistroCompra("15/09", "Mercado Bairro Nobre", 8.91)
                )));
    }

    private void estilizarBadgeCategoria(JLabel badge, String categoria) {
        badge.setText(categoria);
        badge.setOpaque(true);
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        if ("Hortifrúti".equalsIgnoreCase(categoria)) {
            badge.setBackground(new Color(209, 250, 229));
            badge.setForeground(new Color(6, 95, 70));
        } else if ("Alimentos".equalsIgnoreCase(categoria)) {
            badge.setBackground(new Color(219, 234, 254));
            badge.setForeground(new Color(30, 64, 175));
        } else if ("Açougue".equalsIgnoreCase(categoria)) {
            badge.setBackground(new Color(237, 233, 254));
            badge.setForeground(new Color(91, 33, 182));
        } else {
            badge.setBackground(COR_FUNDO_PAGINA);
            badge.setForeground(COR_TEXTO_MEDIO);
        }
    }

    private void estilizarPillStatus(JPanel pill) {
        pill.setBackground(COR_FUNDO_PAGINA);
        pill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)));
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