import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.Preferences; // added for saving light/dark mode preference across sessions

public class TheCal {//Authored by Vaibhav Thakkar, Ariane Quenum, Michael Woelfel
    // Modified by Arek Gubala
    
    static JLabel LabelMonth;
    static JButton buttonPrev, buttonNext, buttonDarkMode;
    static JTable tabelCal;
    static JComboBox comboYear;
    static JFrame mainFrame;
    static Container pane;
    static JScrollPane theScrollPane;
    static JPanel panelCal, headerPanel;
    static int todayYear, todayMonth, todayDay, currentYear, currentMonth;
    static DefaultTableModel mtabelCal;
    static boolean darkMode = false; // [ADDED] tracks current theme state
    static Map<String, List<String>> reminders = new HashMap<>();

    // color palette for both light and dark mode
    static final Color LIGHT_BG           = Color.white;
    static final Color LIGHT_TEXT         = Color.black;
    static final Color LIGHT_TODAY        = new Color(255, 220, 220);
    static final Color LIGHT_WEEKEND      = new Color(235, 235, 255);
    static final Color LIGHT_SELECTED     = new Color(204, 255, 204);
    static final Color LIGHT_INACTIVE_FG  = new Color(180, 180, 180);
    static final Color LIGHT_INACTIVE_WKD = new Color(245, 245, 255);
    static final Color LIGHT_INACTIVE_WKD2= new Color(250, 250, 250);
    static final Color LIGHT_SEL_INACTIVE = new Color(235, 255, 235);
    static final Color LIGHT_HEADER       = new Color(238, 238, 238);
 
    static final Color DARK_BG            = new Color(30, 30, 30);
    static final Color DARK_TEXT          = new Color(220, 220, 220);
    static final Color DARK_TODAY         = new Color(120, 50, 50);
    static final Color DARK_WEEKEND       = new Color(40, 40, 70);
    static final Color DARK_SELECTED      = new Color(40, 80, 40);
    static final Color DARK_INACTIVE_FG   = new Color(90, 90, 90);
    static final Color DARK_INACTIVE_WKD  = new Color(35, 35, 55);
    static final Color DARK_INACTIVE_WKD2 = new Color(28, 28, 28);
    static final Color DARK_SEL_INACTIVE  = new Color(30, 55, 30);
 
    static final Color DARK_PANEL         = new Color(45, 45, 45);
    static final Color DARK_HEADER        = new Color(40, 40, 40);

