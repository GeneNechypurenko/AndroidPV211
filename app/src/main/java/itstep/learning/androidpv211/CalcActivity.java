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
        Log.d("onOperatorClick", "lastOperatorUsed: " + lastOperatorUsed);
    }

    private void getOperationResult(String result, String expression, String operator) {
        double doubleResult = getDoubleResult();
        resultValue = formatResult(doubleResult);
        Log.d("getOperationResult", "resultValue: " + resultValue);

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
            expression = result + operator;
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

        if (isCleared) {
            isCleared = false;
            result = digitKeyEntry;
            resultValue = result;
            tvResult.setText(result);
            Log.d("onDigitClick", "resultValue: " + resultValue);
        } else if (result.contains(equal) && !expression.endsWith(")")) {
            if (expression.replaceAll("\\D", "").length() < 11) {
                expression += digitKeyEntry;
                expressionsLastValue = getExpressionLastValue(expression);
                tvExpression.setText(expression);
                Log.d("onDigitClick", "expressionsLastValue: " + expressionsLastValue);
            }
        } else if (!expression.endsWith(")")) {
            if (result.replaceAll("\\D", "").length() < 11) {
                result += digitKeyEntry;
                resultValue = result;
                tvResult.setText(result);
                Log.d("onDigitClick", "resultValue: " + resultValue);
            }
        }
    }

    private void onCommaClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (isCleared) {
            if (!result.contains(comma)) {
                isCleared = false;
                result += comma;
                resultValue += ".";
                tvResult.setText(result);
                Log.d("onCommaClick", "resultValue: " + resultValue);
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
                Log.d("onCommaClick", "expressionsLastValue: " + expressionsLastValue);
            }
        } else if (!expression.endsWith(")") && !result.contains(comma)) {
            result += comma;
            resultValue += ".";
            tvResult.setText(result);
            Log.d("onCommaClick", "resultValue: " + resultValue);
        }
    }

    private void onPlusMinusClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (!isCleared && !result.equals(zero) && !result.contains(equal)) {
            if (result.startsWith(minus)) {
                result = result.substring(1);
            } else {
                result = minus + result;
            }
            tvResult.setText(result);
        } else if (result.contains(equal)
                && !expression.endsWith(plus)
                || !expression.endsWith(minus)
                || !expression.endsWith(multiply)
                || !expression.endsWith(division)
                || !expression.endsWith(equal)) {
            expression = getExpressionWithoutLastValue(expression);
            if (expressionsLastValue.startsWith("-")) {
                expression += expressionsLastValue.substring(1);
                expressionsLastValue = expressionsLastValue.substring(1);
            } else {
                expression += "(" + minus + expressionsLastValue + ")";
                expressionsLastValue = "-" + expressionsLastValue;
            }
            Log.d("onPlusMinusClick",
                    "expressionsLastValue: " + expressionsLastValue
                            + "; lastOperatorUsed: " + lastOperatorUsed
                            + "; resultValue: " + resultValue);
            tvExpression.setText(expression);
        }
    }

    private String getExpressionLastValue(String expression) {
        if (!expression.isEmpty()) {
            String lastValue = expression
                    .substring(expression.lastIndexOf(lastOperatorUsed) + lastOperatorUsed.length())
                    .trim();
            lastValue = lastValue.replaceAll("[()]", "");

            return lastValue;
        }
        return "";
    }

    private String getExpressionWithoutLastValue(String expression) {
        if (!expression.isEmpty()) {
            return expression
                    .substring(0, expression.lastIndexOf(lastOperatorUsed) + lastOperatorUsed.length())
                    .trim();
        }
        return "";
    }

    private void onBackspaceClick(View view) {
        String result = tvResult.getText().toString();
        if (!result.equals(zero)) {
            StringBuilder sbResult = new StringBuilder(result);
            if (sbResult.length() > 1) {
                sbResult.deleteCharAt(sbResult.length() - 1);
            } else {
                sbResult = new StringBuilder(zero);
            }
            tvResult.setText(sbResult);
        }
    }

    private void onClearEntryClick(View view) {
        String result = tvResult.getText().toString();
        String expression = tvExpression.getText().toString();

        if (!expression.isEmpty()) {
            if (expression.endsWith(expressionsLastValue)) {
                expression = expression.substring(0, expression.length() - expressionsLastValue.length());

                Log.d("onClearEntryClick", "expressionsLastValue " + expressionsLastValue);
            } else if (expression.endsWith(lastOperatorUsed)) {
                expression = expression.substring(0, expression.length() - lastOperatorUsed.length());

                Log.d("onClearEntryClick", "lastOperatorUsed " + lastOperatorUsed);
            }

            tvExpression.setText(expression);
        }

        if (expression.isEmpty()) {
            clear();
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