package emanondev.core;

public class UtilsMath {

	UtilsMath(){
		throw new UnsupportedOperationException();
	}
	/**
	 * Evaluate the given formula.<br>
	 * You can use variables and replace them before evaluating the formula.<br><br>
	 * Grammar:<br>
	 * expression -&#62; term | expression `+` term | expression `-` term<br>
	 * term -&#62; factor | term `*` factor | term `/` factor<br>
	 * factor -&#62; `+` factor | `-` factor | `(` expression `)`<br>
	 *        | number | functionName factor | factor `^` factor<br>
	 * functionName -&#62; sqrt | sin | cos | tan | abs | ln | log | floor | ceil | exp<br><br>
	 * Examples: <br>
	 * 2+2*2 = 6<br>
	 * 3*2^2 = 12<br>
	 * sqrt(4*(5+3)^2) = 16<br>
	 * log(1000) = 3<br>
	 * ln(exp(3)) = 3<br>
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
	            if (pos < formula.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }


	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(formula.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = formula.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("abs")) x = Math.abs(x);
	                else if (func.equals("ln")) x = Math.log(x);
	                else if (func.equals("log")) x = Math.log10(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else if (func.equals("exp")) x = Math.exp(x);
	                else if (func.equals("floor")) x = Math.floor(x);
	                else if (func.equals("ceil")) x = Math.ceil(x);
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }
	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponential

	            return x;
	        }
	    }.parse();
	}

}
