package com.company;

import net.proteanit.sql.DbUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGUI extends JFrame{
    private JRadioButton cpuAMDRadioButton;
    private JRadioButton intelRadioButton;
    private JRadioButton gpuAMDRadioButton;
    private JRadioButton NVIDIARadioButton;
    private JRadioButton cpuBestPerformanceRadioButton;
    private JRadioButton gpuBestPerformanceRadioButton;
    private JSlider budgetSlider;
    private JPanel panel1;
    private JTextField budgetValueText;
    private JButton generateListButton;
    private JTable resultTable;
    private String platform;
    private String gpuBrand;

    public MainGUI(){
        updateBudgetValue();
        try {populateTable(resultTable);} catch (Exception e1){e1.printStackTrace();}

        cpuAMDRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==cpuAMDRadioButton){
                    platform = "X570";
                    System.out.println(platform);
                }
            }
        });
        cpuBestPerformanceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == cpuBestPerformanceRadioButton) {
                    platform = "X570";
                    System.out.println(platform);
                }
            }
        });
        budgetSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateBudgetValue();
            }
        });
        gpuAMDRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==gpuAMDRadioButton){
                    gpuBrand = "AMD";
                    System.out.println(gpuBrand);
                }
            }
        });
        NVIDIARadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==NVIDIARadioButton){
                    gpuBrand = "NVIDIA";
                    System.out.println(gpuBrand);
                }
            }
        });
        gpuBestPerformanceRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==gpuBestPerformanceRadioButton){
                    gpuBrand = "NVIDIA";
                    System.out.println(gpuBrand);
                }
            }
        });
        intelRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==intelRadioButton){
                    platform = "LGA1200";
                    System.out.println(platform);
                    
                }
            }
        });
    }

    private void updateBudgetValue(){
        BigDecimal tempValue = BigDecimal.valueOf(budgetSlider.getValue());
        budgetValueText.setText("£" + tempValue);
        budgetValueText.setSize(100,100);
    }

    public static Connection getConnection() throws Exception{
        try{
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/javaproject";
            String username = "root";
            String password = "";
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connected");
            return conn;
        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public void populateTable(JTable resultTable) throws Exception {
        try {
        String sql = "(SELECT CPU .CpuID, CPU.ProductName, CPU.Price FROM CPU INNER JOIN motherboard ON CPU .Platform = motherboard.Platform WHERE motherboard.Platform = ? AND CPU.Price < ? AND( SELECT MAX(CPU.AverageFPSPercent) FROM CPU) GROUP BY CPU.cpuID ORDER BY CPU.Price DESC LIMIT 1) UNION(SELECT motherboard.MotherboardID, motherboard.ProductName, MAX(motherboard.Price)FROM motherboard INNER JOIN pccase ON motherboard.Format = pccase.Msize WHERE motherboard.Format = ? AND motherboard.Price < ? AND motherboard.Platform = ? GROUP BY motherboard.MotherboardID ORDER BY motherboard.Price DESC LIMIT 1) UNION(SELECT pccase.CaseID, pccase.ProductName, MAX(pccase.price)FROM pccase INNER JOIN motherboard ON pccase.MSize = motherboard.Format WHERE pccase.Price < ? GROUP BY pccase.CaseID ORDER BY pccase.Price DESC LIMIT 1) UNION(SELECT ram.RamID, ram.ProductName, MAX(ram.Price)FROM ram WHERE ram.Price < ? AND(SELECT MIN(ram.Timing1) AND MIN(ram.Timing2) AND MIN(ram.Timing3) AND MIN(ram.Timing4) AND MAX(ram.Speed)FROM ram)GROUP BY ram.RamID ORDER BY ram.Price DESC LIMIT 1) UNION(SELECT psu.PsuID, psu.ProductName, MAX(psu.Price)FROM psu INNER JOIN pccase ON psu.FormFactor = pccase.Size WHERE psu.Price < ? AND(SELECT MAX(psu.Wattage)FROM psu)GROUP BY psu.PsuID ORDER BY psu.Price DESC LIMIT 1) UNION(SELECT gpu.GpuID, gpu.ProductName, MAX(gpu.price)FROM gpu WHERE gpu.Price < ? AND gpu.ProductBrand = ? AND(SELECT MAX(gpu.AverageFPS)FROM gpu)GROUP BY gpu.GpuID ORDER BY gpu.Price DESC LIMIT 1) UNION(SELECT cpucooler.CpuCoolerID, cpucooler.ProductName, MAX(cpucooler.price)FROM cpucooler WHERE cpucooler.Price < ? AND(SELECT MAX(cpucooler.TDP)FROM cpucooler)GROUP BY cpucooler.CpuCoolerID ORDER BY cpucooler.Price DESC LIMIT 1)";
        String[] columns = {"ProductID", "ProductName", "ProductPrice"};
        DefaultTableModel Resultmodel = new DefaultTableModel(columns,0);
        BigDecimal budgetSliderValue = BigDecimal.valueOf(budgetSlider.getValue());
        BigDecimal sixPercent = new BigDecimal("6.0");
        BigDecimal eightPercent = new BigDecimal("8.0");
        BigDecimal twelvePercent = new BigDecimal("12.0");
        BigDecimal sixteenPercent = new BigDecimal("16.0");
        BigDecimal twentyPercent = new BigDecimal("20.0");
        BigDecimal thirtytwoPercent = new BigDecimal("32.0");
        BigDecimal divide = new BigDecimal("100.0");
        BigDecimal cpuBudget = budgetSliderValue.multiply(twentyPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(cpuBudget);
        BigDecimal gpuBudget = budgetSliderValue.multiply(thirtytwoPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(gpuBudget);
        BigDecimal motherboardBudget = budgetSliderValue.multiply(sixteenPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(motherboardBudget);
        BigDecimal caseBudget = budgetSliderValue.multiply(sixPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(caseBudget);
        BigDecimal psuBudget = budgetSliderValue.multiply(sixPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(psuBudget);
        BigDecimal ramBudget = budgetSliderValue.multiply(twelvePercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(ramBudget);
        BigDecimal cpucoolerBudget = budgetSliderValue.multiply(eightPercent).divide(divide,BigDecimal.ROUND_HALF_UP);
        System.out.println(cpucoolerBudget);
        BigDecimal total = new BigDecimal("0.0").add(cpuBudget).add(gpuBudget).add(motherboardBudget).add(caseBudget).add(psuBudget).add(ramBudget).add(cpucoolerBudget);
        System.out.println("Total:" + total);
        PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement(sql);
        stmt.setString(1, platform);
        stmt.setBigDecimal(2, cpuBudget);
            String mFormat = "ATX";
            stmt.setString(3, mFormat);
        stmt.setBigDecimal(4, motherboardBudget);
        stmt.setString(5, platform);
        stmt.setBigDecimal(6, caseBudget);
        stmt.setBigDecimal(7,ramBudget);
        stmt.setBigDecimal(8,psuBudget);
        stmt.setBigDecimal(9,gpuBudget);
        stmt.setString(10,gpuBrand);
        stmt.setBigDecimal(11,cpucoolerBudget);
        ResultSet rs = stmt.executeQuery();
        resultTable.setModel(DbUtils.resultSetToTableModel(rs));
        System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
        resultTable.setModel(Resultmodel);
        rs.close();
        stmt.close();
        Objects.requireNonNull(getConnection()).close();
        } catch (SQLException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, (String) null);
        }
    }

    public static void main(String[] args) throws Exception {
        getConnection();
        JFrame frame = new JFrame("Computer Component Generator");
        frame.setContentPane(new MainGUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

