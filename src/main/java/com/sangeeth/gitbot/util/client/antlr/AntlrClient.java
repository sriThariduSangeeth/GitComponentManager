package com.sangeeth.gitbot.util.client.antlr;

import com.sangeeth.gitbot.antlr.Java8BaseListener;
import com.sangeeth.gitbot.antlr.Java8Parser;
import com.sun.istack.internal.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

/**
 * @author dtsangeeth
 * @created 15 / 05 / 2020
 * @project GitComponentManager
 */
public class AntlrClient extends Java8BaseListener {

    private Java8Parser.MethodDeclarationContext currentMethod;
    private Java8Parser.NormalClassDeclarationContext classContext;

    String skeleton = null;
    StringBuilder stringBuilder;

    public AntlrClient(){
        stringBuilder = new StringBuilder();
    }

    @Override
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        // enum classes are non-normal
        List<Java8Parser.ClassModifierContext> modifier = ctx.classModifier();
        modifier.forEach(cmc -> {
            if(ObjectUtils.notEqual(cmc.PUBLIC(),null)){
                stringBuilder.append("Enter public class : ClassName");
            }else if (ObjectUtils.notEqual(cmc.PRIVATE(),null)){
                stringBuilder.append("Enter private class : ClassName");
            }else if(ObjectUtils.notEqual(cmc.STATIC(),null)){
                stringBuilder.append("Enter static class : ClassName");
            }else if(ObjectUtils.notEqual(cmc.ABSTRACT(),null)){
                stringBuilder.append("Enter abstract class : ClassName");
            }

        });
    }

    @Override
    public void exitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        // enum classes are non-normal

        // outer classes are: inside classdeclaration -> inside typedeclaration -> inside compilationunit
        // inner classes are: inside classdeclration -> inside classmemberdeclaration -> inside classdeclaration
        if(ctx.getParent() instanceof Java8Parser.ClassDeclarationContext
                && ctx.getParent().getParent() instanceof Java8Parser.TypeDeclarationContext){
            // outer class
        }else if(ctx.getParent() instanceof Java8Parser.ClassDeclarationContext
                && ctx.getParent().getParent() instanceof Java8Parser.ClassMemberDeclarationContext){
            // inner class
        }else{
            System.err.println("!!! Neither inner nor outer class");
        }

        stringBuilder.append("Exit noraml class : ClassName");
    }

    @Override
    public void enterLiteral(@NotNull Java8Parser.LiteralContext ctx) {
        if(ObjectUtils.notEqual(ctx.IntegerLiteral(),null)){
            stringBuilder.append("Integer val");
        }
        if (ObjectUtils.notEqual(ctx.StringLiteral(), null)){
            stringBuilder.append("String val");
        }
    }

    @Override
    public void exitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        String pckName = "";
        for(TerminalNode node : ctx.Identifier()){
            if(pckName.length() > 0){
                pckName += ".";
            }
            pckName += node.getText();
        }
        stringBuilder.append("PackageName : "+pckName);
    }

    @Override
    public void exitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        if(ctx.singleTypeImportDeclaration() != null){
            stringBuilder.append("Single Type Import Declaration exit: "
                    + ctx.singleTypeImportDeclaration().typeName().getText());
        }
        else if(ctx.typeImportOnDemandDeclaration() != null){
            stringBuilder.append("Type Import OnDemand Declaration exit: "
                    + ctx.typeImportOnDemandDeclaration().packageOrTypeName().getText());
        }
        else if(ctx.singleStaticImportDeclaration() != null){
            stringBuilder.append("Single Static Import Declaration exit: "
                    + ctx.singleStaticImportDeclaration().typeName().getText());
        }
        else if(ctx.staticImportOnDemandDeclaration() != null){
            stringBuilder.append("Static Import OnDemand Declaration exit: "
                    + ctx.staticImportOnDemandDeclaration().typeName().getText());
        }
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {

        stringBuilder.append("Method Declaration enter: "
                + ctx.methodHeader());

        currentMethod = ctx;
    }

    @Override
    public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        // method if a class member declaration -> is a class body declaration -> is a class body -> is a normal class
        stringBuilder.append("<<Method Declaration exit: "
                + ctx.methodHeader().methodDeclarator().getText());
        if(currentMethod == ctx){
            stringBuilder.append("Same Method Context!!!");
        }
        if(ctx.getParent() instanceof Java8Parser.ClassMemberDeclarationContext
                && ctx.getParent().getParent() instanceof Java8Parser.ClassBodyDeclarationContext
                && ctx.getParent().getParent().getParent() instanceof Java8Parser.ClassBodyContext){
            if(ctx.getParent().getParent().getParent().getParent() == classContext){
                stringBuilder.append("Method is inside our Class");
            }
        }else{
            stringBuilder.append("!!! Method is somewhere else");
        }

    }

    @Override public void enterTypeVariable(Java8Parser.TypeVariableContext ctx) {
        stringBuilder.append("enterPrimitiveType: "+ctx.Identifier().getSymbol());
    }

    @Override public void enterPrimitiveType(Java8Parser.PrimitiveTypeContext ctx) {
        stringBuilder.append("enterPrimitiveType: "+ctx.numericType().getText());
    }

    @Override public void enterType(Java8Parser.TypeContext ctx) {
        stringBuilder.append("enterType: "+ctx.primitiveType().toString());
    }

    @Override
    public void enterAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        stringBuilder.append("enterAssignmentOperator: "+ctx.getText());
    }

    @Override
    public void exitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        stringBuilder.append(ctx.getText());
    }

    @Override
    public void enterEqualityExpression(Java8Parser.EqualityExpressionContext ctx) {
        stringBuilder.append("Enter =");
    }

    @Override
    public void
    enterMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx) {
        stringBuilder.append(ctx.DIV());
    }

    @Override
    public void enterIntegralType(Java8Parser.IntegralTypeContext ctx) {
        stringBuilder.append(ctx.INT().getText());
    }

    public StringBuilder returnSkeleton (){
        return stringBuilder;
    }


}
