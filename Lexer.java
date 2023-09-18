//Brandon Walton
package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    static String stringLexeme;
    static boolean noError = true;
    static ArrayList<String> Tokens = new ArrayList<>();
    static int current;

    /*Creates Tokens and adds to an ArrayList*/
    public static void Tokenize(String fileName) throws FileNotFoundException, IOException {
        File f = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(f));
        StringBuilder charLexeme = new StringBuilder();
        int prev = -1;
        while ((current = br.read()) != -1) {
            /* '/' Is checked first inorder to ignored inline and wrapped comments */
            if (current == '/') {
                StringBuilder sb = new StringBuilder();
                sb.append((char) current);
                stringLexeme = sb.toString();
                sb.setLength(0);
                current = br.read();
                if (current == '/') {
                    while (current != '\n') {
                        sb.append((char) current);
                        current = ' ';
                        current = br.read();
                    }
                } else if (current == '*') {
                    //Creating a string and setting to null until */ is encountered
                    current = br.read(); //Advance to ignore the '*'
                    while (current != -1 && !stringLexeme.endsWith("*/")) {
                        sb.append((char) current);
                        stringLexeme = sb.toString();
                        current = br.read();
                    }
                } else {
                    Tokens.add("DIV_OP");
                }
            }
            if (current == '"') {
                StringBuilder sb2 = new StringBuilder();
                sb2.append((char) current);
                current = br.read();
                while (current != '"' && current != -1) {
                    sb2.append((char) current);
                    current = br.read();
                }
                if (current == '"') {
                    sb2.append((char) current);
                    current = br.read();
                }
//                System.out.println(sb2);
                stringLexeme = sb2.toString();
//                System.out.println("Lexeme: " + stringLexeme);
//                System.out.println("Match?: " + stringLexeme.matches("\"(\\w|\\s)+\""));
                if (stringLexeme.matches("\"(\\w|\\s)+\"")) {
                    Tokens.add("STR_CONST");
                } else {
                    noError = false;
                    error(Tokens, stringLexeme);
                }
            }

            prev = current;
            //Creates Lexeme
            if (Character.isLetterOrDigit(current)) {
                charLexeme.append((char) current);
                prev = current;
                current = br.read();
                while (Character.isLetterOrDigit(current)) {
                    charLexeme.append((char) current);
                    prev = current;
                    current = br.read();
                }
                stringLexeme = charLexeme.toString();
//                System.out.println("Lexeme is: " + stringLexeme + " " + "Does it match: " + stringLexeme.matches("^[A-Za-z].*"));
                //Checks for keyword and Identifiers
                if (stringLexeme.equals("for")) {
                    Tokens.add("FOR");
                } else if (stringLexeme.equals("if")) {
                    Tokens.add("IF");
                } else if (stringLexeme.equals("else")) {
                    Tokens.add("ELSE");
                } else if (stringLexeme.equals("procedure")) {
                    Tokens.add("PROC");
                } else if (stringLexeme.equals("return")) {
                    Tokens.add("RETURN");
                } else if (stringLexeme.equals("int")) {
                    Tokens.add("INT");
                } else if (stringLexeme.equals("do")) {
                    Tokens.add("DO");
                } else if (stringLexeme.equals("break")) {
                    Tokens.add("BREAK");
                } else if (stringLexeme.equals("while")) {
                    Tokens.add("WHILE");
                } else if (stringLexeme.equals("end")) {
                    Tokens.add("END");
                    //Grad Student (Optional): STR_CONST
                } else if (stringLexeme.equals("string")) {
                    Tokens.add("STR");
                } else if (Character.isDigit(stringLexeme.charAt(0))) {
                    if (stringLexeme.matches("\\d+(\\;)?")) {
                        Tokens.add("INT_CONST");
                    } else {
                        noError = false;
                        error(Tokens, stringLexeme);
                    }
                    //IDENT CHECKER: Is placed under isDigit to check for Alphanumeric characters
                } else if (stringLexeme.matches("^[A-Za-z].*")) {
                    Tokens.add("IDENT");
                } else {
                    noError = false;
                    error(Tokens, stringLexeme);
                }
                //Resets charLexeme;
                charLexeme.setLength(0);
            }

            //Single Character Checks
            if (current == '+') {
                charLexeme.append((char) current);
                current = br.read();
                if (current == '+') {
                    Tokens.add("INC");
                } else {
                    Tokens.add("ADD_OP");
                }
                charLexeme.setLength(0);
            } else if (current == '>') {
                charLexeme.append((char) current);
                current = br.read();
                if (current == '=') {
                    Tokens.add("GE");
                } else {
                    Tokens.add("GT");
                }
                charLexeme.setLength(0);
            } else if (current == '<') {
                charLexeme.append((char) current);
                current = br.read();
                if (current == '=') {
                    Tokens.add("LE");
                } else {
                    Tokens.add("LT");
                }
                charLexeme.setLength(0);
            } else if (current == '=' && prev != '>' && prev != '<') {
                current = br.read();
                if (current == '=') {
                    Tokens.add("EE");
                } else {
                    Tokens.add("ASSIGN");
                }
                charLexeme.setLength(0);
            } else if (current == '-') {
                Tokens.add("SUB_OP");
            } else if (current == '*') {
                Tokens.add("MUL_OP");
            } else if (current == '%') {
                Tokens.add("MOD_OP");
            } else if (current == '(') {
                Tokens.add("LP");
            } else if (current == ')') {
                Tokens.add("RP");
            } else if (current == '{') {
                Tokens.add("LB");
            } else if (current == '}') {
                Tokens.add("RB");
            } else if (current == '|') {
                Tokens.add("OR");
            } else if (current == '&') {
                Tokens.add("AND");
            } else if (current == '!') {
                Tokens.add("NEG");
            } else if (current == ',') {
                Tokens.add("COMMA");
            } else if (current == ';') {
                Tokens.add("SEMI");
            } else if (!Character.isWhitespace(current) && current != -1 && current != '/') {
                for (int i = 0; i < Tokens.size(); i++) {
                    System.out.println(Tokens.get(i));
                }
                System.out.println("SYNTAX ERROR: INVALID CHARACTER");
                System.exit(0);
            }
        }
        //Resets stringLexeme
        stringLexeme = null;
        br.close(); //Closes File
        if (noError) {
            for (int i = 0; i < Tokens.size(); i++) {
                System.out.println(Tokens.get(i));
            }
        }
    }

    /*Prints an error message, and stops the program*/
    public static void error(ArrayList<String> Tokens, String Lexeme) {
//        System.out.println("Incorrect Lexeme: " + Lexeme);
        for (int i = 0; i < Tokens.size(); i++) {
            System.out.println(Tokens.get(i));
        }
        System.out.println("SYNTAX ERROR: INVALID IDENTIFIER NAME");
        System.exit(0);
    }
}

/* Commented out since STR_IDENT is not in the implementation
                StringBuilder sb3 = new StringBuilder();
                    current = br.read();
                    charLexeme.setLength(0);
                    charLexeme.append((char) current);
                    stringLexeme = charLexeme.toString();
                    //Creating the lexeme after the space of string
                    if (Character.isLetterOrDigit(current)) {
                        while (Character.isLetterOrDigit(current)) {
                            sb3.append((char) current);
                            current = br.read();
                        }
                    }
                    stringLexeme = sb3.toString();
                    //Used to check if the String's var. name != String
                    if (stringLexeme.matches("(^[A-Za-z])(\\w+)?") && !stringLexeme.equals("string")) {
                        Tokens.add("STR_IDENT");
                    } else if (stringLexeme.matches("\\d.*?")) {
                        noError = false;
                        error(Tokens, stringLexeme);
                    }
 */
