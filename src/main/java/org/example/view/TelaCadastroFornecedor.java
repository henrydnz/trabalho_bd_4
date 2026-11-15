package org.example.view;


import javax.swing.*;
import java.awt.*;
import org.example.DaoEndereco;

public class TelaCadastroFornecedor extends JPanel {
    private final DaoEndereco daoEndereco = new DaoEndereco();

    private final JTextField txtNome = new JTextField();
    private final JTextField txtCnpj = new JTextField();
    private final JTextField txtEstado = new JTextField();
    private final JTextField txtCidade = new JTextField();
    private final JTextField txtBairro = new JTextField();

    // private final JComboBox<String> cbTipoLogradouro = new JComboBox<>(daoEndereco.getTipoLogradouro());

    private final JTextField txtLogradouro = new JTextField();
    private final JTextField txtNumero = new JTextField();
    private final JTextField txtComplemento = new JTextField();

    private final JTextArea txtTelefones = new JTextArea(3, 25);
    private final JTextArea txtEmails = new JTextArea(3, 25);

    // Botões
    private final JButton btnSalvar = new JButton("Salvar");
    private final JButton btnLimpar = new JButton("Limpar");

    public TelaCadastroFornecedor() {
        configurarTela();
        configurarEventos();
    }

    private void configurarTela() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Cadastro de Fornecedor");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int linha = 0;

        // Identificação
        adicionarCampo(formulario, gbc, linha++, "Nome:", txtNome);
        adicionarCampo(formulario, gbc, linha++, "CNPJ:", txtCnpj);

        // Endereço
        adicionarCampo(formulario, gbc, linha++, "Estado:", txtEstado);
        adicionarCampo(formulario, gbc, linha++, "Cidade:", txtCidade);
        adicionarCampo(formulario, gbc, linha++, "Bairro:", txtBairro);

//        adicionarCampo(formulario, gbc, linha++,
//                "Tipo de logradouro:", cbTipoLogradouro);

        adicionarCampo(formulario, gbc, linha++,
                "Logradouro:", txtLogradouro);

        adicionarCampo(formulario, gbc, linha++,
                "Número:", txtNumero);

        adicionarCampo(formulario, gbc, linha++,
                "Complemento:", txtComplemento);

        txtTelefones.setLineWrap(true);
        txtEmails.setLineWrap(true);

        adicionarCampo(formulario, gbc, linha++,
                "Telefones:", new JScrollPane(txtTelefones));

        adicionarCampo(formulario, gbc, linha++,
                "E-mails:", new JScrollPane(txtEmails));

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.weighty = 1;
        formulario.add(new JLabel(), gbc);

        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);

        add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnSalvar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, JComponent componente) {
        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel(rotulo), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(componente, gbc);
    }

    private void configurarEventos() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limpar());
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String cnpj = txtCnpj.getText().trim();

        String estado = txtEstado.getText().trim();
        String cidade = txtCidade.getText().trim();

        // String tipoLogradouro = (String) cbTipoLogradouro.getSelectedItem();

        String logradouro = txtLogradouro.getText().trim();
        String numero = txtNumero.getText().trim();
        String complemento = txtComplemento.getText().trim();


        String telefones = txtTelefones.getText().trim();
        String emails = txtEmails.getText().trim();

        System.out.println(nome);
        System.out.println(cnpj);
        System.out.println(estado);
        System.out.println(cidade);
        System.out.println(logradouro);
        System.out.println(numero);
        System.out.println(complemento);
        System.out.println(telefones);
        System.out.println(emails);
    }

    private void limpar() {
        txtNome.setText("");
        txtCnpj.setText("");
        txtEstado.setText("");
        txtCidade.setText("");
        txtBairro.setText("");
        // cbTipoLogradouro.setSelectedIndex(0);
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtComplemento.setText("");
        txtTelefones.setText("");
        txtEmails.setText("");
        txtNome.requestFocusInWindow();
    }

    // Teste independente da tela
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Cadastro de Fornecedor");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(new TelaCadastroFornecedor());
            janela.setSize(650, 650);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
