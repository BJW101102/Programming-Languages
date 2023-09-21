//Brandon Walton
/* This program is a IEEE-754 floating-point converting program that prompts a user for 
   a number, double, and returns both its 16 and 32 bit IEEE-754 binary representation*/  

package ieee754;
import java.util.Scanner;
import java.util.Stack;

public class IEEE754 {

    //Converts Numbers into the IEEE754 Format 
    public static void IEEE754(double num) {
        String answer;
        if (Double.toString(num).contains("E")) {
            if (num >= 1 || num < 0) {
                StringBuilder sb = new StringBuilder();
                //Loop is used to print all values before the '.' to avoid too many 0's
                for (int i = 0; i < formatedDecimal(num).length(); i++) {
                    if (formatedDecimal(num).charAt(i) != '.') {
                        sb.append(formatedDecimal(num).charAt(i));
                    }
                    if (formatedDecimal(num).charAt(i) == '.') {
                        sb.append('.');
                        sb.append(0);
                        break;
                    }
                }
                String formatNum = sb.toString();
                System.out.println("Input: " + formatNum);
            } else {
                System.out.println("Input: " + formatedDecimal(num));
            }
        } else {
            System.out.println("Input: " + num);
        }

        // Positive Numbers & excludes -0
        if (num >= 0 && !Double.toString(num).contains("-")) {
            answer = "0 " + getMantExp(num, 23, Double.toString(num));
            System.out.println(answer);
            answer = "0 " + getMantExp(num, 52, Double.toString(num));
            System.out.println(answer);
        }

        //Negative Numbers && -0
        if (num < 0 || Double.toString(num).contains("-")) {
            answer = "1 " + getMantExp(num, 23, Double.toString(num));
            System.out.println(answer);
            answer = "1 " + getMantExp(num, 52, Double.toString(num));
            System.out.println(answer);
        }
    }

    //Returns the Exponential and Mantissa
    public static String getMantExp(double num, int bits, String binNum) {

        StringBuilder sb = new StringBuilder();
        boolean isZero = false;
        boolean isSpecialZero = false;
        int shift;

        //Ignores the negative Sign
        if (num == 0) {
            isZero = true;
            binNum = toBinary(num, bits);
            //Negative Num -> Ignore negative sign 
        } else if (num < 0) {
            num = num * - 1;
            binNum = toBinary(num, bits);
        } else {
            //Positive Num.
            binNum = toBinary(num, bits);
        }
        //Non-Zero Cases 
        if (!isZero) {

            //Check For SpecialZero: Ex: 0.085
            if (num < 1 && num > 0) {
                isSpecialZero = true;
            }

            //Fractional Portion
            char current;
            boolean isFrac = false;
            for (int i = 0; i < binNum.length(); i++) {
                current = binNum.charAt(i);
                if (current == '.') {
                    isFrac = true;
                }
                if (isFrac && current != '.') {
                    sb.append(current);
                }
            }
            String binFrac = sb.toString();
            sb.setLength(0);

            //Whole Number Portion
            for (int i = 0; i < binNum.length(); i++) {
                current = binNum.charAt(i);
                if (current == '.') {
                    break;
                } else {
                    sb.append(current);
                }
            }
            String binWhole = sb.toString();
            sb.setLength(0);

            //Copying Everything After the the first One
            boolean startCopying = false;
            String binCopy = binNum;
            int counter = 0;
            for (int i = 0; i < binCopy.length(); i++) {
                current = binCopy.charAt(i);
                if (current == '1' && !startCopying) {
                    startCopying = true;
                } else {
                    sb.append(current);
                }
            }

            //Finding The Shift Amount 
            String newCopy = sb.toString();
            int count = 0;
            for (int i = 0; i < newCopy.length(); i++) {
                current = newCopy.charAt(i);
                if (current != '.') {
                    count++;
                }
                if (current == '.') {
                    break;
                }
            }

            //Setting to negative to be  num - shift
            if (isSpecialZero) {
                //Takes Into Account for those with full 0's
                if (!newCopy.contains("1")) {
                    shift = - 1 * (count);
                } else {
                    shift = - 1 * (count + 1);
                }
            } else {
                shift = count;
            }

            sb.setLength(0);

            //Truncated Mantissa
            String newBinNum = binWhole + binFrac;
            boolean isFirstOne = true;
            int count2 = 0;
            //For Numbers that don't contain 1
            if (!newBinNum.contains("1")) {
                isFirstOne = false;
            }
            for (int i = 0; i < newBinNum.length(); i++) {
                current = newBinNum.charAt(i);
                if (current == '1' && isFirstOne) {
                    isFirstOne = false;
                } else if (!isFirstOne) {
                    count2++;
                    sb.append(current);
                }
                if (count2 == bits) {
                    break;
                }
            }

            //Assigning Mantissa and exp variable
            String mantissa = sb.toString();
            sb.setLength(0);
            String exp;
            if (num == 1) {
                sb.append(0);
                if (bits == 23) {
                    for (int i = 1; i < 8; i++) {
                        sb.append(1);
                    }
                } else if (bits == 52) {
                    for (int i = 1; i < 11; i++) {
                        sb.append(1);
                    }
                }

                exp = sb.toString();
            } else {
                exp = getExp(shift, num, bits, isSpecialZero);
            }

            return exp + " " + mantissa;
        } else {
            //Zero Case
            if (bits == 23) {
                for (int i = 0; i < 8; i++) {
                    sb.append(0);
                }
            } else if (bits == 52) {
                for (int i = 0; i < 11; i++) {
                    sb.append(0);
                }
            }
            String exp = sb.toString();
            String mantissa = binNum;
            return exp + " " + mantissa;
        }
    }

