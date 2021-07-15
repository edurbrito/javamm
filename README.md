# The JMM language
Jmm is a compiler for the Java minus minus language, a subset of the Java language. 

It's divided into five main sections:
1. Lexical analyzer: divides the code into a chain of tokens;
2. Syntactic analyzer: parses the chain of tokens, reporting any syntactical errors, and constructing a AST;
3. Semantic analyzer: analyses the code for semantic errors, and constructs the SymbolTable;
4. Code Optimizer: using the AST and the SymbolTable, builds the OLLIR code;
5. Code Generation: converts the previously parsed OLLIR code into Jasmin.

We implemented some enhancements in our project:
1. In the semantic stage, we verify if the variables are initialized. If not, we report a warning;
2. Method overloading is supported;
3. The optimizations Constant Propagation, Constant Folding and optimized While loops are implemented.


## DEALING WITH SYNTACTIC ERRORS:
The syntactic error recovery acts in while cycles. 
When encountering an error in one, it recovers from the error, reporting it and discarding the rest of the loop. 
However, for other errors, it returns immediately.


## SEMANTIC ANALYSIS:
The main rules implemented are:
* all operations must be of the same type;
* it is not possible to use arrays directly in arithmetic/boolean expressions;
* only arrays can be accessed;
* array access index must be an integer;
* In equal statements, the assignee type must be equal to the assigned type;
* The if/while must result in a boolean;
* When calling a function, verifies if it exists.
Extras:
* Supports method overloading;
* Verifies if variables are initialized.


## CODE GENERATION:
In order to generate JMC, the tool needs to first generate an intermediate code representation, the ollir code. 
Starting in the first node of the AST, the tool traverses the tree, and, according to the node kind, it calls helper functions to parse it. 
For complex expressions, temporary variables are used to store intermediate and final calculations.
In this stage, constant propagation and constant folding were implemented and can be used. 
The resulting ollir code is formatted and cleaned up to be parsed by an ollir parser.
Afterwards a jasmin representation is generated based on the ollir parser result.


## TASK DISTRIBUTION:

### Checkpoint 1
Creating the tokens - Eduardo and Paulo
While Cycle error recovery -  Luís and Ricardo
Creating more tests - Everyone 

### Checkpoint 2
Creating the SymbolTable - Eduardo and Paulo
Filling the SymbolTable - Luís and Ricardo
Checking for semantic errors - Luís and Ricardo
Creating more tests -  Eduardo and Paulo
OLLIR - Luís and Ricardo
Jasmin - Eduardo and Paulo

### Checkpoint 3
OLLIR - Luís and Ricardo
Jasmin - Eduardo and Paulo
Tests - Everyone

### Final delivery
Implementation of optimizations - Eduardo and Paulo


## PROS:
This tool can handle expressions with small and medium complexity and run its code appropriately.
Some clever tests prove that in spite of the language simplicity, it is versatile and can handle most use cases.
The tool is fast on compiling and executing.
It handles arithmetic expressions and precedence rules very well.
Generates comprehensive errors for users.


## CONS:
Ollir code generation fails in some difficult expressions not generating the appropriate temporary variables or not the proper type assignments.
Ollir code generation does not take into account efficiency.
Ollir code generation is coded in a way that it is difficult to evolve/improve functionalities due to numerous "patches" instead of refactors.


# Compilers Project

For this project, you need to [install Gradle](https://gradle.org/install/)

## Project setup

Copy your ``.jjt`` file to the ``javacc`` folder. If you change any of the classes generated by ``jjtree`` or ``javacc``, you also need to copy them to the ``javacc`` folder.

Copy your source files to the ``src`` folder, and your JUnit test files to the ``test`` folder.

## Compile

To compile the program, run ``gradle build``. This will compile your classes to ``classes/main/java`` and copy the JAR file to the root directory. The JAR file will have the same name as the repository folder.

### Run

To run you have two options: Run the ``.class`` files or run the JAR.

### Run ``.class``

To run the ``.class`` files, do the following:

```cmd
java -cp "./build/classes/java/main/" <class_name> <arguments>
```

Where ``<class_name>`` is the name of the class you want to run and ``<arguments>`` are the arguments to be passed to ``main()``.

### Run ``.jar``

To run the JAR, do the following command:

```cmd
java -jar <jar filename> <arguments>
```

Where ``<jar filename>`` is the name of the JAR file that has been copied to the root folder, and ``<arguments>`` are the arguments to be passed to ``main()``.

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening ``build/reports/tests/test/index.html``.

## Checkpoint 1
For the first checkpoint the following is required:

1. Convert the provided e-BNF grammar into JavaCC grammar format in a .jj file
2. Resolve grammar conflicts (projects with global LOOKAHEAD > 1 will have a penalty)
3. Proceed with error treatment and recovery mechanisms for the while expression
4. Convert the .jj file into a .jjt file
5. Include missing information in nodes (i.e. tree annotation). E.g. include class name in the class Node.
6. Generate a JSON from the AST

### JavaCC to JSON
To help converting the JavaCC nodes into a JSON format, we included in this project the JmmNode interface, which can be seen in ``src-lib/pt/up/fe/comp/jmm/JmmNode.java``. The idea is for you to use this interface along with your SimpleNode class. Then, one can easily convert the JmmNode into a JSON string by invoking the method JmmNode.toJson().

Please check the SimpleNode included in this repository to see an example of how the interface can be implemented, which implements all methods except for the ones related to node attributes. How you should store the attributes in the node is left as an exercise.

### Reports
We also included in this project the class ``src-lib/pt/up/fe/comp/jmm/report/Report.java``. This class is used to generate important reports, including error and warning messages, but also can be used to include debugging and logging information. E.g. When you want to generate an error, create a new Report with the ``Error`` type and provide the stage in which the error occurred.

### Parser Interface

We have included the interface ``src-lib/pt/up/fe/comp/jmm/JmmParser.java``, which you should implement in a class that has a constructor with no parameters (please check ``src/Main.java`` for an example). This class will be used to test your parser. The interface has a single method, ``parse``, which receives a String with the code to parse, and returns a JmmParserResult instance. This instance contains the root node of your AST, as well as a List of Report instances that you collected during parsing.

To configure the name of the class that implements the JmmParser interface, use the file ``parser.properties``.


## GROUP: COMP2021-3C

NAME: Eduardo Brito, NR: 201806271, CONTRIBUTION: 25%
NAME: Luís Miguel Maia Marques Torres E Silva, NR: 201808912, CONTRIBUTION: 25%
NAME: Paulo Jorge Salgado Marinho Ribeiro, NR: 201806505, CONTRIBUTION: 25%
NAME: Ricardo Amaral Nunes, NR: 201706860, CONTRIBUTION: 25%