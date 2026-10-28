import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Main extends JFrame {
    // Константы с исходным размером окна приложения
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    // Массив коэффициентов многочлена
    private Double[] coefficients;
    private JFileChooser fileChooser = null;
    // Элементы меню
    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;
    // Поля ввода для считывания значений переменных
    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;
    private Box hBoxResult;
    private GornerTableCellRenderer renderer = new
            GornerTableCellRenderer();
    // Модель данных с результатами вычислений
    private GornerTableModel data;
    private Boolean third_view=false;
    public Main(Double[] coefficients) {
        // Обязательный вызов конструктора предка
        super("Табулирование многочлена на отрезке по схеме Горнера");

        // Запомнить во внутреннем поле переданные коэффициенты
        this.coefficients = coefficients;

        // Установить размеры окна
        setSize(WIDTH, HEIGHT);

        // Отцентрировать окно приложения на экране
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2,
                (kit.getScreenSize().height - HEIGHT) / 2);
        JMenuBar menuBar = new JMenuBar();
        // Установить меню в качестве главного меню приложения
        setJMenuBar(menuBar);
// Добавить в меню пункт меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
// Добавить его в главное меню
        menuBar.add(fileMenu);
// Создать пункт меню "Таблица"
        JMenu tableMenu = new JMenu("Таблица");
// Добавить его в главное меню
        menuBar.add(tableMenu);
        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
// Если диалоговое окно "Открыть файл" ещѐ не создано,
// то создать его
                    fileChooser = new JFileChooser();
// и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
// Показать диалоговое окно
                if (fileChooser.showSaveDialog(Main.this) ==
                        JFileChooser.APPROVE_OPTION){
// Если результат его показа успешный, сохранить данные в
// текстовый файл
                    saveToTextFile(fileChooser.getSelectedFile());}
            }
        };
// Добавить соответствующий пункт подменю в меню "Файл"
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        saveToTextMenuItem.setEnabled(false);
        // Создать новое "действие" по сохранению в текстовый файл
        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {
        public void actionPerformed(ActionEvent event) {
            if (fileChooser==null) {
// Если диалоговое окно "Открыть файл" ещѐ не создано,
// то создать его
                fileChooser = new JFileChooser();
// и инициализировать текущей директорией
                fileChooser.setCurrentDirectory(new File("."));
            }
// Показать диалоговое окно
            if (fileChooser.showSaveDialog(Main.this) ==
                    JFileChooser.APPROVE_OPTION) {
// Если результат показа успешный,
// сохранить данные в двоичный файл
                saveToGraphicsFile(fileChooser.getSelectedFile());
            }
        }
        };
// Добавить соответствующий пункт подменю в меню "Файл"
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
// По умолчанию пункт меню является недоступным(данных ещѐ нет)
        saveToGraphicsMenuItem.setEnabled(false);
        // Создать новое действие по поиску значений многочлена
        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
// Запросить пользователя ввести искомую строку
                String value = JOptionPane.showInputDialog(Main.this,
                        "Введите значение для поиска", "Поиск значения",
                        JOptionPane.QUESTION_MESSAGE);
// Установить введенное значение в качестве иголки в визуализаторе
                renderer.setNeedle(value);
// Обновить таблицу
                getContentPane().repaint();
            }
        };
// Добавить действие в меню "Таблица"
        searchValueMenuItem = tableMenu.add(searchValueAction);
// По умолчанию пункт меню является недоступным (данных ещѐ нет)
        searchValueMenuItem.setEnabled(false);
        JLabel labelFrom = new JLabel("X на интервале от:");
        textFieldFrom = new JTextField("0.0", 5);
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());

        JLabel labelTo = new JLabel("до:");
        textFieldTo = new JTextField("1.0", 5);
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());

        JLabel labelStep = new JLabel("с шагом:");
        textFieldStep = new JTextField("0.1", 5);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());

        Box hBoxRange = Box.createHorizontalBox();
        hBoxRange.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        hBoxRange.add(Box.createHorizontalGlue());
        hBoxRange.add(labelFrom);
        hBoxRange.add(Box.createHorizontalStrut(10));
        hBoxRange.add(textFieldFrom);
        hBoxRange.add(Box.createHorizontalStrut(20));
        hBoxRange.add(labelTo);
        hBoxRange.add(Box.createHorizontalStrut(10));
        hBoxRange.add(textFieldTo);
        hBoxRange.add(Box.createHorizontalStrut(20));
        hBoxRange.add(labelStep);
        hBoxRange.add(Box.createHorizontalStrut(10));
        hBoxRange.add(textFieldStep);
        hBoxRange.add(Box.createHorizontalGlue());
        getContentPane().add(hBoxRange, BorderLayout.NORTH);


        // Создать кнопку "Вычислить"
        JButton buttonCalc = new JButton("Вычислить");

// Задать действие на нажатие "Вычислить" и привязать к кнопке
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
// Считать значения границ отрезка, шага из полей ввода
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
// На основе считанных данных создать модель таблицы
                    data = new GornerTableModel(from, to, step,
                            Main.this.coefficients, Main.this.third_view);
// Создать новый экземпляр таблицы
                    JTable table = new JTable(data);
// Установить в качестве визуализатора ячеек для класса
// Double разработанный визуализатор
                    table.setDefaultRenderer(Double.class, renderer);
