import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.NullPointerException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import antlr4.Java1_4BaseListener;
import antlr4.Java1_4Lexer;
import antlr4.Java1_4Parser;
import antlr4.Java1_4Parser.AssignmentOpContext;
import antlr4.Java1_4Parser.BlockStatementsContext;
import antlr4.Java1_4Parser.CatchClauseContext;
import antlr4.Java1_4Parser.ConstantExpressionContext;
import antlr4.Java1_4Parser.ElseKeywordContext;
import antlr4.Java1_4Parser.EqSignContext;
import antlr4.Java1_4Parser.Expression1Context;
import antlr4.Java1_4Parser.ExpressionContext;
import antlr4.Java1_4Parser.IdentifierContext;
import antlr4.Java1_4Parser.LocalVariableDeclarationStatementContext;
import antlr4.Java1_4Parser.MethodDeclaratorRestContext;
import antlr4.Java1_4Parser.ParExpressionContext;
import antlr4.Java1_4Parser.PostfixOpContext;
import antlr4.Java1_4Parser.PrefixOpContext;
import antlr4.Java1_4Parser.PrimaryContext;
import antlr4.Java1_4Parser.QualifiedIdentifierContext;
import antlr4.Java1_4Parser.StatementContext;
import antlr4.Java1_4Parser.VariableDeclaratorContext;
import antlr4.Java1_4Parser.VariableInitializerContext;

/**Builds and outputs textual visualizations of CFG graphs made from Java1.4 files specified through arguments or user input at runtime.*/
public class AnalysisTool {
	static CommonTokenStream tokenStream;
	static MethodListener methodListener;
	
