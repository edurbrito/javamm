**GROUP: COMP2021-3C



(Names, numbers, self assessment, and contribution of the members of the group to the project according to:)
NAME1: Eduardo Ribas Brito, NR1: 201806271, GRADE1: <0 to 20 value>, CONTRIBUTION1: <0 to 100 %>
NAME2: Luís Miguel Maia Marques Torres E Silva, NR2: 201808912, GRADE2: <0 to 20 value>, CONTRIBUTION2: <0 to 100 %>
NAME3: Paulo Jorge Salgado Marinho Ribeiro, NR2: < student number >, GRADE2: <0 to 20 value>, CONTRIBUTION2: <0 to 100 %>
NAME4: Ricardo Amaral Nunes, NR2: 201706860, GRADE2: <0 to 20 value>, CONTRIBUTION2: <0 to 100 %>

...
(Note that the sum of the CONTRIBUTION? values must be 100 %)



GLOBAL Grade of the project: 18



**SUMMARY: (Describe what your tool does and its main features.)
Jmm is a compiler for the Java minus minus language, a subset of the Java language. It's divided into five main sections:
1. Lexical analyzer: divides the code into a chain of tokens;
2. Syntactic analyzer: parses the chain of tokens, reporting any syntactical errors, and constructing a AST;
3. Semantic analyzer: analyses the code for semantic errors, and constructs the SymbolTable;
4. Code Optimizer: using the AST and the SymbolTable, builds the OLLIR code;
5. Code Generation: converts the previously parsed OLLIR code into Jasmin.

We implemented three enhaments in our project:
1. In the semantic stage, we verify if the variables are initialized. If not, we report a warning;
2. Method overload is supported;
3. The optimizations Constant Propagation and Constant Folding are implemented.




**DEALING WITH SYNTACTIC ERRORS: (Describe how the syntactic error recovery of your tool works. Does it exit after the first error?)
The syntactic error recovery acts in while cycles. When encountering an error in one, it recovers from the error, reporting it and discarting the rest of the loop. However, for other errors, it returns immediatly.



**SEMANTIC ANALYSIS: (Refer the semantic rules implemented by your tool.)
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



**CODE GENERATION: (describe how the code generation of your tool works and identify the possible problems your tool has regarding code generation.)
In order to generate JMC, the tool needs to first generate an intermediate code repesentation, the ollir code. Starting in the first node of the AST, the tool traverses the tree, and, according to the node kind, it calls helper functions to parse it. For complex expressions, temporary variables are used to store intermediate and final calculations. The resulting ollir code is formated and cleaned up to be parsed by an ollir parser. Afterwards a jasmin representation is generated based on the ollir parser result. In this stage,  constant propagation optimization and constant folding optimization were implemented.



**TASK DISTRIBUTION: (Identify the set of tasks done by each member of the project. You can divide this by checkpoint it if helps)

***Checkpoint 1
Creating the tokens - Eduardo and Paulo
While Cycle error recovery -  Luís and Ricardo
Creating more tests - Everyone 

***Checkpoint 2
Creating the SymbolTable - Eduardo and Paulo
Filling the SymbolTable - Luís and Ricardo
Checking for semantic errors - Luís and Ricardo
Creating more tests -  Eduardo and Paulo
OLLIR - Luís and Ricardo
Jasmin - Eduardo and Paulo

***Checkpoint 3
OLLIR - Luís and Ricardo
Jasmin - Eduardo and Paulo
Tests - Everyone

***Final delivery
Implementation of otimizations - Eduardo and Paulo


**PROS: (Identify the most positive aspects of your tool)
This tool can handle expressions with small and medium complexity and run its code apprpriatey.
Some clever tests prove that in spite of the language simplicity, it is versatile and can handle most use cases.
The tool is fast on compiling and executing.
It handles arithmetic expressions and precedence rules very well.
Generates comprehensive errors for users.


**CONS: (Identify the most negative aspects of your tool)
Ollir code generation fails in some difficult expressions not generating the appropriate temporary variables or not the propper type assignements.
Ollir code generation does not take into account efficiency.
Ollir code generation is coded in a way that it is difficult to evolve/improve functionalities due to numerous "patches" instead of refactors.