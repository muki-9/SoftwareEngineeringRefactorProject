package uk.ac.ucl.jsh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Jsh {
    private static String currentDirectory = System.getProperty("user.dir");
    private static ArrayList<String> commands = new ArrayList<>();
    
    public void eval(String cmdline, OutputStream output) throws IOException {
        if (cmdline.compareTo("exit") == 1) {
            System.exit(4);
        }

        Jsh.commands.add(cmdline);
        CharStream cs = CharStreams.fromString(cmdline);
        AntlrGrammarLexer lexer = new AntlrGrammarLexer(cs);
        CommonTokenStream cts = new CommonTokenStream(lexer);
        AntlrGrammarParser parser = new AntlrGrammarParser(cts);
        ParseTree tree = parser.start();
        MyTreeVisitor myVisitor = new MyTreeVisitor();

        CommandVisitable command = myVisitor.visit(tree);
        CommandVisitor commandVisitor = new Eval(output);
        command.accept(commandVisitor);
    }

    public static ArrayList<String> getCommands() {
		return commands;
	}

    public static String getCurrentDirectory() {
        return currentDirectory;
    }
    
    public static void setCurrentDirectory(String dir) {
        Jsh.currentDirectory = dir;
    }

    /**
     * 
     * @param s - this is the string the user enters at the command line
     * @return boolean value- true when enterred text is blank or contains nothing but space (ascii value 32)
     */
    public static boolean blankShell(String s) {
        if (s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if ((int) s.charAt(i) != 32) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        Jsh newShell = new Jsh();
        System.out.println("Inside shell");
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("jsh: wrong number of arguments");
                return;
            }
            if (!args[0].equals("-c")) {
                System.out.println("jsh: " + args[0] + ": unexpected argument");
            }
            try {
                newShell.eval(args[1], System.out);
            } catch (Exception e) {
                System.out.println("jsh: " + e.getMessage());
            }
            } 
            else {
                Scanner input = new Scanner(System.in);
                try {
                    while (true) {
                        String prompt = currentDirectory + "> ";
                        System.out.print(prompt);
                        try {
                            String cmdline = input.nextLine();
                            if (blankShell(cmdline)) {
                                continue;
                            }
                            newShell.eval(cmdline, System.out);
                        } catch (Exception e) {
                            System.out.println("jsh: " + e.getMessage());
                        }
                    }
                } finally {
                    input.close();
                }
            }
        }
}

//     public void eval(String cmdline, OutputStream output) throws IOException {
//         OutputStreamWriter writer = new OutputStreamWriter(output);
//         ArrayList<String> rawCommands = new ArrayList<String>();
// 		int closingPairIndex, prevDelimiterIndex = 0, splitIndex = 0;
// 		for (splitIndex = 0; splitIndex < cmdline.length(); splitIndex++) {
// 			char ch = cmdline.charAt(splitIndex);
// 			if (ch == ';') {
// 				String command = cmdline.substring(prevDelimiterIndex, splitIndex).trim();
// 				rawCommands.add(command);
// 				prevDelimiterIndex = splitIndex + 1;
// 			} else if (ch == '\'' || ch == '\"') {
// 				closingPairIndex = cmdline.indexOf(ch, splitIndex + 1);
// 				if (closingPairIndex == -1) {
// 					continue;
// 				} else {
// 					splitIndex = closingPairIndex;
// 				}
// 			}
// 		}
// 		if (!cmdline.isEmpty() && prevDelimiterIndex != splitIndex) {
// 			String command = cmdline.substring(prevDelimiterIndex).trim();
// 			if (!command.isEmpty()) {
// 				rawCommands.add(command);
// 			}
// 		}
//         for (String rawCommand : rawCommands) {
//             String spaceRegex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";
//             ArrayList<String> tokens = new ArrayList<String>();
//             Pattern regex = Pattern.compile(spaceRegex);
//             Matcher regexMatcher = regex.matcher(rawCommand);
//             String nonQuote;
//             while (regexMatcher.find()) {
//                 if (regexMatcher.group(1) != null || regexMatcher.group(2) != null) {
//                     String quoted = regexMatcher.group(0).trim();
//                     tokens.add(quoted.substring(1,quoted.length()-1));
//                 } else {
//                     nonQuote = regexMatcher.group().trim();
//                     ArrayList<String> globbingResult = new ArrayList<String>();
//                     Path dir = Paths.get(currentDirectory);
//                     DirectoryStream<Path> stream = Files.newDirectoryStream(dir, nonQuote);
//                     for (Path entry : stream) {
//                         globbingResult.add(entry.getFileName().toString());
//                     }
//                     if (globbingResult.isEmpty()) {
//                         globbingResult.add(nonQuote);
//                     }
//                     tokens.addAll(globbingResult);
//                 }
//             }