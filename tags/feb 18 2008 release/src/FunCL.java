/*
 * @(#)FunCL.java            0.500   2008-02-17
 *
 * Title: FunCL - Functor Clause Language.
 *
 * Description: Functional programming language based on functors; main class
 *     for main read-evaluate-update loop.
 *
 * Author: William F. Gilreath (wgilreath@gmail.com)
 *
 * Copyright (c) 2008 All Rights Reserved.
 *
 * License: This software is subject to the terms of the
 * GNU General Public License  (GPL)  available  at  the
 * following link: http://www.gnu.org/copyleft/gpl.html.
 *
 * You	must accept the terms of the GNU General  Public
 * License license agreement to	use this software.
 *
 */
package funcl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public final class FunCL
{
    private FileWriter     functorWriter = null;
    private BufferedReader functorReader = null;

    private static FileWriter traceWriter = null;
    private static FileWriter errorWriter = null;
    
    public boolean evalFlag   = false;
    public boolean exitFlag   = false;
    public boolean defineFlag = false;

    public String prompt  = null;
    public String functor = null;

    public final StringVector clauseStack = new StringVector();//c-stack
    public final StringVector evalStack   = new StringVector();//e-stack
    public final StringVector paramStack  = new StringVector();//p-stack
    public final StringVector exprStack   = new StringVector();//x-stack
    public final StringVector boolStack   = new StringVector();//b-stack
    public final ObjectVector frameStack  = new ObjectVector();//f-stack  
       
   
    /* Feb 17 2008
     * FunCL interpreter/vm command line arguments:
     *
     * -dump   : dump FunCL VM state before and after each interpreter routine
     * -logerr : log all error messages to external "error.log" file
     * -trace  : trace all non-error messages to external "trace.log" file
     * 
     */
    
    //command-line arguement attributes have "option" prefix in name
    private static boolean optionTraceFlag  = false;
    private static boolean optionLogErrFlag = false;
    private static boolean optionDumpFlag   = false;
    
    public FunCL(final String[] args)
    {
        //process command line arguements
        for(int x=0;x<args.length;x++)
        {
            String arg = args[x];
            
            if(args[x].equals(FunCLConst.OPTION_DUMP))
            {
                FunCL.optionDumpFlag = true;
            }
            else
            if(args[x].equals(FunCLConst.OPTION_LOGERR))
            {
                FunCL.optionLogErrFlag = true;
            }
            else
            if(args[x].equals(FunCLConst.OPTION_TRACE))
            {
                FunCL.optionTraceFlag = true;
            }
            else
            {
                FunCL.errln("Unidentified command-line arguement:  "+arg);
            }//end if
            
        }//end for
               
    }//end constructor

    private final void initializeExternalLog()
    {
        try
        {
            if(FunCL.optionTraceFlag)
            {
                this.traceWriter = new FileWriter(FunCLConst.FILE_TRACE_LOG);
            }//end if
            
            if(FunCL.optionLogErrFlag)
            {
                this.errorWriter = new FileWriter(FunCLConst.FILE_ERROR_LOG);
            }//end if
        }
        catch(IOException io)
        {
            System.err.println(io.getMessage()); //avoid infinite loop 
        }//end try
        
    }//end initialzeExternalLog

    private final void finalizeExternalLog()
    {
        try
        {
            if(FunCL.optionTraceFlag)
            {
                this.traceWriter.flush();
                this.traceWriter.close();
            }//end if
            
            if(FunCL.optionLogErrFlag)
            {
                this.errorWriter.flush();
                this.errorWriter.close();
            }//end if
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try
        
    }//end finalizeExternalLog

    public final void dumpState(String str)
    {
        if(FunCL.optionDumpFlag)
        {
            System.out.println();
            System.out.println(str);
            System.out.println();
            System.out.println("defineFlag   = " + this.defineFlag);
            System.out.println("evalFlag     = " + this.evalFlag);
            System.out.println("exitFlag     = " + this.exitFlag);
            System.out.println("Functor word = " + this.functor);
            System.out.println("Clause Stack = " + this.clauseStack);
            System.out.println("Eval   Stack = " + this.evalStack);
            System.out.println("Param  Stack = " + this.paramStack);
            System.out.println("Bool   Stack = " + this.boolStack);
            System.out.println("Expr   Stack = " + this.exprStack);
            System.out.println("Frame  Stack = " + this.frameStack);
            System.out.println();
        }//end if

    }//end dumpState

    public final void initialization()
    {
        this.prompt = FunCLConst.DEFAULT_PROMPT;
        
        //set default separator for file system ??

        this.initializeExternalLog(); //initialize logging before all else
        
        FunCL.printHeader();
        

        
        //report dump/error/trace status
        if(FunCL.optionDumpFlag)
        {
            putln("FunCL runtime status dump enabled.");
            newln();
        }//end if

        if(FunCL.optionLogErrFlag)
        {
            putln("FunCL runtime error logging enabled.");
            newln();
        }//end if
        
        if(FunCL.optionTraceFlag)
        {
            putln("FunCL VM runtime trace logging enabled.");
            newln();
        }//end if
        
    }//end initialization

    public final void finalization()
    {
        FunCL.printFooter();
 
        //close writers for logging error, trace
        this.finalizeExternalLog();
        
    }//end finalization

    public final static String readline()
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( System.in));
        String line = null;

        try
        {
            line = reader.readLine();
        }
        catch(IOException io)
        {
            errln("Error: "+io.getMessage());
            io.printStackTrace();
        }//end try

        if(FunCL.optionTraceFlag) //write user input directly to log to avoid double-echo effect
        {
            FunCL.writeTraceLog(line); 
            FunCL.writeTraceLog("\n\r");
        }//end if
        
        return line;
    }//end readline

   public final static String trimLit(String str)
   {
       StringBuffer result = new StringBuffer(str);

       if(result.charAt(0) == '"' || result.charAt(0) == '\'')
       {
           result.delete(0,1);
           result.delete(result.length()-1, result.length());
       }//end if

       return result.toString();
   }//end trimLit

    public final static String[] tokenizeLine(String str)
    {
       return Tokenize.tokenize(str);
    }//end tokenize

    private final void moveFrameToClauseStack()
    {
        String[] array = (String[]) this.frameStack.remove(0);
        this.clauseStack.append(array);

    }//end moveFrameToClauseStack

    private final void moveClauseToFrameStack()
    {
        String[] array = this.clauseStack.toArray();
        this.clauseStack.clear();
        this.frameStack.add(0, array);

    }//end moveClauseToFrameStack

    private final void moveClauseToEvalStack()
    {
        String token = null;
        if(this.clauseStack.has("."))
        {
            final int size = this.clauseStack.size(); //size changes
            for(int x=0;x<size;x++)
            {
                token = this.clauseStack.remove(0); //change .pop()??
                evalStack.add(x, token);
                if(token.equalsIgnoreCase("."))
                {
                    break;
                }//end if
                token = null;
            }//end for
        }
        else
        {
            this.evalFlag = false;
        }//end if

    }//end moveClauseToEvalStack

    private final void moveEvalToClauseStack()
    {
        final int size = this.evalStack.size();
        for(int x=0;x<size;x++)
        {
            String token = this.evalStack.remove(0);
            this.clauseStack.add(x, token);
        }//end for

    }//end moveEvalToClauseStack

    private final void moveEvalToParamStack() //modified to pass all in sequence non-functor as param
    {
        final int size = this.evalStack.size();

        for(int x=0;x<size;x++)
        {
            if(! Functor.isFunctor(this.evalStack.get(0)))
            {
                this.paramStack.add(x, this.evalStack.remove(0));
            }
            else
            {
                break;
            }//end if

        }//end for

    }//end moveEvalToParamStack

    public final static int fnvhash(final String str)
    {
        int    seed = 0x811c9dc5;
        byte[] buf  = str.getBytes();

        for (int i = 0; i < str.length(); i++)
        {
          seed += (seed << 1) + (seed << 4) + (seed << 7) + (seed << 8) + (seed << 24);
          seed ^= buf[i];
        }//end for

        return seed;

    }//end fnvhash

    public final void evaluateFunctor()
    {
        final int functorHash = FunCL.fnvhash(this.functor.toLowerCase());

        switch(functorHash)
        {
            case Functor.ADD  :  Functor.ADD(this);
                                 break;

            case Functor.DEC  :  Functor.DEC(this); break;

            case Functor.DEF  :  Functor.DEF(this);
                                 break;

            case Functor._DOT_:  break;

            case Functor.DUP  :  Functor.DUP(this);
                                 break;

            case Functor.ECHO :  Functor.ECHO(this);
                                 break;

            case Functor.ECHOP:  Functor.ECHOP(this);
                                 break;

            case Functor.EQ   :  Functor.EQ(this);
                                 break;

            case Functor.EXIT :  Functor.EXIT(this);
                                 break;

            case Functor.FALSE:  Functor.FALSE_(this); break;

            case Functor.FLUSHB: Functor.FLUSHB(this); break;
            case Functor.FLUSHC: Functor.FLUSHC(this); break;
            case Functor.FLUSHE: Functor.FLUSHE(this); break;
            case Functor.FLUSHP: Functor.FLUSHP(this); break;
            case Functor.FLUSHX: Functor.FLUSHX(this); break;

            case Functor.INC   : Functor.INC(this); break;

            case Functor.MUL   : Functor.MUL(this);
                                 break;

            case Functor.NE   : Functor.NE(this); break;

            case Functor.NOP : Functor.NOP(this);
                                break;

            case Functor.NOT:   Functor.NOT(this);
                                break;

            case Functor.POP:   Functor.POP(this);
                                break;

            case Functor.POPX:  Functor.POPX(this);
                                break;

            case Functor._PROMPT: Functor._PROMPT(this); break; //command environment functor prefixed with :

            case Functor.PUSH:  Functor.NOP(this);
                                break;

            case Functor.PUSHX: Functor.PUSHX(this);
                                break;

            case Functor.SAY  : Functor.SAY(this); break;

            case Functor.SIZE:  Functor.SIZE(this);
                                break;

            case Functor.TRUE:  Functor.TRUE_(this); break;

            case Functor.WHEN : Functor.WHEN(this);
                                break;

            default : //ERROR? or loadFunctor and execute??

        }//end switch

    }//end evaluateFunctor

    public final void loadFunctor()
    {
        //check if file exists, if not??  isExternalFunctor??

        this.openReaderFile(this.functor + FunCLConst.FUNCL_EXTENSION);

        this.writeFileToClause();
        this.closeReaderFile();

    }//end loadFunctor

    public final void process()
    {
        if(this.evalStack.isEmpty())
        {
            if(! this.clauseStack.isEmpty()) //clause stack is full
            {
                this.moveClauseToEvalStack();
            }
            else  //clause stack is empty
            {
                 if(!this.frameStack.isEmpty())
                 {
                     this.moveFrameToClauseStack();
                     this.moveClauseToEvalStack();
                 }
                 else
                 {
                     this.evalFlag = false;
                 }//end if
            }//end if
        }//end if

    }//end process

    public final void execute()
    {
        if(this.evalStack.isEmpty()) return;

        if(this.defineFlag) return; //do not execute if defining

        String word = this.evalStack.get(0);

        if(Functor.isFunctor(word))
        {
            this.functor = this.evalStack.remove(0);
        }
        else
        {
            errln(word+" is not a functor.");
            this.evalFlag = false;
            this.functor = null;
        }//end if

        this.moveEvalToParamStack();

    }//end execute

    public final void openWriterFile(final String file)
    {
        try
        {
            this.functorWriter = new FileWriter(file);
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try
        
    }//end openWriterFile

    public final void writeFile(final String token)
    {
        try
        {
           this.functorWriter.write(token);
           this.functorWriter.write("\n");
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try

    }//end writeFile

    public final static void writeErrorLog(final String str)
    {
        try
        {
           FunCL.errorWriter.write(str);
        }
        catch(IOException io)
        {
            System.err.println(io.getMessage()); //avoid error-infinite loop
        }//end try

    }//end writeErrorLog

    public final static void writeTraceLog(final String str)
    {
        try
        {
           FunCL.traceWriter.write(str);
        }
        catch(IOException io)
        {
            System.err.println(io.getMessage()); //avoid error-infinite loop
        }//end try

    }//end writeTraceLog

    public final void writeEvalToFile()
    {
       final int size = this.evalStack.size();
       for(int x=0;x<size;x++)
       {
           String token = this.evalStack.remove(0);
           this.writeFile(token);
       }//end for

    }//end writeEvalToFile

    public final void closeWriterFile()
    {
        try
        {
            this.functorWriter.close();
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try
        
    }//end closeWriterFile

    public final void openReaderFile(final String file)
    {
        try
        {
            this.functorReader = new BufferedReader(new FileReader(file));
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try
        
    }//end openReaderFile

    public final void writeFileToClause()
    {
        String token = null;
        int    count = 0;

        try
        {
            while((token = this.functorReader.readLine()) != null)
            {
                 this.clauseStack.add(count, token);
                 count++;
            }//end while
         }
         catch(IOException io)
         {
                errln(io.getMessage());
         }//end try

    }//end writeFileToClause

    public final void closeReaderFile()
    {
        try
        {
            this.functorReader.close();
        }
        catch(IOException io)
        {
            errln(io.getMessage());
        }//end try
        
    }//end closeReaderFile


    public final void define()
    {
        //check for terminating condition
        if(clauseStack.isEmpty() && evalStack.size() == 1)
        {
            if(evalStack.get(0).equalsIgnoreCase("."))
            {
                this.defineFlag = false; //back to eval mode
                this.closeWriterFile();
                putln("Functor defined."); //put functor name on p-stack??
            }//end if
        }
        else
        {
            this.writeEvalToFile();
        }//end if

    }//end define

    public final void evaluate()
    {
        this.dumpState(">>> Dump FunCL Virtual Machine <<<");
        
        if(this.defineFlag)
        {
            this.define();
            return;
        }//end if

        if(this.functor == null) return;

        if(Functor.isInternalFunctor(this.functor))
        {
            this.evaluateFunctor();
        }
        else
        {
            this.moveEvalToClauseStack();

            //move clause stack to frame stack - save/create a stack frame.
            if(! this.clauseStack.isEmpty())
            {
                this.moveClauseToFrameStack();
            }//end if
            
            this.loadFunctor();

        }//end if

        this.functor = null; //reset functor word

    }//end eval

    public final static String stringify(String[] str)
    {
        StringBuffer result = new StringBuffer();
        result.append("[ ");

        for(int x=0;x<str.length;x++)
        {
            result.append(str[x]);
            result.append(" ");
        }//end for

        result.append("]");
        return result.toString();

    }//end stringify

    public final void interpret()
    {
        while(! this.exitFlag)
        {
            this.evalFlag = true;

            put(this.prompt);

            String   line   = FunCL.readline();
            String[] tokens = FunCL.tokenizeLine(line);

            this.clauseStack.append(tokens);

            while(this.evalFlag)
            {
                this.process();
                this.execute();
                this.evaluate();
            }//end while

        }//end while

    }//end interpret

    //added Feb 16 2008 - put/putln for adding logging/tracing of FunCL VM.
    public final static void put(final String str)
    {
        System.out.print(str);
        if(FunCL.optionTraceFlag)
        {
            //append message to trace log file 
            FunCL.writeTraceLog(str);
        }//end if
        
    }//end put
    
    public final static void putln(final String str)
    {
        System.out.println(str);
 
        if(FunCL.optionTraceFlag)
        {
            //append message to trace log file 
            FunCL.writeTraceLog(str);
            FunCL.writeTraceLog("\n\r");
        }//end if

    }//end putln
    
    public final static void newln()
    {
        System.out.println();
        
        if(FunCL.optionTraceFlag)
        {
            //append newline to trace log file  
            FunCL.writeTraceLog("\n\r");
        }//end if
        
    }//end newln
   
    public final static void err(final String str)
    {
        System.err.print(str);
        
        if(FunCL.optionLogErrFlag)
        {
           //append string to error log file    
           FunCL.writeErrorLog(str);
        }//end if
        
    }//end err
    
    public final static void errln(final String str)
    {
        System.err.println(str);
        
        if(FunCL.optionLogErrFlag)
        {
            FunCL.writeErrorLog(str);
            FunCL.writeErrorLog("\n\r");
        }//end if
        
    }//end err
    
    public final static void printHeader()
    {
        newln();
        put(FunCLConst.TITLE);
        putln(FunCLConst.VERSION);
        put(FunCLConst.COPYRIGHT);
        putln(FunCLConst.AUTHOR);
        putln(FunCLConst.LICENSE);
        newln();

        newln();
        putln(FunCLConst.FUNCL_MESSAGE_START);
        newln();
    }//end printHeader

    public final static void printFooter()
    {
        newln();
        putln(FunCLConst.FUNCL_MESSAGE_CLOSE);
        newln();
    }//end printFooter

    public static void main(final String[] args)
    {       
        FunCL funcl = new FunCL(args);

        funcl.initialization();
        funcl.interpret();
        funcl.finalization();

        System.exit(0);  

    }//end main

}//end class FunCL
