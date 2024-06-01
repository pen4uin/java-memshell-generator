package jmg.gui.util;


import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * 控制文本颜色,提升用户体验
 */
public class TextPaneUtil {
    private static JTextPane textPane;
    private static final Font font;
    private static final SimpleAttributeSet ERROR_ATT;
    private static final SimpleAttributeSet WARN_ATT;
    private static final SimpleAttributeSet SUCCESS_ATT;
    private static final SimpleAttributeSet RAW_ATT;
    private static final SimpleAttributeSet START_ATT;


    static {
        font = new Font("Lucida Grande", Font.PLAIN, 13);
        ERROR_ATT = ComponentUtil.createSimpleAttributeSet(new Color(255, 0, 0));
        WARN_ATT = ComponentUtil.createSimpleAttributeSet(new Color(255,165,0));
        SUCCESS_ATT = ComponentUtil.createSimpleAttributeSet(new Color(70,135,55));
        RAW_ATT = ComponentUtil.createSimpleAttributeSet(new Color(0, 0, 0));
        START_ATT = ComponentUtil.createSimpleAttributeSet(Color.gray);
    }


    public static void rawPrintln(String str) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("%s\n", str), RAW_ATT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void successPrintln(String str) {

        try {
            // 对 jexpr-encoder-utils 输出的处理
            if (str.startsWith("[+]")) {
                textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("%s\n", str.replace("==>", "==>\n").replace("<==", "<==\n\n\n\n")), SUCCESS_ATT);
            } else {
                textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("[+] %s\n", str), SUCCESS_ATT);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warningPrintln(String str) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("[!] %s\n", str), WARN_ATT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void errorPrintln(String str) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("[x] %s\n", str), ERROR_ATT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startPrintln(String str) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), String.format("[>] %s\n", str), START_ATT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////
    // 以下内部类全都用于实现自动强制折行
    // https://github.com/MrYKK/oimchat/blob/598aedd94767667498d66d1ed682f073f3f181b7/oim-fx/src/test/java/swing/JIMSendTextPane.java
    /////////////////////////////////////////////////////////////
    public static class WarpEditorKit extends StyledEditorKit {
        private static final long serialVersionUID = 1L;
        private ViewFactory defaultFactory = new WarpColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    private static class WarpColumnFactory implements ViewFactory {

        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WarpLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    private static class WarpLabelView extends LabelView {

        public WarpLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////

    public static JTextPane getTextPane() {
        return textPane;
    }

    public static void initTextPane(JTextPane textPane) {
        TextPaneUtil.textPane = textPane;
        TextPaneUtil.textPane.setEditorKit(new WarpEditorKit());
        TextPaneUtil.textPane.setFont(font);
    }
}
