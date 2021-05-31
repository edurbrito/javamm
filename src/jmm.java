
import pt.up.fe.comp.jmm.JmmParser;
import pt.up.fe.comp.jmm.JmmParserResult;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.jasmin.JasminUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
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

            JmmParserResult parserResult = new JmmParserResult(root, javamm.getReports());

            return parserResult;
        } catch (Exception e) {
            // printUsage(e,true);
            ArrayList<Report> reports = new ArrayList<Report>();
            if (e instanceof ParseException)
                reports.add(new Report(ReportType.ERROR, Stage.SYNTATIC, ((ParseException) e).currentToken.beginLine, "Detected generic error: " + e.getMessage()));
            else
                reports.add(new Report(ReportType.ERROR, Stage.SYNTATIC, e.getStackTrace()[0].getLineNumber(), "Detected generic error: " + e.getMessage()));
            return new JmmParserResult(null, reports);
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

            File ff = new File(fileName);

            if(!(ff.exists() && ff.isFile())){
                throw new Exception("Not a valid filepath");
            }

            jmm main = new jmm();

            JmmParserResult parserResult = main.parse(fileName);
            if(parserResult.getReports().size() > 0){
                throw new Exception("There were syntatical errors");
            }

            String name = ff.getName().split("\\.")[0];

            FileOutputStream jsonFile = new FileOutputStream(name + ".json");
            jsonFile.write(parserResult.toJson().getBytes());
            jsonFile.close();

            AnalysisStage analysisStage = new AnalysisStage();

            JmmSemanticsResult semanticsResult = analysisStage.semanticAnalysis(parserResult);
            if(semanticsResult.getReports().size() > 0){
                throw new Exception("There were semantic errors");
            }

            FileOutputStream symbolTable = new FileOutputStream(name + ".symbols.txt");
            symbolTable.write(semanticsResult.getSymbolTable().print().getBytes());
            symbolTable.close();

            OptimizationStage optimizationStage = new OptimizationStage();

            if(o) {
                semanticsResult = optimizationStage.optimize(semanticsResult);
            }

            OllirResult ollirResult = optimizationStage.toOllir(semanticsResult);

            FileOutputStream ollirStream = new FileOutputStream(name + ".ollir");
            ollirStream.write(ollirResult.getOllirCode().getBytes());
            ollirStream.close();

            BackendStage backendStage = new BackendStage();
            JasminResult jasminResult = backendStage.toJasmin(ollirResult);

            FileOutputStream jasminStream = new FileOutputStream(name + ".j");
            jasminStream.write(jasminResult.getJasminCode().getBytes());
            jasminStream.close();

            JasminUtils.assemble(new File(name + ".j"), new File("./"));

            jasminResult.run();

        } catch (Exception e) {
            e.printStackTrace();
            printUsage(e,false);
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