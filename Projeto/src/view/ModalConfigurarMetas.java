package view;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class ModalConfigurarMetas extends JDialog {

    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final Color AZUL_PRIMARIO = new Color(55, 48, 224);
    private static final Color TEXTO_ESCURO = new Color(15, 23, 42);
    private static final Color TEXTO_SECUNDARIO = new Color(100, 116, 139);
    private static final Color BORDA_SUAVE = new Color(226, 232, 240);
    private static final Color FUNDO_CARD = new Color(241, 245, 249);
    private static final Color VERDE_OK = new Color(4, 120, 87);
    private static final Color VERMELHO_ALERTA = new Color(220, 38, 38);

    private final DecimalFormat formatoDecimal;
    private final List<LinhaCategoriaUI> linhasCategorias = new ArrayList<>();

    private JFormattedTextField txtTetoGlobal;
    private JComboBox<String> comboPeriodoVigencia;
    private JCheckBox chkNotificarCabecalho;
    private JLabel lblSomaCategorizada;
    private JLabel lblTotalMetas;

    private boolean confirmado = false;
    private double tetoGlobalSalvo;

    // Estrutura auxiliar para manter os dados e componentes de cada categoria
    private static class LinhaCategoriaUI {
        final String nome;
        final Color corIndicador;
        final double limitePadrao;
        final double consumoRealizado;
        JFormattedTextField campoLimite;
        JLabel lblConsumoPercentual;
        JProgressBar barraConsumo;

        LinhaCategoriaUI(String nome, Color corIndicador, double limitePadrao, double consumoRealizado) {
            this.nome = nome;
            this.corIndicador = corIndicador;
            this.limitePadrao = limitePadrao;
            this.consumoRealizado = consumoRealizado;
        }
    }

    public ModalConfigurarMetas(Frame owner, double tetoInicial) {
        super(owner, "Configurar Metas e Limites Orçamentários", true);
        this.tetoGlobalSalvo = tetoInicial;

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(LOCALE_BR);
        this.formatoDecimal = new DecimalFormat("#,##0.00", simbolos);

        setUndecorated(true);
        setSize(680, 625);
        setLocationRelativeTo(owner);

        inicializarDadosCategorias();
        construirInterface();
        configurarAtalhosTeclado();
        recalcularIndicadores();
    }

    private void inicializarDadosCategorias() {
        linhasCategorias.add(new LinhaCategoriaUI("Alimentos & Mercearia", new Color(37, 99, 235), 700.00, 620.00));
        linhasCategorias.add(new LinhaCategoriaUI("Açougue & Proteínas", new Color(79, 70, 229), 450.00, 380.00));
        linhasCategorias.add(new LinhaCategoriaUI("Hortifrúti & Frescos", new Color(5, 150, 105), 300.00, 245.20));
        linhasCategorias.add(new LinhaCategoriaUI("Limpeza & Higiene", new Color(100, 116, 139), 200.00, 160.00));
        linhasCategorias.add(new LinhaCategoriaUI("Outros & Bebidas", new Color(16, 185, 129), 150.00, 80.00));
    }

    private void construirInterface() {
        JPanel painelRaiz = new JPanel(new BorderLayout());
        painelRaiz.setBackground(Color.WHITE);
        painelRaiz.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));

        // ==========================================
        // 1. CABEÇALHO DO MODAL
        // ==========================================
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(Color.WHITE);
        painelTopo.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDA_SUAVE),
                BorderFactory.createEmptyBorder(14, 20, 14, 16)));

        JLabel lblTituloModal = new JLabel("Configurar Metas e Limites Orçamentários",
                FontIcon.of(FontAwesomeSolid.SLIDERS_H, 14, AZUL_PRIMARIO), JLabel.LEFT);
        lblTituloModal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTituloModal.setForeground(TEXTO_ESCURO);
        lblTituloModal.setIconTextGap(10);

        JButton btnFecharX = new JButton(FontIcon.of(FontAwesomeSolid.TIMES, 13, TEXTO_SECUNDARIO));
        btnFecharX.setBorderPainted(false);
        btnFecharX.setContentAreaFilled(false);
        btnFecharX.setFocusPainted(false);
        btnFecharX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFecharX.addActionListener(e -> fecharSemSalvar());

        painelTopo.add(lblTituloModal, BorderLayout.WEST);
        painelTopo.add(btnFecharX, BorderLayout.EAST);

        // ==========================================
        // 2. CORPO CENTRAL DO MODAL
        // ==========================================
        JPanel painelCorpo = new JPanel();
        painelCorpo.setLayout(new BoxLayout(painelCorpo, BoxLayout.Y_AXIS));
        painelCorpo.setBackground(Color.WHITE);
        painelCorpo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblDescricao = new JLabel(
                "<html>Defina o teto mensal global e os limites recomendados por categoria para alertar sobreconsumo antes da finalização das compras.</html>");
        lblDescricao.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDescricao.setForeground(new Color(71, 85, 105));
        lblDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelCorpo.add(lblDescricao);
        painelCorpo.add(Box.createVerticalStrut(14));

        // --- BLOCO 1: TETO MENSAL GLOBAL ---
        JPanel cardTetoGlobal = new JPanel();
        cardTetoGlobal.setLayout(new BoxLayout(cardTetoGlobal, BoxLayout.Y_AXIS));
        cardTetoGlobal.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTetoGlobal.setBackground(FUNDO_CARD);
        cardTetoGlobal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA_SUAVE),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lblSecao1 = new JLabel("1. Teto Mensal Global",
                FontIcon.of(FontAwesomeSolid.LANDMARK, 13, AZUL_PRIMARIO), JLabel.LEFT);
        lblSecao1.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSecao1.setForeground(TEXTO_ESCURO);
        lblSecao1.setIconTextGap(8);
        lblSecao1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel gridCamposTopo = new JPanel(new GridLayout(1, 2, 16, 0));
        gridCamposTopo.setOpaque(false);
        gridCamposTopo.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridCamposTopo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        // Coluna Esquerda: Teto Limite Mensal (JFormattedTextField)
        JPanel colTeto = new JPanel(new BorderLayout(0, 5));
        colTeto.setOpaque(false);
        JLabel lblCampoTeto = new JLabel("Teto Limite Mensal (JFormattedTextField):");
        lblCampoTeto.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCampoTeto.setForeground(new Color(71, 85, 105));

        JPanel boxInputTeto = new JPanel(new BorderLayout(6, 0));
        boxInputTeto.setBackground(Color.WHITE);
        boxInputTeto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        JLabel lblPrefixoMoeda = new JLabel("R$");
        lblPrefixoMoeda.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPrefixoMoeda.setForeground(TEXTO_SECUNDARIO);

        txtTetoGlobal = criarCampoMonetario(tetoGlobalSalvo, 18, true);
        boxInputTeto.add(lblPrefixoMoeda, BorderLayout.WEST);
        boxInputTeto.add(txtTetoGlobal, BorderLayout.CENTER);

        colTeto.add(lblCampoTeto, BorderLayout.NORTH);
        colTeto.add(boxInputTeto, BorderLayout.CENTER);

        // Coluna Direita: Período de Vigência
        JPanel colPeriodo = new JPanel(new BorderLayout(0, 5));
        colPeriodo.setOpaque(false);
        JLabel lblPeriodo = new JLabel("Período de Vigência:");
        lblPeriodo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPeriodo.setForeground(new Color(71, 85, 105));

        comboPeriodoVigencia = new JComboBox<>(new String[]{
                "Mês Atual (Outubro/2024)",
                "Próximo Mês (Novembro/2024)",
                "Recorrente (Todos os Meses)"
        });
        comboPeriodoVigencia.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comboPeriodoVigencia.setBackground(Color.WHITE);
        comboPeriodoVigencia.setForeground(TEXTO_ESCURO);

        colPeriodo.add(lblPeriodo, BorderLayout.NORTH);
        colPeriodo.add(comboPeriodoVigencia, BorderLayout.CENTER);

        gridCamposTopo.add(colTeto);
        gridCamposTopo.add(colPeriodo);

        chkNotificarCabecalho = new JCheckBox("Notificar automaticamente no cabeçalho quando atingir 80% do teto", true);
        chkNotificarCabecalho.setOpaque(false);
        chkNotificarCabecalho.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chkNotificarCabecalho.setForeground(new Color(51, 65, 85));
        chkNotificarCabecalho.setFocusPainted(false);
        chkNotificarCabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardTetoGlobal.add(lblSecao1);
        cardTetoGlobal.add(Box.createVerticalStrut(10));
        cardTetoGlobal.add(gridCamposTopo);
        cardTetoGlobal.add(Box.createVerticalStrut(10));
        cardTetoGlobal.add(chkNotificarCabecalho);

        painelCorpo.add(cardTetoGlobal);
        painelCorpo.add(Box.createVerticalStrut(16));

        // --- BLOCO 2: LIMITES POR CATEGORIA ---
        JPanel cabecalhoSecao2 = new JPanel(new BorderLayout());
        cabecalhoSecao2.setOpaque(false);
        cabecalhoSecao2.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalhoSecao2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lblSecao2 = new JLabel("2. Limites por Categoria (Teto Específico)",
                FontIcon.of(FontAwesomeSolid.LIST_ALT, 13, AZUL_PRIMARIO), JLabel.LEFT);
        lblSecao2.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSecao2.setForeground(TEXTO_ESCURO);
        lblSecao2.setIconTextGap(8);

        JLabel lblContagemCat = new JLabel(linhasCategorias.size() + " categorias cadastradas");
        lblContagemCat.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblContagemCat.setForeground(TEXTO_SECUNDARIO);

        cabecalhoSecao2.add(lblSecao2, BorderLayout.WEST);
        cabecalhoSecao2.add(lblContagemCat, BorderLayout.EAST);

        painelCorpo.add(cabecalhoSecao2);
        painelCorpo.add(Box.createVerticalStrut(8));

        // Tabela/Lista de Categorias
        JPanel boxTabelaCategorias = new JPanel();
        boxTabelaCategorias.setLayout(new BoxLayout(boxTabelaCategorias, BoxLayout.Y_AXIS));
        boxTabelaCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxTabelaCategorias.setBackground(Color.WHITE);
        boxTabelaCategorias.setBorder(BorderFactory.createLineBorder(BORDA_SUAVE));

        // Cabeçalho da Tabela
        JPanel headerTabela = new JPanel(new GridBagLayout());
        headerTabela.setBackground(new Color(226, 232, 240));
        headerTabela.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        headerTabela.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        adicionarCelulaGrid(headerTabela, criarLabelHeader("Categoria", SwingConstants.LEFT), 0, 0.48);
        adicionarCelulaGrid(headerTabela, criarLabelHeader("Limite Alocado", SwingConstants.RIGHT), 1, 0.24);
        adicionarCelulaGrid(headerTabela, criarLabelHeader("Consumo Realizado", SwingConstants.RIGHT), 2, 0.28);

        boxTabelaCategorias.add(headerTabela);

        // Linhas das 5 Categorias
        for (LinhaCategoriaUI cat : linhasCategorias) {
            JPanel linhaPanel = new JPanel(new GridBagLayout());
            linhaPanel.setBackground(Color.WHITE);
            linhaPanel.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 1, 0, FUNDO_CARD),
                    BorderFactory.createEmptyBorder(7, 14, 7, 14)));
            linhaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            // Coluna 1: Bolinha colorida + Nome da Categoria
            JPanel colNome = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            colNome.setOpaque(false);
            JLabel lblBolinha = new JLabel("●");
            lblBolinha.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblBolinha.setForeground(cat.corIndicador);
            JLabel lblNomeCat = new JLabel(cat.nome);
            lblNomeCat.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblNomeCat.setForeground(new Color(30, 41, 59));
            colNome.add(lblBolinha);
            colNome.add(lblNomeCat);

            // Coluna 2: Input Limite Alocado (R$ 700,00)
            JPanel wrapperInput = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            wrapperInput.setOpaque(false);
            JPanel boxInputCat = new JPanel(new BorderLayout(4, 0));
            boxInputCat.setBackground(FUNDO_CARD);
            boxInputCat.setPreferredSize(new Dimension(112, 26));
            boxInputCat.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDA_SUAVE),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));

            JLabel lblRsCat = new JLabel("R$");
            lblRsCat.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblRsCat.setForeground(TEXTO_SECUNDARIO);

            cat.campoLimite = criarCampoMonetario(cat.limitePadrao, 12, true);
            cat.campoLimite.setBackground(FUNDO_CARD);

            boxInputCat.add(lblRsCat, BorderLayout.WEST);
            boxInputCat.add(cat.campoLimite, BorderLayout.CENTER);
            wrapperInput.add(boxInputCat);

            // Coluna 3: Consumo Realizado + Mini Barra de Progresso
            JPanel colConsumo = new JPanel();
            colConsumo.setLayout(new BoxLayout(colConsumo, BoxLayout.Y_AXIS));
            colConsumo.setOpaque(false);

            cat.lblConsumoPercentual = new JLabel("R$ 0,00 (0%)", SwingConstants.RIGHT);
            cat.lblConsumoPercentual.setFont(new Font("SansSerif", Font.BOLD, 11));
            cat.lblConsumoPercentual.setAlignmentX(Component.RIGHT_ALIGNMENT);

            cat.barraConsumo = new JProgressBar(0, 100);
            cat.barraConsumo.setBorderPainted(false);
            cat.barraConsumo.setBackground(BORDA_SUAVE);
            cat.barraConsumo.setPreferredSize(new Dimension(110, 5));
            cat.barraConsumo.setMaximumSize(new Dimension(110, 5));
            cat.barraConsumo.setAlignmentX(Component.RIGHT_ALIGNMENT);

            colConsumo.add(cat.lblConsumoPercentual);
            colConsumo.add(Box.createVerticalStrut(3));
            colConsumo.add(cat.barraConsumo);

            adicionarCelulaGrid(linhaPanel, colNome, 0, 0.48);
            adicionarCelulaGrid(linhaPanel, wrapperInput, 1, 0.24);
            adicionarCelulaGrid(linhaPanel, colConsumo, 2, 0.28);

            boxTabelaCategorias.add(linhaPanel);
        }

        // Rodapé da Tabela de Categorias: Soma Categorizada + Total das Metas
        JPanel rodapeTabelaCategorias = new JPanel(new BorderLayout());
        rodapeTabelaCategorias.setBackground(new Color(226, 232, 240));
        rodapeTabelaCategorias.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        rodapeTabelaCategorias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        lblSomaCategorizada = new JLabel("Soma Categorizada: 100% alocada com o teto global",
                FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 13, VERDE_OK), JLabel.LEFT);
        lblSomaCategorizada.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblSomaCategorizada.setForeground(VERDE_OK);
        lblSomaCategorizada.setIconTextGap(6);

        JPanel painelTotalDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelTotalDireita.setOpaque(false);
        JLabel lblTxtTotalMetas = new JLabel("TOTAL DAS METAS:");
        lblTxtTotalMetas.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblTxtTotalMetas.setForeground(new Color(71, 85, 105));

        lblTotalMetas = new JLabel("R$ 1.800,00");
        lblTotalMetas.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTotalMetas.setForeground(TEXTO_ESCURO);

        painelTotalDireita.add(lblTxtTotalMetas);
        painelTotalDireita.add(lblTotalMetas);

        rodapeTabelaCategorias.add(lblSomaCategorizada, BorderLayout.WEST);
        rodapeTabelaCategorias.add(painelTotalDireita, BorderLayout.EAST);

        boxTabelaCategorias.add(rodapeTabelaCategorias);
        painelCorpo.add(boxTabelaCategorias);
        painelCorpo.add(Box.createVerticalStrut(12));

        // --- BANNER INFORMATIVO ---
        JPanel bannerInfo = new JPanel(new BorderLayout(10, 0));
        bannerInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bannerInfo.setBackground(FUNDO_CARD);
        bannerInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA_SUAVE),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        bannerInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel lblIconeInfo = new JLabel(FontIcon.of(FontAwesomeSolid.INFO_CIRCLE, 14, AZUL_PRIMARIO));
        JLabel lblTextoInfo = new JLabel(
                "<html>Os limites salvos entrarão em vigor imediatamente na lista atual e nos resumos da barra de status financeira.</html>");
        lblTextoInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTextoInfo.setForeground(new Color(71, 85, 105));

        bannerInfo.add(lblIconeInfo, BorderLayout.WEST);
        bannerInfo.add(lblTextoInfo, BorderLayout.CENTER);

        painelCorpo.add(bannerInfo);

        // ==========================================
        // 3. RODAPÉ DE AÇÕES (BOTÕES)
        // ==========================================
        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(Color.WHITE);
        painelRodape.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDA_SUAVE),
                BorderFactory.createEmptyBorder(12, 20, 14, 20)));

        JButton btnRestaurarPadroes = new JButton("Restaurar Padrões",
                FontIcon.of(FontAwesomeSolid.UNDO, 11, new Color(51, 65, 85)));
        btnRestaurarPadroes.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnRestaurarPadroes.setBackground(Color.WHITE);
        btnRestaurarPadroes.setForeground(new Color(51, 65, 85));
        btnRestaurarPadroes.setFocusPainted(false);
        btnRestaurarPadroes.setContentAreaFilled(false);
        btnRestaurarPadroes.setOpaque(true);
        btnRestaurarPadroes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRestaurarPadroes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        btnRestaurarPadroes.addActionListener(e -> restaurarPadroes());

        JPanel painelBotoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesDireita.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar [Esc]");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(new Color(51, 65, 85));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnCancelar.addActionListener(e -> fecharSemSalvar());

        JButton btnSalvar = new JButton("Salvar Configurações [Enter]",
                FontIcon.of(FontAwesomeSolid.CHECK, 11, Color.WHITE));
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnSalvar.setBackground(AZUL_PRIMARIO);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setContentAreaFilled(false);
        btnSalvar.setOpaque(true);
        btnSalvar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalvar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSalvar.addActionListener(e -> salvarEFechar());

        painelBotoesDireita.add(btnCancelar);
        painelBotoesDireita.add(btnSalvar);

        painelRodape.add(btnRestaurarPadroes, BorderLayout.WEST);
        painelRodape.add(painelBotoesDireita, BorderLayout.EAST);

        painelRaiz.add(painelTopo, BorderLayout.NORTH);
        painelRaiz.add(painelCorpo, BorderLayout.CENTER);
        painelRaiz.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelRaiz);
    }

    private JFormattedTextField criarCampoMonetario(double valorInicial, int tamanhoFonte, boolean negrito) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE_BR);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField campo = new JFormattedTextField(new DefaultFormatterFactory(formatter));
        campo.setValue(valorInicial);
        campo.setText(formatoDecimal.format(valorInicial));
        campo.setHorizontalAlignment(SwingConstants.RIGHT);
        campo.setFont(new Font("SansSerif", negrito ? Font.BOLD : Font.PLAIN, tamanhoFonte));
        campo.setForeground(TEXTO_ESCURO);
        campo.setBorder(null);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                double val = lerValorMonetario(campo.getText(), 0.0);
                campo.setText(formatoDecimal.format(val));
                recalcularIndicadores();
            }
        });

        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcularIndicadores();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcularIndicadores();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcularIndicadores();
            }
        });

        return campo;
    }

    private void recalcularIndicadores() {
        if (txtTetoGlobal == null || lblSomaCategorizada == null || lblTotalMetas == null) {
            return;
        }

        double tetoGlobal = lerValorMonetario(txtTetoGlobal.getText(), tetoGlobalSalvo);
        double somaCategorias = 0.0;

        for (LinhaCategoriaUI cat : linhasCategorias) {
            double limiteCat = lerValorMonetario(cat.campoLimite.getText(), cat.limitePadrao);
            somaCategorias += limiteCat;

            int percentual = limiteCat > 0 ? (int) Math.round((cat.consumoRealizado / limiteCat) * 100.0) : 0;
            Color corStatus = percentual >= 80 ? VERMELHO_ALERTA : VERDE_OK;

            cat.lblConsumoPercentual.setText("R$ " + formatoDecimal.format(cat.consumoRealizado) + " (" + percentual + "%)");
            cat.lblConsumoPercentual.setForeground(corStatus);

            cat.barraConsumo.setValue(Math.min(100, percentual));
            cat.barraConsumo.setForeground(corStatus);
        }

        lblTotalMetas.setText("R$ " + formatoDecimal.format(somaCategorias));

        int percentualAlocado = tetoGlobal > 0 ? (int) Math.round((somaCategorias / tetoGlobal) * 100.0) : 0;
        if (percentualAlocado == 100) {
            lblSomaCategorizada.setText("Soma Categorizada: 100% alocada com o teto global");
            lblSomaCategorizada.setForeground(VERDE_OK);
            lblSomaCategorizada.setIcon(FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 13, VERDE_OK));
        } else if (percentualAlocado < 100) {
            lblSomaCategorizada.setText("Soma Categorizada: " + percentualAlocado + "% alocada (" + (100 - percentualAlocado) + "% livre)");
            lblSomaCategorizada.setForeground(AZUL_PRIMARIO);
            lblSomaCategorizada.setIcon(FontIcon.of(FontAwesomeSolid.INFO_CIRCLE, 13, AZUL_PRIMARIO));
        } else {
            lblSomaCategorizada.setText("Atenção: Soma excede o teto global (" + percentualAlocado + "% alocado)");
            lblSomaCategorizada.setForeground(VERMELHO_ALERTA);
            lblSomaCategorizada.setIcon(FontIcon.of(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 13, VERMELHO_ALERTA));
        }
    }

    private void restaurarPadroes() {
        txtTetoGlobal.setText(formatoDecimal.format(1800.00));
        comboPeriodoVigencia.setSelectedIndex(0);
        chkNotificarCabecalho.setSelected(true);
        for (LinhaCategoriaUI cat : linhasCategorias) {
            cat.campoLimite.setText(formatoDecimal.format(cat.limitePadrao));
        }
        recalcularIndicadores();
    }

    private void configurarAtalhosTeclado() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelarModal");
        actionMap.put("cancelarModal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fecharSemSalvar();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "salvarModal");
        actionMap.put("salvarModal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarEFechar();
            }
        });
    }

    private void salvarEFechar() {
        this.tetoGlobalSalvo = lerValorMonetario(txtTetoGlobal.getText(), tetoGlobalSalvo);
        this.confirmado = true;
        dispose();
    }

    private void fecharSemSalvar() {
        this.confirmado = false;
        dispose();
    }

    private double lerValorMonetario(String texto, double padrao) {
        if (texto == null || texto.trim().isEmpty()) {
            return padrao;
        }
        try {
            String limpo = texto.replace("R$", "").replace(".", "").replace(",", ".").trim();
            return Double.parseDouble(limpo);
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    private JLabel criarLabelHeader(String texto, int alinhamento) {
        JLabel lbl = new JLabel(texto, alinhamento);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private void adicionarCelulaGrid(JPanel container, Component comp, int coluna, double pesoX) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = coluna;
        gbc.gridy = 0;
        gbc.weightx = pesoX;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(comp, gbc);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public double getTetoGlobalSalvo() {
        return tetoGlobalSalvo;
    }

    /**
     * Método utilitário que escurece o JFrame pai enquanto o modal está aberto.
     */
    public static ModalConfigurarMetas exibir(JFrame janelaPai, double tetoAtual) {
        JPanel overlayEscuro = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(15, 23, 42, 115));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayEscuro.setOpaque(false);

        Component glassAnterior = janelaPai.getGlassPane();
        janelaPai.setGlassPane(overlayEscuro);
        overlayEscuro.setVisible(true);

        ModalConfigurarMetas modal = new ModalConfigurarMetas(janelaPai, tetoAtual);
        modal.setVisible(true);

        overlayEscuro.setVisible(false);
        janelaPai.setGlassPane(glassAnterior);

        return modal;
    }
}