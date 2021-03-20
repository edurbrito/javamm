
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jmm implements JmmParser {

    public static final Pattern rOption = Pattern.compile("-r=(\\d+)");
    public static final Pattern oOption = Pattern.compile("-o");
    public static final Pattern fileOption = Pattern.compile(".+\\.jmm$");

    public JmmParserResult parse(String jmmFile){

        try {
            Javamm javamm = new Javamm(new FileInputStream(jmmFile));

            SimpleNode root = javamm.Parse(); // returns reference to root node

            root.dump(""); // prints the tree on the screen

            FileOutputStream jsonFile = new FileOutputStream("AST.json");

            JmmParserResult parserResult = new JmmParserResult(root, javamm.getReports());
            jsonFile.write(parserResult.toJson().getBytes());

            jsonFile.close();

            return parserResult;
        } catch (Exception e) {
            printUsage(e,true);
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            printUsage("Wrong number of arguments");
            return;
        }

        boolean o = false, r = false;
        int num = 0;
        String fileName = "";
        Matcher matcher;

        try {

            for (String arg : args) {

                if ((matcher = fileOption.matcher(arg)).find()) { // if a valid file name is found
                    fileName = arg;
                } else if ((matcher = rOption.matcher(arg)).find()) { // if -r=num flag is found
                    r = true;
                    num = Integer.parseInt(matcher.group(1)); // captures the num parameter
                } else if ((matcher = oOption.matcher(arg)).find()) { // if -o flag is found
                    o = true;
                }
            }

            if (fileName.isEmpty()) {
                throw new Exception("No file provided");
            }

            if (r && num <= 0) {
                throw new Exception("Flag -r must receive a positive integer");
            }

            jmm main = new jmm();

            main.parse(fileName);

        } catch (Exception e) {
            printUsage(e,false);
            return;
        }
    }

    public static void printUsage(String message) {
        System.err.println("Error: " + message + "\nUsage: java jmm [-r=<num>] [-o] <input_file.jmm>");
    }

    public static void printUsage(Exception e, boolean stackTrace) {
        if(stackTrace)
            e.printStackTrace();
        System.err.println("Error: " + String.format("[%s] ", e.getClass().getSimpleName()) + e.getMessage() + "\nUsage: java jmm [-r=<num>] [-o] <input_file.jmm>");
    }
}