	public static void main(String[] args) {

		String filePath = null;
		// Use command line arguments as filename inputs if available
		if (args.length == 1) {
			filePath = args[0];
		} else if (args.length > 1) {
			//Otherwise, request a path to the file from the user
			boolean validInput = true;
			Scanner scanner = new Scanner(System.in);
			String stringInput = "";
			int intInput = 0;
            // Error checking for entering file path
			do {
				if (!validInput) {
					System.out.println("\n********Invalid Selection********");
					System.out.println("\t\"" + stringInput + "\" is not a valid selection.");
					System.out.println("\nPress ENTER to continue");
					scanner.nextLine();
				}

				validInput = true;
				intInput = 0;
				System.out.println("*** Too many files found. Please select which file to analyze. ***");
				for (int num = 1; num <= args.length; num++) {
					System.out.println(String.format("\t%d) %s", num, args[num - 1]));
				}
				stringInput = scanner.nextLine();
				
				try {
					intInput = Integer.parseInt(stringInput);
				} catch (NumberFormatException nfe) {
					validInput = false;
				}
				
				if (validInput && (intInput < 1 || intInput > args.length)) {
					validInput = false;
				}
				
			} while (!validInput);

			filePath = args[intInput - 1];
		} else {

			//No command line arguments, use console input to specify a file at runtime
			System.out.println("*** No command line arguments found. Please enter file to analyze. ***");
			Scanner scanner = new Scanner(System.in);

			filePath = scanner.nextLine();

			//Exit program if "exit" is entered without specifying a file
			if (filePath.equals("")) {
				System.out.println("*** No file input provided, exiting ***");
				System.exit(-1);
			}
		}

		//Attempt to read the file and create a character stream
		String fileName = Paths.get(filePath).getFileName().toString();
		CharStream input = null;
		try {
			input = CharStreams.fromFileName(filePath);
		} catch (IOException e) {
			System.out.println("Failed to open \"" + fileName + "\".");
			e.printStackTrace();
			System.exit(-2);
		}

		//Instantiate ANTLR lexer and parser
		Java1_4Lexer lexer = new Java1_4Lexer(input);
		tokenStream = new CommonTokenStream(lexer);
		Java1_4Parser parser = new Java1_4Parser(tokenStream);

		//Create tree from base node of compilationUnit
		ParseTree tree = parser.compilationUnit();
		ParseTreeWalker walker = new ParseTreeWalker();

		//Create a method listener and walk the compilationUnit,
		//	creating a new graph for each method encountered
		methodListener = new MethodListener();
		walker.walk(methodListener, tree);

		//Create the output with the finished graphs
		String output = "";

		Edge prevEdgePrinted = null;
		for (PDGGraph graph : methodListener.allGraphs) {
			output = String.format("\nPDG %s:", graph.name);

			// Iterate through nodes in graph
			for (Node node : graph.allNodes) {
				if (node.antlrNode.getStart() != null) {
					int nodeLine = node.antlrNode.getStart().getLine();
					int nodePos = node.antlrNode.getStart().getCharPositionInLine();
					output += String.format("\n\tNode \"%s\" (L:%d, C%d):\n", node.label, nodeLine, nodePos);
				} else {
					output += String.format("\n\tNode %s:\n", node.label);
				}

				// Iterate through CDG edges in node
				String outputDep = "";
				for (Edge edge : node.cdgEdges) {
					int line = edge.from.antlrNode.getStart().getLine();
					int pos = edge.from.antlrNode.getStart().getCharPositionInLine();
                    // If edge is between the do while condition and a node is inside the do while loop, execution occurs again if comparison is true/false
                    if (edge.to == node && node.nestedIn != null && node.nestedIn.associatedListener instanceof DoWhileListener &&
							getPrevTokenText(edge.from.antlrNode).equals("while") && !edge.label.equals("NoException")) {
						outputDep += String.format("\t\tControl dependent (execution re-occurs) on [\"%s\" -> %s] (L:%d, C:%d)\n", edge.from.label, edge.label, line, pos);
					} else if (edge.to == node) {
                        // If edge is not blank, print with this format
						if (!edge.label.equals("")) {
							outputDep += String.format("\t\tControl dependent on [%s -> %s] (L:%d, C:%d)\n", edge.from.label, edge.label, line, pos);
						} else {
							outputDep += String.format("\t\tControl dependent on [%s -> NoException] (L:%d, C:%d)\n", edge.from.label, line, pos);
						}
					}


				}
				// Iterate through DDG edges in node
				for (Edge edge : node.ddgEdges) {
					if (edge.to == node) {
						int line = edge.from.varAntlrNode.get(edge.var).getStart().getLine();
						int pos = edge.from.varAntlrNode.get(edge.var).getStart().getCharPositionInLine();
						outputDep += String.format("\t\tData dependent on \"%s\" in [%s] (L:%d, C:%d)\n ", edge.label, edge.from.label, line, pos);
					}
				}
                // Print that there are no dependencies if there are none for that node
				if (outputDep.equals("")) {
					outputDep += "\t\tNo control or data dependencies\n";
				}
				output += outputDep;
			}
			output += "\n";

			//Print the output to console
			System.out.println(output);
		}
		
		Scanner scanner = new Scanner(System.in);
		Pattern pattern;
		Matcher matcher;
		String userInput;
		Node nodeToRunCIAOn = null;
		String exitCommand = "exit";

		//Change Impact Analysis Loop
		do {
			nodeToRunCIAOn = null;
			boolean backToMenu = false;
			Integer numericSelection = 1;

			System.out.println("\n\n********Change Impact Analysis Tool********");
			System.out.println("Enter text to search for the statement you wish to change, or the enter the statement's line number in the form \"line <num>\":");
			userInput = scanner.nextLine();
            // Exit if user types "exit"
			if (userInput.equals(exitCommand)) return;

			pattern = Pattern.compile("line [0-9]+", Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(userInput);

			if (matcher.find()) {
				//Search by line number
				ArrayList<Node> nodesInLine = new ArrayList<>();
				Integer lineNumberToChange = null;
				boolean lineIsValid;

				do {
					lineIsValid = true;
					String lineNumberString = userInput.split(" ")[1];

					try {
						lineNumberToChange = Integer.parseInt(lineNumberString);
						// Check that line number is within the range of line numbers in file
						int firstLineNumber = methodListener.allGraphs.get(0).allNodes.get(1).antlrNode.getStart().getLine();
						int lastLineNumber = methodListener.allGraphs.get(methodListener.allGraphs.size() - 1).allNodes.get(methodListener.allGraphs.get(methodListener.allGraphs.size() - 1).allNodes.size() - 2).antlrNode.getStart().getLine();
						if (lineNumberToChange > lastLineNumber || lineNumberToChange < firstLineNumber) {
							lineIsValid = false;
							System.out.println("\n********Invalid line number********");
							System.out.println("\tLine number \"" + userInput + "\" is not in file or there is no statement at the specified line.");
							System.out.println("\nPress ENTER to continue");
							scanner.nextLine();
							backToMenu = true;
							break;

						} else {
							// Check that line number specified has a node
							for (int pdgNumber = 0; pdgNumber < methodListener.allGraphs.size(); pdgNumber++) {
								for (int nodeIndex = 1; nodeIndex <= methodListener.allGraphs.get(pdgNumber).allNodes.size() - 2; nodeIndex++) {
									Node node = methodListener.allGraphs.get(pdgNumber).allNodes.get(nodeIndex);
									if (node.antlrNode.getStart().getLine() == lineNumberToChange) {
										nodesInLine.add(node);
									}
								}
							}
                            // Line number is invalid if there is no node on it
							if (nodesInLine.size() == 0) {
								lineIsValid = false;
								System.out.println("\n********Invalid line number********");
								System.out.println("\tThere are no statements in line " + lineNumberToChange + ".");
								System.out.println("\nPress ENTER to continue");
								scanner.nextLine();
								backToMenu = true;
								break;
							}
						}
                        // User typed in an invalid line number (ie. line 3d)
					} catch (NumberFormatException nfe) {
						lineIsValid = false;
						System.out.println("\n********Invalid input********");
						System.out.println("\t\"" + userInput + "\" is not an integer.");
						System.out.println("\nPress ENTER to continue");
						scanner.nextLine();
                        backToMenu = true;
                        break;
					}
					if (userInput.equals(exitCommand)) return;
				} while (!lineIsValid);
                // Return to main menu if user input error occurred
				if (backToMenu) continue;

				boolean validChoice;
				do {
					validChoice = true;
                    // If there are multiple nodes on user selected line, force them to choose one
					if (nodesInLine.size() > 1) {
						System.out.println("\nMultiple possible changes found on line " + lineNumberToChange + ".");
						System.out.println("Please select which you would like to change:");
                        // Print out nodes which are on user selected line
						for (int nodeIndex = 0; nodeIndex < nodesInLine.size(); nodeIndex++) {
							System.out.println("\t" + (nodeIndex + 1) + ") " + nodesInLine.get(nodeIndex).label);
						}
						try {
							userInput = scanner.nextLine();
							if (userInput.equals(exitCommand)) return;
							numericSelection = Integer.parseInt(userInput);
                            // Ensure user doesn't type a number outside of range of 1-x where x is the number of nodes on the line
							if (numericSelection > nodesInLine.size() || numericSelection < 1) {
								validChoice = false;
								System.out.println("\n********Invalid selection********");
								System.out.println("\t\"" + userInput + "\" is not a valid selection.");
								System.out.println("\nPress ENTER to continue");
								scanner.nextLine();
							}
                            // Ensure the user doesn't enter text other than a number
						} catch (NumberFormatException nfe) {
							validChoice = false;
							System.out.println("\n********Invalid selection********");
							System.out.println("\t\"" + userInput + "\" is not a valid selection.");
							System.out.println("\nPress ENTER to continue");
							scanner.nextLine();
						}
					}
				} while (validChoice == false);
				nodeToRunCIAOn = nodesInLine.get(numericSelection - 1);
			} else {
                // Return nothing if there is an empty string, rather than returning everything
				if (userInput.equals("")) continue;
				//Search by text
				ArrayList<Node> nodesWithUserText = new ArrayList<>();

				// Strip semicolon and whitespace from line
				String[] temp = userInput.split(" ");
				String textWithoutWhitespace = "";
				for (String t : temp) {
					textWithoutWhitespace += t;
				}
				String textWithoutWhitespaceAndSemicolon = textWithoutWhitespace;
				if (textWithoutWhitespaceAndSemicolon.endsWith(";")) {
					textWithoutWhitespaceAndSemicolon = textWithoutWhitespaceAndSemicolon.substring(0, textWithoutWhitespaceAndSemicolon.length() - 1);
				}
                // Get list of all nodes with the user's text without whitespace or semicolons
				for (int pdgNumber = 0; pdgNumber < methodListener.allGraphs.size(); pdgNumber++) {
					for (int nodeIndex = 1; nodeIndex <= methodListener.allGraphs.get(pdgNumber).allNodes.size() - 2; nodeIndex++) {
						Node node = methodListener.allGraphs.get(pdgNumber).allNodes.get(nodeIndex);
						if (node.label.contains(textWithoutWhitespaceAndSemicolon)) {
							nodesWithUserText.add(node);
						}
					}
				}
                // No lines were found that contain the user's text
				if (nodesWithUserText.size() == 0) {
					System.out.println("\nNo lines found containing input \"" + textWithoutWhitespaceAndSemicolon + "\".");
					System.out.println("Try using line number instead.");
					System.out.println("\nPress ENTER to continue");
					scanner.nextLine();
					continue;
				}

				String text = userInput;
				boolean validChoice;
                
				do {
					validChoice = true;
                    // If multiple nodes were found on the user's selected line, force them to choose one
					if (nodesWithUserText.size() > 1) {
						System.out.println("\nMultiple statements with text " + text + " found.");
						System.out.println("Please select which statement you would like to change:");
						for (int nodeIndex = 0; nodeIndex < nodesWithUserText.size(); nodeIndex++) {
							System.out.println("\t" + (nodeIndex + 1) + ") " + nodesWithUserText.get(nodeIndex).label + " (Line " + nodesWithUserText.get(nodeIndex).antlrNode.getStart().getLine() + ")");
						}
						text = scanner.nextLine();
						if (text.equals(exitCommand)) return;
						try {
							numericSelection = Integer.parseInt(text);
                            // Sanitize input so that it is a number within the range of 1-x where x is the number of nodes on the line
							if (numericSelection > nodesWithUserText.size() || numericSelection < 1) {
								validChoice = false;
								System.out.println("\n********Invalid selection********");
								System.out.println("\t\"" + text + "\" is not a valid selection.");
								System.out.println("\nPress ENTER to continue");
								scanner.nextLine();
							}
                            // Sanitize inputs so that it is numeric
						} catch (NumberFormatException nfe) {
							validChoice = false;
							System.out.println("\n********Invalid selection********");
							System.out.println("\t\"" + text + "\" is not a valid selection.");
							System.out.println("\nPress ENTER to continue");
							scanner.nextLine();
						}
					}
				} while (validChoice == false);
                // Run CIA tool on user specified node
				if (numericSelection != null) {
					nodeToRunCIAOn = nodesWithUserText.get(numericSelection - 1);
				}
			}

			System.out.println("\nRunning CIA on node \"" + nodeToRunCIAOn.label + "\"");

			ArrayList<Node> immediateImpacts = new ArrayList<Node>();
			ArrayList<Node> allImpacts = new ArrayList<Node>();
			analyzeImpact(nodeToRunCIAOn, immediateImpacts, allImpacts);
            // We don't use immediateImpacts since for the project, we are supposed to list everything that could be affected
			immediateImpacts.sort(Comparator.comparingInt(n -> n.antlrNode.getStart().getStartIndex()));
			allImpacts.sort(Comparator.comparingInt(n -> n.antlrNode.getStart().getStartIndex()));

			String impactOutput = "";

			System.out.println("Changing node \"" + nodeToRunCIAOn.label + "\" impacts:");
			impactOutput = "";
            // Print all impacted nodes
			for (Node impactedNode : allImpacts) {
				impactOutput += "\t" + "(L:" + impactedNode.antlrNode.getStart().getLine() + ", "
						+ "C:" + impactedNode.antlrNode.getStart().getCharPositionInLine() + ") \""
						+ impactedNode.label + "\"\n";
			}
			System.out.println(impactOutput);
			System.out.println("Change impact analysis complete. Press ENTER to continue.");
			scanner.nextLine();
            // Return to main menu so user can run the CIA tool on another node if the user did not type "exit"
		} while (!userInput.equals(exitCommand));
	}
	
	/**Returns all nodes with either an implicit or explicit dependency on the input node 
	 * @param node
	 * @param immediateImpactList
	 * @param allImpactList
	 */
	public static void analyzeImpact(Node node, ArrayList<Node> immediateImpactList, ArrayList<Node> allImpactList) {
		ArrayList<Edge> PDGEdges = new ArrayList<Edge>();
		PDGEdges.addAll(node.cdgEdges);
		PDGEdges.addAll(node.ddgEdges);
		
		allImpactList.add(node);
		for(Edge edge : PDGEdges) {
			if(edge.from == node) immediateImpactList.add(edge.to);
			ArrayList<Edge> recorded = new ArrayList<Edge>();
			analyzeImpactRecursive(node, allImpactList, recorded);
		}
	}
	
	/**Recursive call of change impact analysis. Follows outgoing edges until a leaf node is reached.
	 * @param node
	 * @param allImpactList
	 * @param recorded
	 */
	public static void analyzeImpactRecursive(Node node, ArrayList<Node> allImpactList, ArrayList<Edge> recorded) {
		ArrayList<Edge> PDGEdges = new ArrayList<Edge>();
		PDGEdges.addAll(node.cdgEdges);
		PDGEdges.addAll(node.ddgEdges);
		
		for(Edge edge : PDGEdges) {
			if(!recorded.contains(edge) && edge.from == node) {
				if(!allImpactList.contains(edge.to)) {
					allImpactList.add(edge.to);
				}
				recorded.add(edge);
				analyzeImpactRecursive(edge.to, allImpactList, recorded);
			}
		}
		
	}
	
	/**Detects if the input node is a Java syntax construct. Upon detection, creates an appropriate listener for the construct
	 * and walks the node with the listener. These listeners can trigger more calls of this function.
	 * @param node Node to do construct detection on
	 * @param parentListener the listener that called this function
	 * @return token stream index of last node within the construct if a construct is detected, otherwise 0
	 */
	public static int handleConstructNode(Node node, ConstructListener parentListener) {
		ArrayList<Node> nodesInsideConstruct = new ArrayList<Node>();
		if(node.checkedForInnerNodes) return 0; 
		if(node.antlrNode == null) return 0;
		
		ConstructListener newListener = null;
		String firstTokenText = getFirstTokenText(node.antlrNode);
		String prevTokenText = getPrevTokenText(node.antlrNode);
		
		//Detect if this node's statement is a Java syntax construct
		//If so, assign the appropriate ConstructListener
		if(firstTokenText.equals("while")) {
			newListener = new WhileListener();
			
		} else if (firstTokenText.equals("switch")){
 			newListener = new SwitchListener();
 			
 		} else if (firstTokenText.equals("do")) {
 			newListener = new DoWhileListener();
 			
 		} else if (firstTokenText.equals("if") && !prevTokenText.equals("else") ) {
 			newListener = new IfListener();
 			
 		} else if (node.antlrNode.getText().startsWith("{")
		&& node.antlrNode.getText().endsWith("}")) {
 			newListener = new AnonFuncListener();
 			
 		} else if (firstTokenText.equals("for")) {
 			newListener = new ForListener();
 			
 		} else if (firstTokenText.equals("try")) {
 			newListener = new TryCatchListener();
 			try {
 				for(int i = 0; true; i++) {
 					try {
 						((StatementContext) node.antlrNode).catches().catchClause(i).CATCH();
 					} catch(NullPointerException e) {
 						break;
 					}
 					
 					CatchClauseContext catchRule = ((StatementContext) node.antlrNode).catches().catchClause(i);
 					((TryCatchListener) newListener).catchIndices.add(catchRule.getStart().getStartIndex());
 					((TryCatchListener) newListener).catchRules.add(catchRule);
 				}
 				if(((StatementContext) node.antlrNode).finallyKeyword() != null) {
 					ParserRuleContext finallyRule = ((StatementContext) node.antlrNode).finallyKeyword();
 					((TryCatchListener) newListener).finallyIndex = finallyRule.getStart().getStartIndex();
 					((TryCatchListener) newListener).finallyRule = finallyRule;
 				}

 				
 			} finally {}
 		} else if(node.label.contains("?")) {
			newListener = new TernaryListener();
			
 		} else {
 			node.isConstruct = false;
 		}
		
		//Handle the syntax construct if detected
		if(newListener != null) {
			if(newListener instanceof WhileListener
			|| newListener instanceof DoWhileListener
			|| newListener instanceof ForListener
			|| newListener instanceof SwitchListener)
				node.isLoopConstruct = true;
			newListener.constructNode = node;
			newListener.parentListener = parentListener;   
			node.associatedListener = newListener;
			
			//Create inner nodes for this construct and make edges between them
 			nodesInsideConstruct = createNodesInConstruct(node, newListener);
 			newListener.makeEdges(nodesInsideConstruct, node, EdgeType.cfg);
		}
		node.checkedForInnerNodes = true;
		
		//Return the token stream index of last node in the construct
		int createdNodesUntilIndex = 0;
		if(!nodesInsideConstruct.isEmpty())
			createdNodesUntilIndex = node.antlrNode.getStop().getTokenIndex();
		return createdNodesUntilIndex;
	}
	
	/**Creates inner nodes for a syntax construct node
	 * @param node 
	 * @param listener The listener to use to walk the construct node
	 * @return List of nodes inside the construct
	 */
	public static ArrayList<Node> createNodesInConstruct(Node node, ConstructListener listener) {
        ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, node.antlrNode);
		return listener.nodes;
	}

    /**Listener that creates a new graph with CFG edges for each method detected in the file upon walking a parse tree.
     */
    public static class MethodListener extends Java1_4BaseListener {
        ArrayList<PDGGraph> allGraphs = new ArrayList<PDGGraph>();
        public int nodeIdentifierIndex;
        public ConstructListener currentOuterListener;
        @Override
        public void enterMethodDeclaratorRest(MethodDeclaratorRestContext methodDecl) {
            //Fetch the statements inside the method
            BlockStatementsContext blockStatements = methodDecl.methodBody().block().blockStatements();

            //Retrieve method name
            String methodName = getPrevTokenText(methodDecl);

            //Create new graph
            PDGGraph newPDGGraph = new PDGGraph(methodName);
            allGraphs.add(newPDGGraph);
            nodeIdentifierIndex = 0;

            //Walk the file with a listener that creates top-level statement nodes
            currentOuterListener = new OuterStatementListener();
            currentOuterListener.nodes.add(newPDGGraph.entryNode);
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(currentOuterListener, blockStatements); 	//This starts off a chain of recursive calls to parse inner syntax constructs and make edges!
            currentOuterListener.nodes.add(getCurrentExitNode());

            //Create CFG edges for the top-level statement nodes and graph entry/exit nodes
            makeCFGEdges(currentOuterListener.nodes, null, EdgeType.cfg);
            newPDGGraph.allNodes.add(getCurrentExitNode());

            for(Node node : currentOuterListener.nodes) {
                if(node.isConstruct) {
                    for(Edge edge : node.edgesAwaitingOutNode) {
                        if(edge.to == null) {
                            edge.to = methodListener.getCurrentExitNode();
                            methodListener.getCurrentExitNode().setCFGEdge(edge);
                        }
                    }
                }
            }

            makeCDGEdgesFromCandidates(newPDGGraph.allNodes);
            makeCDGEdgesFromKeywords(newPDGGraph, (OuterStatementListener) currentOuterListener);
            makeCDGEdgesForTryCatch(newPDGGraph.allNodes);
            
            makeDDGEdges(newPDGGraph);
        }

