# نظام إدارة الموظفين
### مشروع البرمجة الكائنية المتقدمة
**المقرر:** البرمجة الكائنية المتقدمة | **مشروع جامعي - Sprint 2**

---

## 📋 وصف المشروع

تطبيق سطح مكتب مبني بـ Java Swing لإدارة الموظفين والحضور والرواتب والأقسام وطلبات الإجازة والورديات.

---

## 👥 بيانات الدخول التجريبية

| الدور        | البريد الإلكتروني          | كلمة المرور  | الصلاحيات                              |
|--------------|----------------------------|--------------|----------------------------------------|
| مدير النظام  | admin@company.com          | admin123     | وصول كامل لجميع الميزات               |
| مشرف         | manager@company.com        | manager123   | الموظفون، الحضور، اعتماد الإجازات      |
| موظف         | john@company.com           | emp123       | حضوري، إجازاتي، راتبي                  |
| موظف         | emily@company.com          | emp123       | حضوري، إجازاتي، راتبي                  |

---

## 🚀 طريقة التشغيل

### المتطلبات الأساسية
1. **Java JDK 11** أو أحدث
2. تحميل **مشغّل SQLite JDBC** من:  
   https://github.com/xerial/sqlite-jdbc/releases  
   → حمّل `sqlite-jdbc-3.47.1.0.jar`  
   → ضعه في مجلد `lib/`

### Windows
```
compile.bat   ← تجميع المشروع
run.bat       ← تشغيل التطبيق
```

### Mac / Linux
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### الأوامر اليدوية (Windows)
```cmd
javac -cp "lib\sqlite-jdbc-3.47.1.0.jar" -d out -sourcepath src src\Main.java src\model\*.java src\exception\*.java src\database\*.java src\gui\*.java
java -cp "out;lib\sqlite-jdbc-3.47.1.0.jar" Main
```

### الأوامر اليدوية (Mac/Linux)
```bash
javac -cp "lib/sqlite-jdbc-3.47.1.0.jar" -d out -sourcepath src src/Main.java src/model/*.java src/exception/*.java src/database/*.java src/gui/*.java
java -cp "out:lib/sqlite-jdbc-3.47.1.0.jar" Main
```

---

## 📁 هيكل المشروع

```
EmployeeManagementSystem/
│
├── src/
│   ├── Main.java                        ← نقطة الدخول
│   ├── model/
│   │   ├── Person.java                  ← فئة مجردة أساسية
│   │   ├── Employee.java                ← يرث Person، ينفذ Reportable
│   │   ├── Manager.java                 ← يرث Employee
│   │   ├── Admin.java                   ← يرث Person
│   │   ├── Department.java              ← نموذج القسم
│   │   ├── Attendance.java              ← نموذج الحضور
│   │   ├── LeaveRequest.java            ← نموذج طلب الإجازة
│   │   └── Reportable.java              ← واجهة التقارير
│   ├── database/
│   │   ├── DatabaseConnection.java      ← مدير اتصال SQLite
│   │   ├── EmployeeDAO.java             ← عمليات CRUD للموظفين
│   │   ├── AttendanceDAO.java           ← عمليات CRUD للحضور
│   │   └── LeaveRequestDAO.java         ← عمليات CRUD للإجازات
│   ├── gui/
│   │   ├── LoginForm.java               ← شاشة تسجيل الدخول + نظام الألوان
│   │   ├── MainDashboard.java           ← لوحة التحكم الرئيسية
│   │   ├── EmployeeManagementForm.java  ← إضافة/تعديل/حذف الموظفين
│   │   ├── AttendanceForm.java          ← سجلات الحضور والغياب
│   │   ├── LeaveRequestForm.java        ← طلبات الإجازة
│   │   ├── SalaryPanel.java             ← الرواتب والحاسبة
│   │   ├── DepartmentForm.java          ← إدارة الأقسام
│   │   └── ReportsPanel.java            ← التقارير
│   └── exception/
│       └── EmployeeException.java       ← استثناء مخصص
├── lib/
│   └── sqlite-jdbc-3.47.1.0.jar        ← مشغّل SQLite (حمّله منفصلاً)
├── out/                                 ← ملفات .class (تُنشأ تلقائياً)
├── compile.bat / compile.sh
├── run.bat / run.sh
└── README.md
```

---

## ✅ مفاهيم البرمجة الكائنية

| المفهوم              | الملف                     | التفاصيل                                               |
|----------------------|---------------------------|--------------------------------------------------------|
| **التجريد**          | `Person.java`             | `abstract String getRole()` - لا يمكن إنشاء كائن مباشر|
| **الواجهة**          | `Reportable.java`         | `generateReport()` + `getReportTitle()`                |
| **التغليف**          | جميع فئات model           | حقول خاصة + getters/setters                           |
| **الوراثة**          | `Employee→Person`         | سلسلة: `Manager→Employee→Person`                      |
| **تجاوز الطرق**      | `Manager.java`            | `@Override getRole()` و `getInfo()`                   |
| **تعدد الأشكال**     | `ReportsPanel.java`       | `emp.generateReport()` يعمل للموظف والمشرف            |
| **معالجة الاستثناءات**| `EmployeeDAO.java`       | `try-catch` مع `EmployeeException`                    |

---

## 🎨 نظام الألوان

| اللون       | الكود              | الاستخدام                |
|-------------|--------------------|--------------------------|
| أزرق ملكي   | `#1964C8`          | الأزرار الرئيسية، الهيدر  |
| أخضر        | `#00A050`          | إضافة، حاضر، نجاح        |
| أحمر        | `#C81E1E`          | حذف، خروج، خطأ           |
| برتقالي     | `#D26E00`          | الإجازات، تحذير           |
| بنفسجي      | `#781EB4`          | الأقسام                   |
| فيروزي      | `#008C8C`          | الرواتب، فيروزي           |

---

## 📝 توليد Javadoc

```bash
# Windows
javadoc -cp "lib\sqlite-jdbc-3.47.1.0.jar" -d docs -sourcepath src -subpackages model:database:gui:exception

# Mac/Linux
javadoc -cp "lib/sqlite-jdbc-3.47.1.0.jar" -d docs -sourcepath src -subpackages model:database:gui:exception
```
افتح `docs/index.html` في المتصفح.

---

## 👨‍💻 أعضاء الفريق

| الاسم | الرقم الجامعي | المساهمة |
|-------|--------------|----------|
| [الاسم 1] | [الرقم] | فئات النماذج وقاعدة البيانات |
| [الاسم 2] | [الرقم] | واجهات المستخدم وتسجيل الدخول |
| [الاسم 3] | [الرقم] | التقارير والتوثيق |

---
*تم تطوير هذا المشروع كجزء من مقرر البرمجة الكائنية المتقدمة.*
