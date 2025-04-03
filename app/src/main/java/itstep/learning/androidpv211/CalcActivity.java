package itstep.learning.androidpv211;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class CalcActivity extends AppCompatActivity {
    private TextView tvExpression;
    private TextView tvResult;
    private String zero;
    private String comma;
    private String division;
    private String multiply;
    private String minus;
    private String plus;
    private String equal;
    private String lastOperatorUsed;
    private String expressionsLastValue;
    private String resultValue;
    int digitsEntriesCounter = 0;
    private boolean isCleared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvExpression = findViewById(R.id.calc_tv_expression);
        tvResult = findViewById(R.id.calc_tv_result);
        zero = getString(R.string.calc_btn_0);
        comma = getString(R.string.calc_btn_plus_comma);
        division = getString(R.string.calc_btn_divide);
        multiply = getString(R.string.calc_btn_multiply);
        minus = getString(R.string.calc_btn_minus);
        plus = getString(R.string.calc_btn_plus);
        equal = getString(R.string.calc_btn_equals);

        findViewById(R.id.calc_btn_0).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_1).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_2).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_3).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_4).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_5).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_6).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_7).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_8).setOnClickListener(this::onDigitClick);
        findViewById(R.id.calc_btn_9).setOnClickListener(this::onDigitClick);

        findViewById(R.id.calc_btn_c).setOnClickListener(this::onClearClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::onClearEntryClick);
        findViewById(R.id.calc_btn_backspace).setOnClickListener(this::onBackspaceClick);

        findViewById(R.id.calc_btn_percent).setOnClickListener(this::onPercentClick);
        findViewById(R.id.calc_btn_inversion).setOnClickListener(this::onInverseClick);
        findViewById(R.id.calc_btn_sqr).setOnClickListener(this::onSquareClick);
        findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::onSquareRootClick);
        findViewById(R.id.calc_btn_plus_minus).setOnClickListener(this::onPlusMinusClick);
        findViewById(R.id.calc_btn_comma).setOnClickListener(this::onCommaClick);

        findViewById(R.id.calc_btn_divide).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.calc_btn_multiply).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.calc_btn_minus).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.calc_btn_plus).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.calc_btn_equals).setOnClickListener(this::onOperatorClick);

        if (savedInstanceState == null) {
            clear();
        }
    }

    private void onSquareRootClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.equals(zero)) {
            double parsedResult = Double.parseDouble(result);
            parsedResult = Math.sqrt(parsedResult);
            if (parsedResult % 1 == 0) {
                tvResult.setText(String.valueOf((int) parsedResult));
            } else {
                tvResult.setText(String.valueOf(parsedResult));
            }
        }
    }

    private void onSquareClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.equals(zero)) {
            double parsedResult = Double.parseDouble(result);
            parsedResult *= parsedResult;
            if (parsedResult % 1 == 0) {
                tvResult.setText(String.valueOf((int) parsedResult));
            } else {
                tvResult.setText(String.valueOf(parsedResult));
            }
        }
    }

    private void onInverseClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.equals(zero)) {
            double parsedResult = Double.parseDouble(result);
            parsedResult = 1 / parsedResult;
            if (parsedResult % 1 == 0) {
                tvResult.setText(String.valueOf((int) parsedResult));
            } else {
                tvResult.setText(String.valueOf(parsedResult));
            }
        }
    }

    private void onPercentClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.equals(zero)) {
            double parsedResult = Double.parseDouble(result);
            parsedResult /= 100;
            if (parsedResult % 1 == 0) {
                tvResult.setText(String.valueOf((int) parsedResult));
            } else {
                tvResult.setText(String.valueOf(parsedResult));
            }
        }
    }

    private void onOperatorClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();
        String operatorKeyEntry = ((Button) view).getText().toString();
        getOperationResult(result, expression, operatorKeyEntry);
        lastOperatorUsed = operatorKeyEntry;
    }

    private void getOperationResult(String result, String expression, String operator) {
        double doubleResult = getDoubleResult();

        resultValue = formatResult(doubleResult);

        if (!(operator.equals(division) && Double.parseDouble(resultValue) == 0.0)) {
            displayOperator(result, expression, operator);

            if (result.contains(equal)) {
                result = equal + resultValue;

                if (operator.equals(equal)) {
                    isCleared = true;
                }
                tvResult.setText(result);
            }
        }
    }

    private String formatResult(double result) {
        String resultStr = String.valueOf(result);

        if (resultStr.contains("E") || resultStr.contains("e")) {
            resultStr = String.format("%.10f", result);
        }

        if (result == Math.floor(result)) {
            return String.valueOf((long) result);
        }

        if (resultStr.length() > 11) {
            if (resultStr.contains(".")) {
                String[] parts = resultStr.split("\\.");
                String intPart = parts[0];
                int maxDecimals = 10 - intPart.length();

                if (maxDecimals < 0) {
                    return intPart.substring(0, 11);
                } else {
                    resultStr = String.format("%." + maxDecimals + "f", result);
                }
            } else {
                resultStr = resultStr.substring(0, 11);
            }
        }

        if (resultStr.contains(".")) {
            resultStr = resultStr.replaceAll("0*$", "");
            if (resultStr.endsWith(".")) {
                resultStr = resultStr.substring(0, resultStr.length() - 1);
            }
        }

        return resultStr;
    }

    private void displayOperator(String result, String expression, String operator) {
        if (!result.contains(equal)) {
            if (result.contains(minus)) {
                expression = "(" + result + ")" + operator;
            } else {
                expression = result + operator;
            }
            result = equal + result;
            tvExpression.setText(expression);
            tvResult.setText(result);
        } else {
            if (expression.endsWith(plus)
                    || expression.endsWith(minus)
                    || expression.endsWith(multiply)
                    || expression.endsWith(division)) {
                StringBuilder sb = new StringBuilder(expression);
                sb.replace(sb.length() - 1, sb.length(), operator);
                tvExpression.setText(sb.toString());
            } else if (expression.endsWith(equal)) {
                isCleared = true;
            } else {
                expression += operator;
                tvExpression.setText(expression);
            }
        }
        digitsEntriesCounter = 0;
    }

    private double getDoubleResult() {
        double doubleResult = Double.parseDouble(resultValue);
        double doubleLastValue = Double.parseDouble(expressionsLastValue);

        return lastOperatorUsed.equals(plus) ? doubleResult + doubleLastValue
                : lastOperatorUsed.equals(minus) ? doubleResult - doubleLastValue
                : lastOperatorUsed.equals(multiply) ? doubleResult * doubleLastValue
                : lastOperatorUsed.equals(division) ? (doubleLastValue == 0.0 ? doubleResult : doubleResult / doubleLastValue)
                : doubleResult;
    }

    private void onDigitClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();
        String digitKeyEntry = ((Button) view).getText().toString();

        if (digitsEntriesCounter < 10) {
            if (isCleared) {
                isCleared = false;
                result = digitKeyEntry;
                resultValue = result;
                tvResult.setText(result);

            } else if (result.contains(equal) && !expression.endsWith(")")) {
                expression += digitKeyEntry;
                expressionsLastValue = getExpressionLastValue(expression);
                tvExpression.setText(expression);

            } else if (!expression.endsWith(")")) {
                result += digitKeyEntry;
                resultValue = result;
                tvResult.setText(result);
            }
            ++digitsEntriesCounter;
        }
    }

    private void onCommaClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (digitsEntriesCounter < 10) {
            if (isCleared) {
                if (!result.contains(comma)) {
                    isCleared = false;
                    result += comma;
                    resultValue += ".";
                    tvResult.setText(result);
                }

            } else if (result.contains(equal) && !expression.endsWith(")")) {
                if (!expressionsLastValue.contains(".")) {
                    if (expression.endsWith(division)
                            || expression.endsWith(multiply)
                            || expression.endsWith(minus)
                            || expression.endsWith(plus)) {
                        expression += zero;
                        expression += comma;
                        expressionsLastValue += ".";
                    } else if (!expression.endsWith(equal) || !expression.endsWith(")")) {
                        expression += comma;
                        expressionsLastValue += ".";
                    }
                    tvExpression.setText(expression);
                }

            } else if (!expression.endsWith(")") && !result.contains(comma)) {
                result += comma;
                resultValue += ".";
                tvResult.setText(result);
            }

            ++digitsEntriesCounter;
        }
    }

    private void onPlusMinusClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (digitsEntriesCounter < 10) {
            if (!result.equals(zero) && !result.contains(equal)) {

                if (result.startsWith(minus)) {
                    result = result.substring(1);
                } else {
                    result = minus + result;
                }
                tvResult.setText(result);

            } else if (result.contains(equal)
                    && (!expression.endsWith(plus)
                    || !expression.endsWith(minus)
                    || !expression.endsWith(multiply)
                    || !expression.endsWith(division)
                    || !expression.endsWith(equal))) {
                expression = getExpressionWithoutLastValue(expression);
                if (expressionsLastValue.startsWith("-")) {
                    expression += expressionsLastValue.substring(1);
                    expressionsLastValue = expressionsLastValue.substring(1);
                } else {
                    expression += "(" + minus + expressionsLastValue + ")";
                    expressionsLastValue = "-" + expressionsLastValue;
                }
                tvExpression.setText(expression);
            }

            ++digitsEntriesCounter;
        }
    }

    private String getExpressionLastValue(String expression) {
        if (!expression.isEmpty()) {
            String temp;

            temp = expression
                    .substring(expression.lastIndexOf(lastOperatorUsed) + lastOperatorUsed.length())
                    .trim();

            temp = temp.replaceAll("[()]", "");
            return temp;
        }
        return "";
    }

    private String getExpressionLastValue(String expression, String operator) {
        if (!expression.isEmpty()) {
            String temp = expression.substring(0, expression.length() - 1);

            temp = temp
                    .substring(temp.lastIndexOf(operator) + operator.length())
                    .trim();

            temp = temp.replaceAll("[()]", "");
            return temp;
        }
        return "";
    }

    private String getPrevOperator(String expression) {
        if (!expression.isEmpty()) {
            String temp = expression.substring(0, expression.lastIndexOf(lastOperatorUsed) - 1);

            int prevOperatorIndex = Math.max(
                    Math.max(temp.lastIndexOf(plus), temp.lastIndexOf(minus)),
                    Math.max(temp.lastIndexOf(multiply), temp.lastIndexOf(division))
            );

            if (prevOperatorIndex != -1) {
                return String.valueOf(temp.charAt((prevOperatorIndex)));
            }
        }
        return "";
    }

    private String getLastOperatorUsed(String expression) {
        if (!expression.isEmpty()) {
            String temp = getExpressionWithoutLastValue(expression);
            return temp.substring(temp.length() - 1);
        }
        return "";
    }

    private String getExpressionWithoutLastValue(String expression) {
        if (!expression.isEmpty()) {
            if (expressionsLastValue.contains("-")) {
                return expression
                        .substring(0, expression.lastIndexOf(lastOperatorUsed) - lastOperatorUsed.length());
            } else if (!lastOperatorUsed.isEmpty()) {
                return expression
                        .substring(0, expression.lastIndexOf(lastOperatorUsed) + lastOperatorUsed.length())
                        .trim();
            }
        }
        return expression;
    }

    private void onBackspaceClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (digitsEntriesCounter > 0) {
            if (!result.contains(equal) && !result.equals(zero)) {
                StringBuilder sbResult = new StringBuilder(result);
                if (sbResult.length() > 1) {
                    sbResult.deleteCharAt(sbResult.length() - 1);
                    digitsEntriesCounter--;
                } else {
                    sbResult = new StringBuilder(zero);
                    clear();
                }
                tvResult.setText(sbResult);

            } else if (!expression.isEmpty()
                    && (!expression.endsWith(plus)
                    || !expression.endsWith(minus)
                    || !expression.endsWith(multiply)
                    || !expression.endsWith(division)
                    || !expression.endsWith(equal))) {
                StringBuilder sbExpression = new StringBuilder(expression);
                sbExpression.deleteCharAt(sbExpression.length() - 1);
                tvExpression.setText(sbExpression);
                digitsEntriesCounter--;
            }
        }
    }

    private void onClearEntryClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (!expression.isEmpty() && !expression.endsWith(lastOperatorUsed)) {
            lastOperatorUsed = getLastOperatorUsed(expression);
            expression = getExpressionWithoutLastValue(expression);
            String prevOperator = getPrevOperator(expression);

            if (prevOperator.isEmpty()) {
                clear();
                return;
            } else {
                lastOperatorUsed = prevOperator;
            }

            expressionsLastValue = getExpressionLastValue(expression, lastOperatorUsed);
            double doubleResult = rollbackToPreviousResult(expression);

            if (doubleResult % 1 == 0) {
                result = equal + (int) doubleResult;
            }

            digitsEntriesCounter = 0;
            tvResult.setText(result);
            tvExpression.setText(expression);

        } else if (!expression.isEmpty() && expression.endsWith(lastOperatorUsed)) {
            double doubleResult = rollbackToPreviousResult(expression);
            if (doubleResult % 1 == 0) {
                result = equal + (int) doubleResult;
            }
            tvResult.setText(result);

            expression = expression.substring(0, expression.length() - 1);
            expressionsLastValue = getExpressionLastValue(expression);
            expression = getExpressionWithoutLastValue(expression);
            String prevOperator = getPrevOperator(expression);

            if (prevOperator.isEmpty()) {
                clear();
                return;
            } else {
                lastOperatorUsed = prevOperator;
            }
            tvExpression.setText(expression);

        } else {
            clear();
        }
    }

    private double rollbackToPreviousResult(String expression) {
        if (expression.isEmpty()) return 0.0;

        try {
            return evaluateExpression(expression);
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(num.toString()));
                continue;
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return b == 0 ? Double.NaN : a / b;
            default:
                return 0;
        }
    }

    private void onClearClick(View view) {
        clear();
    }

    private void clear() {
        tvExpression.setText("");
        tvResult.setText(zero);
        isCleared = true;
        lastOperatorUsed = "";
        expressionsLastValue = "0";
        resultValue = "0";
        digitsEntriesCounter = 0;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("result", tvResult.getText());
        outState.putCharSequence("expression", tvExpression.getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        tvExpression.setText(savedInstanceState.getCharSequence("expression"));
    }
}