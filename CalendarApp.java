import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


public class CalendarApp {
    private JFrame frame;
    private JTable calendarTable;
    private DefaultTableModel model;
    private JButton btnAddSchedule;
    private JTextArea scheduleArea;
    //private Map<String, String> schedules = new HashMap<>();
    private Map<String, LinkedList<String>> schedules = new HashMap<>();

    public CalendarApp() {
        frame = new JFrame("予定表");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Calendar cal = Calendar.getInstance();
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        model = new DefaultTableModel(null, headers) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        calendarTable = new JTable(model);
        fillCalendar(cal);
        calendarTable.setRowSelectionAllowed(false);
        calendarTable.setColumnSelectionAllowed(false);
        //calendarTable.setColumnSelectionAllowed(true);

        btnAddSchedule = new JButton("予定追加");
        btnAddSchedule.addActionListener(e -> {
            addSchedule(cal); // この行を追加
        });

        //btnAddSchedule.addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        int row = calendarTable.getSelectedRow();
        //        int col = calendarTable.getSelectedColumn();
        //        if (row != -1 && col != -1) {
        //            String date = model.getValueAt(row, col).toString();
        //            String key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + date;
//
        //            String currentSchedule = schedules.getOrDefault(key, "");
        //            String newSchedule = JOptionPane.showInputDialog(frame, "予定を入力してください:", currentSchedule);
//
        //            if (newSchedule != null && !newSchedule.trim().isEmpty()) {
        //                schedules.put(key, newSchedule);
        //                scheduleArea.setText(newSchedule);
        //            }
        //        }
        //    }
        //});

    JButton btnPrevMonth = new JButton("先月");
    JButton btnNextMonth = new JButton("来月");
    JLabel lblCurrentMonth = new JLabel();

    // 現在の月を表示
    updateMonthLabel(cal, lblCurrentMonth);

    // 先月ボタンのアクション
    btnPrevMonth.addActionListener(e -> {
        cal.add(Calendar.MONTH, -1);
        fillCalendar(cal);
        updateMonthLabel(cal, lblCurrentMonth);
    });

    // 来月ボタンのアクション
    btnNextMonth.addActionListener(e -> {
        cal.add(Calendar.MONTH, 1);
        fillCalendar(cal);
        updateMonthLabel(cal, lblCurrentMonth);
    });

    loadScheduleData();  // 起動時にデータを読み込む

    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            saveScheduleData();  // ウィンドウが閉じられる際にデータを保存する
        }
    });
        //calendarTable.getSelectionModel().addListSelectionListener(e -> {
        //    if (!e.getValueIsAdjusting()) {
        //        int row = calendarTable.getSelectedRow();
        //        int col = calendarTable.getSelectedColumn();
        //        if (row != -1 && col != -1) {
        //            String date = model.getValueAt(row, col).toString();
        //            String key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + date;
        //            scheduleArea.setText(schedules.getOrDefault(key, ""));
        //        }
        //    }
        //});
        calendarTable.getSelectionModel().addListSelectionListener(e -> updateTextArea(cal));
        calendarTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> updateTextArea(cal));

        scheduleArea = new JTextArea(10, 40);
        scheduleArea.setEditable(false);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(btnPrevMonth, BorderLayout.WEST);
        panelTop.add(lblCurrentMonth, BorderLayout.CENTER);
        panelTop.add(btnNextMonth, BorderLayout.EAST);

        frame.add(panelTop, BorderLayout.NORTH);
        frame.add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        frame.add(btnAddSchedule, BorderLayout.WEST);
        frame.add(new JScrollPane(scheduleArea), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    // 予定データを保存するメソッド
    private void saveScheduleData() {
        try (FileOutputStream fos = new FileOutputStream("plan.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(schedules);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 予定データを読み込むメソッド
    private void loadScheduleData() {
        File file = new File("plan.dat");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                schedules = (Map<String, LinkedList<String>>) ois.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateTextArea(Calendar cal) {
        int row = calendarTable.getSelectedRow();
        int col = calendarTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            Object value = model.getValueAt(row, col);
            if (value != null) {
                String date = value.toString();
                String key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + date;
                //scheduleArea.setText(schedules.getOrDefault(key, ""));
                LinkedList<String> currentSchedules = schedules.getOrDefault(key, new LinkedList<>());
                scheduleArea.setText(String.join("\n", currentSchedules));
            } else {
                scheduleArea.setText("");
            }
        }
    }

    private void updateMonthLabel(Calendar cal, JLabel label) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        label.setText(sdf.format(cal.getTime()));

    }

    private void fillCalendar(Calendar cal) {
        model.setRowCount(0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int monthStart = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Object[] week = new Object[7];
        for (int i = 1; i <= daysInMonth; i++) {
            week[monthStart] = i;
            if (monthStart == 6 || i == daysInMonth) {
                model.addRow(week);
                week = new Object[7];
            }
            monthStart = (monthStart + 1) % 7;
        }
    }
    private void addSchedule(Calendar cal) {
        int row = calendarTable.getSelectedRow();
        int col = calendarTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            String date = model.getValueAt(row, col).toString();
            String key = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + date;

            LinkedList<String> currentSchedules = schedules.getOrDefault(key, new LinkedList<>());
            String newSchedule = JOptionPane.showInputDialog(frame, "予定を入力してください:", String.join("\n", currentSchedules));

            if (newSchedule != null && !newSchedule.trim().isEmpty()) {
                if (currentSchedules.size() >= 3) {
                    currentSchedules.poll();
                }
                currentSchedules.add(newSchedule);
                schedules.put(key, currentSchedules);
                scheduleArea.setText(String.join("\n", currentSchedules));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarApp());
    }
}