        /**Adds the node to the current graph
         * @param node
         */
        public void addNodeToGraph(Node node) {
            allGraphs.get(allGraphs.size()-1).allNodes.add(node);
        }

        /**Returns the exit node of the current graph
         * @return
         */
        public Node getCurrentExitNode() {
            return allGraphs.get(allGraphs.size()-1).exitNode;
        }

        public PDGGraph getCurrentGraph() {
            return allGraphs.get(allGraphs.size()-1);
        }
    }
	
	public static class Variable {
		ArrayList<String> names = new ArrayList<String>();
		String classIdentifier = "";
		
		Variable(String name, String classIdentifier){
			this.names.add(name);
			this.classIdentifier = classIdentifier; 
		}
	}
	
	/**Makes the DDG edges for the PDG
	 * @param graph
	 */
	public static void makeDDGEdges(PDGGraph graph) {

    	ParseTreeWalker walker = new ParseTreeWalker();
    	ArrayList<Node> nodes = graph.allNodes;
    	
    	//Cache the identifiers in each node's text in the node's fields
		for(int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			
			//Make an identifier listener and walk the node with it to detect and classify the identifiers
			IdentifierListener idListener = new IdentifierListener();
			idListener.node = node;
			walker.walk(idListener, node.antlrNode);
			
			//Make an assignment operation listener and walk through the node with it to detect location and type of assignment operator
			AssignmentOpListener asOpListener = new AssignmentOpListener();
			walker.walk(asOpListener, node.antlrNode);
			
			//Create read/write variable nodes for each identifier detected in the identifier listener
			for(ParserRuleContext var : idListener.idContexts) {
				handleVariable(var, node, asOpListener, idListener);
			}
		}
		
		for(Node node : graph.allNodes) {
			if(node.varReads.isEmpty()) continue;
			
			for(Variable varRead : node.varReads) {
				for(Edge edge : node.cfgEdges) {
					if(edge.to == node) 
						recursiveDDGEdgeMaking(node, edge.from, new ArrayList<>(), varRead);
				}
			}
		}
	}
	

	/**Recursive call for making DDG edges
	 * @param varReadNode
	 * @param currentNode
	 * @param edgesChecked
	 * @param varRead
	 */
	public static void recursiveDDGEdgeMaking(Node varReadNode, Node currentNode, ArrayList<Edge> edgesChecked, Variable varRead) {
		boolean stopRecursion = false;
				
		Boolean writesToVarRead = false;
		
		for(Variable varWrite : currentNode.varWrites) {
			if (varWrite.names.equals(varRead.names)) {
				writesToVarRead = true;
				break;		
			}
		}
		
		if(writesToVarRead)
		{
			stopRecursion = true;
			boolean duplicate = false;
			// Create new ddg edge
			Edge edge = new Edge(EdgeType.ddg, currentNode, varReadNode, varRead.names.get(0));
			edge.var = varRead;

			// Check if this DDG edge has already been made
			for (Edge DDGEdge : varReadNode.ddgEdges) {
				if (edge.from == DDGEdge.from && edge.to == DDGEdge.to) {
					duplicate = true;
				}
			}
			
			// Only create edge if no duplicate edge is being created
			if(!duplicate) {
				varReadNode.ddgEdges.add(edge);
				if(currentNode != varReadNode)
					currentNode.ddgEdges.add(edge);
			}
		}

		
		// If the nearest DDG "from" edge candidate has not been found
		if(!stopRecursion) {
			// Traverse each incoming edge until there are none
			for (Edge edge : currentNode.cfgEdges) {
				// Check if edge is incoming cfg edge
				if (edge.type == EdgeType.cfg && edge.to == currentNode && !edgesChecked.contains(edge)) {
					edgesChecked.add(edge);
					recursiveDDGEdgeMaking(varReadNode, edge.from, edgesChecked, varRead);
				}
			}
		}
	}
	
	
	/**Identifies and classifies the assignment operator in an expression/statement*/
	public static class AssignmentOpListener extends Java1_4BaseListener {
		int equalSignIndex = -1;
		boolean isAssignmentOp = false;
		
    	public void enterAssignmentOp(AssignmentOpContext ctx) {
    		if(ctx.getText().equals("=")) return; //handled separately in enterEqSign
    		isAssignmentOp = true;
    		equalSignIndex = ctx.getStart().getStartIndex();
    	}
    	
    	public void enterPrefixOp(PrefixOpContext ctx) {
    		isAssignmentOp = true;

    	}
    	
    	public void enterPostfixOp(PostfixOpContext ctx) {
    		isAssignmentOp = true;
    	}
    	
    	public void enterEqSign(EqSignContext ctx) {
    		equalSignIndex = ctx.getStart().getStartIndex();
    	}
	}
	
	/**Identifies identifiers in a statement/expression and stores it in a list*/
	public static class IdentifierListener extends Java1_4BaseListener {
		ArrayList<ParserRuleContext> idContexts = new ArrayList<ParserRuleContext>();
		String writeVarClassIdentifier = "";
		Node node = null;
		
		public void enterVariableDeclarator(VariableDeclaratorContext ctx) {
			writeVarClassIdentifier = ctx.identifier().getText();
			
			//Check if expression (second half after eqSign) of variable declarator is an identifier
			VariableInitializerContext init = ctx.variableDeclaratorRest().variableInitializer();
			if(init != null) {
				PrimaryContext primary = null; 
				try {
					primary = init.expression().expression1(0).expression2().expression3().primary();
				} catch (NullPointerException e) {}
				if(primary != null && primary.qualifiedIdentifier() == null)
					return;
			}
			node.isAliasFormat = true;
		}
		
		public void enterExpression(ExpressionContext ctx) {
			//Check if first syntaxrule is an identifier
			PrimaryContext primary = ctx.expression1(0).expression2().expression3().primary();
			if(primary != null && primary.qualifiedIdentifier() == null)
				return;

			//Check if the assignment op is an equal sign
			AssignmentOpContext asOp = ctx.assignmentOp();
			if(asOp != null && asOp.eqSign() == null) 
				return;
			
			//Check if third syntaxrule is an identifier
			Expression1Context secondExpression = ctx.expression1(1);
			if(secondExpression == null) 
				return;
			primary = secondExpression.expression2().expression3().primary();
			if(primary != null && primary.qualifiedIdentifier() == null)
				return;
			
			node.isAliasFormat = true;
		}
		
		public void enterConstantExpression(ConstantExpressionContext ctx) {
    		handleIdentifier(ctx);
		}
		
    	public void enterQualifiedIdentifier(QualifiedIdentifierContext ctx) {
    		handleIdentifier(ctx);
    	}
    	
    	public void enterIdentifier(IdentifierContext ctx) {
    		handleIdentifier(ctx);
    	}
    	
    	public void handleIdentifier(ParserRuleContext ctx) {
    		if(getPrevTokenText(ctx).equals(".")) return;	//in a.b.c.var, don't add "var", "c.var", etc.
    		if(getNextTokenText(ctx).equals("(")) return;	//ignore method/class calls/declarations
    		if(getNextTokenText(ctx).equals(".")) return;

    		Token nextToken = tokenStream.get(ctx.getStart().getTokenIndex() + 1);
    		if (nextToken.getType() == Java1_4Lexer.IDENTIFIER) return;	//ignore class type names
    		idContexts.add(ctx);
    	}
    	
	}
	
	/**Returns all qualified identifier access paths for a qualified identifier
	 * For example, a.b.c.var will return: 
	 * a.b.c.var, a.b.c, a.b, a
	 * @param qualifiedIdentifier
	 * @return
	 */
	public static ArrayList<String> getVariablesOfQualifiedIdentifier(String qualifiedIdentifier) {
        ArrayList<String> variables = new ArrayList<>();
        ArrayList<String> identifiers = new ArrayList<>(Arrays.asList(qualifiedIdentifier.split("\\.")));
        for (String identifier : identifiers) {
            String variable = identifiers.get(0);
            for (int index = 1; index <= identifiers.indexOf(identifier); index++) {
                variable += "." + identifiers.get(index);
            }
            variables.add(variable);
        }
        return variables;
    }
	
	

	/**Creates appropriate read/write variables for an identifier and adds them to the node
	 * @param ctx
	 * @param node
	 * @param asOpListener
	 * @param idListener
	 */
	public static void handleVariable(ParserRuleContext ctx, Node node, AssignmentOpListener asOpListener, IdentifierListener idListener) {
		PDGGraph graph = methodListener.getCurrentGraph();
		ArrayList<String> names = getVariablesOfQualifiedIdentifier(ctx.getText());
		String text = ctx.getText();

		//Create the variable if it doesn't already exist
		ArrayList<String> varsToMake = getVariablesOfQualifiedIdentifier(text);
		for(String varToMake : varsToMake) {
			Variable newVar = new Variable(varToMake, idListener.writeVarClassIdentifier);	
			methodListener.getCurrentGraph().variables.add(newVar);
		}
		
		//Use various rules to determine if the node's statement is reading or writing to this variable
		if(ctx instanceof ConstantExpressionContext) {
			//Switch construct case node - reading variable only
			int indexOfEq = node.label.indexOf("==");
			ArrayList<String> switchVar = new ArrayList<String>() {{add("" + node.label.charAt(indexOfEq-1));}};

			setNodeReads(ctx, node, switchVar);
		} else if(asOpListener.isAssignmentOp) {
			//Reading and writing to variable
			if(ctx.getStart().getStartIndex() < asOpListener.equalSignIndex || asOpListener.equalSignIndex <= 0) {
				setNodeWrites(ctx, node, idListener.writeVarClassIdentifier);
			}
			setNodeReads(ctx, node, names);
		} else if(asOpListener.equalSignIndex != -1 && ctx.getStart().getStartIndex() < asOpListener.equalSignIndex) {
			//Writing to variable only
			Variable newVar = setNodeWrites(ctx, node, idListener.writeVarClassIdentifier);
		} else {
			//Default: Reading to variable only
			setNodeReads(ctx, node, names);
		}
	}
	
	
	/**Retrieve a variable from the global list based on name
	 * @param name
	 * @return
	 */
	public static Variable getVar(String name) {
		ArrayList<Variable> variables = methodListener.getCurrentGraph().variables;
		for(Variable var : variables) {
			for(String varName : var.names) {
				if(name.equals(varName)) {
					return var;
				}
			}
		}
		return null;
	}
	
	/**Adds a variable to a node's read variables
	 * @param ctx
	 * @param node
	 * @param names
	 */
	public static void setNodeReads(ParserRuleContext ctx, Node node, ArrayList<String> names) {
		for(String name : names) {
			Variable var = getVar(name);
			if(var != null) { 
				node.varReads.add(var);
				node.varAntlrNode.put(var, ctx);
			}
		}
	}
	