// Установить размер строки таблицы в 30 пикселов (рис. 3.4)
                    table.setRowHeight(30);
// Удалить все вложенные элементы из контейнера hBoxResult
                    hBoxResult.removeAll();
// Добавить в hBoxResult таблицу, "обѐрнутую" в панель с
// полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
// Обновить область содержания главного окна
                    getContentPane().validate();
// Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    searchValueMenuItem.setEnabled(true);
                } catch (NumberFormatException ex) {
// В случае ошибки преобразования показать сообщение об ошибке
                    JOptionPane.showMessageDialog(Main.this,
                            "Ошибка в формате записи числа с плавающей точкой",
                            "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
// Создать кнопку "Очистить поля"
        JButton buttonReset = new JButton("Очистить поля");
// Задать действие на нажатие "Очистить поля" и привязать к кнопке
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
// Установить в полях ввода значения по умолчанию
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
// Удалить все вложенные элементы контейнера hBoxResult
                hBoxResult.removeAll();
// Добавить в контейнер пустую панель
                hBoxResult.add(new JPanel());
                renderer.setNeedle(null); // Сбрасываем искомую "иголку"
                repaint();                // Просим окно обновиться
// Пометить элементы меню как недоступные
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                searchValueMenuItem.setEnabled(false);
// Обновить область содержания главного окна
                getContentPane().validate();
            }
        });
        JCheckBox third_col=new JCheckBox("3 столбец", Main.this.third_view);
        third_col.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                Main.this.third_view = third_col.isSelected();
                if (data != null) {
                    data.setThird_col(Main.this.third_view);
                    getContentPane().validate();
                    Component comp = hBoxResult.getComponent(0);
                    if (comp instanceof JScrollPane) {
                        JTable table = (JTable) ((JScrollPane) comp).getViewport().getView();
                        table.setDefaultRenderer(Double.class, renderer);}
                }
            }
        });
        // Поместить созданные кнопки в контейнер
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalStrut(15));
        hboxButtons.add(third_col);
        hboxButtons.add(Box.createHorizontalGlue());
// Установить предпочтительный размер области равным удвоенному
// минимальному, чтобы при компоновке окна область совсем не сдавили
        hboxButtons.setPreferredSize(new Dimension(new
                Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new
                Double(hboxButtons.getMinimumSize().getHeight()).intValue()*2));
// Разместить контейнер с кнопками в нижней (южной) области компоновки
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
// Область для вывода результата пока что пустая
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
// Установить контейнер hBoxResult в главной области компоновки
        getContentPane().add(hBoxResult, BorderLayout.CENTER);






        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    protected void saveToTextFile(File selectedFile) {
        try {
// Создать новый символьный поток вывода, направленный в указанный файл
            PrintStream out = new PrintStream(selectedFile);
// Записать в поток вывода заголовочные сведения
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i=0; i<coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" + (coefficients.length-i-1));
                if (i!=coefficients.length-1)
                    out.print(" + ");
            }
            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " +
                    data.getTo() + " с шагом " + data.getStep());
            out.println("================================================");
// Записать в поток вывода значения в точках
            for (int i = 0; i<data.getRowCount(); i++) {
                out.print("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));
                if (data.getColumnCount() == 3) {
                    out.print(" (Краевая симметрия: " + data.getValueAt(i, 2) + ")");
                }
                out.println();
            }
// Закрыть поток
            out.close();
        } catch (FileNotFoundException e) {
// Исключительную ситуацию "ФайлНеНайден" в данном случае можно
            // не обрабатывать, так как мы файл создаѐм, а не открываем для чтения
        }
    }
    protected void saveToGraphicsFile(File selectedFile) {
        try {
// Создать байтовый поток вывода, направленный в указанный файл
            DataOutputStream out = new DataOutputStream(new
                    FileOutputStream(selectedFile));
            for (int i = 0; i<data.getRowCount(); i++) {
// Записать в поток вывода значение X в точке
                out.writeDouble((Double)data.getValueAt(i,0));
// значение многочлена в точке
                out.writeDouble((Double)data.getValueAt(i,1));
            }
// Закрыть поток вывода
            out.close();
        } catch (Exception e) {
// Исключительную ситуацию "ФайлНеНайден" в данном случае можно
// не обрабатывать, так как мы файл создаѐм, а не открываем
        }
    }

    public static void main(String[] args){
        // Если не задано ни одного аргумента командной строки -
// Продолжать вычисления невозможно, коэффиценты неизвестны
        if (args.length==0) {
            System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента!");
            System.exit(-1);
        }
// Зарезервировать места в массиве коэффициентов столько,
// сколько аргументов командной строки
        Double[] coefficients = new Double[args.length];
        int i = 0;
        try {
// Перебрать все аргументы, пытаясь преобразовать их в Double
            for (String arg: args) {
                coefficients[i++] = Double.parseDouble(arg);
            }
        }
        catch (NumberFormatException ex) {
            // Если преобразование невозможно - сообщить об ошибке и завершиться
            System.out.println("Ошибка преобразования строки '" + args[i] +
                    "' в число типа Double");
            System.exit(-2);
        }
// Создать экземпляр главного окна, передав ему массив коэффициентов
        Main frame = new Main(coefficients);
// Задать действие, выполняемое при закрытии окна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// Показать главное окно
        frame.setVisible(true);
    }

}