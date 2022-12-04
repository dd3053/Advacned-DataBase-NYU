# Replicated Concurrency Conctrol and Recovery (RepCRec)
Student 1 : Devesh Devendra [dd3053]

Student 2 : Anand Kumar [ak8288]

Programming Language : **Java**

Semester : Fall 2022 Semester

## Comiling The Files : 
There are 8 .java Files which are given as below : 
1. Command.java
2. CommandManager.java
3. DataManager.java
4. Lock.java
5. Logger.java
6. Solution.java
7. Transaction.java
8. TransactionManager.java

The file **Solution.java** contains the **main()** function for the execution of the program.

To run all the programs, all of the .java files must be present in the same folder. 

They can be compiled by the following Command : 

```
javac *.java
```

## Executing the Program

The Program runs in 3 modes : 

### Mode 1 : Interactive Mode

In this mode, the input is provided using the Command Line.

It is started using the following Command : 
```
java Solution
```

Example : 
```
COunt of Args : 0
===========================================
TestCase : 1
===========================================
begin(T1)
begin(T2)
W(T1,x1,101)
W(T2,x2,202)
W(T1,x2,102)
Lock Conflict for Transaction : T1
W(T2,x1,201)
Lock Conflict for Transaction : T2
end(T1)
Aborting Transaction : T2
Writing Value : x1.2
Writing Value : x2.1
Writing Value : x2.2
Writing Value : x2.3
Writing Value : x2.4
Writing Value : x2.5
Writing Value : x2.6
Writing Value : x2.7
Writing Value : x2.8
Writing Value : x2.9
Writing Value : x2.10
Transaction Committed : T1
dump()
site 1 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 2 - x1: 101 x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x11: 110 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 3 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 4 - x2: 102 x3: 30 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x13: 130 x14: 140 x16: 160 x18: 180 x20: 200 
site 5 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 6 - x2: 102 x4: 40 x5: 50 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x15: 150 x16: 160 x18: 180 x20: 200 
site 7 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 8 - x2: 102 x4: 40 x6: 60 x7: 70 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x17: 170 x18: 180 x20: 200 
site 9 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 10 - x2: 102 x4: 40 x6: 60 x8: 80 x9: 90 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x19: 190 x20: 200 
Exit
```

### Mode 2 : Input From File and Output to Standard Mode

In this mode, the input is provided using an Input File.

The Program assumes that a Valid File Name is provided

A Sample File has been provided with the Submission.

Required Command : 

```
java Solution <inputFile>
```

Example : [Only a Portion of Output is being shown as it contains over 20 Test Cases. Please check the provided output File for more details.]

```
java Solution TestCases.txt
COunt of Args : 1
Input File : TestCases.txt
===========================================
TestCase : 1
===========================================
Lock Conflict for Transaction : T1
Lock Conflict for Transaction : T2
Aborting Transaction : T2
Writing Value : x1.2
Writing Value : x2.1
Writing Value : x2.2
Writing Value : x2.3
Writing Value : x2.4
Writing Value : x2.5
Writing Value : x2.6
Writing Value : x2.7
Writing Value : x2.8
Writing Value : x2.9
Writing Value : x2.10
Transaction Committed : T1
site 1 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 2 - x1: 101 x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x11: 110 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 3 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 4 - x2: 102 x3: 30 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x13: 130 x14: 140 x16: 160 x18: 180 x20: 200 
site 5 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 6 - x2: 102 x4: 40 x5: 50 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x15: 150 x16: 160 x18: 180 x20: 200 
site 7 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 8 - x2: 102 x4: 40 x6: 60 x7: 70 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x17: 170 x18: 180 x20: 200 
site 9 - x2: 102 x4: 40 x6: 60 x8: 80 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x20: 200 
site 10 - x2: 102 x4: 40 x6: 60 x8: 80 x9: 90 x10: 100 x12: 120 x14: 140 x16: 160 x18: 180 x19: 190 x20: 200 

```

### Mode 3 : Input From File and Output To File

In this mode, the input is provided using an Input File.

The output is provided using an Output File.

The program assumes that the Input File is a Valid File and the Program has authority to create a new File

Required Command : 
```
java Solution <inputFile> <outputFile>
```

Example : 

```
java Solution TestCases.txt output.txt
COunt of Args : 2
Input File : TestCases.txt
Output File : output.txt
```

## Basic Commands 
The following commands are supported : 

1. begin(T) : Starts a new Transaction
2. begin(RO) : Start a new Read-Only Transaction
3. R(T, x) : The transaction T reads a variable x
4. W(T, x, val) : The Transaction T wants to write the value val to the variable x.
5. dump() : Prints out the commited values of all copies
6. end(T) : Ends The Transaction T. It shows whether Transaction T was commited or T was aborted.
7. fail(siteNumber) : The siteNumber Fails
8. recover(siteNumber) : The SiteNumber recovers
9. Exit() : Exits the Program
10. nextTC() : Starts a new Simulation without Exiting the Program