    //Calculates the Exponential
    public static String getExp(int shift, double num, int bits, boolean isSpecialZero) {
        Stack binStack = new Stack();
        StringBuilder sb = new StringBuilder();
        int signBit = 1;
        int totalBits = 0;
        double whole = getWhole(num);
        if (bits == 23) {
            totalBits = 32;
        } else if (bits == 52) {
            totalBits = 64;
        }

        //Declaring an exponent variable
        int exponent = ((totalBits - bits) - 1) - signBit;
        //System.out.println("Exponent: " + exponent);

        // Finding tMax w/ signBit included
        double tMax = (Math.pow(2, exponent));
        double doubleExp = (tMax - 1) + shift;
        int expNum = (int) doubleExp;

        //Creating Stack for Exp
        while (expNum > 0) {
            binStack.push(expNum % 2);
            expNum /= 2;
        }

        if (whole == 1) {
            binStack.push(0);
        }
        //Pushing Extra 0 for specialZero
        if (isSpecialZero) {
            if (expNum == 0 && expNum % 2 == 0) {
                binStack.push(expNum % 2);
            }
        }

        //Empyting Stack for Exp
        while (!binStack.empty()) {
            sb.append(binStack.pop());
        }
        return sb.toString();
    }

    //Converts to Binary
    public static String toBinary(double num, int bits) {
        String stringWhole = null;
        StringBuilder sb = new StringBuilder();
        Stack binStack = new Stack();
        boolean isSpecialZero = false;
        double doubleWhole = (int) num;
        double frac;
        long whole;
        double specialFrac = 0;

        //Returning all 0's for Zero Case
        if (num == 0) {
            for (int i = 0; i < bits; i++) {
                sb.append(0);
            }
            return sb.toString();

        } else if (num > 0 && num < 1) {
            isSpecialZero = true;
        }
        if (num % 1 == 0) {
            frac = 0;
            char current;
            //Gathering the whole with Numbers that convert with Compiler
            if (Double.toString(num).contains("E")) {
                StringBuilder sb2 = new StringBuilder();
                String stringNum = formatedDecimal(num);
                for (int i = 0; i < stringNum.length(); i++) {
                    current = stringNum.charAt(i);

                    if (current != '.') {
                        sb2.append(current);
                    } else if (current == '.') {
                        break;
                    }
                }
                //Setting whole to this numerical string to avoid truncating and rounding
                //Ex: 2147483648 being truncated to 2147483647
                whole = Long.parseLong(sb2.toString());
            }
            whole = (long) num;

        } else {
            // Gathering Whole Number and Fractional Part
            frac = Math.abs(doubleWhole - num);
            whole = (long) doubleWhole;
        }

        //Whole Number -> Binary 
        if (isSpecialZero) {
            int count = 1;
            double numCopy = num;
            double denom = 1;
            double answer = numCopy / denom;

            //Used Contains E to deal with Java Compiler Automatic scientific notation            
            while (!Double.toString(answer).startsWith("1") || (Double.toString(answer).contains("E") && answer < 1)) {
                denom = Math.pow(2, -(count));
                answer = numCopy / denom;
                if (answer > 1) {
                    binStack.push(1);
                } else {
                    binStack.push(0);
                }
                //binStack.push(Double.toString(answer).charAt(0));
                count++;
            }
            //Why is this Used?
            specialFrac = answer;
            while (!binStack.empty()) {
                sb.append(binStack.pop());
            }

            stringWhole = sb.reverse().toString();
        } //Whole Number -> nonSpecialZero
        else if (!isSpecialZero) {
            while (whole > 0) {
//                System.out.println("Whole is: " + whole);
//                System.out.println("Remainder from / 2: " + whole % 2);
                if (whole % 2 >= 1) {
                    binStack.push(1);
                } else {
                    binStack.push(0);
                }

//                binStack.push(whole % 2);
                whole /= 2;
            }

            //Empyting Stack for Whole
            while (!binStack.empty()) {
                sb.append(binStack.pop());
            }

            //Setting a string for Whole
            stringWhole = sb.toString();
            sb.setLength(0);
        }

        if (isSpecialZero) {
            //Fractional -> SpecialZero
            frac = getFrac(specialFrac);
            for (int i = 0; i < bits; i++) {
                binStack.push(Double.toString(getWhole(frac * 2)).charAt(0));
                frac = getFrac(frac * 2);
            }
            while (!binStack.empty()) {
                sb.append(binStack.pop());
            }
        } else if (!isSpecialZero) {
            //Fractional -> nonSpecialZero
            for (int i = 0; i < bits; i++) {
//                System.out.println("Frac: " + formatedDecimal(frac * 2));
                if (frac * 2 >= 1) {
                    binStack.push(1);
                } else {
                    binStack.push(0);
                }
//                binStack.push(Double.toString(getWhole(frac * 2)).charAt(0));
//                System.out.println("Frac is: " + frac);
                frac = getFrac(frac * 2);
            }

            while (!binStack.empty()) {
                sb.append(binStack.pop());
            }
        }

        String stringFrac = sb.reverse().toString();
        String toBinary = stringWhole + "." + stringFrac;
        return toBinary;
    }

