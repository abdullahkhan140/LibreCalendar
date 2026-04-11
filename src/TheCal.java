import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TheCal {//Authored by Vaibhav Thakkar, Ariane Quenum, Michael Woelfel
    
    static JLabel LabelMonth;
    static JButton buttonPrev, buttonNext;
    static JTable tabelCal;
    static JComboBox comboYear;
    static JFrame mainFrame;
    static Container pane;
    static JScrollPane theScrollPane;
    static JPanel panelCal;
    static int Year, Month, Day, currentYear, currentMonth;
    static DefaultTableModel mtabelCal;

    public static void main (String args[]){
        mainFrame = new JFrame ("LibreCalendar");
        pane = mainFrame.getContentPane();
        pane.setLayout(new BorderLayout()); 
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        LabelMonth = new JLabel ("January"); 
        LabelMonth.setFont(new Font("TimesNewRoman", Font.BOLD, 32)); 

        comboYear = new JComboBox();
        buttonPrev = new JButton ("<<");
        buttonNext = new JButton (">>");
        mtabelCal = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tabelCal = new JTable(mtabelCal);
        theScrollPane = new JScrollPane(tabelCal);
        panelCal = new JPanel(new BorderLayout());
        
        panelCal.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        buttonPrev.addActionListener(new buttonPrev_Action());
        buttonNext.addActionListener(new buttonNext_Action());
        comboYear.addActionListener(new comboYear_Action());
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.add(buttonPrev);
        headerPanel.add(LabelMonth);
        headerPanel.add(comboYear);
        headerPanel.add(buttonNext);
                

        panelCal.add(headerPanel, BorderLayout.NORTH);
        panelCal.add(theScrollPane, BorderLayout.CENTER);
        pane.add(panelCal, BorderLayout.CENTER);
        
        mainFrame.setResizable(true);
        
        GregorianCalendar cal = new GregorianCalendar(); 
        Day = cal.get(GregorianCalendar.DAY_OF_MONTH);
        Month = cal.get(GregorianCalendar.MONTH);
        Year = cal.get(GregorianCalendar.YEAR);
        currentMonth = Month;
        currentYear = Year;
            
        String[] headers = {"Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i=0; i<7; i++){
            mtabelCal.addColumn(headers[i]);
        }

        tabelCal.getParent().setBackground(tabelCal.getBackground());
        tabelCal.getTableHeader().setResizingAllowed(true);
        tabelCal.getTableHeader().setReorderingAllowed(true);
        tabelCal.setColumnSelectionAllowed(true);
        tabelCal.setRowSelectionAllowed(true);
        tabelCal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelCal.setRowHeight(75);
        mtabelCal.setColumnCount(7);
        mtabelCal.setRowCount(6);
        
        for (int i=Year-100; i<=Year+100; i++){
            comboYear.addItem(String.valueOf(i));
        }

        tabelCal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearHighlight");
                
        // Clear selection w/ light green cell when Escape key is pressed
        tabelCal.getActionMap().put("clearHighlight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                tabelCal.clearSelection(); 
            }
        });

        refreshCalendar (Month, Year);
        mainFrame.setMinimumSize(new Dimension(800, 450));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
    
    public static void refreshCalendar(int month, int year){
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som;
        
        buttonPrev.setEnabled(true);
        buttonNext.setEnabled(true);
        if (month == 0 && year <= Year-10){buttonPrev.setEnabled(false);}
        if (month == 11 && year >= Year+100){buttonNext.setEnabled(false);}
        LabelMonth.setText(months[month]);
        
        comboYear.setSelectedItem(String.valueOf(year));

        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);

        int numOfRows = ((nod + som - 2) / 7) + 1;

        mtabelCal.setRowCount(numOfRows);

        for (int i=0; i<numOfRows; i++){
            for (int j=0; j<7; j++){
                mtabelCal.setValueAt(null, i, j);
            }
        }

        for (int i=1; i<=nod; i++){
            int row = (i+som-2)/7;
            int column  =  (i+som-2)%7;
            mtabelCal.setValueAt(i, row, column);
        }

        tabelCal.setDefaultRenderer(tabelCal.getColumnClass(0), new tabelCalRenderer());
    }
    
    static class tabelCalRenderer extends DefaultTableCellRenderer{
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setHorizontalAlignment(SwingConstants.LEFT);
            setVerticalAlignment(SwingConstants.TOP);
            if (selected) {
                setBackground(new Color(204, 255, 204)); // Light green highlight
            } else {
                if (column == 0 || column == 6){ //Week-end
                    setBackground(new Color(255, 220, 220));
                }
                else{ //Week
                    setBackground(new Color(255, 255, 255));
                }
                if (value != null){
                    if (Integer.parseInt(value.toString()) == Day && currentMonth == Month && currentYear == Year){ //Today
                        setBackground(new Color(220, 220, 255));
                    }
                }
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
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
}