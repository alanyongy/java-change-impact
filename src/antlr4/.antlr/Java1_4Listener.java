// Generated from e:/School/CPSC499/Project/src/antlr4/Java1_4.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Java1_4Parser}.
 */
public interface Java1_4Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(Java1_4Parser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(Java1_4Parser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#qualifiedIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedIdentifier(Java1_4Parser.QualifiedIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#qualifiedIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedIdentifier(Java1_4Parser.QualifiedIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(Java1_4Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(Java1_4Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(Java1_4Parser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(Java1_4Parser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(Java1_4Parser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(Java1_4Parser.StatementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(Java1_4Parser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(Java1_4Parser.ConstantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression1}.
	 * @param ctx the parse tree
	 */
	void enterExpression1(Java1_4Parser.Expression1Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression1}.
	 * @param ctx the parse tree
	 */
	void exitExpression1(Java1_4Parser.Expression1Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression1Rest}.
	 * @param ctx the parse tree
	 */
	void enterExpression1Rest(Java1_4Parser.Expression1RestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression1Rest}.
	 * @param ctx the parse tree
	 */
	void exitExpression1Rest(Java1_4Parser.Expression1RestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression2}.
	 * @param ctx the parse tree
	 */
	void enterExpression2(Java1_4Parser.Expression2Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression2}.
	 * @param ctx the parse tree
	 */
	void exitExpression2(Java1_4Parser.Expression2Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression2Rest}.
	 * @param ctx the parse tree
	 */
	void enterExpression2Rest(Java1_4Parser.Expression2RestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression2Rest}.
	 * @param ctx the parse tree
	 */
	void exitExpression2Rest(Java1_4Parser.Expression2RestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#expression3}.
	 * @param ctx the parse tree
	 */
	void enterExpression3(Java1_4Parser.Expression3Context ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#expression3}.
	 * @param ctx the parse tree
	 */
	void exitExpression3(Java1_4Parser.Expression3Context ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(Java1_4Parser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(Java1_4Parser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#identifierSuffix}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierSuffix(Java1_4Parser.IdentifierSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#identifierSuffix}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierSuffix(Java1_4Parser.IdentifierSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void enterPrefixOp(Java1_4Parser.PrefixOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#prefixOp}.
	 * @param ctx the parse tree
	 */
	void exitPrefixOp(Java1_4Parser.PrefixOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void enterPostfixOp(Java1_4Parser.PostfixOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#postfixOp}.
	 * @param ctx the parse tree
	 */
	void exitPostfixOp(Java1_4Parser.PostfixOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#infixOp}.
	 * @param ctx the parse tree
	 */
	void enterInfixOp(Java1_4Parser.InfixOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#infixOp}.
	 * @param ctx the parse tree
	 */
	void exitInfixOp(Java1_4Parser.InfixOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#assignmentOp}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOp(Java1_4Parser.AssignmentOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#assignmentOp}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOp(Java1_4Parser.AssignmentOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#eqSign}.
	 * @param ctx the parse tree
	 */
	void enterEqSign(Java1_4Parser.EqSignContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#eqSign}.
	 * @param ctx the parse tree
	 */
	void exitEqSign(Java1_4Parser.EqSignContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#selector}.
	 * @param ctx the parse tree
	 */
	void enterSelector(Java1_4Parser.SelectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#selector}.
	 * @param ctx the parse tree
	 */
	void exitSelector(Java1_4Parser.SelectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void enterSuperSuffix(Java1_4Parser.SuperSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void exitSuperSuffix(Java1_4Parser.SuperSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(Java1_4Parser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(Java1_4Parser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#bracketsOpt}.
	 * @param ctx the parse tree
	 */
	void enterBracketsOpt(Java1_4Parser.BracketsOptContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#bracketsOpt}.
	 * @param ctx the parse tree
	 */
	void exitBracketsOpt(Java1_4Parser.BracketsOptContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(Java1_4Parser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(Java1_4Parser.CreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void enterInnerCreator(Java1_4Parser.InnerCreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void exitInnerCreator(Java1_4Parser.InnerCreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreatorRest(Java1_4Parser.ArrayCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreatorRest(Java1_4Parser.ArrayCreatorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterClassCreatorRest(Java1_4Parser.ClassCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitClassCreatorRest(Java1_4Parser.ClassCreatorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(Java1_4Parser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(Java1_4Parser.ArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(Java1_4Parser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(Java1_4Parser.VariableInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(Java1_4Parser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(Java1_4Parser.ParExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(Java1_4Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(Java1_4Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatements(Java1_4Parser.BlockStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatements(Java1_4Parser.BlockStatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(Java1_4Parser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(Java1_4Parser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(Java1_4Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(Java1_4Parser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#elseKeyword}.
	 * @param ctx the parse tree
	 */
	void enterElseKeyword(Java1_4Parser.ElseKeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#elseKeyword}.
	 * @param ctx the parse tree
	 */
	void exitElseKeyword(Java1_4Parser.ElseKeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#finallyKeyword}.
	 * @param ctx the parse tree
	 */
	void enterFinallyKeyword(Java1_4Parser.FinallyKeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#finallyKeyword}.
	 * @param ctx the parse tree
	 */
	void exitFinallyKeyword(Java1_4Parser.FinallyKeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(Java1_4Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(Java1_4Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#catches}.
	 * @param ctx the parse tree
	 */
	void enterCatches(Java1_4Parser.CatchesContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#catches}.
	 * @param ctx the parse tree
	 */
	void exitCatches(Java1_4Parser.CatchesContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(Java1_4Parser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(Java1_4Parser.CatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#switchBlockStatementGroups}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroups(Java1_4Parser.SwitchBlockStatementGroupsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#switchBlockStatementGroups}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroups(Java1_4Parser.SwitchBlockStatementGroupsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(Java1_4Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(Java1_4Parser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(Java1_4Parser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(Java1_4Parser.SwitchLabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#moreStatementExpressions}.
	 * @param ctx the parse tree
	 */
	void enterMoreStatementExpressions(Java1_4Parser.MoreStatementExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#moreStatementExpressions}.
	 * @param ctx the parse tree
	 */
	void exitMoreStatementExpressions(Java1_4Parser.MoreStatementExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(Java1_4Parser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(Java1_4Parser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(Java1_4Parser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(Java1_4Parser.ForUpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#modifiersOpt}.
	 * @param ctx the parse tree
	 */
	void enterModifiersOpt(Java1_4Parser.ModifiersOptContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#modifiersOpt}.
	 * @param ctx the parse tree
	 */
	void exitModifiersOpt(Java1_4Parser.ModifiersOptContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(Java1_4Parser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(Java1_4Parser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#constantDeclaratorsRest}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclaratorsRest(Java1_4Parser.ConstantDeclaratorsRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#constantDeclaratorsRest}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclaratorsRest(Java1_4Parser.ConstantDeclaratorsRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(Java1_4Parser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(Java1_4Parser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclarator(Java1_4Parser.ConstantDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclarator(Java1_4Parser.ConstantDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#variableDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorRest(Java1_4Parser.VariableDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#variableDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorRest(Java1_4Parser.VariableDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#constantDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclaratorRest(Java1_4Parser.ConstantDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#constantDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclaratorRest(Java1_4Parser.ConstantDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(Java1_4Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(Java1_4Parser.VariableDeclaratorIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(Java1_4Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(Java1_4Parser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(Java1_4Parser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(Java1_4Parser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(Java1_4Parser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(Java1_4Parser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#classOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceDeclaration(Java1_4Parser.ClassOrInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#classOrInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceDeclaration(Java1_4Parser.ClassOrInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(Java1_4Parser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(Java1_4Parser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(Java1_4Parser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(Java1_4Parser.InterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(Java1_4Parser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(Java1_4Parser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(Java1_4Parser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(Java1_4Parser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(Java1_4Parser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(Java1_4Parser.InterfaceBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(Java1_4Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(Java1_4Parser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void enterMemberDecl(Java1_4Parser.MemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void exitMemberDecl(Java1_4Parser.MemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#methodOrFieldDecl}.
	 * @param ctx the parse tree
	 */
	void enterMethodOrFieldDecl(Java1_4Parser.MethodOrFieldDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#methodOrFieldDecl}.
	 * @param ctx the parse tree
	 */
	void exitMethodOrFieldDecl(Java1_4Parser.MethodOrFieldDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#methodOrFieldRest}.
	 * @param ctx the parse tree
	 */
	void enterMethodOrFieldRest(Java1_4Parser.MethodOrFieldRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#methodOrFieldRest}.
	 * @param ctx the parse tree
	 */
	void exitMethodOrFieldRest(Java1_4Parser.MethodOrFieldRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBodyDeclaration(Java1_4Parser.InterfaceBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBodyDeclaration(Java1_4Parser.InterfaceBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceMemberDecl}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDecl(Java1_4Parser.InterfaceMemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceMemberDecl}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDecl(Java1_4Parser.InterfaceMemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceMethodOrFieldDecl}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodOrFieldDecl(Java1_4Parser.InterfaceMethodOrFieldDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceMethodOrFieldDecl}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodOrFieldDecl(Java1_4Parser.InterfaceMethodOrFieldDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceMethodOrFieldRest}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodOrFieldRest(Java1_4Parser.InterfaceMethodOrFieldRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceMethodOrFieldRest}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodOrFieldRest(Java1_4Parser.InterfaceMethodOrFieldRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#methodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaratorRest(Java1_4Parser.MethodDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#methodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaratorRest(Java1_4Parser.MethodDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#interfaceMethodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodDeclaratorRest(Java1_4Parser.InterfaceMethodDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#interfaceMethodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodDeclaratorRest(Java1_4Parser.InterfaceMethodDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#voidInterfaceMethodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterVoidInterfaceMethodDeclaratorRest(Java1_4Parser.VoidInterfaceMethodDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#voidInterfaceMethodDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitVoidInterfaceMethodDeclaratorRest(Java1_4Parser.VoidInterfaceMethodDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#constructorDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaratorRest(Java1_4Parser.ConstructorDeclaratorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#constructorDeclaratorRest}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaratorRest(Java1_4Parser.ConstructorDeclaratorRestContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#qualifiedIdentifierList}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedIdentifierList(Java1_4Parser.QualifiedIdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#qualifiedIdentifierList}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedIdentifierList(Java1_4Parser.QualifiedIdentifierListContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(Java1_4Parser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(Java1_4Parser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(Java1_4Parser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(Java1_4Parser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java1_4Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(Java1_4Parser.MethodBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java1_4Parser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(Java1_4Parser.MethodBodyContext ctx);
}