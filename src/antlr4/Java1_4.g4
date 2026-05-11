grammar Java1_4;

//Non-token generating lexemes
WHITESPACE: (' ' | '\t' | '\f' | LINETERMINATOR) -> skip ;	// '-> skip' means no token generation for this match
fragment LINETERMINATOR: '\r\n' | '\r' | '\n';
COMMENT: ('/*' .*? '*/' | '//' ~[\r\n]*) -> skip;

MODIFIER: PUBLIC | PROTECTED | PRIVATE | STATIC 
		| ABSTRACT | FINAL | NATIVE | SYNCHRONIZED 
		| TRANSIENT | VOLATILE | STRICTFP;
		
BASICTYPE: BYTE | SHORT | CHAR | INT | LONG | FLOAT | DOUBLE | BOOLEAN;
       
ABSTRACT      : 'abstract'; 
BOOLEAN       : 'boolean';
BREAK         : 'break';
BYTE          : 'byte';
CASE          : 'case';
CATCH         : 'catch';
CHAR          : 'char';
CLASS         : 'class';
CONST         : 'const';
CONTINUE      : 'continue';
DEFAULT       : 'default';
DO            : 'do';
DOUBLE        : 'double';
ELSE          : 'else';
EXTENDS       : 'extends';
FINAL         : 'final';
FINALLY       : 'finally';
FLOAT         : 'float';
FOR           : 'for';
GOTO          : 'goto';
IF            : 'if';
IMPLEMENTS    : 'implements';
IMPORT        : 'import';
INSTANCEOF    : 'instanceof';
INT           : 'int';
INTERFACE     : 'interface';
LONG          : 'long';
NATIVE        : 'native';
NEW           : 'new';
PACKAGE       : 'package';
PRIVATE       : 'private';
PROTECTED     : 'protected';
PUBLIC        : 'public';
RETURN        : 'return';
SHORT         : 'short';
STATIC        : 'static';
STRICTFP      : 'strictfp';
SUPER         : 'super';
SWITCH        : 'switch';
SYNCHRONIZED  : 'synchronized';
THIS          : 'this';
THROW         : 'throw';
THROWS        : 'throws';
TRANSIENT     : 'transient';
TRY           : 'try';
VOID          : 'void';
VOLATILE      : 'volatile';
WHILE         : 'while';
ASSERT		  : 'assert';

//Literals
LITERAL: INTEGERLITERAL | FLOATINGPOINTLITERAL | CHARACTERLITERAL | BOOLEANLITERAL | STRINGLITERAL;

INTEGERLITERAL: DECIMALINTEGERLITERAL | HEXINTEGERLITERAL | OCTALINTEGERLITERAL ;
fragment DECIMALINTEGERLITERAL	: ('0' | [1-9] [0-9]*) [lL]?;	//0, or 1-9 followed by any number of 0-9. Can be suffixed by l/L for "long"
fragment HEXINTEGERLITERAL		: '0' [xX] HEXDIGIT+ [lL]?; 	//>=1 length string composed of 0-9 a-f A-F and prefixed by 0. Can be suffixed by l/L for "long"
fragment OCTALINTEGERLITERAL	: '0' [0-7]+ [lL]?;				//>=1 length string composed of 0-7 and prefixed by 0. Can be suffixed by l/L for "long"
	//These definitions were broken down into smaller sub-fragments in JLS, but they aren't reused across the specification so they were un-abstracted

FLOATINGPOINTLITERAL: [0-9]+ '.' [0-9]* EXPONENTPART? [fF]?
				  	| '.' [0-9]+ EXPONENTPART? [fF]?
				  	| [0-9]+ EXPONENTPART [fF]?			//If not prefixed by ., must have either exponentPart or float suffix (see next)
				  	| [0-9]+ EXPONENTPART? [fF];		//see previous
fragment EXPONENTPART: [eE] [+\-] [0-9]+; 				//e+#, E+#, e-#, E-# (\ is needed to use - as 'minus' instead of specifying a range)

