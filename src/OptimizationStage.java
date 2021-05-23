import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pt.up.fe.comp.jmm.JmmNode;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.report.Report;

/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

public class OptimizationStage implements JmmOptimization {

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {

        JmmNode node = semanticsResult.getRootNode();

        // Convert the AST to a String containing the equivalent OLLIR code
        OllirVisitor visitor = new OllirVisitor(( SymbolTableImp) semanticsResult.getSymbolTable());
        visitor.visit(node);
        String ollirCode = visitor.getOllirCode();

        //formating string
        StringBuilder temp = new StringBuilder();
        int count = 0;
        ollirCode=ollirCode.replace(";\n;",";\n").replace(";;",";");
        for(String i:ollirCode.split("\n")){
            if(i.length()==0)
                continue;
            /*if (Pattern.matches("[tu][ib][0-9]+\\..{3,6}",i))
                continue;*/
            if(!i.trim().contains(" ")&&!i.contains("}"))
                continue;
            i=i.replace(";;",";");
            temp.append("\t".repeat(count)+i+"\n\n");
            if (i.contains("{"))
                count++;
            if (i.contains("}"))
                count--;
        }
        ollirCode = temp.toString();
        File file = new File("test.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream outFile = new FileOutputStream(file);
            outFile.write(ollirCode.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(ollirCode);

        // More reports from this stag
        List<Report> reports = new ArrayList<>();

        return new OllirResult(semanticsResult, ollirCode, reports);
    }

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return semanticsResult;
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        // THIS IS JUST FOR CHECKPOINT 3
        return ollirResult;
    }

}