	/**Adds a variable to a node's write variables
	 * @param ctx
	 * @param node
	 * @param classIdentifier
	 * @return
	 */
	public static Variable setNodeWrites(ParserRuleContext ctx, Node node, String classIdentifier) {
		Variable varToSet = getVar(ctx.getText());
		
		if(varToSet != null) { 
			node.varWrites.add(varToSet);
			node.varAntlrNode.put(varToSet, ctx);
		}
		return varToSet;
	}
	
	
	//Creates CDG edges that are not dependent on control flow altering keywords
	public static void makeCDGEdgesFromCandidates(ArrayList<Node> nodeList) {
		ArrayList<Node> candidatesNodes = collectCDGCandidateNodes(nodeList);

		for (Node node : nodeList) {
			recursiveCDGEdgeMaking(node, node, new ArrayList<>(), candidatesNodes);
		}
	}
	
	
	/**Collects all nodes with multiple outgoing CFG edges
	 * @param nodeList
	 * @return
	 */
	public static ArrayList<Node> collectCDGCandidateNodes(ArrayList<Node> nodeList) {
		ArrayList<Node> candidatesNodes = new ArrayList<>();
		// Find all nodes which are "from" node candidates for cdg depencencies
		for(Node node : nodeList) {
			int numOutgoingEdges = 0;
			for (Edge edge : node.cfgEdges) {
				// If node is the "from" node of the edge, edge is an outgoing edge
				if (edge.from == node && edge.type == EdgeType.cfg) {
					numOutgoingEdges++;
				}
			}
			// If node has multiple outgoing edges, it is a "from" node candidates for cdg depencencies
			if (numOutgoingEdges > 1) {
				candidatesNodes.add(node);
			}
		}
		return candidatesNodes;
	}

	public static void recursiveCDGEdgeMaking(Node startingNode, Node currentNode, ArrayList<Edge> edgesChecked, ArrayList<Node> candidatesNodes) {
		Node node = startingNode;
		boolean stopRecursion = false;

		while (node.nestedIn != null) {
			// Check that the starting node is within the scope of the current node
			Node nestedInNode = node.nestedIn;
			if (nestedInNode == currentNode.nestedIn) {
				// Check if node is a "from" node CDG candidate
				if (candidatesNodes.contains(currentNode) && currentNode != startingNode) {
					stopRecursion = true;
					// Create new cdg edge
					Edge edge = new Edge(EdgeType.cdg, currentNode, startingNode, edgesChecked.get(edgesChecked.size() - 1).label);
					startingNode.setCDGEdges(edge);
					currentNode.setCDGEdges(edge);
				}
			}
			node = nestedInNode;
		}

        // Walk out of starting node until a try catch construct is found
        Node startingNodeFirstTryConstruct = startingNode.nestedIn;
        while(startingNodeFirstTryConstruct != null && !(startingNodeFirstTryConstruct.associatedListener instanceof TryCatchListener))
            startingNodeFirstTryConstruct = startingNodeFirstTryConstruct.nestedIn;

        // Walk out of currentNode until a try catch construct is found
        Node currentNodeFirstTryConstruct = currentNode.nestedIn;
        while(currentNodeFirstTryConstruct != null && !(currentNodeFirstTryConstruct.associatedListener instanceof TryCatchListener))
            currentNodeFirstTryConstruct = currentNodeFirstTryConstruct.nestedIn;
        // If starting node is in same try catch as currentNode
        if(startingNodeFirstTryConstruct != null && currentNodeFirstTryConstruct != null 
		&& startingNodeFirstTryConstruct == currentNodeFirstTryConstruct) {
            TryCatchListener listener = (TryCatchListener) startingNodeFirstTryConstruct.associatedListener;
            if(currentNode.antlrNode.getStart().getStartIndex() < listener.catchIndices.get(0) 
    		&& startingNode.antlrNode.getStart().getStartIndex() > listener.catchIndices.get(0) 
    		&& startingNode.antlrNode.getStart().getStartIndex() < listener.finallyIndex) {
                Edge edge = new Edge(EdgeType.cdg, currentNode, startingNode, "ThrewException");
                startingNode.setCDGEdges(edge);
                currentNode.setCDGEdges(edge);
            }
        }

		// If the nearest CDG "from" edge candidate has not been found
		if(!stopRecursion) {
			// Traverse each incoming edge until there are none
			for (Edge edge : currentNode.cfgEdges) {
				// Check if edge is incoming cfg edge
				if (edge.type == EdgeType.cfg && edge.to == currentNode && !edgesChecked.contains(edge)) {
					edgesChecked.add(edge);
					recursiveCDGEdgeMaking(startingNode, edge.from, edgesChecked, candidatesNodes);
				}
			}
		}
	}
	
	enum LoopListeners {
	    WhileListener(WhileListener.class),
	    DoWhileListener(DoWhileListener.class),
	    ForListener(ForListener.class);

		public final Class<?> classType;
		LoopListeners(Class<?> classType) {
			this.classType = classType;
		}
	}
	
	enum LoopListenersForKeywords {
	    WhileListener(WhileListener.class),
	    DoWhileListener(DoWhileListener.class),
	    ForListener(ForListener.class),
		SwitchListener(SwitchListener.class);

		public final Class<?> classType;
		LoopListenersForKeywords(Class<?> classType) {
			this.classType = classType;
		}
	}
	
	enum ConditionListeners {
		TernaryListener(TernaryListener.class),
		IfListener(IfListener.class),
		SwitchListener(SwitchListener.class),
		WhileListener(WhileListener.class),
		ForListener(ForListener.class);
		
		public final Class<?> classType;
		ConditionListeners(Class<?> classType) {
			this.classType = classType;
		}
	}


	/**Detects all continue/break/throw/return keywords and makes cdg edges from the condition that decides 
	 * execution of the keyword to nodes below with no other control dependencies
	 * @param graph
	 * @param outerListener
	 */
	public static void makeCDGEdgesFromKeywords(PDGGraph graph, OuterStatementListener outerListener) {
	    for(Node node : graph.allNodes) {
	        if (node.antlrNode.getStart() == null) continue;
	        String nodeText = getFirstTokenText(node.antlrNode);
	        boolean isBreakContinue = nodeText.equals("continue") || nodeText.equals("break");
	        boolean isReturnThrow = nodeText.equals("return") || nodeText.equals("throw");
	        if (!isBreakContinue && !isReturnThrow) continue;
	    	
	        Node loop = null;
	        Node mainCondition = null;
	        
	        Node currentNode = node;
	        while(currentNode.nestedIn != null) {
	            ConstructListener parentListener = currentNode.nestedIn.associatedListener;
	            
	            //Get the condition that decides if the keyword executes
            	if(mainCondition == null)
	                for(ConditionListeners listenerType : ConditionListeners.values()) 
	                    if(listenerType.classType.isInstance(parentListener))
	                    		mainCondition = currentNode.nestedIn.constructEntryNode;

            	//Find the binding loop for break/continue
	            if(isBreakContinue) {
	                for(LoopListenersForKeywords listenerType : LoopListenersForKeywords.values()) 
	                    if(listenerType.classType.isInstance(parentListener)) 
	                        loop = currentNode.nestedIn;
	            }

	            if(mainCondition != null && (isBreakContinue ? loop != null : true)) break;
	            currentNode = currentNode.nestedIn;
	        }

	        //Get the nodes that will be dependent on the keyword's condition
	        ArrayList<Node> dependents = isBreakContinue ? findCDGKeywordDependents(loop.nestedNodes, node)
        												: findCDGKeywordDependents(outerListener.nodes, node);
	        
	        //Check if node is on opposite branch of the keyword's condition
	        for(Node nodeTo : dependents) {
	            if(nodeTo.antlrNode.getStop() == null) continue;
	            if(nodeTo.antlrNode.getStop().getStopIndex() > node.antlrNode.getStop().getStopIndex()) {
	            	
	                ArrayList<Edge> fromAllConditions = new ArrayList<Edge>();
	                collectCDGConditions(node, fromAllConditions); 

	                ArrayList<Edge> toAllConditions = new ArrayList<Edge>();
	                collectCDGConditions(nodeTo, toAllConditions); 

	                Boolean oppositeOfKeywordCondition = false;
	                for(Edge edgeTo : toAllConditions) {
	                	for(Edge edgeFrom : fromAllConditions) {
		                	if(edgeTo.from == edgeFrom.from
		                	&& edgeTo.label != edgeFrom.label) {
		                		oppositeOfKeywordCondition = true;
		                	}
	                	}
	                }
	                
	                //If node isn't on opposite branch of keyword's condition, create the edge
	                if(!oppositeOfKeywordCondition) {
		                Edge newEdge = new Edge(EdgeType.cdg, mainCondition, nodeTo, "");
		                node.cdgEdges.add(newEdge);
		                nodeTo.cdgEdges.add(newEdge);
	                }
	            }
	        }
	    }
	}

	// Make cdg edges between nodes inside try to nodes below the try, since execution is dependent on nodes inside try not throwing an exception
	public static void makeCDGEdgesForTryCatch(ArrayList<Node> listOfNodes) {
		Node lastNodeInFinally = null;
		ConstructListener outerListener = null;
        List<Node> tryBlockNodes = new ArrayList<>();

        // Iterate through nodes until a node is found in the try catch
		for(int nodeIndex = 1; nodeIndex < listOfNodes.size() - 1; nodeIndex++){
			Node node = listOfNodes.get(nodeIndex);
			outerListener = node.nestedIn != null ? node.nestedIn.associatedListener : null;
			// Check if node is in a try catch / try catch finally / try finally block
			while(outerListener != null && !(outerListener instanceof TryCatchListener))
			{
				outerListener = outerListener.parentListener;
			}
			// Node is in try catch
			if(outerListener != null){
				for (int i = listOfNodes.indexOf(node); i < listOfNodes.size() - 1; i++) {
                    Node nodeInsideTry = listOfNodes.get(i);
                    if (nodeInsideTry.antlrNode.getStart().getStartIndex() < ((TryCatchListener) outerListener).catchIndices.get(0)) {
                        tryBlockNodes.add(nodeInsideTry);
                    }
					ConstructListener listener = listOfNodes.get(i).nestedIn == null ? null : listOfNodes.get(i).nestedIn.associatedListener;
					// Get last node of finally
					// Check if node is in a try catch / try catch finally / try finally block
					while(listener != null && listener != outerListener)
					{
						listener = listener.parentListener;
					}
					if(listener == null){
						lastNodeInFinally = listOfNodes.get(i - 1);
						break;
					}
				}
			}

            if(lastNodeInFinally != null) {
                ArrayList<Node> dependents = findCDGKeywordDependents(methodListener.currentOuterListener.nodes, lastNodeInFinally);

                for (Node nodeTo : dependents) {
                    if (nodeTo.antlrNode.getStop() == null) continue;
                    if (nodeTo.antlrNode.getStop().getStopIndex() >
                            lastNodeInFinally.antlrNode.getStop().getStopIndex()) {
                        for(Node nodeFrom : tryBlockNodes){
                            Edge edge = new Edge(EdgeType.cdg, nodeFrom, nodeTo, "NoException");
                            boolean duplicate = false;

                            // Check if there is a CDG edge that is from the same node and goes to the same node
                            for (Edge CDGEdge : nodeFrom.cdgEdges) {
                                if (edge.from == CDGEdge.from && edge.to == CDGEdge.to && (edge.label.equals(CDGEdge.label) || CDGEdge.label.equals(""))) {
                                    duplicate = true;
                                }
                            }
                            // Only create edge if no duplicate edge is being created
                            if(!duplicate) {
                                nodeFrom.cdgEdges.add(edge);
                                nodeTo.cdgEdges.add(edge);
                            }
                        }
                    }
                }
            }
		}
	}


