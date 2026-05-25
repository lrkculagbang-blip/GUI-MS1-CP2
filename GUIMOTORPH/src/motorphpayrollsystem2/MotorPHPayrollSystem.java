package motorphpayrollsystem2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotorPHPayrollSystem extends JFrame {

    // ================= DATA =================
    ArrayList<Integer> empNo = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> position = new ArrayList<>();
    ArrayList<Double> rate = new ArrayList<>();

    HashMap<Integer, List<Attendance>> attendanceMap = new HashMap<>();

    DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter tf = DateTimeFormatter.ofPattern("H:mm");

    // ================= GUI =================
    CardLayout card = new CardLayout();
    JPanel main = new JPanel(card);

    JTextField user = new JTextField(15);
    JPasswordField pass = new JPasswordField(15);

    JTextField empField = new JTextField(10);

    JTextArea empOut = new JTextArea();
    JTextArea payOut = new JTextArea();

    JTable table;

    // ================= MAIN =================
    public static void main(String[] args) {
        new MotorPHPayrollSystem();
    }

    public MotorPHPayrollSystem() {

        loadEmployees();
        loadAttendance();

        setTitle("MOTORPH PAYROLL SYSTEM");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        main.add(loginPanel(), "login");
        main.add(employeePanel(), "employee");
        main.add(payrollPanel(), "payroll");

        add(main);
        setVisible(true);
    }

    // ================= LOGIN =================
    JPanel loginPanel() {

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel title = new JLabel("MOTORPH PAYROLL SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton login = new JButton("LOGIN");

        c.insets = new Insets(10, 10, 10, 10);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        p.add(title, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        p.add(new JLabel("Username:"), c);

        c.gridx = 1;
        p.add(user, c);

        c.gridx = 0; c.gridy = 2;
        p.add(new JLabel("Password:"), c);

        c.gridx = 1;
        p.add(pass, c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        p.add(login, c);

        login.addActionListener(e -> {

            String u = user.getText();
            String pw = new String(pass.getPassword());

            if (u.equals("employee") && pw.equals("12345")) {
                card.show(main, "employee");
            } else if (u.equals("payroll_staff") && pw.equals("12345")) {
                card.show(main, "payroll");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login");
            }
        });

        return p;
    }

    // ================= EMPLOYEE PANEL =================
    JPanel employeePanel() {

        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel();

        JButton info = new JButton("View Info");
        JButton payslip = new JButton("View Payslip");
        JButton logout = new JButton("Logout");

        top.add(new JLabel("Employee ID:"));
        top.add(empField);
        top.add(info);
        top.add(payslip);
        top.add(logout);

        empOut.setFont(new Font("Monospaced", Font.PLAIN, 12));
        empOut.setEditable(false);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(empOut), BorderLayout.CENTER);

        info.addActionListener(e -> showInfo(empField.getText()));
        payslip.addActionListener(e -> showPayslip(empField.getText(), empOut));
        logout.addActionListener(e -> card.show(main, "login"));

        return p;
    }

    // ================= PAYROLL PANEL =================
    JPanel payrollPanel() {

        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel();

        JButton one = new JButton("Process Employee");
        JButton all = new JButton("Process All");
        JButton logout = new JButton("Logout");

        top.add(one);
        top.add(all);
        top.add(logout);

        payOut.setFont(new Font("Monospaced", Font.PLAIN, 12));

        table = new JTable();
        loadTable();

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table),
                new JScrollPane(payOut)
        );

        one.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Enter Employee ID:");
            showPayslip(id, payOut);
        });

        all.addActionListener(e -> {
            payOut.setText("");
            for (int i = 0; i < empNo.size(); i++) {
                showPayslip(String.valueOf(empNo.get(i)), payOut);
            }
        });

        logout.addActionListener(e -> card.show(main, "login"));

        p.add(top, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);

        return p;
    }

    // ================= TABLE =================
    void loadTable() {

        String[] cols = {"ID", "Name", "Position", "Rate"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);

        for (int i = 0; i < empNo.size(); i++) {
            m.addRow(new Object[]{
                    empNo.get(i),
                    name.get(i),
                    position.get(i),
                    rate.get(i)
            });
        }

        table.setModel(m);
    }

    // ================= INFO =================
    void showInfo(String idStr) {

        int id = safe(idStr);
        int i = empNo.indexOf(id);

        if (i == -1) {
            empOut.setText("Employee not found");
            return;
        }

        empOut.setText(
                "ID: " + empNo.get(i) + "\n" +
                "Name: " + name.get(i) + "\n" +
                "Position: " + position.get(i) + "\n" +
                "Rate: " + rate.get(i)
        );
    }

    // ================= PAYSLIP =================
    void showPayslip(String idStr, JTextArea out) {

        int id = safe(idStr);
        int i = empNo.indexOf(id);

        if (i == -1) {
            out.append("\nEmployee not found\n");
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("\n==============================\n");
        sb.append("PAYSLIP: ").append(name.get(i)).append("\n");
        sb.append("==============================\n");

        for (int m = 6; m <= 6; m++) { // sample month (June)

            double c1 = hours(id, m, 1, 15) * rate.get(i);
            double c2 = hours(id, m, 16, 31) * rate.get(i);

            double gross = c1 + c2;

            // ===== FULL FORMULA (MS1 RULE) =====
            double sss = SSS(gross);
            double phil = PhilHealth(gross);
            double pag = PAGIBIG(gross);
            double tax = WithholdingTax(gross - (sss + phil + pag));

            double totalDeduction = sss + phil + pag + tax;

            double net1 = c1;                 // no deduction first cutoff
            double net2 = c2 - totalDeduction; // deduction applied here
            if (net2 < 0) net2 = 0;

            sb.append("\nCUT OFF 1 NET: ").append(net1);
            sb.append("\nCUT OFF 2 GROSS: ").append(c2);

            sb.append("\n--- DEDUCTIONS ---\n");
            sb.append("SSS: ").append(sss).append("\n");
            sb.append("PhilHealth: ").append(phil).append("\n");
            sb.append("PagIBIG: ").append(pag).append("\n");
            sb.append("Tax: ").append(tax).append("\n");

            sb.append("\nNET CUT OFF 2: ").append(net2);
            sb.append("\nMONTHLY NET: ").append(net1 + net2);
        }

        out.append(sb.toString());
    }

    // ================= HOURS =================
    double hours(int emp, int m, int s, int e) {

        List<Attendance> list = attendanceMap.get(emp);
        if (list == null) return 0;

        double t = 0;

        for (Attendance a : list) {
            if (a.date.getMonthValue() != m) continue;

            int d = a.date.getDayOfMonth();
            if (d < s || d > e) continue;

            t += Duration.between(a.in, a.out).toHours();
        }

        return t;
    }

    // ================= LOAD EMPLOYEES =================
    void loadEmployees() {

        InputStream is = getClass().getResourceAsStream("/employees.csv");

        if (is == null) {
            System.out.println("employees.csv not found");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            br.readLine();
            String l;

            while ((l = br.readLine()) != null) {

                String[] d = l.split(",");

                empNo.add(Integer.parseInt(d[0]));
                name.add(d[2] + " " + d[1]);
                position.add(d[11]);
                rate.add(Double.parseDouble(d[18].replace(",", "")));
            }

        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }

    // ================= LOAD ATTENDANCE =================
    void loadAttendance() {

        try {
            InputStream is = getClass().getResourceAsStream("/attendance.csv");
            if (is == null) return;

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            br.readLine();
            String l;

            while ((l = br.readLine()) != null) {

                String[] d = l.split(",");
                int id = Integer.parseInt(d[0]);

                attendanceMap
                        .computeIfAbsent(id, k -> new ArrayList<>())
                        .add(new Attendance(
                                LocalDate.parse(d[3], df),
                                LocalTime.parse(d[4], tf),
                                LocalTime.parse(d[5], tf)
                        ));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ================= UTIL =================
    int safe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return -1; }
    }

    class Attendance {
        LocalDate date;
        LocalTime in, out;

        Attendance(LocalDate d, LocalTime i, LocalTime o) {
            date = d; in = i; out = o;
        }
    }

    // ================= FORMULAS =================
    double SSS(double g) {
        if (g < 3250) return 135;
        else if (g < 3750) return 157.5;
        else if (g < 4250) return 180;
        else if (g < 4750) return 202.5;
        else return 1125;
    }

    double PhilHealth(double g) {
        double t = g * 0.03;
        return Math.min(Math.max(t / 2, 300), 1800);
    }

    double PAGIBIG(double g) {
        return Math.min((g <= 1500 ? g * 0.01 : g * 0.02), 100);
    }

    double WithholdingTax(double t) {
        if (t <= 20832) return 0;
        else if (t <= 33332) return (t - 20833) * 0.2;
        else return 10000;
    }
}
}
