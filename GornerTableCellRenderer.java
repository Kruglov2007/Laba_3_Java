import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class GornerTableCellRenderer implements TableCellRenderer {
    private String needle = null;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

    public void setNeedle(String needle) {
        this.needle = needle;
    }

    public GornerTableCellRenderer() {
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel();
        panel.add(label);

        String formattedDouble = "";
        if (value != null) {
            if (value instanceof Number) {
                formattedDouble = formatter.format(value);
            } else {
                formattedDouble = value.toString();
            }
        }
        label.setText(formattedDouble);

        // Исправленный алгоритм подсчета суммы цифр целой части
        boolean sumDivisibleBy10 = false;
        if (value instanceof Number) {
            long integerPart = Math.abs(((Number) value).longValue());
            long sum = 0;

            if (integerPart == 0) {
                sum = 0; // Для нуля сумма цифр равна 0
            } else {
                while (integerPart > 0) {
                    sum += integerPart % 10;
                    integerPart /= 10;
                }
            }

            // 0 делится на 10 без остатка (0 % 10 == 0)
            if (sum % 10 == 0) {
                sumDivisibleBy10 = true;
            }
        }

        boolean isSymmetric = false;
        if (table.getModel().getColumnCount() == 3) {
            Object symmetryValue = table.getModel().getValueAt(row, 2);
            if (symmetryValue instanceof Boolean) {
                isSymmetric = (Boolean) symmetryValue;
            }
        }

        if (col == 1 && needle != null && !needle.isEmpty() && needle.equals(formattedDouble)) {
            panel.setBackground(Color.BLUE);
            label.setForeground(Color.WHITE);
        }
        else if (sumDivisibleBy10) {
            panel.setBackground(Color.ORANGE);
            label.setForeground(Color.BLACK);
        }
        else if (isSymmetric) {
            panel.setBackground(new Color(20, 255, 204));
            label.setForeground(Color.BLACK);
        }
        else {
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                panel.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
        }
        return panel;
    }
}