    //Gets the Whole Number
    public static double getWhole(double num) {
        //Used to gather the whole section of a number
        long whole = (long) num;
        return whole;
    }

    //Gets The Fractional Number
    public static double getFrac(double num) {
        double frac;
        double doubleNum;

        //Used to get the fractional of an existing number
        long whole = (long) num;
        doubleNum = whole;
        frac = Math.abs(doubleNum - num);
        return frac;
    }

    //Formats Numbers to avoid Java Scientific Conversion
    public static String formatedDecimal(double num) {
        //Used to Avoid The Java Compiler Conversion
        StringBuilder sb = new StringBuilder();
        String stringNum = Double.toString(num);
        String decimalFormat;

        //Creating a StringBuilder of the num
        for (int i = 0; i < stringNum.length(); i++) {
            sb.append(stringNum.charAt(i));
        }
        //Reverseing String to appened numbers until "E"
        String reverseNum = sb.reverse().toString();
        sb.setLength(0);

        //Setting a loop that appends every digit before "E"
        for (int i = 0; i < reverseNum.length(); i++) {

            if (Character.isDigit(reverseNum.charAt(i))) {
                sb.append(reverseNum.charAt(i));
            } else {
                break;
            }
        }

        //Setting a String that will later be used in printf();
        //Note: sb.reverse().toString() = num to shift decimal 
        //This is Kept to ensure the full value is being printed 
        String decShift = sb.reverse().toString();
//        System.out.println("Dec Shift: " + decShift);
        decimalFormat = "%." + sb.reverse().toString() + "f";

        return String.format(decimalFormat, num);
    }
}
