package motorphpayrollsystem2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MotorPHPayrollSystem extends JFrame {

    // ================= DATA =================
    ArrayList<Integer> empNo = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> bday = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<String> phone = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> position = new ArrayList<>();
    ArrayList<Double> rate = new ArrayList<>();

    HashMap<Integer, List<Attendance>> attendanceMap = new HashMap<>();

    DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter tf = DateTimeFormatter.ofPattern("H:mm");

    // ================= GUI =================
    CardLayout card = new CardLayout();
    JPanel main = new JPanel(card);

    JTextField user = new JTextField(10);
    JPasswordField pass = new JPasswordField(10);

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

        setTitle("MOTOR PH PAYROLL SYSTEM");
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

    JLabel title = new JLabel("MOTOR PH PAYROLL SYSTEM");
    title.setFont(new Font("Arial", Font.BOLD, 20));

    JButton login = new JButton("LOGIN");
    login.setFont(new Font("Arial", Font.BOLD, 14));

    user.setPreferredSize(new java.awt.Dimension(200, 25));
    pass.setPreferredSize(new java.awt.Dimension(200, 25));

    c.insets = new java.awt.Insets(10,10,10,10);

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

    login.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String u = user.getText();
            String pw = new String(pass.getPassword());
            if (u.equals("employee") && pw.equals("12345")) {
                card.show(main,"employee");
            } else if (u.equals("payroll_staff") && pw.equals("12345")) {
                card.show(main,"payroll");
            } else {
                JOptionPane.showMessageDialog(MotorPHPayrollSystem.this, "Invalid login");
            }
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

        top.add(new JLabel("Employee #:"));
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

        logout.addActionListener(e -> card.show(main,"login"));

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
        payOut.setEditable(false);

        table = new JTable();

        JScrollPane tableScroll = new JScrollPane(table);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                tableScroll,
                new JScrollPane(payOut));

        loadTable();

        one.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this,"Enter Employee ID:");
            showPayslip(id, payOut);
        });

        all.addActionListener(e -> {
            payOut.setText("");
            for(int i=0;i<empNo.size();i++){
                showPayslip(String.valueOf(empNo.get(i)), payOut);
            }
        });
         logout.addActionListener(e -> {
        payOut.setText("");
        card.show(main,"login");
        });

        p.add(top, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);

        return p;
    }

    // ================= TABLE =================
    void loadTable(){

        String[] cols = {"ID","Name","Position","Rate"};

        DefaultTableModel m = new DefaultTableModel(cols,0);

        for(int i=0;i<empNo.size();i++){

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
    void showInfo(String idStr){

        int id = safe(idStr);
        int i = empNo.indexOf(id);

        if(i==-1){
            empOut.setText("Employee not found");
            return;
        }

        empOut.setText(
                "==== EMPLOYEE INFO ====\n"+
                        "ID: "+empNo.get(i)+"\n"+
                        "Name: "+name.get(i)+"\n"+
                        "Position: "+position.get(i)+"\n"+
                        "Rate: "+rate.get(i)
        );
    }

    // ================= PAYSLIP =================
    void showPayslip(String idStr, JTextArea out){

    int id = safe(idStr);
    int i = empNo.indexOf(id);

    if(i==-1){
        out.append("\nEmployee not found\n");
        return;
    }

    StringBuilder sb = new StringBuilder();

    sb.append("\n====================================\n");
    sb.append("PAYSLIP - ").append(name.get(i)).append("\n");
    sb.append("Position: ").append(position.get(i)).append("\n");
    sb.append("Rate: ").append(rate.get(i)).append("\n");
    sb.append("====================================\n");

    for(int m=6;m<=12;m++){

        double c1Hours = hours(id,m,1,15);
        double c2Hours = hours(id,m,16,31);

        double c1 = c1Hours * rate.get(i);
        double c2 = c2Hours * rate.get(i);

        double gross = c1 + c2;

        // FULL MONTH DEDUCTIONS (MS1 RULE)
        double sss = SSS(gross);
        double phil = PhilHealth(gross);
        double pag = PAGIBIG(gross);
        double tax = WithholdingTax(gross - (sss + phil + pag));

        double totalDeduction = sss + phil + pag + tax;

        double net1 = c1; // no deduction on first cutoff
        double net2 = c2 - totalDeduction;
        if(net2 < 0) net2 = 0;

        sb.append("\n------------------------------------\n");
        sb.append("MONTH: ").append(Month.of(m)).append("\n");
        sb.append("------------------------------------\n");

        // CUT OFF 1
        sb.append("CUT OFF 1 (1-15)\n");
        sb.append("Hours: ").append(c1Hours).append("\n");
        sb.append("Net Income: ").append(String.format("%.2f", net1)).append("\n");

        // CUT OFF 2
        sb.append("\nCUT OFF 2 (16-end)\n");
        sb.append("Hours: ").append(c2Hours).append("\n");
        sb.append("Gross Income: ").append(String.format("%.2f", c2)).append("\n");

        sb.append("\nDEDUCTIONS (based on monthly gross)\n");
        sb.append("SSS: ").append(String.format("%.2f", sss)).append("\n");
        sb.append("PhilHealth: ").append(String.format("%.2f", phil)).append("\n");
        sb.append("Pag-IBIG: ").append(String.format("%.2f", pag)).append("\n");
        sb.append("Tax: ").append(String.format("%.2f", tax)).append("\n");
        sb.append("Total Deduction: ").append(String.format("%.2f", totalDeduction)).append("\n");

        sb.append("\nNet Income Cutoff 2: ").append(String.format("%.2f", net2)).append("\n");

        sb.append("\nMONTHLY SUMMARY\n");
        sb.append("Gross: ").append(String.format("%.2f", gross)).append("\n");
        sb.append("Net: ").append(String.format("%.2f", net1 + net2)).append("\n");
    }

    out.append(sb.toString());
}

    // ================= HOURS =================
    double hours(int emp,int m,int s,int e){

        List<Attendance> list = attendanceMap.get(emp);
        if(list==null) return 0;

        double t=0;

        for(Attendance a:list){

            if(a.date.getMonthValue()!=m) continue;

            int d=a.date.getDayOfMonth();
            if(d<s||d>e) continue;

            t+=Duration.between(a.in,a.out).toHours();
        }

        return t;
    }

    // ================= LOAD EMPLOYEE =================
    void loadEmployees(){

        try(BufferedReader br =
            new BufferedReader(
                new InputStreamReader(
                    getClass().getResourceAsStream("/employees.csv")))){

            br.readLine();
            String l;

            while((l=br.readLine())!=null){

                String[] d=parse(l);

                empNo.add(Integer.parseInt(d[0]));
                name.add(d[2]+" "+d[1]);
                bday.add(d[3]);
                address.add(d[4]);
                phone.add(d[5]);
                status.add(d[10]);
                position.add(d[11]);
                rate.add(Double.parseDouble(d[18].replace(",","")));
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    // ================= LOAD ATTENDANCE =================
    void loadAttendance(){

        try(BufferedReader br =
            new BufferedReader(
                new InputStreamReader(
                    getClass().getResourceAsStream("/attendance.csv")))){

            br.readLine();
            String l;

            while((l=br.readLine())!=null){

                String[] d=parse(l);

                int id=Integer.parseInt(d[0]);

                attendanceMap
                        .computeIfAbsent(id,k->new ArrayList<>())
                        .add(new Attendance(
                                LocalDate.parse(d[3],df),
                                LocalTime.parse(d[4],tf),
                                LocalTime.parse(d[5],tf)
                        ));
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    // ================= CSV =================
    String[] parse(String s){

        List<String> o=new ArrayList<>();
        StringBuilder sb=new StringBuilder();
        boolean q=false;

        for(char c:s.toCharArray()){

            if(c=='"') q=!q;

            else if(c==','&&!q){
                o.add(sb.toString());
                sb.setLength(0);
            }else sb.append(c);
        }

        o.add(sb.toString());
        return o.toArray(new String[0]);
    }

    int safe(String s){
        try{return Integer.parseInt(s.trim());}
        catch(Exception e){return -1;}
    }

    class Attendance{
        LocalDate date;
        LocalTime in,out;

        Attendance(LocalDate d,LocalTime i,LocalTime o){
            date=d;in=i;out=o;
        }
    }

    // ================= FULL FORMULAS =================
    double SSS(double g){
        if(g<3250) return 135;
        else if(g<3750) return 157.5;
        else if(g<4250) return 180;
        else if(g<4750) return 202.5;
        else if(g<5250) return 225;
        else if(g<5750) return 247.5;
        else if(g<6250) return 270;
        else if(g<6750) return 292.5;
        else if(g<7250) return 315;
        else if(g<7750) return 337.5;
        else if(g<8250) return 360;
        else if(g<8750) return 382.5;
        else if(g<9250) return 405;
        else if(g<9750) return 427.5;
        else return 1125;
    }

    double PhilHealth(double g){
        double t=g*0.03;
        if(t<300) t=300;
        if(t>1800) t=1800;
        return t/2;
    }

    double PAGIBIG(double g){
        double r=(g<=1500)?g*0.01:g*0.02;
        return Math.min(r,100);
    }

    double WithholdingTax(double t){
        if(t<=20832) return 0;
        else if(t<=33332) return (t-20833)*0.2;
        else return 10000;
    }
}
