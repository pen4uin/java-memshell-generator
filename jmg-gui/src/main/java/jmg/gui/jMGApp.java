package jmg.gui;

import com.formdev.flatlaf.FlatLightLaf;
import jmg.gui.form.jMGForm;

import javax.swing.*;

public class jMGApp {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(jMGApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        jMGForm.start();
    }
}
