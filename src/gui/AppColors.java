package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * نظام الألوان والأنماط الموحّد للتطبيق كله.
 * كل لون زر غامق وخط أبيض واضح.
 *
 * @author فريق المشروع
 */
public class AppColors {

    // ================================================================
    //  الألوان الأساسية - غامقة وقوية
    // ================================================================
    public static final Color PRIMARY       = new Color(13,  71,  161); // أزرق داكن
    public static final Color PRIMARY_DARK  = new Color(0,   40,  120); // أزرق أعمق (hover)

    public static final Color SUCCESS       = new Color(27,  94,  32);  // أخضر داكن
    public static final Color SUCCESS_DARK  = new Color(10,  60,  15);

    public static final Color DANGER        = new Color(183, 28,  28);  // أحمر داكن
    public static final Color DANGER_DARK   = new Color(120, 10,  10);

    public static final Color WARNING       = new Color(230, 81,  0);   // برتقالي داكن
    public static final Color WARNING_DARK  = new Color(160, 50,  0);

    public static final Color PURPLE        = new Color(74,  20,  140); // بنفسجي داكن
    public static final Color PURPLE_DARK   = new Color(45,  5,   100);

    public static final Color TEAL          = new Color(0,   96,  100); // فيروزي داكن
    public static final Color TEAL_DARK     = new Color(0,   60,  65);

    public static final Color DARK          = new Color(33,  33,  33);  // أسود دافئ
    public static final Color DARK_HOVER    = new Color(10,  10,  10);

    // ================================================================
    //  ألوان الخلفية والنصوص
    // ================================================================
    public static final Color BG_MAIN       = new Color(236, 239, 245); // خلفية رمادية فاتحة
    public static final Color BG_WHITE      = Color.WHITE;
    public static final Color BG_HEADER     = new Color(13,  71,  161); // نفس PRIMARY
    public static final Color BG_ROW_ALT    = new Color(245, 248, 255); // صفوف بديلة

    public static final Color TEXT_WHITE    = Color.WHITE;
    public static final Color TEXT_DARK     = new Color(20,  20,  40);
    public static final Color TEXT_MUTED    = new Color(90,  90,  110);

    public static final Color BORDER_COLOR  = new Color(180, 195, 220);
    public static final Color TABLE_HEADER  = new Color(13,  71,  161);

    // ألوان حالات الحضور
    public static final Color ROW_PRESENT   = new Color(200, 240, 200);
    public static final Color ROW_ABSENT    = new Color(255, 205, 205);
    public static final Color ROW_LATE      = new Color(255, 243, 195);

    // ألوان حالات الإجازة
    public static final Color ROW_APPROVED  = new Color(200, 240, 200);
    public static final Color ROW_REJECTED  = new Color(255, 205, 205);
    public static final Color ROW_PENDING   = new Color(255, 243, 195);

    // ================================================================
    //  مصنع الأزرار - لون غامق + خط أبيض
    // ================================================================

    /**
     * يُنشئ زراً بلون غامق ونص أبيض مع تأثير hover.
     */
    public static JButton button(String text, Color bg, Color bgHover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgHover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    /** زر أزرق داكن */
    public static JButton btnPrimary(String text) {
        return button(text, PRIMARY, PRIMARY_DARK);
    }

    /** زر أخضر داكن */
    public static JButton btnSuccess(String text) {
        return button(text, SUCCESS, SUCCESS_DARK);
    }

    /** زر أحمر داكن */
    public static JButton btnDanger(String text) {
        return button(text, DANGER, DANGER_DARK);
    }

    /** زر برتقالي داكن */
    public static JButton btnWarning(String text) {
        return button(text, WARNING, WARNING_DARK);
    }

    /** زر بنفسجي داكن */
    public static JButton btnPurple(String text) {
        return button(text, PURPLE, PURPLE_DARK);
    }

    /** زر فيروزي داكن */
    public static JButton btnTeal(String text) {
        return button(text, TEAL, TEAL_DARK);
    }

    /** زر رمادي داكن */
    public static JButton btnDark(String text) {
        return button(text, DARK, DARK_HOVER);
    }

    // ================================================================
    //  مساعدات الحقول والتسميات
    // ================================================================

    /** تسمية بنص عريض داكن */
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        return l;
    }

    /** حقل نص بتنسيق موحّد */
    public static JTextField textField(String initial) {
        JTextField f = new JTextField(initial);
        styleField(f);
        return f;
    }

    public static JTextField textField() {
        return textField("");
    }

    /** تطبيق تنسيق موحّد على أي حقل نص */
    public static void styleField(JTextField f) {
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setBackground(BG_WHITE);
        f.setForeground(TEXT_DARK);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    /** ComboBox بتنسيق موحّد */
    public static void styleCombo(JComboBox<?> c) {
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setBackground(BG_WHITE);
        c.setForeground(TEXT_DARK);
        c.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    // ================================================================
    //  مساعدات الجداول
    // ================================================================

    /** تطبيق تنسيق موحّد على الجدول */
    public static void styleTable(JTable t) {
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(26);
        t.setGridColor(new Color(210, 220, 235));
        t.setSelectionBackground(new Color(173, 216, 255));
        t.setSelectionForeground(TEXT_DARK);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));

        // رأس الجدول
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        t.getTableHeader().setBackground(TABLE_HEADER);
        t.getTableHeader().setForeground(Color.BLACK);
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        t.getTableHeader().setReorderingAllowed(false);
    }

    // ================================================================
    //  مساعدات الألواح
    // ================================================================

    /** شريط عنوان أزرق داكن */
    public static JPanel headerBar(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_HEADER);
        p.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel t = new JLabel("  " + title);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        t.setForeground(TEXT_WHITE);
        p.add(t, BorderLayout.WEST);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel s = new JLabel(subtitle + "  ");
            s.setFont(new Font("Arial", Font.PLAIN, 12));
            s.setForeground(new Color(190, 210, 255));
            p.add(s, BorderLayout.EAST);
        }
        return p;
    }

    /** لوحة قسم بعنوان ملوّن */
    public static Border sectionBorder(String title, Color color) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2),
            "  " + title + "  ",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 13),
            color
        );
    }
}
