package jmg.gui.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

public class ComponentUtil {
    public static DocumentListener createDocumentListener(JTextComponent textField, Consumer<String> updateFunction) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // 文本改变时触发（对于普通文本字段可以忽略）
            }

            private void updateText() {
                String text = textField.getText();
                if (text.isEmpty()) {
                    updateFunction.accept(null);
                } else {
                    updateFunction.accept(text);
                }
            }
        };
    }

    /**
     * 恢复滚动条位置
     * @param scrollPane
     */
    public static void restoreScrollPosition(JScrollPane scrollPane) {
        try {
            //  windows 下窗口闪动
            scrollPane.setDoubleBuffered(true);
            int scrollValue = scrollPane.getVerticalScrollBar().getValue();
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(scrollValue);
            });
        } catch (Exception ignored) {
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static void setTextIfNotEmpty(JTextComponent component, Consumer<String> setter) {
        String text = component.getText().trim();
        if (!text.isEmpty()) {
            setter.accept(text);
        }
    }

    public static SimpleAttributeSet createSimpleAttributeSet(Color foregroundColor) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setItalic(attributeSet, false);
        StyleConstants.setForeground(attributeSet, foregroundColor);
        return attributeSet;
    }


}