    static String reminderKey(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }
    static void showReminderDialog(int year, int month, int day) {
        String key = reminderKey(year, month, day);
        String[] months = {"January","February","March","April","May","June",
                       "July","August","September","October","November","December"};
        List<String> dayReminders = reminders.computeIfAbsent(key, k -> new ArrayList<>());
        JDialog dialog = new JDialog(mainFrame, months[month] + " " + day + ", " + year, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setMinimumSize(new Dimension(350, 280));
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String r : dayReminders) listModel.addElement(r);
        JList<String> list = new JList<>(listModel);
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JTextField field = new JTextField();
        JButton addBtn = new JButton("Add");
        JButton delBtn = new JButton("Delete");
        addBtn.addActionListener(e -> {
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                dayReminders.add(text);
                field.setText("");
                tabelCal.repaint();
            }
        });
        field.addActionListener(e -> addBtn.doClick()); // Enter key adds reminder
        delBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                listModel.remove(idx);
                dayReminders.remove(idx);
                tabelCal.repaint();
            }
        }); 
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnRow.add(delBtn);
        btnRow.add(addBtn);
        bottom.add(field, BorderLayout.CENTER);
        bottom.add(btnRow, BorderLayout.SOUTH);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    public static void main (String args[]){
        Preferences prefs = Preferences.userNodeForPackage(TheCal.class);
        darkMode = prefs.getBoolean("darkMode", false);

        mainFrame = new JFrame ("LibreCalendar");
        pane = mainFrame.getContentPane();
        pane.setLayout(new BorderLayout()); // Set the main window to BorderLayout to allow for dynamic component placement
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        LabelMonth = new JLabel ("January", SwingConstants.CENTER); // Set month label with centered text
        LabelMonth.setFont(new Font("TimesNewRoman", Font.BOLD, 32)); 
        LabelMonth.setPreferredSize(new Dimension(190, 40));

        comboYear = new JComboBox();
        buttonPrev = new JButton ("<<");
        buttonNext = new JButton (">>");

        buttonDarkMode = new JButton("\u263E");
        buttonDarkMode.setFont(new Font("Serif", Font.PLAIN, 18));
        buttonDarkMode.setToolTipText("Toggle dark mode");
        buttonDarkMode.addActionListener(new buttonDarkMode_Action());

        mtabelCal = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tabelCal = new JTable(mtabelCal);
        theScrollPane = new JScrollPane(tabelCal);
        panelCal = new JPanel(new BorderLayout());
        
        panelCal.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        buttonPrev.addActionListener(new buttonPrev_Action());
        buttonNext.addActionListener(new buttonNext_Action());
        comboYear.addActionListener(new comboYear_Action());
        
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.add(buttonPrev);
        headerPanel.add(LabelMonth);
        headerPanel.add(comboYear);
        headerPanel.add(buttonNext);
        headerPanel.add(buttonDarkMode);
                

        panelCal.add(headerPanel, BorderLayout.NORTH);
        panelCal.add(theScrollPane, BorderLayout.CENTER);
        pane.add(panelCal, BorderLayout.CENTER);
        
        mainFrame.setResizable(true);
        
        GregorianCalendar cal = new GregorianCalendar(); 
        todayDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
        todayMonth = cal.get(GregorianCalendar.MONTH);
        todayYear = cal.get(GregorianCalendar.YEAR);
        currentMonth = todayMonth;
        currentYear = todayYear;
            
        String[] headers = {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i=0; i<7; i++){
            mtabelCal.addColumn(headers[i]);
        }

        tabelCal.getParent().setBackground(tabelCal.getBackground());
        tabelCal.getTableHeader().setResizingAllowed(false);
        tabelCal.getTableHeader().setReorderingAllowed(false);
        tabelCal.setColumnSelectionAllowed(true);
        tabelCal.setRowSelectionAllowed(true);
        tabelCal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabelCal.setShowGrid(true);
        tabelCal.setDefaultRenderer(Object.class, new tabelCalRenderer());

        tabelCal.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBackground(darkMode ? DARK_HEADER : LIGHT_HEADER);
                setForeground(darkMode ? DARK_TEXT : LIGHT_TEXT);
                setBorder(BorderFactory.createMatteBorder(0, column == 0 ? 1 : 0, 1, 1,
                    darkMode ? new Color(40, 40, 40) : new Color(200, 200, 200)));
                    
                return this;
            }
        });

        tabelCal.setGridColor(new Color(225, 225, 225));
        
        for (int i=todayYear-100; i<=todayYear+100; i++){
            comboYear.addItem(String.valueOf(i));
        }

         // Clear selection w/ light green cell when Escape key is pressed
        tabelCal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearHighlight");
                
        tabelCal.getActionMap().put("clearHighlight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tabelCal.clearSelection(); 
            }
        });
        tabelCal.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabelCal.rowAtPoint(e.getPoint());
                    int col = tabelCal.columnAtPoint(e.getPoint());
                    Object val = mtabelCal.getValueAt(row, col);
                    if (val != null) {
                        int dateVal = Integer.parseInt(val.toString());
                        if (dateVal > 0) { // only open for current month days
                            showReminderDialog(currentYear, currentMonth, dateVal);
                        }
                    }
                }
            }
        });
        // Handle verticle scaling of calendar cells when the window is resized
        theScrollPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateRowHeights();
            }
        });

        refreshCalendar (todayMonth, todayYear);
        mainFrame.setMinimumSize(new Dimension(600, 450));
        mainFrame.pack();
        buttonDarkMode.setText(darkMode ? "\u2600" : "\u263E");
        applyTheme();
        mainFrame.setVisible(true);
    }
    
    //  Repaints all non-table UI components to match the current darkMode state
    public static void applyTheme() {
        Color bg     = darkMode ? DARK_PANEL  : null;  // null = let Swing use its default
        Color header = darkMode ? DARK_HEADER : null;
        Color fg     = darkMode ? DARK_TEXT   : null;
 
        panelCal.setBackground(bg);
        panelCal.setOpaque(darkMode);
 
        headerPanel.setBackground(header);
        headerPanel.setOpaque(darkMode);
 
        LabelMonth.setForeground(darkMode ? DARK_TEXT : null);
        LabelMonth.setBackground(header);
        LabelMonth.setOpaque(darkMode);
 
        // Force the scroll pane's viewport background to match
        theScrollPane.getViewport().setBackground(darkMode ? DARK_BG : Color.white);
        tabelCal.setBackground(darkMode ? DARK_BG : Color.white);
        tabelCal.setGridColor(darkMode ? new Color(60, 60, 60) : new Color(225, 225, 225));
        theScrollPane.setBorder(darkMode ? BorderFactory.createLineBorder(DARK_PANEL) 
        : BorderFactory.createLineBorder(new Color(200, 200, 200)));
 
        // Keep "Calendar" at the top-left readable regardless of theme
        panelCal.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(darkMode ? DARK_PANEL : Color.gray),
            "Calendar", 0, 0, null,
            darkMode ? DARK_TEXT : null
        ));
 
        mainFrame.getContentPane().setBackground(darkMode ? DARK_PANEL : null);
        tabelCal.getTableHeader().setOpaque(true);

        tabelCal.getTableHeader().setBackground(darkMode ? DARK_HEADER : LIGHT_HEADER);
        tabelCal.getTableHeader().setForeground(darkMode ? DARK_TEXT : LIGHT_TEXT);
        tabelCal.getTableHeader().repaint();
        tabelCal.repaint(); 
    }

    public static void refreshCalendar(int month, int year){
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som;
        
        mtabelCal.setRowCount(0);
        buttonPrev.setEnabled(true);
        buttonNext.setEnabled(true);
        if (month == 0 && year <= todayYear-100){buttonPrev.setEnabled(false);}
        if (month == 11 && year >= todayYear+100){buttonNext.setEnabled(false);}
        LabelMonth.setText(months[month]);
        
        comboYear.setSelectedItem(String.valueOf(year));

        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);


        // Calculate the number of days in the PREVIOUS month
        GregorianCalendar prevCal = new GregorianCalendar(year, month - 1, 1);
        int prevNod = prevCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        int numOfRows = ((nod + som - 2) / 7) + 1;
        mtabelCal.setRowCount(numOfRows);

        // Fill the leading empty cells with dates from previous month
        for (int i = 0; i < som - 1; i++) {
            int prevDate = prevNod - (som - 2 - i);
            mtabelCal.setValueAt(-prevDate, 0, i);
        }
        // Fill the current month
        for (int i=1; i<=nod; i++){
            int row = (i+som-2)/7;
            int column  =  (i+som-2)%7;
            mtabelCal.setValueAt(i, row, column);
        }
        // Fill the trailing empty cells with dates for the following month
        int nextMonthDay = 1;
        for (int i = nod + som - 1; i < numOfRows * 7; i++) {
            int row = i / 7;
            int column = i % 7;
            mtabelCal.setValueAt(-nextMonthDay, row, column);
            nextMonthDay++;
        }
        updateRowHeights();
    }
    
    static class tabelCalRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.TOP);
            setBorder(null);

            if (value != null) {
                int dateVal = Integer.parseInt(value.toString());
                int displayDate = Math.abs(dateVal); // Strips the negative sign
                setText(String.valueOf(displayDate)); 
                // Show holiday name as tooltip
                String holiday = getHolidayName(currentYear, currentMonth, displayDate);
                setToolTipText(holiday);
                if (dateVal > 0) {
                    // Current/active month
                    setForeground(darkMode ? DARK_TEXT : LIGHT_TEXT);
 
                    if (selected) {
                        setBackground(darkMode ? DARK_SELECTED : LIGHT_SELECTED);
                    } else if (displayDate == todayDay && currentMonth == todayMonth && currentYear == todayYear) { 
                        setBackground(darkMode ? DARK_TODAY : LIGHT_TODAY);
                    } else if (column == 0 || column == 6) { // weekend
                        setBackground(darkMode ? DARK_WEEKEND : LIGHT_WEEKEND);
                    } else { //weekday
                        setBackground(darkMode ? DARK_BG : LIGHT_BG);
                    }
                } else {
                    // Inactive month (prior to & following the current month)
                    setForeground(darkMode ? DARK_INACTIVE_FG : LIGHT_INACTIVE_FG); 
 
                    if (selected) {
                        setBackground(darkMode ? DARK_SEL_INACTIVE : LIGHT_SEL_INACTIVE);
                    } else if (column == 0 || column == 6) { // weekend
                        setBackground(darkMode ? DARK_INACTIVE_WKD : LIGHT_INACTIVE_WKD);
                    } else { // weekday
                        setBackground(darkMode ? DARK_INACTIVE_WKD2 : LIGHT_INACTIVE_WKD2);
                    }
                }
            } else {
                String h = getHolidayName(currentYear, currentMonth, displayDate);
                setBackground(h != null
                    ? (darkMode ? new Color(80,65,20) : new Color(255,240,200))
                    : (darkMode ? DARK_BG : LIGHT_BG));
            }
            return this;
        }
    }
    
    static String getHolidayName(int year, int month, int day) {
        Map<String, String> holidays = new HashMap<>();
        holidays.put("1-1",  "New Year's Day");
        holidays.put("7-4",  "Independence Day");
        holidays.put("12-25","Christmas Day");
        holidays.put("10-31","Halloween");
        holidays.put("2-14", "Valentine's Day");
        holidays.put("6-19", "Juneteenth");
        holidays.put("11-11","Veterans Day");
    
        // Check fixed holidays
        String fixed = holidays.get((month + 1) + "-" + day);
        if (fixed != null) return fixed;
    
        // Thanksgiving: 4th Thursday of November
        if (month == 10) {
            GregorianCalendar c = new GregorianCalendar(year, month, 1);
            int count = 0;
            while (c.get(Calendar.MONTH) == month) {
                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) count++;
                if (count == 4 && c.get(Calendar.DAY_OF_MONTH) == day) return "Thanksgiving";
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        // Labor Day: 1st Monday of September
        if (month == 8) {
            GregorianCalendar c = new GregorianCalendar(year, month, 1);
            while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                c.add(Calendar.DAY_OF_MONTH, 1);
            if (c.get(Calendar.DAY_OF_MONTH) == day) return "Labor Day";
        }
        return null;
    }
    static class buttonPrev_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0) {
                currentMonth = 11;
                currentYear -= 1;
            }
            else {
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }

    static class buttonDarkMode_Action implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            darkMode = !darkMode;
            buttonDarkMode.setText(darkMode ? "\u2600" : "\u263E"); // ☀ or ☾
            applyTheme();
            Preferences prefs = Preferences.userNodeForPackage(TheCal.class); // remember theme choice
            prefs.putBoolean("darkMode", darkMode);
        }
    }
    
    static class buttonNext_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){
                currentMonth = 0;
                currentYear += 1;
            }
            else{
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    
    static class comboYear_Action implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (comboYear.getSelectedItem() != null){
                String b = comboYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear);
            }
        }
    }

    // rather than always setting a fixed height, this method determines row height 
    // based on the number of rows and the available vertical space in the calendar
    public static void updateRowHeights() {
        int availableHeight = theScrollPane.getViewport().getHeight();
        if (availableHeight > 0 && tabelCal.getRowCount() > 0) {
            int newRowHeight = availableHeight / tabelCal.getRowCount();
            tabelCal.setRowHeight(newRowHeight);
        }
    }
}