CHARACTERLITERAL: '\'' (SINGLECHARACTER | ESCAPESEQUENCE) '\'';
fragment SINGLECHARACTER : ~[\r\n'\\];

BOOLEANLITERAL:	'true' | 'false';

STRINGLITERAL: '"' STRINGCHARACTERS '"';
fragment STRINGCHARACTERS: STRINGCHARACTER+;
fragment STRINGCHARACTER: ~[\r\n"\\] | ESCAPESEQUENCE;
fragment ESCAPESEQUENCE: '\\' [btnfr"'\\] | OCTALESCAPE;
fragment OCTALESCAPE: '\\' OCTALDIGIT
				    | '\\' OCTALDIGIT OCTALDIGIT
				    | '\\' ZEROTOTHREE OCTALDIGIT OCTALDIGIT;
fragment OCTALDIGIT: [0-7];
fragment ZEROTOTHREE: [0-3];

NULLLITERAL: 'null';

//Identifiers (Leave below Keywords and BooleanLiteral and NullLiteral
IDENTIFIER:	JAVALETTER JAVALETTERORDIGIT*; 	//Unabstracted IdentifierChars into Identifier
fragment JAVALETTER: [A-Za-z$_];  			//ASCII letters, underscore, dollar sign
fragment JAVALETTERORDIGIT: JAVALETTER | [0-9];

//Unicode and Input characters
UNICODEINPUTCHARACTER: UNICODEESCAPE | RAWINPUTCHARACTER;
fragment UNICODEESCAPE: '\\' UNICODEMARKER HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT;
fragment UNICODEMARKER: 'u'+;
fragment RAWINPUTCHARACTER: . ;
fragment HEXDIGIT: [0-9a-fA-F];
fragment INPUTCHARACTER: ~[\r\n];

//Separators
	/*SEPARATOR: [(){}[\];,.];
	 * 	Unused?
	 */


identifier : IDENTIFIER;

qualifiedIdentifier: identifier ('.' identifier)*;

expression: expression1 (assignmentOp expression1)?;

type: identifier ('.' identifier)* bracketsOpt | BASICTYPE bracketsOpt;

statementExpression: expression;

constantExpression: expression;

expression1: expression2 expression1Rest?;

expression1Rest: ('?'  expression ':'   expression1);

expression2: expression3 expression2Rest?;

expression2Rest: (infixOp expression3)* | expression3 INSTANCEOF type;

expression3: prefixOp expression3 | '(' expression | type ')' expression3 | primary (selector)* (postfixOp)*;

primary: '(' expression ')' | THIS arguments? | SUPER superSuffix | LITERAL | NEW creator
         | qualifiedIdentifier identifierSuffix? | BASICTYPE bracketsOpt '.' CLASS | VOID '.' CLASS;

identifierSuffix:  '[' (']' bracketsOpt '.' CLASS | expression ']') | arguments
                  | '.' ( CLASS | THIS | SUPER arguments | NEW innerCreator );

prefixOp: '++' | '--' | '!' | '~' | '+' | '-';

postfixOp: '++' | '--';

infixOp: '||' | '&&' | '|' | '^'
     | '&' | '==' | '!=' | '<'
     | '>' | '<=' | '>=' | '<<'
     | '>>' | '>>>' | '+' | '-'| '*'
     | '/' | '%';
          
assignmentOp: eqSign | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '%=' | '<<=' | '>>=' | '>>>=';
eqSign: '=';

selector: '.' identifier arguments? | '.' THIS | '.' SUPER superSuffix | '.' NEW innerCreator | '[' expression ']';

superSuffix: arguments | '.' identifier arguments?;

arguments: '(' (expression ( ',' expression )*)? ')';

bracketsOpt: ('['']')*;

creator: qualifiedIdentifier ( arrayCreatorRest | classCreatorRest );

innerCreator: identifier classCreatorRest;

arrayCreatorRest: '[' ( ']' bracketsOpt arrayInitializer | expression ']' ('['expression ']')* bracketsOpt );

classCreatorRest: arguments classBody?;

arrayInitializer: '{' (variableInitializer (',' variableInitializer)* ','?)? '}';

variableInitializer: arrayInitializer | expression;

parExpression: '(' expression ')';

block: '{' blockStatements '}';

blockStatements: blockStatement*;

blockStatement : localVariableDeclarationStatement | classOrInterfaceDeclaration | (IDENTIFIER ':')? statement;

localVariableDeclarationStatement: FINAL? type variableDeclarators ';' ;

elseKeyword: ELSE;
finallyKeyword : FINALLY;

statement:  block | IF parExpression statement (elseKeyword statement)? |
            FOR '(' forInit ';' expression? ';' forUpdate ')' statement |
            WHILE parExpression statement | DO statement WHILE parExpression ';' |
            TRY block ( catches | catches? finallyKeyword block ) |
            SWITCH parExpression '{' switchBlockStatementGroups '}' | SYNCHRONIZED parExpression block |
            RETURN expression? ';' | THROW expression ';' | BREAK identifier? | CONTINUE identifier? | ';' |
            statementExpression | identifier ':' statement;

catches: catchClause catchClause*;

catchClause: CATCH '(' formalParameter ')' block;

switchBlockStatementGroups: switchBlockStatementGroup*;

switchBlockStatementGroup: switchLabel blockStatements;

switchLabel: CASE constantExpression ':' | DEFAULT ':';

moreStatementExpressions: (',' statementExpression)*;

forInit: statementExpression moreStatementExpressions | FINAL? type variableDeclarators;

forUpdate: statementExpression moreStatementExpressions;

modifiersOpt: (MODIFIER)*;

variableDeclarators: variableDeclarator (',' variableDeclarator)*;

constantDeclaratorsRest: constantDeclaratorRest (',' constantDeclarator)*;

variableDeclarator: identifier variableDeclaratorRest;

constantDeclarator: identifier constantDeclaratorRest;

variableDeclaratorRest: bracketsOpt (eqSign variableInitializer)?;

constantDeclaratorRest: bracketsOpt eqSign variableInitializer;

variableDeclaratorId: identifier bracketsOpt;

compilationUnit: (PACKAGE qualifiedIdentifier ';' )? importDeclaration* typeDeclaration* EOF;

importDeclaration: IMPORT identifier ('.' identifier)* ( '.' '*' )?';';

typeDeclaration: classOrInterfaceDeclaration | ';';

classOrInterfaceDeclaration: modifiersOpt (classDeclaration | interfaceDeclaration);

classDeclaration: CLASS identifier (EXTENDS type)? (IMPLEMENTS typeList)? classBody;

interfaceDeclaration: identifier identifier (EXTENDS typeList)? interfaceBody;

typeList: type (','   type)*;

classBody: '{' classBodyDeclaration* '}';

interfaceBody: '{' interfaceBodyDeclaration* '}';

classBodyDeclaration: ';' | STATIC? block | modifiersOpt memberDecl;

memberDecl: methodOrFieldDecl | VOID IDENTIFIER methodDeclaratorRest | IDENTIFIER constructorDeclaratorRest |
            classOrInterfaceDeclaration;

methodOrFieldDecl: type IDENTIFIER methodOrFieldRest;

methodOrFieldRest: variableDeclaratorRest | methodDeclaratorRest;

interfaceBodyDeclaration: ';' | modifiersOpt interfaceMemberDecl;

interfaceMemberDecl: interfaceMethodOrFieldDecl | VOID IDENTIFIER voidInterfaceMethodDeclaratorRest |
    classOrInterfaceDeclaration;

interfaceMethodOrFieldDecl: type IDENTIFIER interfaceMethodOrFieldRest;

interfaceMethodOrFieldRest: constantDeclaratorsRest ';' | interfaceMethodDeclaratorRest;

methodDeclaratorRest: formalParameters bracketsOpt (THROWS qualifiedIdentifierList)? (methodBody | ';');

interfaceMethodDeclaratorRest: formalParameters bracketsOpt (THROWS qualifiedIdentifierList)? ';';

voidInterfaceMethodDeclaratorRest: formalParameters (THROWS qualifiedIdentifierList)? ';';

constructorDeclaratorRest: formalParameters (THROWS qualifiedIdentifierList)? methodBody;

qualifiedIdentifierList: qualifiedIdentifier (',' qualifiedIdentifier)*;

formalParameters: '(' (formalParameter (',' formalParameter)*)? ')';

formalParameter: FINAL? type variableDeclaratorId;

methodBody: block;
