// package uk.ac.ucl.jsh;

// import java.io.BufferedReader;
// import java.io.File;
// import java.io.IOException;
// import java.io.OutputStream;
// import java.io.OutputStreamWriter;
// import java.nio.charset.Charset;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.DirectoryStream;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.ArrayList;
// import java.util.Scanner;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class Echo {

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