	public static void collectCDGConditions(Node node, ArrayList<Edge> edgesChecked) {
        for (Edge edge : node.cdgEdges) {
            if(edge.to == node 
    		&& edge.type == EdgeType.cdg 
    		&& !edgesChecked.contains(edge)) {
                edgesChecked.add(edge);
                collectCDGConditions(edge.from, edgesChecked);
            }
        }
	}
	
	public static ArrayList<Node> findCDGKeywordDependents(ArrayList<Node> nodes, Node keyword) {
	    ArrayList<Node> dependencies = new ArrayList<>();

	    for (Node nodeTo : nodes) {
	        boolean enterNested = false;

	        if (nodeTo.isConstruct) {
	            if (nodeTo.associatedListener instanceof AnonFuncListener 
	            || nodeTo.associatedListener instanceof DoWhileListener)
	                enterNested = true;
	            else if (nodeTo.antlrNode.getStart().getStartIndex() < keyword.antlrNode.getStart().getStartIndex())
	                enterNested = true;
	        }
	       

	        if (enterNested)
	            dependencies.addAll(findCDGKeywordDependents(nodeTo.nestedNodes, keyword));
	        else if (nodeTo.isConstruct)
	            dependencies.add(nodeTo.constructEntryNode);
	        else
	            dependencies.add(nodeTo);
	    }

	    return dependencies;
	}
	
    /** Main syntax construct statement listener extended by the various Java construct listeners.
     *  Listens for and creates nodes for statements in the method.
     */
    public static class ConstructListener extends Java1_4BaseListener {
    	ArrayList<Node> nodes = new ArrayList<Node>();
    	ConstructListener parentListener;
    	Node constructNode;

    	ParserRuleContext originalStatement;
    	int createdNodesUntilIndex = 0;
    	int nodesCreated = 0;
    	
    	String customText = null;
    	String nodeLabel = "";

    	@Override
    	public void enterParExpression(ParExpressionContext ctx) {
    		handleStatement(ctx);}
    	
    	@Override
    	public void enterStatement(StatementContext ctx) {
    		//Filter out partial if statements
    		if(getFirstTokenText(ctx).equals("if") && getPrevTokenText(ctx).equals("else")) return;		
    		handleStatement(ctx);};
    	
    	@Override
    	public void enterLocalVariableDeclarationStatement(LocalVariableDeclarationStatementContext ctx) {
    		handleStatement(ctx);}
    	   
    	/**Cleans and creates a node for the statement
    	 * @param statement ANTLR parse tree node to create a node for.
    	 */
    	public void handleStatement(ParserRuleContext statement) {
    		if(statement.getStop().getTokenIndex() < createdNodesUntilIndex) return;
    		
    		//Stop the syntax construct statement itself from being detected as part of the syntax construct
    		if(originalStatement == null) {
    			originalStatement = statement;
    			return;
    		}
    		
    		//Reformat the label of throw statements
    		if(getFirstTokenText(statement).equals("throw")) {
    			String text = statement.getText();
    			customText = "throw(" + text.substring(5, text.length()-1) + ")";
    		}
    		
    		//Only use the statement's getText() as the text for this node if a child listener hasn't written to customText
    		if(customText == null) {
    			nodeLabel = statement.getText();
    		} else {
    			nodeLabel = customText;
    			customText = null;
    		}
    		nodeLabel = nodeLabel.endsWith(";") ? nodeLabel.substring(0, nodeLabel.length()-1) : nodeLabel;
    		
    		//Filter out various unnecessary nodes
    		if(nodeLabel.equals(new String("")) || nodeLabel.equals(new String(";"))) return;
    		String text = statement.getText();
    		if(text.startsWith("{") && text.endsWith("}") && statement.getParent() instanceof StatementContext) return;
    		
    		//Create node and add it to list of nodes
    		createAndAddNode(nodeLabel, statement, true, false);
    	}
    	
    	/**Creates a node for the given statement
    	 * @param label Label to give to the node
    	 * @param statement 
    	 * @param checkForConstructs Whether to check if this statement is a syntax construct 
    	 * @return
    	 */
    	public Node createAndAddNode(String label, ParserRuleContext statement, boolean checkForConstructs, boolean isTempNode) {
    		//Create the node
    		Node newNode = new Node(label, statement, checkForConstructs, isTempNode);
    		nodes.add(newNode);
    		
    		//Set hierarchical data for newly created node
    		newNode.nestedIn = constructNode;
    		
    		if(constructNode != null)
    			constructNode.nestedNodes.add(newNode);
    		
    		if(nodesCreated == 0 && constructNode != null) 
    			constructNode.constructEntryNode = newNode;
    		
    		//Increment listener-wide counter for node creation
    		nodesCreated++;
    		
    		//Check if the newly created node is a Java syntax construct and handle it if so.
    		if(checkForConstructs) {
    			createdNodesUntilIndex = handleConstructNode(newNode, this);
    		}
			else
				newNode.isConstruct = false;
    		
    		if(!newNode.isConstruct && !isTempNode)
    			methodListener.nodeIdentifierIndex++;
    		newNode.identifier = "node" + methodListener.nodeIdentifierIndex;
    		
    		//Add the node to the graph
			if(!newNode.isConstruct && !newNode.isTempNode)
				methodListener.addNodeToGraph(newNode);	
 
    		return newNode;
    	}
    	
    	/**Calls the appropriate function to make edges for the nodes created.
    	 * Child listeners for specific Java syntax constructs will override this to 
    	 * specify a edge making function appropriate for that syntax construct.
    	 * @param nodesInsideConstruct Nodes to make edges for
    	 * @param node The syntax construct node
    	 * @param edgeType 
    	 */
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		makeCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    	
    	/**Adds all edges that are waiting for a target node to the parent construct node to handle
    	 * @param node
    	 */
    	public void throwEdgesAwaitingTargetNode(Node node) {
    		if(parentListener != null)
    			for(Edge edge : node.edgesAwaitingOutNode)
    				if(parentListener.constructNode != null && edge.to == null)
        				parentListener.constructNode.edgesAwaitingOutNode.add(edge);
    	}
    }
    
    /**ConstructListener used for top-level statements in a method.*/
    public static class OuterStatementListener extends ConstructListener {
    	Node previousNode = null;

    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		String text = statement.getText();
    		
    		//Filter and clean text
    		text = text.endsWith(";") ? text.substring(0, text.length()-1) : text;
    		if(text.equals(new String("")) || text.equals(new String(";"))) return;
    		
    		//Ignore statements inside blocks
    		if(isInsideBlock(statement)) return;
    		
    		super.createAndAddNode(text, statement, true, false);
    	}
    	
