//Brandon Walton
package schemeconverter;

import java.util.Scanner;
import java.util.Stack;

public class SchemeConverter {

    /*Method: Converts prefix to Infix and evalutes the Expression*/
    public static void prefixToInfix(String Expression) {
        try {
            StringBuilder sb = new StringBuilder();
            String stringAnswer;

            String toInfix = infixFormat(Expression);
            Double numAnswer = evaluateInfix(toInfix);

            /*Note If Number is Rational, drop the Zero's */
            if (numAnswer % 1 == 0) {
                long stringResult = Math.round(numAnswer);
                stringAnswer = String.valueOf(stringResult);
            } else {
                stringAnswer = String.valueOf(numAnswer);
            }

            /*Note: Creating a string of beforeInfix without the two outer '( )' */
            for (int i = 1; i < toInfix.length() - 1; i++) {
                if (!Character.isWhitespace(toInfix.charAt(i))) {
                    sb.append(toInfix.charAt(i));
                }
            }

            sb.append(' ');
            sb.append('=');
            sb.append(' ');

            /*Note: Appending the stringAnswer to the string builder */
            for (int i = 0; i < stringAnswer.length(); i++) {
                sb.append(stringAnswer.charAt(i));
            }

            String prefixToInfix = sb.toString();
            System.out.println(Expression + " ➔ " + prefixToInfix);
        } catch (Exception e) {
            /*Note: Once An Exception, such as StackEmpty, indicates the given argurments are invalid*/
            System.out.println("Invalid Arguments: " + Expression);
            e.printStackTrace();
        }
    }

    /*Method: Converts prefix to Infix format. Ex:  +*AB*CD -> (A * B) + (C * D)   */
    public static String infixFormat(String prefixExpression) {
        Stack<String> stack = new Stack<>();
        String[] tokens = prefixExpression.split("\\s+");
        //for (String token : tokens) {System.out.println(token);}
        for (int i = tokens.length - 1; i >= 0; i--) {
            //System.out.println("Stack: " + stack.toString());
            String token = tokens[i];
            //System.out.println("Checking this Token: " + token);
            if (!isOperator(token)) {
                stack.push(token);
            } else {
                String operand1 = stack.pop();
                String operand2 = stack.pop();
                String infix = "( " + operand1 + " " + token + " " + operand2 + " )";
                //System.out.println("Infix: " + infix);
                stack.push(infix);
            }

        }
        return stack.pop();
    }

    /*Method: Evalues the Infix Expression*/
    public static double evaluateInfix(String infix) {
        Stack<Double> opnds = new Stack<>();
        Stack<Character> operator = new Stack<>();
        StringBuilder numBuilder = new StringBuilder();
        //StringBuilder sb = new StringBuilder();
        for (int i = 0; i < infix.length(); i++) {
            char ch1 = infix.charAt(i);
            if (ch1 == '(') {
                operator.push(ch1);
                /*NOTE: Checking to make sure a number is pos. or neg*/
            } else if (Character.isDigit(ch1) || (ch1 == '.') || (ch1 == '-' && Character.isDigit(infix.charAt(i + 1)))) {
                /*NOTE: Building a Number so numbers such as 30 do not evalute to 3 & 0 */
                numBuilder.append(ch1);
                if ((i == infix.length() - 1) || (!Character.isDigit(infix.charAt(i + 1)) && infix.charAt(i + 1) != '.')) {
                    opnds.push(Double.parseDouble(numBuilder.toString()));
                    numBuilder = new StringBuilder();
                }
            } else if (ch1 == ')') {
                /*NOTE: Performing Arthimetic calculations when encountring a ')' in order to keep values grouped together */
                while (operator.peek() != '(') {
                    //System.out.println("**************** CLOSED BRACKET **********************");
                    double y = opnds.pop();
                    double x = opnds.pop();
                    char ch2 = operator.pop();
                    double result = doArithmetic(x, y, ch2);
                    opnds.push(result);
                    //System.out.println("(" + x + " " + ch2 + " " + y + ")" + " = " + result);
                    //System.out.println("************* END OF CLOSED BRACKET ****************");
                }
                operator.pop();
            } else if (isOperator(String.valueOf(ch1))) {
                operator.push(ch1);
            }
            //System.out.println(" Infix: " + sb.append(infix.charAt(i)));
        }
        return opnds.peek();
    }

    /*Method: Checks to see if a given token is an appropriate operator*/
    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    /*Method: Performs basic arithmetic given two numbers and an operator*/
    public static double doArithmetic(double x, double y, char op) {
        return switch (op) {
            case '+' ->
                x + y;
            case '-' ->
                x - y;
            case '*' ->
                x * y;
            case '/' ->
                x / y;
            case '^' ->
                Math.pow(x, y);
            default ->
                -1;
        };
    } 
}
