package jmg.gui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.core.generator.InjectorGenerator;
import jmg.core.util.ClassNameUtil;
import jmg.core.util.CommonUtil;
import jmg.core.util.RandomHttpHeaderUtil;
import jmg.gui.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class jMGForm {
    private static JFrame frame;
    private JComboBox toolBox;
    private JComboBox serverBox;
    private JComboBox shellBox;
    private JComboBox formatBox;
    private JTextField uriText;
    private JTextField headerValueText;
    private JTextField keyText;
    private JTextField headerNameText;
    private JTextField passText;
    private JTextField injectorClsNameText;
    private JTextField shellClsNameText;
    private JCheckBox enableGadget;
    private JComboBox gadgetTypeBox;
    private JCheckBox enableExpr;
    private JComboBox exprBox;
    private JButton generateButton;
    private JPanel jMGPanel;
    private JPanel TopPanel;
    private JLabel toolLabel;
    private JLabel formatLabel;
    private JLabel serverLabel;
    private JLabel shellLabel;
    private JScrollPane textScrollPane;
    private JPanel MiddlePanel;
    private JLabel keyLabel;
    private JLabel passLabel;
    private JLabel headerValueLabel;
    private JLabel headerNameLabel;
    private JLabel uriLabel;
    private JLabel shellClsNameLabel;
    private JLabel injectorClsNameLabel;
    private JSeparator TopSep;
    private JSeparator MiddleSep;
    private JLabel authorLabel;
    private JLabel noticeLabel;
    private JPanel BottomPanel;
    private JPanel TipPanel;
    private JTextPane textPane;
    private JCheckBox bypassJDKModuleCheckBox;

    private AbstractConfig config;


    public static void start() {
        Locale.setDefault(Locale.CHINA);
        frame = new JFrame(Constants.JMG_NAME + " " + Constants.JMG_VERSION);

        // 将窗口居中显示，解决 windows 下靠左的问题
        frame.setLocationRelativeTo(null);

        frame.setResizable(true);
        jMGForm jmgForm = new jMGForm();
        JPanel contentPanel = jmgForm.jMGPanel;

        contentPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setJMenuBar(MenuUtil.createMenuBar(frame));
        frame.pack();
        frame.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;
        Dimension jfSize = frame.getSize();
        int halfwidth = jfSize.width / 2;
        int halfHeight = jfSize.height / 2;
        frame.setLocation(centerX - halfwidth, centerY - halfHeight);
    }

    private String serverType = Constants.SERVER_TOMCAT;
    private String shellType = Constants.SHELL_LISTENER;
    private String gadgetType = Constants.GADGET_JDK_TRANSLET;
    private String toolType = Constants.TOOL_BEHINDER;
    private String formatType = Constants.FORMAT_BASE64;

    public jMGForm() {
        config = new AbstractConfig();

        ArrayList<String> servletApiShellBox = new ArrayList<>(Arrays.asList(
                Constants.SHELL_LISTENER, Constants.SHELL_FILTER, Constants.SHELL_JAKARTA_LISTENER, Constants.SHELL_JAKARTA_FILTER,
                Constants.SHELL_VALVE
        ));

        ArrayList<String> servletApiServerBox = new ArrayList<>(Arrays.asList(
                Constants.SERVER_TOMCAT, Constants.SERVER_RESIN, Constants.SERVER_WEBLOGIC, Constants.SERVER_WEBSPHERE,
                Constants.SERVER_JETTY, Constants.SERVER_UNDERTOW, Constants.SERVER_GLASSFISH, Constants.SERVER_JBOSS
        ));

        ArrayList<String> interceptorServerBox = new ArrayList<>(Arrays.asList(Constants.SERVER_SPRING_MVC));
        ArrayList<String> interceptorShellBox = new ArrayList<>(Arrays.asList(Constants.SHELL_INTERCEPTOR));
        ArrayList<String> handlerMethodServerBox = new ArrayList<>(Arrays.asList(Constants.SERVER_SPRING_WEBFLUX));
        ArrayList<String> handlerMethodShellBox = new ArrayList<>(Arrays.asList(Constants.SHELL_WF_HANDLERMETHOD));

        ArrayList<String> behinderServerBox = new ArrayList<>();
        behinderServerBox.addAll(servletApiServerBox);
        behinderServerBox.addAll(interceptorServerBox);

        ArrayList<String> behinderShellBox = new ArrayList<>();
        behinderShellBox.addAll(servletApiShellBox);
        behinderShellBox.addAll(interceptorShellBox);

        ArrayList<String> godzillaServerBox = new ArrayList<>();
        godzillaServerBox.addAll(behinderServerBox);
        godzillaServerBox.addAll(handlerMethodServerBox);

        ArrayList<String> godzillaShellBox = new ArrayList<>();
        godzillaShellBox.addAll(behinderShellBox);
        godzillaShellBox.addAll(handlerMethodShellBox);

        ArrayList<String> formatBoxForOther = new ArrayList<>(Arrays.asList(
                Constants.FORMAT_BASE64, Constants.FORMAT_BIGINTEGER, Constants.FORMAT_BCEL, Constants.FORMAT_CLASS,
                Constants.FORMAT_JAR, Constants.FORMAT_JS, Constants.FORMAT_JSP
        ));

        ArrayList<String> formatBoxForTomcat = new ArrayList<>(Arrays.asList(
                Constants.FORMAT_BASE64, Constants.FORMAT_BIGINTEGER, Constants.FORMAT_BCEL, Constants.FORMAT_CLASS,
                Constants.FORMAT_JAR, Constants.FORMAT_JAR_AGENT, Constants.FORMAT_JS, Constants.FORMAT_JSP
        ));

        ArrayList<String> exprEncoderBoxItems = new ArrayList<>(Arrays.asList(
                Constants.EXPR_EL, Constants.EXPR_SPEL, Constants.EXPR_OGNL, Constants.EXPR_FREEMARKER, Constants.EXPR_VELOCITY, Constants.EXPR_JS
        ));

        ArrayList<String> gadgetTypeBoxItems = new ArrayList<>(Arrays.asList(
                Constants.GADGET_JDK_TRANSLET, Constants.GADGET_SNAKEYAML, Constants.GADGET_FJ_GROOVY, Constants.GADGET_XALAN_TRANSLET
        ));

        // 设置模型
        formatBox.setModel(new DefaultComboBoxModel<>(formatBoxForTomcat.toArray(new String[0])));
        serverBox.setModel(new DefaultComboBoxModel<>(behinderServerBox.toArray(new String[0])));
        shellBox.setModel(new DefaultComboBoxModel<>(servletApiShellBox.toArray(new String[0])));

        toolBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolType = (String) toolBox.getSelectedItem();
                if (toolType.equals(Constants.TOOL_GODZILLA)) {
                    serverBox.setModel(new DefaultComboBoxModel(godzillaServerBox.toArray(new String[0])));
                    shellBox.setModel(new DefaultComboBoxModel(godzillaShellBox.toArray(new String[0])));

                } else if (toolType.equals(Constants.TOOL_ANTSWORD)) {
                    shellBox.setModel(new DefaultComboBoxModel(servletApiShellBox.toArray(new String[0])));
                    serverBox.setModel(new DefaultComboBoxModel(servletApiServerBox.toArray(new String[0])));
                } else if (toolType.equals(Constants.TOOL_CUSTOM)) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileFilter classFileFilter = new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.isDirectory() || file.getName().toLowerCase().endsWith(".class");
                        }

                        @Override
                        public String getDescription() {
                            return "Java Class Files (*.class)";
                        }
                    };

                    fileChooser.setFileFilter(classFileFilter);

                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    int result = fileChooser.showSaveDialog(jMGPanel);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        String selectedPath = fileChooser.getSelectedFile().getPath();
                        config.setClassFilePath(selectedPath);
                    }
                } else {
                    serverBox.setModel(new DefaultComboBoxModel(behinderServerBox.toArray(new String[0])));
                    shellBox.setModel(new DefaultComboBoxModel(behinderShellBox.toArray(new String[0])));
                }
            }
        });


        serverBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                serverType = (String) serverBox.getSelectedItem();
                if (serverType.equals(Constants.SERVER_SPRING_MVC)) {
                    shellBox.setModel(new DefaultComboBoxModel<>(interceptorShellBox.toArray(new String[0])));
                    shellType = (String) shellBox.getSelectedItem();
                } else if (serverType.equals(Constants.SERVER_SPRING_WEBFLUX)) {
                    shellBox.setModel(new DefaultComboBoxModel<>(handlerMethodShellBox.toArray(new String[0])));
                    shellType = (String) shellBox.getSelectedItem();
                } else {
                    shellBox.setModel(new DefaultComboBoxModel<>(servletApiShellBox.toArray(new String[0])));
                    shellType = (String) shellBox.getSelectedItem();
                }
                if (!serverType.equals(Constants.SERVER_TOMCAT) && !serverType.equals(Constants.SERVER_SPRING_MVC)) {
                    formatBox.setModel(new DefaultComboBoxModel<>(formatBoxForOther.toArray(new String[0])));
                } else {
                    formatBox.setModel(new DefaultComboBoxModel<>(formatBoxForTomcat.toArray(new String[0])));
                }
            }
        });


        shellBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shellType = (String) shellBox.getSelectedItem();
            }

        });

        formatBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                formatType = (String) formatBox.getSelectedItem();
                assert formatType != null;
                if (formatType.equalsIgnoreCase(Constants.FORMAT_CLASS) || formatType.equalsIgnoreCase(Constants.FORMAT_JSP) || formatType.equalsIgnoreCase(Constants.FORMAT_JAR) || formatType.equalsIgnoreCase(Constants.FORMAT_JAR_AGENT)) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result = fileChooser.showSaveDialog(jMGPanel);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        String selectedPath = fileChooser.getSelectedFile().getPath();
                        config.setSavePath(selectedPath);
                    }
                }
            }
        });


        // 可选参数设置
        passText.getDocument().putProperty("owner", passText);
        passText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(passText, config::setPass));

        keyText.getDocument().putProperty("owner", keyText);
        keyText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(keyText, config::setKey));

        shellClsNameText.getDocument().putProperty("owner", shellClsNameText);
        shellClsNameText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(shellClsNameText, config::setShellClassName));

        injectorClsNameText.getDocument().putProperty("owner", injectorClsNameText);
        injectorClsNameText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(injectorClsNameText, config::setInjectorClassName));

        uriText.getDocument().putProperty("owner", uriText);
        uriText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(uriText, config::setUrlPattern));

        headerNameText.getDocument().putProperty("owner", headerNameText);
        headerNameText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(headerNameText, config::setHeaderName));

        headerValueText.getDocument().putProperty("owner", headerValueText);
        headerValueText.getDocument().addDocumentListener(ComponentUtil.createDocumentListener(headerValueText, config::setHeaderValue));


        exprBox.setEnabled(false);

        enableExpr.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (enableExpr.isSelected()) {
                    config.setExprEncoder(Constants.EXPR_EL);
                    exprBox.setEnabled(enableExpr.isSelected());
                } else {
                    exprBox.setEnabled(enableExpr.isSelected());
                    config.setExprEncoder(null);
                    exprBox.setModel(new DefaultComboBoxModel(exprEncoderBoxItems.toArray(new String[0])));
                }

            }
        });

        exprBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && exprBox.isEnabled()) {
                    config.setExprEncoder((String) exprBox.getSelectedItem());
                } else {
                    config.setExprEncoder(null);
                }
            }
        });

        gadgetTypeBox.setEnabled(false);
        enableGadget.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (enableGadget.isSelected()) {
                    config.setGadgetType(gadgetType);
                    gadgetTypeBox.setEnabled(enableGadget.isSelected());
                } else {
                    gadgetTypeBox.setEnabled(enableGadget.isSelected());
                    config.setGadgetType(null);
                    gadgetTypeBox.setModel(new DefaultComboBoxModel(gadgetTypeBoxItems.toArray(new String[0])));
                }
            }
        });

        gadgetTypeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && gadgetTypeBox.isEnabled()) {
                    config.setGadgetType((String) gadgetTypeBox.getSelectedItem());
                } else {
                    config.setGadgetType(null);
                }
            }
        });

        bypassJDKModuleCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                config.setEnableBypassJDKModule(bypassJDKModuleCheckBox.isSelected());
            }
        });
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextPaneUtil.initTextPane(textPane);
                TextPaneUtil.startPrintln(toolType + " " + serverType + " " + shellType + " " + formatType + "\n");
                try {
                    initConfig(config);
                    new ShellGeneratorUtil().makeShell(config);
                    new InjectorGenerator().makeInjector(config);
                    ResultUtil.resultOutput(config);
                    ComponentUtil.restoreScrollPosition(textScrollPane);
                    resetConfig(config);
                } catch (Throwable ex) {
                    resetConfig(config);
                    TextPaneUtil.errorPrintln(CommonUtil.getThrowableStackTrace(ex));
                }
            }
        });

        textPane.setComponentPopupMenu(MenuUtil.createPopupMenu(frame, textPane));


    }

    public void initConfig(AbstractConfig config) {
        config.setToolType(toolType);
        config.setServerType(serverType);
        config.setShellType(shellType);
        config.setOutputFormat(formatType);

        ComponentUtil.setTextIfNotEmpty(passText, config::setPass);
        ComponentUtil.setTextIfNotEmpty(keyText, config::setKey);
        ComponentUtil.setTextIfNotEmpty(shellClsNameText, config::setShellClassName);
        ComponentUtil.setTextIfNotEmpty(injectorClsNameText, config::setInjectorClassName);
        ComponentUtil.setTextIfNotEmpty(uriText, config::setUrlPattern);
        ComponentUtil.setTextIfNotEmpty(headerNameText, config::setHeaderName);
        ComponentUtil.setTextIfNotEmpty(headerValueText, config::setHeaderValue);

        if (config.getShellClassName() == null)
            config.setShellClassName(ClassNameUtil.getRandomShellClassName(config.getShellType()));
        if (config.getInjectorClassName() == null)
            config.setInjectorClassName(ClassNameUtil.getRandomInjectorClassName());
        Map.Entry<String, String> header = RandomHttpHeaderUtil.generateHeader();
        if (config.getHeaderName() == null) config.setHeaderName(header.getKey());
        if (config.getHeaderValue() == null) config.setHeaderValue(header.getValue());
        if (config.getUrlPattern() == null || config.getUrlPattern().equals("/*") || config.getUrlPattern().equals("/")) {
            if (config.getShellType().equals(Constants.SHELL_WF_HANDLERMETHOD)) {
                config.setUrlPattern("/" + CommonUtil.getRandomString(6).toLowerCase());
            } else {
                config.setUrlPattern("/*");
            }
        }
        if (config.getOutputFormat().contains(Constants.FORMAT_BCEL))
            config.setLoaderClassName(ClassNameUtil.getRandomLoaderClassName());
        config.setInjectorSimpleClassName(CommonUtil.getSimpleName(config.getInjectorClassName()));

    }

    public void resetConfig(AbstractConfig config) {
        config.setPass(null);
        config.setKey(null);
        config.setShellClassName(null);
        config.setInjectorClassName(null);
        config.setHeaderName(null);
        config.setHeaderValue(null);
        config.setUrlPattern(null);
        config.setInjectorSimpleClassName(null);

    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jMGPanel = new JPanel();
        jMGPanel.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        TopPanel = new JPanel();
        TopPanel.setLayout(new GridLayoutManager(1, 8, new Insets(0, 0, 0, 0), -1, -1));
        jMGPanel.add(TopPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        toolLabel = new JLabel();
        this.$$$loadLabelText$$$(toolLabel, this.$$$getMessageFromBundle$$$("messages", "tool.text"));
        TopPanel.add(toolLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        formatLabel = new JLabel();
        this.$$$loadLabelText$$$(formatLabel, this.$$$getMessageFromBundle$$$("messages", "format.text"));
        TopPanel.add(formatLabel, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        serverLabel = new JLabel();
        this.$$$loadLabelText$$$(serverLabel, this.$$$getMessageFromBundle$$$("messages", "server.text"));
        TopPanel.add(serverLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        shellLabel = new JLabel();
        this.$$$loadLabelText$$$(shellLabel, this.$$$getMessageFromBundle$$$("messages", "shell.text"));
        TopPanel.add(shellLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toolBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Behinder");
        defaultComboBoxModel1.addElement("Godzilla");
        defaultComboBoxModel1.addElement("AntSword");
        defaultComboBoxModel1.addElement("Suo5");
        defaultComboBoxModel1.addElement("NeoreGeorg");
        defaultComboBoxModel1.addElement("Custom");
        toolBox.setModel(defaultComboBoxModel1);
        TopPanel.add(toolBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(139, -1), null, 0, false));
        serverBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Tomcat");
        defaultComboBoxModel2.addElement("SpringMVC");
        defaultComboBoxModel2.addElement("Weblogic");
        defaultComboBoxModel2.addElement("Websphere");
        defaultComboBoxModel2.addElement("Resin");
        defaultComboBoxModel2.addElement("Undertow");
        defaultComboBoxModel2.addElement("Jetty");
        defaultComboBoxModel2.addElement("SpringWebFlux");
        serverBox.setModel(defaultComboBoxModel2);
        TopPanel.add(serverBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(139, -1), null, 0, false));
        shellBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("Listener");
        defaultComboBoxModel3.addElement("Filter");
        defaultComboBoxModel3.addElement("Interceptor");
        defaultComboBoxModel3.addElement("WFHandlerMethod");
        shellBox.setModel(defaultComboBoxModel3);
        TopPanel.add(shellBox, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        formatBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("BASE64");
        defaultComboBoxModel4.addElement("BIGINTEGER");
        defaultComboBoxModel4.addElement("BCEL");
        defaultComboBoxModel4.addElement("CLASS");
        defaultComboBoxModel4.addElement("JAR");
        defaultComboBoxModel4.addElement("JS");
        defaultComboBoxModel4.addElement("JSP");
        formatBox.setModel(defaultComboBoxModel4);
        TopPanel.add(formatBox, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        textScrollPane = new JScrollPane();
        jMGPanel.add(textScrollPane, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 360), new Dimension(36, 207), null, 0, false));
        textPane = new JTextPane();
        textPane.setText("");
        textScrollPane.setViewportView(textPane);
        MiddlePanel = new JPanel();
        MiddlePanel.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
        jMGPanel.add(MiddlePanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        keyLabel = new JLabel();
        this.$$$loadLabelText$$$(keyLabel, this.$$$getMessageFromBundle$$$("messages", "key.text"));
        MiddlePanel.add(keyLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passLabel = new JLabel();
        this.$$$loadLabelText$$$(passLabel, this.$$$getMessageFromBundle$$$("messages", "pass.text"));
        MiddlePanel.add(passLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(34, 16), null, 0, false));
        headerValueLabel = new JLabel();
        this.$$$loadLabelText$$$(headerValueLabel, this.$$$getMessageFromBundle$$$("messages", "headerValue.text"));
        MiddlePanel.add(headerValueLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uriText = new JTextField();
        uriText.setText("/*");
        uriText.setToolTipText("可选，默认为 /*");
        MiddlePanel.add(uriText, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        headerValueText = new JTextField();
        headerValueText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(headerValueText, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        keyText = new JTextField();
        keyText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(keyText, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        headerNameLabel = new JLabel();
        this.$$$loadLabelText$$$(headerNameLabel, this.$$$getMessageFromBundle$$$("messages", "headerName.text"));
        MiddlePanel.add(headerNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uriLabel = new JLabel();
        this.$$$loadLabelText$$$(uriLabel, this.$$$getMessageFromBundle$$$("messages", "uri.text"));
        MiddlePanel.add(uriLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        headerNameText = new JTextField();
        headerNameText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(headerNameText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        passText = new JTextField();
        passText.setText("");
        passText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(passText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        shellClsNameLabel = new JLabel();
        this.$$$loadLabelText$$$(shellClsNameLabel, this.$$$getMessageFromBundle$$$("messages", "shellClsName.text"));
        MiddlePanel.add(shellClsNameLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        injectorClsNameLabel = new JLabel();
        this.$$$loadLabelText$$$(injectorClsNameLabel, this.$$$getMessageFromBundle$$$("messages", "injectorClsName.text"));
        MiddlePanel.add(injectorClsNameLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        injectorClsNameText = new JTextField();
        injectorClsNameText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(injectorClsNameText, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        shellClsNameText = new JTextField();
        shellClsNameText.setToolTipText("可选，默认随机生成");
        MiddlePanel.add(shellClsNameText, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        TopSep = new JSeparator();
        jMGPanel.add(TopSep, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        MiddleSep = new JSeparator();
        jMGPanel.add(MiddleSep, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        BottomPanel = new JPanel();
        BottomPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        jMGPanel.add(BottomPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        enableGadget = new JCheckBox();
        this.$$$loadButtonText$$$(enableGadget, this.$$$getMessageFromBundle$$$("messages", "gadget.text"));
        enableGadget.setToolTipText("根据漏洞类型自动完成对类文件的封装，如继承类、实现接口、添加注解等");
        BottomPanel.add(enableGadget, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gadgetTypeBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("JDK_AbstractTranslet");
        defaultComboBoxModel5.addElement("SnakeYaml");
        defaultComboBoxModel5.addElement("XALAN_AbstractTranslet");
        defaultComboBoxModel5.addElement("FastjsonGroovy");
        gadgetTypeBox.setModel(defaultComboBoxModel5);
        BottomPanel.add(gadgetTypeBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enableExpr = new JCheckBox();
        this.$$$loadButtonText$$$(enableExpr, this.$$$getMessageFromBundle$$$("messages", "expr.text"));
        enableExpr.setToolTipText("根据语句类型自动生成相应内存马注入的表达式语句");
        BottomPanel.add(enableExpr, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exprBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("EL");
        defaultComboBoxModel6.addElement("SpEL");
        defaultComboBoxModel6.addElement("OGNL");
        defaultComboBoxModel6.addElement("FreeMarker");
        defaultComboBoxModel6.addElement("Velocity");
        defaultComboBoxModel6.addElement("ScriptEngineManager(JS)");
        exprBox.setModel(defaultComboBoxModel6);
        BottomPanel.add(exprBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateButton = new JButton();
        this.$$$loadButtonText$$$(generateButton, this.$$$getMessageFromBundle$$$("messages", "generate.text"));
        BottomPanel.add(generateButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bypassJDKModuleCheckBox = new JCheckBox();
        bypassJDKModuleCheckBox.setText("Bypass JDK Module");
        bypassJDKModuleCheckBox.setToolTipText("绕过高版本 JDK Module 访问限制");
        BottomPanel.add(bypassJDKModuleCheckBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TipPanel = new JPanel();
        TipPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        jMGPanel.add(TipPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        TipPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        authorLabel = new JLabel();
        authorLabel.setText("请勿用于非法用途");
        TipPanel.add(authorLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        noticeLabel = new JLabel();
        noticeLabel.setText("by pen4uin");
        TipPanel.add(noticeLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jMGPanel;
    }

}
