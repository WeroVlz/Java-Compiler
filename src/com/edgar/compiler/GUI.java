package com.edgar.compiler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class GUI implements ActionListener {

    private JFrame frame;
    private DefaultTableModel model;
    private DefaultTreeModel parserTreeModel;
    private static JTextArea console;

    public GUI() {
        initializeGui();
    }

    private void initializeGui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Font customFont = new Font("Consolas", Font.PLAIN, 22);

        int frameWidth = (int) (screenSize.width * 0.8);
        int frameHeight = (int) (screenSize.height * 0.8);

        frame = new JFrame();
        frame.setTitle("EVM Compiler");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(frameWidth, frameHeight);

        frame.setLocationRelativeTo(null);

        JTextArea editor = new JTextArea();
        console = new JTextArea(2,0);

        EmptyBorder paddingBorder = new EmptyBorder(5, 5, 5, 5);
        console.setBorder(paddingBorder);

        JMenuBar menuBar = createMenuBar(editor);
        JPanel mainPanel = createMainPanel(customFont, editor);

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public JMenuBar createMenuBar(JTextArea editor) {

        JMenuBar menuBarC = new JMenuBar();

        Dimension menuBarSize = new Dimension(menuBarC.getPreferredSize().width, 40);
        menuBarC.setPreferredSize(menuBarSize);

        JMenu fileMenu = new JMenu("File");

        JMenuItem newMenuItem = new JMenuItem("New...");
        ImageIcon newIcon = new ImageIcon("resources/newfile.png");
        newMenuItem.setIcon(newIcon);
        newMenuItem.setIconTextGap(10);

        newMenuItem.addActionListener(
                e -> {
                    editor.setText("");
                    console.setText("");
                    model.setRowCount(0);
                }
        );

        JMenuItem saveMenuItem = getSaveMenuItem(editor);


        JMenuItem openMenuItem = getOpenMenuItem(editor);
        fileMenu.add(newMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(openMenuItem);

        JButton runButton = getRunButton(editor);

        Box container = Box.createHorizontalBox();
        container.add(Box.createHorizontalGlue());
        container.add(runButton);
        container.add(Box.createHorizontalStrut(10));

        menuBarC.add(fileMenu);
        menuBarC.add(container);

        return menuBarC;
    }

    private JMenuItem getOpenMenuItem(JTextArea editor) {
        JMenuItem openMenuItem = new JMenuItem("Open");
        ImageIcon openIcon = new ImageIcon("resources/open.png");
        openMenuItem.setIcon(openIcon);
        openMenuItem.setIconTextGap(10);

        openMenuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
                    fileChooser.setFileFilter(fileFilter);

                    int fileChooserResult = fileChooser.showOpenDialog(frame);

                    if( fileChooserResult == JFileChooser.APPROVE_OPTION){
                        File file = fileChooser.getSelectedFile();

                        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                            StringBuilder content = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null){
                                content.append(line).append("\n");
                            }
                            editor.setText(content.toString());
                        }catch(IOException err){
                            err.fillInStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error opening text from file",
                                    "Open Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
        );
        return openMenuItem;
    }

    private JButton getRunButton(JTextArea editor) {
        JButton runButton = new JButton("Run");
        runButton.setBackground(new Color(28, 162, 56));

        runButton.setFocusPainted(false);
        ImageIcon runIcon = new ImageIcon("resources/play.png");
        runButton.setIcon(runIcon);

        runButton.addActionListener(
                e -> {
                    long startTime = System.nanoTime();
                    console.setText("");
                    Lexer lexer = new Lexer(editor.getText());
                    Vector<Token> tokens = lexer.getTokens();
                    model.setRowCount(0);
                    fillTable(model,tokens);

                    DefaultMutableTreeNode treeRoot = Parser.run(tokens, this);
                    fillParserTree(parserTreeModel, treeRoot);

                    consoleOutput(console, tokens.size(), lexer.getRows(), lexer.getErrorCount());

                    long endTime = System.nanoTime();
                    long executionTime = endTime - startTime;

                    console.append("\nExecution finished after: " + executionTime / 1000000 + " ms");

                }
        );
        return runButton;
    }

    private JMenuItem getSaveMenuItem(JTextArea editor) {
        JMenuItem saveMenuItem = new JMenuItem("Save");
        ImageIcon saveIcon = new ImageIcon("resources/save.png");
        saveMenuItem.setIcon(saveIcon);
        saveMenuItem.setIconTextGap(10);

        saveMenuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
                    fileChooser.setFileFilter(fileFilter);

                    int fileChooserResult = fileChooser.showSaveDialog(frame);

                    if (fileChooserResult == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();

                        // Make sure the file has the correct extension
                        if (!file.getName().toLowerCase().endsWith(".txt")) {
                            file = new File(file.getAbsolutePath() + ".txt");
                        }

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                            writer.write(editor.getText());
                            JOptionPane.showMessageDialog(frame, "Text exported to " + file.getAbsolutePath(),
                                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException err) {
                            err.fillInStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error exporting text to file",
                                    "Export Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
        );
        return saveMenuItem;
    }


    public JPanel createMainPanel(Font customFont, JTextArea editor) {

        JPanel mainPanelC = new JPanel();

        mainPanelC.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;

        // ---------- Editor --------------
        editor.setFont(customFont);
        JScrollPane editorScrollPane = new JScrollPane(editor);
        editorScrollPane.setPreferredSize(new Dimension((int) (frame.getPreferredSize().getWidth() * 0.5), 100));
        setGbcData(gbc, 0,0,0.6,0.66,1, 2);

        mainPanelC.add(editorScrollPane, gbc);

        // ------------------- Tabbed Pane -----------------
        JTabbedPane tabbedPane = new JTabbedPane();

        // *** Lexical Analysis ***
        String[] columnNames = {"Line", "Word", "Token"};
        Object[][] data = {};
        model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane variableExplorer = new JScrollPane(table);
        variableExplorer.setPreferredSize(new Dimension((int) (frame.getPreferredSize().getWidth() * 0.5), 100));
        // *** Parser Tree ***
        parserTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Parser Expression Tree"));
        JTree parserTree = new JTree(parserTreeModel);
        JScrollPane parserTreeScrollable = new JScrollPane(parserTree);
        parserTreeScrollable.setPreferredSize(new Dimension((int) (frame.getPreferredSize().getWidth() * 0.5), 100));



        tabbedPane.add("Variable Explorer", variableExplorer);
        tabbedPane.add("Parser Tree", parserTreeScrollable);

        setGbcData(gbc,1,0,0.4,0.66,1, 2);

        mainPanelC.add(tabbedPane, gbc);

        // --------------- Console ------------------
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(customFont);
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);

        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setGbcData(gbc, 0,2,1.0,0.33,2,1);
        Border consoleBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Console");
        consoleScrollPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0), consoleBorder));

        mainPanelC.add(consoleScrollPane, gbc);
        return mainPanelC;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private void consoleOutput(JTextArea console, int numTokens, int rowCount, int errorCount){
        if (!console.getText().isEmpty()){
            console.append("\n");
        }
        console.append(numTokens + " strings found in " + (rowCount-1) + " lines.\n");
        console.append(errorCount + " strings do not match any rule.\n");
    }

    public void writeConsoleLine(String message){
        console.append(message + "\n");
    }

    private void fillTable(DefaultTableModel model, Vector<Token> tokens){
        for(Token token: tokens){
            model.addRow(new Object[]{token.getRow(),token.getWord(),token.getToken()});
        }
    }

    private void fillParserTree(DefaultTreeModel treeModel, DefaultMutableTreeNode newRoot){
        treeModel.setRoot(newRoot);
    }

    private void setGbcData(GridBagConstraints gbc, int gridX, int gridY, double weightX, double weightY, int gridW, int gridH){
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        gbc.gridwidth = gridW;
        gbc.gridheight = gridH;
    }
}
