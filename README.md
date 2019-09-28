##Features
This program can convert UML class diagrams (tested with files from violetUML, support for other diagram programs not tested)
into Java source code with the following specifications:

- Default Constructor always created
- attributes automatically created
- Methods with return type have standard return statements
- (Getter/Setter methods have the standard getter/setter method body)

##How to use
Either, use the jar via the console, or in an IDE, have the program arguments set to:

1. path to class diagram file
2. path to the output directory
3. (optional) default package name