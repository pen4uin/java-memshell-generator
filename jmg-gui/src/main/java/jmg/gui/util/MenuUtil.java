package jmg.gui.util;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import jmg.core.config.Constants;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

public class MenuUtil {
    public static JMenuBar createMenuBar(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createSettingMenu(frame));
        menuBar.add(createAboutnMenu(frame));
        return menuBar;
    }


    private static JMenu createSettingMenu(JFrame frame) {
        JMenu settingsMenu = new JMenu("设置");
        settingsMenu.add(MenuUtil.createThemeMenu(frame));
        return settingsMenu;
    }


    public static JPopupMenu createPopupMenu(JFrame frame, JTextPane textPane) {
        // 创建右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copySelected = new JMenuItem("复制选中部分");
        JMenuItem copyAll = new JMenuItem("复制全部");
        JMenuItem saveAs = new JMenuItem("保存为");

        // 添加菜单项的动作监听器
        copySelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedText = textPane.getSelectedText();
                if (selectedText != null) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(selectedText), null);
                }
            }
        });

        copyAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String allText = textPane.getText();
                if (allText != null) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(allText), null);
                }
            }
        });

        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    String textToSave = textPane.getText();
                    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(selectedFilePath)))) {
                        writer.print(textToSave);
                        writer.flush();
                        JOptionPane.showMessageDialog(frame, "文件保存成功！");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "保存文件时出错：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        popupMenu.add(copySelected);
        popupMenu.add(copyAll);
        popupMenu.add(saveAs);
        return popupMenu;
    }

    private static JMenu createThemeMenu(JFrame frame) {
        JMenu themeMenu = new JMenu("主题");

        String[] themeNames = {"FlatLight", "FlatDarcula", "FlatIntelliJ", "FlatMacDark", "FlatDark", "FlatMacLight"};

        for (String themeName : themeNames) {
            JMenuItem themeItem = createThemeMenuItem(frame, themeName);
            themeMenu.add(themeItem);
        }
        return themeMenu;
    }

    private static JMenuItem createThemeMenuItem(JFrame frame, String themeName) {
        JMenuItem themeItem = new JMenuItem(themeName);
        themeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLookAndFeel(themeName, frame);
            }
        });
        return themeItem;
    }

    private static void setLookAndFeel(String themeName, JFrame frame) {
        try {
            switch (themeName) {
                case "FlatLight":
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    break;
                case "FlatDark":
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    break;
                case "FlatIntelliJ":
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
                case "FlatDarcula":
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    break;
                case "FlatMacLight":
                    UIManager.setLookAndFeel(new FlatMacLightLaf());
                    break;
                case "FlatMacDark":
                    UIManager.setLookAndFeel(new FlatMacDarkLaf());
                    break;
                default:
                    UIManager.setLookAndFeel(new NimbusLookAndFeel());
                    break;
            }
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }


    private static JMenu createAboutnMenu(JFrame frame) {
        try {
            JMenu verMenu = new JMenu("关于");
            JMenuItem authorItem = new JMenuItem("作者");
            JMenuItem versionItem = new JMenuItem("版本");
            versionItem.addActionListener(e -> {
                try {
                    JOptionPane.showMessageDialog(frame, "社区版 " + Constants.JMG_VERSION);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            authorItem.addActionListener(e -> {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    URI oURL = new URI("https://github.com/pen4uin");
                    desktop.browse(oURL);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            authorItem.setToolTipText("pen4uin");
            verMenu.add(authorItem);
            verMenu.add(versionItem);
            return verMenu;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
