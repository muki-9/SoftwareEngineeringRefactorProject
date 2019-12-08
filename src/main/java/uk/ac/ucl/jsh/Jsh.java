package uk.ac.ucl.jsh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Jsh {
    private static String currentDirectory = System.getProperty("user.dir");

    public void eval(String cmdline, OutputStream output) throws IOException {
        // OutputStreamWriter writer = new OutputStreamWriter(output);

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

//     private static String currentDirectory = System.getProperty("user.dir");

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

//             String appName = tokens.get(0);
//             ArrayList<String> appArgs = new ArrayList<String>(tokens.subList(1, tokens.size()));

//             switch (appName) {
//             case "cd":
//                 cd(appArgs);
//                 break;
//             case "pwd":
//                 pwd(writer);
//                 break;
//             case "ls":
//                 ls(appArgs, writer);
//                 break;
//             case "cat":
//                 cat(appArgs, writer);
//                 break;
//             case "echo":
//                 echo(appArgs, writer);
//                 break;
//             case "head":
//                 head(appArgs, writer);
//                 break;
//             case "tail":
//                 tail(appArgs, writer);
//                 break;
//             case "grep":
//                 grep(appArgs, writer);
//                 break;
//             default:
//                 throw new RuntimeException(appName + ": unknown application");
//             }
//         }
//     }

//     public void cd(ArrayList<String> args_cd) throws IOException {
//         if (args_cd.isEmpty()) {
//             //implement functionality to take you to the home directory if cd is inputted alone
//             throw new RuntimeException("cd: missing argument");
//         } else if (args_cd.size() > 1) {
//             throw new RuntimeException("cd: too many arguments");
//         }
//         String dirString = args_cd.get(0);
//         File dir = new File(currentDirectory, dirString);
//         if (!dir.exists() || !dir.isDirectory()) {
//             throw new RuntimeException("cd: " + dirString + " is not an existing directory");
//         }
//         currentDirectory = dir.getCanonicalPath();
//     }

//     public void pwd(OutputStreamWriter writer_pwd) throws IOException {
//         writer_pwd.write(currentDirectory);
//         writer_pwd.write(System.getProperty("line.separator"));
//         writer_pwd.flush();
//     }

//     public void ls(ArrayList<String> args_ls, OutputStreamWriter writer_ls) throws IOException {
//         File currDir;
//                 if (args_ls.isEmpty()) {
//                     currDir = new File(currentDirectory);
//                 } else if (args_ls.size() == 1) {
//                     currDir = new File(args_ls.get(0));
//                 } else {
//                     throw new RuntimeException("ls: too many arguments");
//                 }
//                 try {
//                     File[] listOfFiles = currDir.listFiles();
//                     boolean atLeastOnePrinted = false;
//                     for (File file : listOfFiles) {
//                         if (!file.getName().startsWith(".")) {
//                             writer_ls.write(file.getName());
//                             writer_ls.write("\t");
//                             writer_ls.flush();
//                             atLeastOnePrinted = true;
//                         }
//                     }
//                     if (atLeastOnePrinted) {
//                         writer_ls.write(System.getProperty("line.separator"));
//                         writer_ls.flush();
//                     }
//                 } catch (NullPointerException e) {
//                     throw new RuntimeException("ls: no such directory");
//                 }
//     }

//     public void cat(ArrayList<String> args_cat, OutputStreamWriter writer_cat) {
//         if (args_cat.isEmpty()) {
//             throw new RuntimeException("cat: missing arguments");
//         } else {
//             for (String arg : args_cat) {
//                 Charset encoding = StandardCharsets.UTF_8;
//                 File currFile = new File(currentDirectory + File.separator + arg);
//                 if (currFile.exists()) {
//                     Path filePath = Paths.get(currentDirectory + File.separator + arg);
//                     try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
//                         String line = null;
//                         while ((line = reader.readLine()) != null) {
//                             writer_cat.write(String.valueOf(line));
//                             writer_cat.write(System.getProperty("line.separator"));
//                             writer_cat.flush();
//                         }
//                     } catch (IOException e) {
//                         throw new RuntimeException("cat: cannot open " + arg);
//                     }
//                 } else {
//                     throw new RuntimeException("cat: file does not exist");
//                 }
//             }
//         }
//     }

//     public void echo(ArrayList<String> args_echo, OutputStreamWriter writer_echo) throws IOException {
//         boolean atLeastOnePrinted = false;
//                 for (String arg : args_echo) {
//                     writer_echo.write(arg);
//                     writer_echo.write(" ");
//                     writer_echo.flush();
//                     atLeastOnePrinted = true;
//                 }
//                 if (atLeastOnePrinted) {
//                     writer_echo.write(System.getProperty("line.separator"));
//                     writer_echo.flush();
//                 }
//     }

//     public void head(ArrayList<String> args_head, OutputStreamWriter writer_head) throws IOException {
//         if (args_head.isEmpty()) {
//             throw new RuntimeException("head: missing arguments");
//         }
//         if (args_head.size() != 1 && args_head.size() != 3) {
//             throw new RuntimeException("head: wrong arguments");
//         }
//         if (args_head.size() == 3 && !args_head.get(0).equals("-n")) {
//             throw new RuntimeException("head: wrong argument " + args_head.get(0));
//         }
//         int headLines = 10;
//         String headArg;
//         if (args_head.size() == 3) {
//             try {
//                 headLines = Integer.parseInt(args_head.get(1));
//             } catch (Exception e) {
//                 throw new RuntimeException("head: wrong argument " + args_head.get(1));
//             }
//             headArg = args_head.get(2);
//         } else {
//             headArg = args_head.get(0);
//         }
//         File headFile = new File(currentDirectory + File.separator + headArg);
//         if (headFile.exists()) {
//             Charset encoding = StandardCharsets.UTF_8;
//             Path filePath = Paths.get((String) currentDirectory + File.separator + headArg);
//             try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
//                 for (int i = 0; i < headLines; i++) {
//                     String line = null;
//                     if ((line = reader.readLine()) != null) {
//                         writer_head.write(line);
//                         writer_head.write(System.getProperty("line.separator"));
//                         writer_head.flush();
//                     }
//                 }
//             } catch (IOException e) {
//                 throw new RuntimeException("head: cannot open " + headArg);
//             }
//         } else {
//             // throw new RuntimeException("head: " + headArg + " does not exist");
//             // should take value from stdin
//             writer_head.write(headArg);
//             writer_head.flush();
//         }
//     }

//     public void tail(ArrayList<String> args_tail, OutputStreamWriter writer_tail) throws IOException {
//         if (args_tail.isEmpty()) {
//             throw new RuntimeException("tail: missing arguments");
//         }
//         if (args_tail.size() != 1 && args_tail.size() != 3) {
//             throw new RuntimeException("tail: wrong arguments");
//         }
//         if (args_tail.size() == 3 && !args_tail.get(0).equals("-n")) {
//             throw new RuntimeException("tail: wrong argument " + args_tail.get(0));
//         }
//         int tailLines = 10;
//         String tailArg;
//         if (args_tail.size() == 3) {
//             try {
//                 tailLines = Integer.parseInt(args_tail.get(1));
//             } catch (Exception e) {
//                 throw new RuntimeException("tail: wrong argument " + args_tail.get(1));
//             }
//             tailArg = args_tail.get(2);
//         } else {
//             tailArg = args_tail.get(0);
//         }
//         File tailFile = new File(currentDirectory + File.separator + tailArg);
//         if (tailFile.exists()) {
//             Charset encoding = StandardCharsets.UTF_8;
//             Path filePath = Paths.get((String) currentDirectory + File.separator + tailArg);
//             ArrayList<String> storage = new ArrayList<>();
//             try (BufferedReader reader = Files.newBufferedReader(filePath, encoding)) {
//                 String line = null;
//                 while ((line = reader.readLine()) != null) {
//                     storage.add(line);
//                 }
//                 int index = 0;
//                 if (tailLines > storage.size()) {
//                     index = 0;
//                 } else {
//                     index = storage.size() - tailLines;
//                 }
//                 for (int i = index; i < storage.size(); i++) {
//                     writer_tail.write(storage.get(i) + System.getProperty("line.separator"));
//                     writer_tail.flush();
//                 }
//             } catch (IOException e) {
//                 throw new RuntimeException("tail: cannot open " + tailArg);
//             }
//         } else {
//             // throw new RuntimeException("tail: " + tailArg + " does not exist");
//             writer_tail.write(tailArg);
//             writer_tail.flush();
//         }
//     }
//     public void grep(ArrayList<String> args_grep, OutputStreamWriter writer_grep) {
//         if (args_grep.size() < 2) {
//             throw new RuntimeException("grep: wrong number of arguments");
//         }
//         Pattern grepPattern = Pattern.compile(args_grep.get(0));
//         int numOfFiles = args_grep.size() - 1;
//         Path filePath;
//         Path[] filePathArray = new Path[numOfFiles];
//         Path currentDir = Paths.get(currentDirectory);
//         for (int i = 0; i < numOfFiles; i++) {
//             filePath = currentDir.resolve(args_grep.get(i + 1));
//             if (Files.notExists(filePath) || Files.isDirectory(filePath) || !Files.exists(filePath)
//                     || !Files.isReadable(filePath)) {
//                 throw new RuntimeException("grep: wrong file argument");
//             } else {
//                 filePathArray[i] = filePath;
//             }
//         }
//         for (int j = 0; j < (filePathArray.length); j++) {
//             Charset encoding = StandardCharsets.UTF_8;
//             try (BufferedReader reader = Files.newBufferedReader(filePathArray[j], encoding)) {
//                 String line = null;
//                 while ((line = reader.readLine()) != null) {
//                     Matcher matcher = grepPattern.matcher(line);
//                     if (matcher.find()) {
//                         writer_grep.write(line);
//                         writer_grep.write(System.getProperty("line.separator"));
//                         writer_grep.flush();
//                     }
//                 }
//             } catch (IOException e) {
//                 throw new RuntimeException("grep: cannot open " + args_grep.get(j + 1));
//             }
//         }
//     }
//     public static void main(String[] args) {
//         Jsh newShell = new Jsh();
//         if (args.length > 0) {
//             if (args.length != 2) {
//                 System.out.println("jsh: wrong number of arguments");
//                 return;
//             }
//             if (!args[0].equals("-c")) {
//                 System.out.println("jsh: " + args[0] + ": unexpected argument");
//             }
//             try {
//                 newShell.eval(args[1], System.out);
//             } catch (Exception e) {
//                 System.out.println("jsh: " + e.getMessage());
//             }
//         } else {
//             Scanner input = new Scanner(System.in);
//             try {
//                 while (true) {
//                     String prompt = currentDirectory + "> ";
//                     System.out.print(prompt);
//                     try {
//                         String cmdline = input.nextLine();
//                         newShell.eval(cmdline, System.out);
//                     } catch (Exception e) {
//                         System.out.println("jsh: " + e.getMessage());
//                     }
//                 }
//             } finally {
//                 input.close();
//             }
//         }
//     }

// }
