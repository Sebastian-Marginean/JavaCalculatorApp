package prob1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class prob1 extends JFrame {
    private JTextField displayField;

    public prob1() {
        super("Calculator");

        // Crearea panoului principal
        JPanel mainPanel = new JPanel(new GridBagLayout());

        // Crearea campului de afisare
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setFont(new Font("Arial", Font.BOLD, 20)); // Set the font and size
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 30; // Set the height of the text field
        mainPanel.add(displayField, gbc);

     // Matricea de butoane
        String[][] buttonLabels = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"}
        };

        // Adăugarea butoanelor în panoul principal
        Font buttonFont = new Font("Arial", Font.BOLD, 20);
        Dimension buttonDimension = new Dimension(100, 100);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                JButton button = new JButton(buttonLabels[row][col]);
                button.setFont(buttonFont);
                button.setPreferredSize(buttonDimension);
                gbc.gridx = col;
                gbc.gridy = row + 1;
                mainPanel.add(button, gbc);
                button.addActionListener(new ButtonClickListener());
            }
        }

        // Configurarea ferestrei
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    // Listener pentru acțiunile butoanelor
    class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JButton source = (JButton) event.getSource();
            String buttonText = source.getText();

            // Verificarea acțiunii butonului și prelucrarea ei
            if (buttonText.equals("=")) {
                String expression = displayField.getText();
                try {
                    double result = evaluateExpression(expression);
                    displayField.setText(Double.toString(result));
                } catch (Exception e) {
                    displayField.setText("Error");
                }
            } else if (buttonText.equals("C")) {
                displayField.setText("");
            } else {
                displayField.setText(displayField.getText() + buttonText);
            }
        }

        // Evaluarea expresiei matematice
        private double evaluateExpression(String expression) {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    while (true) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    while (true) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor());

                    return x;
                }
            }.parse();
        }
    }

    public static void main(String[] args) {
        new prob1();
    }
}
