package emanondev.core;

public class UtilsMath {

    UtilsMath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Evaluate the given formula.<br>
     * You can use variables and replace them before evaluating the formula.<br>
     * <br>
     * Grammar:<br>
     * expression -&#62; term | expression `+` term | expression `-` term<br>
     * term -&#62; factor | term `*` factor | term `/` factor<br>
     * factor -&#62; `+` factor | `-` factor | `(` expression `)`
     * | number | functionName factor | factor `^` factor<br>
     * functionName -&#62; sqrt | sin | cos | tan | abs | ln | log | floor | ceil |
     * exp<br>
     * <br>
     * Examples: <br>
     * 2+2*2 = 6<br>
     * 3*2^2 = 12<br>
     * sqrt(4*(5+3)^2) = 16<br>
     * log(1000) = 3<br>
     * ln(exp(3)) = 3<br>
     *
     * @param formula the string to evaluate
     * @return evaluation of the formula
     * @throws RuntimeException if syntax is wrong
     */
    public static double eval(final String formula) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < formula.length()) ? formula.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ')
                    nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < formula.length())
                    throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+'))
                        x += parseTerm(); // addition
                    else if (eat('-'))
                        x -= parseTerm(); // subtraction
                    else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*'))
                        x *= parseFactor(); // multiplication
                    else if (eat('/'))
                        x /= parseFactor(); // division
                    else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return parseFactor(); // unary plus
                if (eat('-'))
                    return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.')
                        nextChar();
                    x = Double.parseDouble(formula.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z')
                        nextChar();
                    String func = formula.substring(startPos, this.pos);
                    x = parseFactor();
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "abs" -> Math.abs(x);
                        case "ln" -> Math.log(x);
                        case "log" -> Math.log10(x);
                        case "sin" -> Math.sin(Math.toRadians(x));
                        case "cos" -> Math.cos(Math.toRadians(x));
                        case "tan" -> Math.tan(Math.toRadians(x));
                        case "exp" -> Math.exp(x);
                        case "floor" -> Math.floor(x);
                        case "ceil" -> Math.ceil(x);
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                if (eat('^'))
                    x = Math.pow(x, parseFactor()); // exponential

                return x;
            }
        }.parse();
    }

}
