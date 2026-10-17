
package com.cet.finder.ui;

import com.cet.finder.dao.CutoffPredictorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class CutoffPredictorPanel extends JPanel {

    private final JTextField percentileField = new JTextField(9);
    private final JTextField courseField = new JTextField(16);

    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{
                    "All Categories", "AI", "MI", "DEFOBCO",
                    "GNT1H", "GNT1O", "GNT2H", "GNT2O",
                    "GNT3H", "GNT3O", "GOBCH", "GOPENH",
                    "GOPENO", "GOBCO", "GSCH", "GSCO",
                    "GSEBCH", "GSEBCO", "GSTH", "GSTO",
                    "GVJH", "GVJO", "LNT1H", "LNT2H",
                    "LNT3H", "LNT3O", "LOBCH", "LOBCO",
                    "LOPENH", "LOPENO", "LSCH", "LSEBCH",
                    "LSEBCO", "LSTH", "LSTO", "LVJH",
                    "LVJO", "ORPHAN", "PHCH", "PHCOBCH"
            });

    private final JComboBox<String> roundBox = new JComboBox<>(
            new String[]{"All Rounds", "1", "2", "3", "4"});

    private final JLabel resultCount =
            new JLabel("Enter your details and click Predict Colleges.");

    private final JLabel likelyCount = new JLabel("0");
    private final JLabel borderlineCount = new JLabel("0");
    private final JLabel reachCount = new JLabel("0");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "College Name", "Course", "Year", "CAP Round",
                    "Category", "Historical Cutoff", "Estimated Status"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    public CutoffPredictorPanel() {

        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(245, 243, 250));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel heading = new JLabel("MHT-CET Cutoff Predictor");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(new Color(103, 58, 183));

        JLabel subtitle = new JLabel(
                "Compare your percentile with historical CAP cutoff data.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 110));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 7));
        titlePanel.setOpaque(false);
        titlePanel.add(heading);
        titlePanel.add(subtitle);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        filters.setBackground(Color.WHITE);
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 220, 235)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        filters.add(createLabel("Your Percentile"));
        filters.add(percentileField);
        filters.add(createLabel("Category"));
        filters.add(categoryBox);
        filters.add(createLabel("Course"));
        filters.add(courseField);
        filters.add(createLabel("CAP Round"));
        filters.add(roundBox);

        JButton predictButton = new JButton("Predict Colleges");
        styleButton(predictButton, new Color(103, 58, 183));
        filters.add(predictButton);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(createSummaryCard(
                "LIKELY", likelyCount, new Color(38, 140, 91)));
        summaryPanel.add(createSummaryCard(
                "BORDERLINE", borderlineCount, new Color(205, 133, 35)));
        summaryPanel.add(createSummaryCard(
                "REACH", reachCount, new Color(190, 65, 75)));

        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(225, 215, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(235, 232, 240));
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(103, 58, 183));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 36));
        header.setReorderingAllowed(false);

        int[] widths = {360, 320, 75, 100, 110, 155, 165};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        table.getColumnModel().getColumn(6).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean selected,
                            boolean focus, int row, int column) {

                        JLabel label = (JLabel)
                                super.getTableCellRendererComponent(
                                        table, value, selected,
                                        focus, row, column);

                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setFont(new Font("SansSerif", Font.BOLD, 12));

                        if (!selected) {
                            String status = value == null ? "" : value.toString();

                            switch (status) {
                                case "Likely" ->
                                        label.setForeground(
                                                new Color(25, 125, 75));
                                case "Borderline" ->
                                        label.setForeground(
                                                new Color(190, 120, 20));
                                case "Reach" ->
                                        label.setForeground(
                                                new Color(185, 50, 65));
                                default ->
                                        label.setForeground(Color.DARK_GRAY);
                            }
                        }

                        return label;
                    }
                });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(225, 220, 235)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        resultCount.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultCount.setForeground(new Color(103, 58, 183));

        JLabel disclaimer = new JLabel(
                "Note: Estimates use historical cutoffs and do not guarantee admission.");
        disclaimer.setFont(new Font("SansSerif", Font.ITALIC, 12));
        disclaimer.setForeground(new Color(100, 100, 110));

        JPanel resultHeader = new JPanel(new BorderLayout(0, 5));
        resultHeader.setOpaque(false);
        resultHeader.add(resultCount, BorderLayout.NORTH);
        resultHeader.add(disclaimer, BorderLayout.SOUTH);

        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setOpaque(false);
        resultsPanel.add(resultHeader, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(0, 14));
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new BorderLayout(0, 12));
        controls.setOpaque(false);
        controls.add(filters, BorderLayout.NORTH);
        controls.add(summaryPanel, BorderLayout.CENTER);

        topPanel.add(controls, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);

        predictButton.addActionListener(e -> predict());
        percentileField.addActionListener(e -> predict());
    }

    private JPanel createSummaryCard(
            String title, JLabel count, Color color) {

        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 220, 235)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(color);

        count.setFont(new Font("SansSerif", Font.BOLD, 25));
        count.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(count, BorderLayout.CENTER);

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(65, 65, 75));
        return label;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void predict() {

        double studentPercentile;

        try {
            String input = percentileField.getText().trim();

            if (input.isEmpty()) {
                throw new NumberFormatException();
            }

            studentPercentile = Double.parseDouble(input);

            if (!Double.isFinite(studentPercentile)
                    || studentPercentile < 0
                    || studentPercentile > 100) {

                JOptionPane.showMessageDialog(
                        this,
                        "Percentile must be between 0 and 100.",
                        "Invalid Percentile",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid percentile, for example 95.50.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedCategory =
                (String) categoryBox.getSelectedItem();

        String category = "All Categories".equals(selectedCategory)
                ? "" : selectedCategory;

        String selectedRound = (String) roundBox.getSelectedItem();
        int capRound = "All Rounds".equals(selectedRound)
                ? 0 : Integer.parseInt(selectedRound);

        String course = courseField.getText().trim();

        try {
            resultCount.setText("Loading historical cutoff records...");
            model.setRowCount(0);
            updateSummary(0, 0, 0);

            List<String[]> results =
                    new CutoffPredictorDAO().predictColleges(
                            studentPercentile, category, course, capRound);

            int likely = 0;
            int borderline = 0;
            int reach = 0;

            for (String[] result : results) {
                model.addRow(result);

                switch (result[6]) {
                    case "Likely" -> likely++;
                    case "Borderline" -> borderline++;
                    case "Reach" -> reach++;
                }
            }

            updateSummary(likely, borderline, reach);

            resultCount.setText(
                    results.size() + " historical cutoff records found");

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No matching cutoff records were found. " +
                                "Try another category, course, or CAP round.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            resultCount.setText("Could not load results.");

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load cutoff data:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            ex.printStackTrace();
        }
    }

    private void updateSummary(int likely, int borderline, int reach) {
        likelyCount.setText(String.valueOf(likely));
        borderlineCount.setText(String.valueOf(borderline));
        reachCount.setText(String.valueOf(reach));
    }
}