    	/**Returns whether or not statement is inside a block*/
    	private boolean isInsideBlock(ParserRuleContext ctx) {
    	    ParserRuleContext parent = ctx.getParent();
    	    //Traverse up parse tree to determine if the node is inside a block
    	    while (parent != null) {
    	        if (parent instanceof StatementContext) {
    	            StatementContext st = (StatementContext) parent;
    	            if (st.block() != null) {
    	                return true;
    	            }
    	        }
    	        parent = parent.getParent();
    	    }
    	    return false;
    	}
    }
    
    /**ConstructListener used for anonymous functions.*/
    public static class AnonFuncListener extends ConstructListener{
    	//Same implementation as super class
    }
    
    /**ConstructListener used for switch statements.*/
    public static class SwitchListener extends ConstructListener{
    	String switchCondition;
    	
    	//Constant expressions are the expressions after "case", detect to further parse
    	public void enterConstantExpression(ConstantExpressionContext ctx) {
    		handleStatement(ctx);
    	}
    	
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		String text = statement.getText();
    		//Detect and store the switch variable
    		if(createdNodesUntilIndex == 0 && text.startsWith("(") && text.endsWith(")")) {
    			switchCondition = text.substring(1, text.length() - 1);
    			return;
    		}

    		//Create string describing the case, constant expression, and switch variable
    		if(statement.getClass() == ConstantExpressionContext.class) 
    			customText = "case_" + switchCondition + "==" + statement.getText();
    		
    		//Create a temporary "default" node for edge making purposes
    		if(getPrevTwoTokenText(statement).equals("default")) 
    			createAndAddNode("default", statement, false, true);

			super.handleStatement(statement);
    	}
    	
    	/**Runs the appropriate function for making edges in switch constructs*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addSwitchCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**ConstructListener used for if statements*/
    public static class IfListener extends ConstructListener{
    	int elseIndex = -1;
    	boolean elseNodeCreated = false;
    	ParserRuleContext primaryStatement = null;

    	public void enterElseKeyword(ElseKeywordContext ctx) {
    		//if the else is at the scope of this if statement (not a nested one) store it's index
		    ParserRuleContext currentCtx = (ParserRuleContext) ctx.parent;
			while(currentCtx instanceof StatementContext) {
								
				if(currentCtx.getText().equals(primaryStatement.getText())) {
	        		if(!getNextTokenText(ctx).equals("if")) {
	        			elseIndex = ctx.ELSE().getSymbol().getStartIndex();
	        			break;
	        		}
	    		}
				currentCtx = (ParserRuleContext) currentCtx.parent;
    		}

    	}
    	
    	@Override
    	public void enterParExpression(ParExpressionContext statement) {
    		//Store the if statement to do else keyword detection at the correct scope
    		if(primaryStatement == null) 
    			primaryStatement = (ParserRuleContext) statement.parent;
    		
    		originalStatement = statement;
    		handleStatement(statement);
    	}
    	
    	@Override
    	public void enterStatement(StatementContext statement) {
    		if(!(getFirstTokenText(statement).equals("if") && getPrevTokenText(statement).equals("else"))) {
    			handleStatement(statement);
    		}
    	}
    	
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
			String toPrepend = "";
			//Determine whether to prepend "if" or "elseif" to node text
    		if(statement.getClass() == ParExpressionContext.class) {
    			if(getPrevTokenText(statement).equals("if")) {
    				toPrepend = "if";
    				if(getPrevTwoTokenText(statement).equals("else")) {
    					toPrepend = "elseif";
    				}
    			}
    		}
    		
			customText = toPrepend + statement.getText();
			
    		//Make temporary node for "else" keyword to separate statements when making edges
    		if(elseIndex > 0 
			&& statement.getStart().getStartIndex() > elseIndex 
			&& !elseNodeCreated
			&& !getNextTokenText(statement).equals("if")) {
    			createAndAddNode("else", statement, false, true);
    			elseNodeCreated = true;
    		}
			
    		super.handleStatement(statement);
    	}
    	
    	/**Runs the appropriate function for making edges in if constructs*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addIfCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**ConstructListener used for Do-While statements*/
    public static class DoWhileListener extends ConstructListener{
    	
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		//No need to create a "do" node, because first statement in a do while construct can be inferred to be after the "do" keyword
    		if(getPrevTokenText(statement).equals("while")) 
    			customText = "while" + statement.getText();
    		super.handleStatement(statement);
    	}
    	
    	/**Runs the appropriate function for making edges in a do-while statement*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addDoWhileCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**ConstructListener used for while statements*/
    public static class WhileListener extends ConstructListener{ 
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		//If this is the first expression in the construct, add "while" to the expression for this node
    		if(statement.getClass() == ParExpressionContext.class && nodesCreated == 0) 
    			customText = "while" + statement.getText();
    		super.handleStatement(statement);
    	}
    	
    	/**Runs the appropriate function for creating edges in a while construct*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addWhileCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**ConstructListener used for ternary statements*/
    public static class TernaryListener extends ConstructListener{ 
    	/**Converts ternary statement to the if/else statement format and creates nodes accordingly*/
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		if(nodesCreated == 0) {
    			super.createAndAddNode(statement.getText(), statement, false, false);
    		}
    	}
    	
    	/**Runs the appropriate function for creating edges in a ternary expression (converted to if/else format)*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addTernaryCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**ConstructListener used for 'for' statements. Foreach does not exist in java1.4*/
    public static class ForListener extends ConstructListener{
        String forUpdateText = "";
        ParserRuleContext forStatement;
		boolean forHandled = false;
    	@Override
    	public void handleStatement(ParserRuleContext statement) {


            String forInitText = "";
            String forConditionText = "";
            
            if(!forHandled && getFirstTokenText(statement).equals("for")) {

                //Separate the statement into the forInit, forCondition, forUpdate sections.
                String[] parts = statement.getText().split(";", -1); 

                //Clean the output of each section
                if(parts.length > 0) forInitText = parts[0].trim().substring(4) ;
                if(parts.length > 1) forConditionText = parts[1].trim();
                if(parts.length > 2) {
                	String [] forUpdateParts = parts[2].trim().split("\\{", -1) ;
                	forUpdateText = forUpdateParts[0].substring(0, forUpdateParts[0].length()-1);
                }
                this.forStatement = statement;
                
                //Convert empty conditions to "true" for clarity
                if(forConditionText.equals("")) forConditionText = "true";
                
                //Create nodes for each section of the for statement. 
                if(!forInitText.equals(""))
                	super.createAndAddNode("forInit(" + forInitText + ")", ((StatementContext) statement).forInit(), true, false);
                super.createAndAddNode("forCondition(" + forConditionText + ")", ((StatementContext) statement).expression(), true, false);
                //forUpdate will be created later, since it must come after the body.
                
                forHandled = true;
            } else {
            	super.handleStatement(statement);
            }


    	}
    	
    	/**Creates the forUpdate node, before running the appropriate function for making edges in for statements.*/
    	@Override
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		super.createAndAddNode("forUpdate(" + forUpdateText + ")", ((StatementContext) forStatement).forUpdate(), true, false);
    		addForCFGEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    /**ConstructListener used for try/catch statements.*/
    public static class TryCatchListener extends ConstructListener {
    	ArrayList<ParserRuleContext> catchRules = new ArrayList<ParserRuleContext>();
    	ArrayList<Integer> catchIndices = new ArrayList<Integer>();
    	int finallyIndex = Integer.MAX_VALUE;
    	ParserRuleContext finallyRule = null;
    	int nextCatchIndex = 0;
    	boolean finishedKeywords;
    	
    	@Override
    	public void handleStatement(ParserRuleContext statement) {
    		int nextKeywordIndex = nextCatchIndex < catchIndices.size() 
    								? catchIndices.get(nextCatchIndex) 
									: finallyIndex;
    		
    		if(!finishedKeywords && statement.getStart().getStartIndex() > nextKeywordIndex) {

    			ParserRuleContext rule = null;
    			String label = "";
    			
    			if(nextKeywordIndex == finallyIndex) {
    				finishedKeywords = true;
    				rule = finallyRule;
    				label = "finally";
    			} else {
        			rule = catchRules.get(nextCatchIndex);
        			label = "catch"; 
    			}

    			Node keywordNode = new Node(label, rule, false, true);
    			nodes.add(keywordNode);
    			constructNode.nestedNodes.add(keywordNode);
    			nextCatchIndex++;
    		}
    		
    		super.handleStatement(statement);
    	}
    	
    	/**Runs the appropriate function for creating edges in a try/catch statement*/
    	public void makeEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
    		addTryCatchEdges(nodesInsideConstruct, node, edgeType);
 			throwEdgesAwaitingTargetNode(node);
    	}
    }
    
    /**Helper function to get the text of the first token in a statement.*/
    static String getFirstTokenText(ParserRuleContext statement) {
		int firstTokenIndex = statement.getStart().getTokenIndex();
		String firstTokenText = tokenStream.getTokens(firstTokenIndex, firstTokenIndex).get(0).getText();
    	return firstTokenText;
    }
    
    /**Helper function to get the text of the previous token in a statement.*/
    static String getPrevTokenText(ParserRuleContext statement) {
		int firstTokenIndex = statement.getStart().getTokenIndex();
		String prevTokenText = tokenStream.getTokens(firstTokenIndex-1, firstTokenIndex-1).get(0).getText();
    	return prevTokenText;
    }
    
    /**Helper function to get the text of the token two tokens before the statement.*/
    static String getPrevTwoTokenText(ParserRuleContext statement) {
		int firstTokenIndex = statement.getStart().getTokenIndex();
		String prevTwoTokenText = tokenStream.getTokens(firstTokenIndex-2, firstTokenIndex-2).get(0).getText();
    	return prevTwoTokenText;
    }
    
    /**Helper function to get the text of the next token after a statement.*/
    static String getNextTokenText(ParserRuleContext statement) {
		int firstTokenIndex = statement.getStop().getTokenIndex();
		String nextTokenText = tokenStream.getTokens(firstTokenIndex+1, firstTokenIndex+1).get(0).getText();
    	return nextTokenText;
    }
    
    
    /**PDGGraph structure. Edges of various types may be made between the edges of the nodes inside.*/
    public static class PDGGraph {
    	public String name = "";
    	public ArrayList<Node> allNodes = new ArrayList<Node>();
    	public Node entryNode = new Node("entryNode", "entry", new ParserRuleContext(), false);
    	public Node exitNode = new Node("exitNode", "exit", new ParserRuleContext(), false);
    	public ArrayList<Variable> variables = new ArrayList<Variable>();
    	
    	PDGGraph(String name){
    		this.name = name;
    		allNodes.add(entryNode);
    	}
    }
    
    /**Node structure. Not to be confused with ANTLR's ParserRuleContext nodes.*/
    public static class Node {
    	public String label;
    	public String identifier;
    	public ParserRuleContext antlrNode;
    	public ArrayList<Edge> cfgEdges = new ArrayList<Edge>();
    	public ArrayList<Edge> cdgEdges = new ArrayList<Edge>();
    	public ArrayList<Edge> ddgEdges = new ArrayList<Edge>();
    	public Node nestedIn;
    	
    	public boolean checkedForInnerNodes = false;
    	public ArrayList<Edge> edgesAwaitingOutNode = new ArrayList<Edge>();
    	public ConstructListener associatedListener;
    	public boolean isConstruct = true;
    	public boolean isTempNode = false;
    	public Node constructEntryNode;
    	public boolean isLoopConstruct;
    	
    	public ArrayList<Variable> varWrites = new ArrayList<Variable>();
    	public ArrayList<Variable> varReads = new ArrayList<Variable>();
		public Boolean isAliasFormat = true;
		public HashMap<Variable, Boolean> isDeclarator = new HashMap<Variable, Boolean>();
		public HashMap<Variable, ParserRuleContext> varAntlrNode = new HashMap<Variable, ParserRuleContext>();
	
    	
    	public ArrayList<Node> nestedNodes = new ArrayList<Node>();
    	
    	Node(String label, ParserRuleContext antlrNode, boolean isConstruct, boolean isTempNode) {
    		this.label = label;
    		this.antlrNode = antlrNode;
    		this.isConstruct = isConstruct;
    		this.isTempNode = isTempNode;
    	}
    	
    	Node(String identifier, String label, ParserRuleContext antlrNode, boolean isConstruct) {
    		this.identifier = identifier;
    		this.label = label;
    		this.antlrNode = antlrNode;
    		this.isConstruct = isConstruct;
    	}

		public void setCDGEdges(Edge edge) {

			boolean duplicate = false;
			// Check if there is a CDG edge that is from the same node and goes to the same node
			for (Edge CDGEdge : edge.from.cdgEdges) {
				if (edge.from == CDGEdge.from && edge.to == CDGEdge.to) {
					duplicate = true;
				}
			}
			// Only create edge if no duplicate edge is being created
			if (!duplicate) {
				cdgEdges.add(edge);

			}
		}

    	/**Adjusts and makes edge appropriate for a CFG.*/
    	public void setCFGEdge(Edge edge) {
	
    		//Enforce behavior on return, throw, break keywords
    		boolean isThrow = false;
    		boolean isReturn = false;
    		if(edge.from.antlrNode.getStart() != null) {
    			isThrow = getFirstTokenText(edge.from.antlrNode).equals("throw");
    			isReturn = getFirstTokenText(edge.from.antlrNode).equals("return");
    		}

			if((isReturn || isThrow) && !edge.handled) {
				//Set target nodes for return and throw edges to the exit node
				edge.to = methodListener.getCurrentExitNode();
				edge.handled = true;
				methodListener.getCurrentExitNode().setCFGEdge(edge);
				this.setCFGEdge(edge);
				return;
			} else if (edge.from.label.equals("break") && !edge.handled) {
				//Give break nodes to the first loop-type syntax construct parent node as a node awaiting a target
				edge.to = null;
				Node currentNode = edge.from;
				
				//Traverse up syntax construct node hierarchy until a loop-type syntax construct is found
				while(currentNode != null) {
					if(currentNode.isLoopConstruct) {
						//Give the edge to the loop-type syntax construct
						edge.from.nestedIn.edgesAwaitingOutNode.remove(edge);
						currentNode.edgesAwaitingOutNode.add(edge);
						break;
					}
					currentNode = currentNode.nestedIn;
				}
				//Mark it so it doesn't get thrown further in successive calls
				edge.handled = true;
			} else if (edge.from.label.equals("continue")){
				//Adjust the target node of continue edges to the entry node of the first loop-type syntax construct
				Node currentNode = edge.from;
				
				//Traverse up the syntax construct hierarchy until a loop-type syntax construct is found
				do {
				    if (currentNode.isLoopConstruct)
				        break;
				    currentNode = currentNode.nestedIn;
				} while (currentNode != null);
				
				//Assign target node to entry node of loop-type syntax construct
				if(currentNode == null)
					edge.to = new Node("Invalid continue", new ParserRuleContext(edge.from.antlrNode, 0), false, false);
				else {
					ArrayList<Node> nodesInLoop = currentNode.associatedListener.nodes;

					//Handle continue behavior appropriately based on the loop it is associated with
					if(currentNode.associatedListener instanceof WhileListener)
						edge.to = currentNode.constructEntryNode;
					else if(currentNode.associatedListener instanceof DoWhileListener)
						edge.to = nodesInLoop.get(nodesInLoop.size()-1);
					else if(currentNode.associatedListener instanceof ForListener) 
						for(Node node : nodesInLoop) {
							if(node.label.startsWith("forCondition")) {
								edge.to = node;
								break;
							}
						}
				}
					
			}

			//If the target of the edge is a construct, change the target to the first non-construct in the children (depth first)
    		if(edge.to != null && edge.to.isConstruct) {
    			Node currentNode = edge.to.constructEntryNode;
    			while(currentNode.isConstruct)		
        			currentNode = currentNode.constructEntryNode;

    			edge.to = currentNode;
    		}
    		
    		//If this is a construct making an edge to a node, propagate target node to all nodes in this construct awaiting a target node
    		if(edge.to != null && edge.from.equals(this) && isConstruct) {
    			for(Edge edgeAwaitingOutNode : edgesAwaitingOutNode) {
    				if(edgeAwaitingOutNode.to == null) {
        				edgeAwaitingOutNode.to = edge.to;
        				Edge handledOutEdge = new Edge(EdgeType.cfg, edgeAwaitingOutNode.from, edge.to, edge.label);
        				handledOutEdge.handled = true;
        				edge.to.setCFGEdge(handledOutEdge);
        				if(!handledOutEdge.from.cfgEdges.contains(edgeAwaitingOutNode)) {
        					handledOutEdge.from.setCFGEdge(handledOutEdge);
        				}
    				}
    			}
    		}
    		
    		//If edge is being given to a construct, give it to the construct's entry node as well
    		if(edge.to != null && edge.to.equals(this.constructEntryNode) && isConstruct) {
    			edge.to.setCFGEdge(edge);
    		}
    		
    		//If edge to node was changed, give this edge to the new to node as well
    		if(edge.to != null && edge.to != edge.originalTo && this == edge.originalTo) {
    			edge.to.setCFGEdge(edge);
    		}
    		
    		//Add the (adjusted) edge to this node 
    		if(!edge.from.isConstruct 
			&& edge.to != edge.from 
			&& !(this != edge.from && edge.to == null) 
			&& !cfgEdges.contains(edge)
			&& !edge.from.equals(methodListener.getCurrentExitNode())) {
    			cfgEdges.add(edge);
    		}
    	}
    }
    
    /**Edge structure. Has a source and target node, and an edge type (see enum EdgeType)*/
    public static class Edge {
    	EdgeType type; 
    	Node originalFrom;
    	Node originalTo;
    	Node from;
    	Node to;
    	String label;
    	Variable var;
    	boolean handled = false;
    	
    	Edge(EdgeType type, Node from, Node to, String label){
    		this.originalFrom = from;
    		this.originalTo = to;
    		this.type = type;
    		this.from = from;
    		this.to = to;
    		this.label = label;
    	}
    	
    	public Edge(Edge edgeToDuplicate) {
        	this.type = edgeToDuplicate.type;
        	this.originalFrom = edgeToDuplicate.originalFrom;
        	this.originalTo = edgeToDuplicate.originalTo;
        	this.from = edgeToDuplicate.from;
        	this.to = edgeToDuplicate.to;
        	this.label = edgeToDuplicate.label;
        	this.var = edgeToDuplicate.var;
        	this.handled = edgeToDuplicate.handled;
		}

		/**Overridden toString method for debugging purposes. Used heavily during development.*/
    	@Override
		public String toString() {
    		return (from == null ? "null " : from.label).toString() + " to " + (to == null ? "null " : to.label).toString();
    	}
    }
    
    /**Types of edges that an edge can be*/
    enum EdgeType {
    	cfg, cdg, ddg
    }

    /**Makes edges with basic rules between input nodes. Specifically, edges are made directionally from one node to the next.*/
    public static void makeCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType)
    {
        Node previousNode = null;
        Node currentNode = null;
        Edge edge = null;
        
        for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
            currentNode = nodesInsideConstruct.get(nodeIndex);

            if(previousNode != null) {
                // Create edge from previous node to current node
                edge = new Edge(edgeType, previousNode, currentNode, "");
                previousNode.setCFGEdge(edge);
                currentNode.setCFGEdge(edge);
            }
            previousNode = currentNode;
        }
        
        //Add edge awaiting target node to last node
        edge = new Edge(edgeType, currentNode, null, "");
        if(node != null)
        	node.edgesAwaitingOutNode.add(edge);
        currentNode.setCFGEdge(edge);
    }
    
    /**Makes appropriate CFG edges between ternary statement nodes*/
    public static void addTernaryCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType)
    {
      Edge edge = null;
    	
	  // Add edge from current edge to next node outside the ternary construct
	  edge = new Edge(edgeType, nodesInsideConstruct.get(0), null, "");
	  node.edgesAwaitingOutNode.add(edge);
	  nodesInsideConstruct.get(0).setCFGEdge(edge);
    }
    
    /**Makes appropriate CFG edges between if statement nodes*/
    public static void addIfCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
		Node previousNode = null;
		Node previousConditionalNode = null;
		Node currentNode = null;
		Node previousElseIfNode = null;
		Edge edge = null;
		boolean madeFalseEdgeForIf = false;
		
		for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
			currentNode = nodesInsideConstruct.get(nodeIndex);

			if (previousNode != null) {
				// Previous node was an if / else if
				if (previousNode.antlrNode.getClass() == ParExpressionContext.class && (previousNode.label.startsWith("if") || previousNode.label.startsWith("elseif"))) {
					// Create true edge for if / else if
					edge = new Edge(edgeType, previousNode, currentNode, "true");
					previousNode.setCFGEdge(edge);
					currentNode.setCFGEdge(edge);

					previousConditionalNode = previousNode;
				}
				// Current node is if / else if / else
				else if ((currentNode.label.startsWith("if") || currentNode.label.startsWith("elseif") || currentNode.label.startsWith("else"))) {
					// Create edge to out node if previous node is the last statement inside if/elseif/else block
					if (currentNode.label.startsWith("elseif") || currentNode.label.startsWith("else")) {
						edge = new Edge(edgeType, previousNode, null, "");
						node.edgesAwaitingOutNode.add(edge);
						previousNode.setCFGEdge(edge);
					}

					// Create false edge of if / else if
					// Current node is an else if
					if (currentNode.label.startsWith("elseif")) {
						edge = new Edge(edgeType, previousConditionalNode, currentNode, "false");
						previousConditionalNode.setCFGEdge(edge);
						currentNode.setCFGEdge(edge);
						madeFalseEdgeForIf = true;
						previousElseIfNode = currentNode;
					}
				}
				// Current node is not if / else if / else and previous node is not if / else if
				else {
					// Current node is the one after an else
					if (previousNode.label.equals("else")) {
						edge = new Edge(edgeType, previousConditionalNode, currentNode, "false");
						previousConditionalNode.setCFGEdge(edge);
						currentNode.setCFGEdge(edge);
						madeFalseEdgeForIf = true;
					} else {
						// Create edge between current and previous nodes that are both in the if construct
						edge = new Edge(edgeType, previousNode, currentNode, "");
						previousNode.setCFGEdge(edge);
						currentNode.setCFGEdge(edge);
					}
				}
				// Add edge between last node in construct and next node outside of if construct
				if (nodeIndex == nodesInsideConstruct.size() - 1) {
					edge = new Edge(edgeType, currentNode, null, "");
					node.edgesAwaitingOutNode.add(edge);
					currentNode.setCFGEdge(edge);
				}
			}
			previousNode = currentNode;
		}
		
		//Handle if statements without elseif/else
		if(!madeFalseEdgeForIf) {
			edge = new Edge(edgeType, nodesInsideConstruct.get(0), null, "false");
			node.edgesAwaitingOutNode.add(edge);
			nodesInsideConstruct.get(0).setCFGEdge(edge);
		}
		
		//Handle if-elseif without else
		if(previousElseIfNode != null) {
			boolean lastElseIfHasFalseEdge = false;
			for(Edge edgeInElseIf : previousElseIfNode.cfgEdges) {
				if(edgeInElseIf.from == previousElseIfNode && edgeInElseIf.label.equals("false")) {
					lastElseIfHasFalseEdge = true;
					break;
				}
			}
			if(!lastElseIfHasFalseEdge) {
				edge = new Edge(edgeType, previousElseIfNode, null, "false");
				node.edgesAwaitingOutNode.add(edge);
				previousElseIfNode.setCFGEdge(edge);
			}
		}

	}
    
    /**Makes appropriates CFG edges between do-while nodes*/
    public static void addDoWhileCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType)
    {
        Node previousNode = null;
        Node currentNode = null;
        Edge edge = null;

        for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
            currentNode = nodesInsideConstruct.get(nodeIndex);

            if(previousNode != null)
            {
                // Current node is the do while condition
                if(nodeIndex == nodesInsideConstruct.size() - 1)
                {
                    // Create true edge between first statement in body and while condition
                    edge = new Edge(edgeType, currentNode, nodesInsideConstruct.get(0), "true");
                    nodesInsideConstruct.get(0).setCFGEdge(edge);
                    currentNode.setCFGEdge(edge);


                    // Create false edge between while condition and next node outside of the do while construct
                    edge = new Edge(edgeType, currentNode, null, "false");
                    node.edgesAwaitingOutNode.add(edge);
                    currentNode.setCFGEdge(edge);

                }
                edge = new Edge(edgeType, previousNode, currentNode, "");
                previousNode.setCFGEdge(edge);
                currentNode.setCFGEdge(edge);
            }
            previousNode = currentNode;
        }
    }
    
    /**Makes appropriates CFG edges between while statement nodes*/
    public static void addWhileCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType)
	{
		for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
			

			Node currentNode = nodesInsideConstruct.get(nodeIndex);
			Node nextNode;
			Edge edge;

			// Create true and false edges for condition of while loop
			if (nodeIndex == 0) {
				// Connect condition node to next node after while construct (false edge)
				// To node is null temporarily as the next node is not known yet
				edge = new Edge(edgeType, currentNode, null, "false");
				node.edgesAwaitingOutNode.add(edge);
				currentNode.setCFGEdge(edge);

				
				if(nodeIndex + 1 < nodesInsideConstruct.size()) {
					// Connect condition node to the next node inside while construct (true edge)
					nextNode = nodesInsideConstruct.get(nodeIndex + 1);
					edge = new Edge(edgeType, currentNode, nextNode, "true");
					// Set true edges
					currentNode.setCFGEdge(edge);
					nextNode.setCFGEdge(edge);
				}
			}
			// Connect last node in while construct to the condition node
			else if (nodeIndex == nodesInsideConstruct.size() - 1) {
				nextNode = nodesInsideConstruct.get(0);
				edge = new Edge(edgeType, currentNode, nextNode, "");
				// Add edges for the to and from nodes
				currentNode.setCFGEdge(edge);
				nextNode.setCFGEdge(edge);
			}
			// Connect node in while construct to the next node
			else {
				if(nodeIndex + 1 < nodesInsideConstruct.size()) {
					nextNode = nodesInsideConstruct.get(nodeIndex + 1);
					edge = new Edge(edgeType, currentNode, nextNode, "");
					// Add edges for the to and from nodes
					currentNode.setCFGEdge(edge);
					nextNode.setCFGEdge(edge);
				}
			}
		}
	}
    
    /**Makes appropriates CFG edges between switch statement nodes*/
    public static void addSwitchCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType)
	{
		Node previousNode = null;
		Node previousCaseNode = null;
		Node nodeBeforePreviousNode = null;
		Node currentNode = null;
		Edge edge = null;

		for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
			currentNode = nodesInsideConstruct.get(nodeIndex);

			if(previousNode != null) {
				// If current and previous node is not a case or default, add an edge from the previous to the current node.
				if (!(currentNode.label.equals("default") || currentNode.antlrNode.getClass() == ConstantExpressionContext.class ||
						previousNode.label.equals("default") || previousNode.antlrNode.getClass() == ConstantExpressionContext.class))
				{
					edge = new Edge(edgeType, previousNode, currentNode, "");
					previousNode.setCFGEdge(edge);
					currentNode.setCFGEdge(edge);
				}
				if(previousNode.antlrNode.getClass() == ConstantExpressionContext.class || previousNode.label.equals("default"))
				{
					// If previous node is a case, then add the true edge from case to next statement under case
					if (previousNode.antlrNode.getClass() == ConstantExpressionContext.class) {
						edge = new Edge(edgeType, previousNode, currentNode, "true");
						previousNode.setCFGEdge(edge);
						currentNode.setCFGEdge(edge);

						previousCaseNode = previousNode;
					}
					// If previous node is default, add false edge from previous case node to next statement after default
					else if (previousNode.label.equals("default")) {
						edge = new Edge(edgeType, previousCaseNode, currentNode, "false");
						if(previousCaseNode != null) {
							previousCaseNode.setCFGEdge(edge);
							currentNode.setCFGEdge(edge);
						}

					}
					// Create edge between node before case / default and after case / default since there is no break
					if (nodeBeforePreviousNode != null && !nodeBeforePreviousNode.label.equals("break")) {
						edge = new Edge(edgeType, nodeBeforePreviousNode, currentNode, "");
						nodeBeforePreviousNode.setCFGEdge(edge);
						currentNode.setCFGEdge(edge);
					}
				}
				// If current node is a case, then add the false edge from case to the previous case
				if(currentNode.antlrNode.getClass() == ConstantExpressionContext.class)
				{
					edge = new Edge(edgeType, previousCaseNode, currentNode, "false");
					previousCaseNode.setCFGEdge(edge);
					currentNode.setCFGEdge(edge);
				}
				// If current node is a break statement, then add an edge between the break and the next statement outside the switch construct
				else if (currentNode.label.equals("break"))
				{
					edge = new Edge(edgeType, currentNode, null, "");
					currentNode.setCFGEdge(edge);
					node.edgesAwaitingOutNode.add(edge);
				}
				// Add edge between the last node in switch construct and next node outside of switch construct
				if(nodeIndex == nodesInsideConstruct.size() - 1)
				{
					edge = new Edge(edgeType, currentNode, null, "");
					node.edgesAwaitingOutNode.add(edge);
					currentNode.setCFGEdge(edge);
				}
			}
			nodeBeforePreviousNode = previousNode;
			previousNode = currentNode;
		}
		// Create false edge from last case to next node outside of switch construct if there is no default
		boolean isFalseEdge = false;
		if(previousCaseNode != null) {
			for(Edge edgeInCaseNode : previousCaseNode.cfgEdges) {
				if (edgeInCaseNode.from == previousCaseNode && edgeInCaseNode.label.equals("false")) {
					isFalseEdge = true;
				}
			}
		}
		// If there is no false edge from last case statement, create one to next node outside of switch statement
		if(!isFalseEdge && previousCaseNode != null)
		{
			edge = new Edge(edgeType, previousCaseNode, null, "false");
			node.edgesAwaitingOutNode.add(edge);
			previousCaseNode.setCFGEdge(edge);
		}
	}
    
    /**Makes appropriates CFG edges between for loop statement nodes*/
    public static void addForCFGEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
        Node previousNode = null;
        Node currentNode = null;
        Node forConditionNode = null;
        Edge edge = null;

        for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
            currentNode = nodesInsideConstruct.get(nodeIndex);
            
            // If for initializer exists, create edge from it to the for condition
            if (!currentNode.label.startsWith("forCondition(") && nodeIndex == 0)
            {
                edge = new Edge(edgeType, currentNode, nodesInsideConstruct.get(nodeIndex + 1), "");
                currentNode.setCFGEdge(edge);
                nodesInsideConstruct.get(nodeIndex + 1).setCFGEdge(edge);
            }
            else if (currentNode.label.startsWith("forCondition("))
            {
                // Create false edge between for condition and the next node outside the for construct
                edge = new Edge(edgeType, currentNode, null, "false");
                currentNode.setCFGEdge(edge);
                node.edgesAwaitingOutNode.add(edge);

                forConditionNode = currentNode;
            }
            else if (previousNode.label.startsWith("forCondition(")) {
                // Create true edge between for condition and the next node inside the for construct
                edge = new Edge(edgeType, previousNode, currentNode, "true");
                previousNode.setCFGEdge(edge);
                currentNode.setCFGEdge(edge);
            }
            else
            {
                // Set edge between previous and current nodes which are both inside the for loop body
                edge = new Edge(edgeType, previousNode, currentNode, "");
                previousNode.setCFGEdge(edge);
                currentNode.setCFGEdge(edge);
            }

            if (nodeIndex == nodesInsideConstruct.size() - 1)
            {
                // Set edge between the last node in the for loop body and the for condition
                edge = new Edge(edgeType, currentNode, forConditionNode, "");
                currentNode.setCFGEdge(edge);
                forConditionNode.setCFGEdge(edge);
            }
            
            previousNode = currentNode;
        }
    }
    
    public static void addTryCatchEdges(ArrayList<Node> nodesInsideConstruct, Node node, EdgeType edgeType) {
		TryCatchListener listener = ((TryCatchListener) node.associatedListener);
		
		int stopIndex = -1;
		ArrayList<Node> tryNodes = new ArrayList<Node>();
		ArrayList<Node> allNodes = methodListener.getCurrentGraph().allNodes;
		
		Node firstCFGNodeInConstruct = allNodes.get(1);
		while(firstCFGNodeInConstruct.antlrNode.getStart().getStartIndex() < node.antlrNode.getStart().getStartIndex())
			firstCFGNodeInConstruct = allNodes.get(allNodes.indexOf(firstCFGNodeInConstruct) + 1);

		if(!listener.catchIndices.isEmpty()) {
			stopIndex = listener.catchIndices.get(0);
		} else {
			stopIndex = listener.finallyIndex;
		}
		
		Node nextNode = firstCFGNodeInConstruct;
		//Finding all nodes in try block
		while(nextNode.antlrNode.getStart().getStartIndex() < stopIndex) {

			tryNodes.add(nextNode);
			if(allNodes.indexOf(nextNode)+1 >= allNodes.size()) break;
			do {
				nextNode = allNodes.get(allNodes.indexOf(nextNode)+1);
			} while (nextNode.isTempNode);
		}
		
		//Every try node to every first catch node
		for(Node tryNode : tryNodes) {
			for(Integer catchIndex : listener.catchIndices) {
				nextNode = firstCFGNodeInConstruct;
				while(nextNode.antlrNode.getStart().getStartIndex() <= catchIndex) {
					do {
						nextNode = allNodes.get(allNodes.indexOf(nextNode)+1);
					} while (nextNode.isTempNode);
				}
				Edge newEdge = new Edge(EdgeType.cfg, tryNode, nextNode, "ThrewException");
				nextNode.cfgEdges.add(newEdge);
				tryNode.cfgEdges.add(newEdge);
			}
		}
	
		//last node in try to first node in finally 
		Node firstFinallyNode = null;
		if(listener.finallyIndex != Integer.MAX_VALUE) {
			while(allNodes.get(allNodes.indexOf(nextNode)).antlrNode.getStart().getStartIndex() <= listener.finallyIndex) {
				nextNode = allNodes.get(allNodes.indexOf(nextNode)+1);
			}
			firstFinallyNode = nextNode;
		}

		
		//for every catch index, every catch node to the next, as long as next node is < next catch index
		for(int i = 0; i < listener.catchIndices.size(); i++) {
			Node firstCatchNode = nodesInsideConstruct.get(0);
			int nextIndex = 0;
			while(firstCatchNode.antlrNode.getStart().getStartIndex() <= listener.catchIndices.get(i)) {
				do {
					nextIndex++;
					firstCatchNode = nodesInsideConstruct.get(nextIndex);
				} while (firstCatchNode.isTempNode);

			}

			Node currentNode = firstCatchNode;
			stopIndex = Integer.MAX_VALUE;
			
			if(i+1 <= listener.catchIndices.size()-1)
				stopIndex = listener.catchIndices.get(i+1);
			else if (listener.finallyIndex != Integer.MAX_VALUE) 
				stopIndex = listener.finallyIndex;
			else 
				stopIndex = nodesInsideConstruct.get(nodesInsideConstruct.size()-1).antlrNode.getStart().getStartIndex();

			while(nodesInsideConstruct.indexOf(currentNode)+1 < nodesInsideConstruct.size()
			&& nodesInsideConstruct.get(nodesInsideConstruct.indexOf(currentNode)+1).antlrNode.getStart().getStartIndex() <= stopIndex) {
				nextNode = currentNode;
				do {
					nextNode = nodesInsideConstruct.get(nodesInsideConstruct.indexOf(nextNode)+1);
				} while(nextNode.isTempNode);
				if(nextNode.antlrNode.getStart().getStartIndex() <= stopIndex) {
					//Sequential edges between nodes in each catch
					Edge newEdge = new Edge(EdgeType.cfg, currentNode, nextNode, "");
					currentNode.setCFGEdge(newEdge);
					nextNode.setCFGEdge(newEdge);
				} else {
					//Edge from last node in a catch
					Node exitOrFinally = listener.finallyIndex == Integer.MAX_VALUE ? methodListener.getCurrentExitNode() : firstFinallyNode;
					Edge newEdge = new Edge(EdgeType.cfg, currentNode, exitOrFinally, "");
					
					currentNode.setCFGEdge(newEdge);
					exitOrFinally.setCFGEdge(newEdge);
				}
				currentNode = nextNode;
			}
		}
    	
		//Sequential edges
		Integer currentNodeStart;
		Node currentNode = null;
		Integer previousNodeStart;
		Node previousNode = null;
		Edge edge = null;


		for (int nodeIndex = 0; nodeIndex < nodesInsideConstruct.size(); nodeIndex++) {
			while(nodesInsideConstruct.get(nodeIndex).isTempNode) {
				nodeIndex++;
			}

			// Make edge from current node to the previous node
			currentNode = nodesInsideConstruct.get(nodeIndex);
			currentNodeStart = currentNode.antlrNode.getStart().getStartIndex();
			
			int firstCatchIndex = listener.catchIndices.isEmpty() ? Integer.MAX_VALUE : listener.catchIndices.get(0);
			
			if(currentNodeStart > listener.finallyIndex 
			|| (currentNodeStart < firstCatchIndex && currentNodeStart < listener.finallyIndex)) {
				if(previousNode != null) {
					edge = new Edge(EdgeType.cfg, previousNode, currentNode, "");
					previousNode.setCFGEdge(edge);
					currentNode.setCFGEdge(edge);
				}
				
				//Make edge from last node in finally block
				if(nodeIndex == nodesInsideConstruct.size()-1) {
					
		    		//If this is a construct making an edge to a node, propagate target node to all nodes in this construct awaiting a target node
					ArrayList<Edge> edgesAwaitingOutNode = new ArrayList<Edge>();
		    		if(currentNode.isConstruct) {
		    			for(Edge edgeAwaitingOut : currentNode.edgesAwaitingOutNode) {
		    				edgesAwaitingOutNode.add(new Edge(edgeAwaitingOut));
		    			}
		    		}

					// Create edge to exit node
					edge = new Edge(EdgeType.cfg, currentNode, methodListener.getCurrentExitNode(), "ExceptionOccuredPrior");
					currentNode.setCFGEdge(edge);
					methodListener.getCurrentExitNode().setCFGEdge(edge);

		    		if(currentNode.isConstruct) {
						node.edgesAwaitingOutNode.addAll(edgesAwaitingOutNode);
		    		}
					
					// Create edge to outside construct
					edge = new Edge(EdgeType.cfg, currentNode, null, "");
					currentNode.setCFGEdge(edge);
					node.edgesAwaitingOutNode.add(edge);

				}

				previousNode = currentNode;
				previousNodeStart = previousNode.antlrNode.getStart().getStartIndex();
			}
		}
	}
}
