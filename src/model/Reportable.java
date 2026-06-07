package model;

/**
 * واجهة للكائنات التي يمكنها توليد تقارير.
 * <p>
 * مفهوم البرمجة الكائنية: الواجهة (Interface) - تُعرّف عقداً يجب على الفئات اتباعه.
 * </p>
 *
 * @author فريق المشروع
 * @version 1.0
 */
public interface Reportable {

    /**
     * يُولّد ويُرجع تقريراً بصيغة نصية.
     * @return نص التقرير
     */
    String generateReport();

    /**
     * يُرجع عنوان التقرير.
     * @return عنوان التقرير
     */
    String getReportTitle();
}
