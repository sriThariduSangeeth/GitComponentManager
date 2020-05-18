package com.sangeeth.gitbot.util.client.antlr;

import com.sangeeth.gitbot.antlr.Java8Lexer;
import com.sangeeth.gitbot.antlr.Java8Parser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * @author dtsangeeth
 * @created 15 / 05 / 2020
 * @project GitComponentManager
 */
public class AntlrPaser {

    private String codeSkeleton;
    private AntlrClient antlrClient;

    public AntlrPaser(String text){

        CharStream charStream = CharStreams.fromString(text);
        Java8Lexer java8Lexer = new Java8Lexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(java8Lexer);
        Java8Parser java8Parser = new Java8Parser(commonTokenStream);
        ParseTree parseTree = java8Parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        antlrClient = new AntlrClient();
        walker.walk(antlrClient, parseTree);

    }


    public String getCodeSkeleton(){

        StringBuilder stringBuilder = antlrClient.returnSkeleton();
        codeSkeleton = stringBuilder.toString();
        return codeSkeleton;
    }
}
