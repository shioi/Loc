package com.lox;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException{

        if(args.length > 1) {
            System.out.println("Usage: jloc <script.lox>");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    //methods
    private static void runFile(String filename) throws IOException {
        byte [] codebyte = Files.readAllBytes(Paths.get(filename));
        String source = new String(codebyte, Charset.defaultCharset());
        run(source);
        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);

    }

    private static void runPrompt () throws  IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for(;;) {
            System.out.print(">> ");
            String source = in.readLine();
            if (source == null) break;
            run(source);
            hadError = false;
        }

    }

    private static void run(String source) {
        Scanner sc = new Scanner(source);
        List<Token> tokens = sc.scanTokens();

        Parser parser = new Parser(tokens);
        List <Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF){
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }




}