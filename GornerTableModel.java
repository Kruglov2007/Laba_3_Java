import javax.swing.table.AbstractTableModel;
public class GornerTableModel extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;
    private boolean third_col;
    public GornerTableModel(Double from, Double to, Double step, Double[]
            coefficients, boolean third_col) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.coefficients = coefficients;
        this.third_col=third_col;
    }
    public Double getFrom() {
        return from;
    }
    public Double getTo() {
        return to;
    }
    public Double getStep() {
        return step;
    }
    public boolean getThird_col(){
        return third_col;
    }

    public void setThird_col(boolean third_col) {
        this.third_col = third_col;
        fireTableStructureChanged();
    }

    @Override
    public int getColumnCount() {
        if(third_col){return 3;}else{
        return 2;}
    }
    @Override
    public int getRowCount() {
        return new Double(Math.ceil((to-from)/step)).intValue()+1;
    }
    @Override
    public Object getValueAt(int row, int col) {
        // Вычислить значение X
        double x = from + step * row;
        if (col == 0) {
            return x;
        }

        // Вычисляем значение многочлена по схеме Горнера для 1 и 2 столбцов
        double result = 0.0;
        if (coefficients != null && coefficients.length > 0) {
            result = coefficients[0];
            for (int i = 1; i < coefficients.length; i++) {
                result = result * x + coefficients[i];
            }
        }

        if (col == 1) {
            return result;
        }

        if (col == 2) {
            // ИСПРАВЛЕНО: Безопасный перевод Double в строку без использования внешнего formatter
            String formattedDouble = String.valueOf(result);
            if (formattedDouble.endsWith(".0")) {
                formattedDouble = formattedDouble.substring(0, formattedDouble.length() - 2);
            }

            // Очищаем от знака минус и точки, чтобы проверять только значащие цифры
            String cleanStr = formattedDouble.replace("-", "").replace(".", "");

            if (!cleanStr.isEmpty()) {
                char firstChar = cleanStr.charAt(0);
                char lastChar = cleanStr.charAt(cleanStr.length() - 1);
                return firstChar == lastChar;
            }
            return false;
        }

        return null;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col==2){
            return Boolean.class;
        }else {
        return Double.class;}
    }
    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X";
            case 1:
                return "Значение многочлена";
            default:
                return "Краевая симметрия";
        }
    }

}