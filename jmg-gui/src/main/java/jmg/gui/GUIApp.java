package jmg.gui;

import com.formdev.flatlaf.FlatLightLaf;
import jmg.gui.form.jMGForm;

import javax.swing.*;

public class GUIApp {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(GUIApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        jMGForm.start();
    }
}
