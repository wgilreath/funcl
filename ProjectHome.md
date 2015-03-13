# FunCL: The Functor Clause Language #

FunCL is the Functor Clause Language, an interpreted functional programming language originally inspired in part by John Backus's Turing Award Lecture "Can Programming Be Liberated From the von Neumann Style?"

FunCL uses functors which are like functions, only more generalized. Functor syntax is unique, reminiscent of Forth and LISP--but not either. Functors are made up of clauses, each clause is a series of functors which manipulate the stacks and environment of the FunCL virtual machine (FVM). The FVM is implemented in Java, with the internal functors implemented using Java class methods.