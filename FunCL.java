import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * @(#)FunCL.java   4.11   2008-01-28
 *
 * Title: FunCL - Functor Clause Language.
 *
 * Description: Functional Programming Language Based on Functors; main class
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

public final class FunCL
{
    private FileWriter     functorWriter = null;
    private BufferedReader functorReader = null;

    private boolean evalFlag   = false;
    public  boolean exitFlag   = false;
    public  boolean defineFlag = false; //default is eval mode

    public final static String FUNCTOR_EXTENSION = ".fncl"; //move constants to FunclConstants ??
    public final static String DEFAULT_PROMPT    = ":>";

    public String  prompt  = null;
    public String  functor = null;

    public final StringVector clauseStack = new StringVector();//c-stack
    public final StringVector evalStack   = new StringVector();//e-stack
    public final StringVector paramStack  = new StringVector();//p-stack
    public final StringVector exprStack   = new StringVector();//x-stack
    public final StringVector boolStack   = new StringVector();//b-stack
    public final ObjectVector frameStack  = new ObjectVector();//f-stack  //create and use external vector for portability 3-1-2007

    public FunCL()
    {
    }//end constructor

    private final static boolean DUMP = false;

    public final void dumpState(String str)
    {
        if(DUMP)
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
        this.prompt = DEFAULT_PROMPT;
        //set default separator for file system ??

        FunCL.printHeader();

    }//end initialization

    public final void finalization()
    {

        FunCL.printFooter();
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
            System.err.println("Error: "+io.getMessage());
            io.printStackTrace();
        }//end try

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
        dumpState("### load functor: "+this.functor);

        //check if file exists, if not??  isExternalFunctor??

        this.openReaderFile(this.functor + FunCL.FUNCTOR_EXTENSION);

        this.writeFileToClause();
        this.closeReaderFile();

    }//end loadFunctor

    public final void process()
    {
        this.dumpState("## process ##");

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

        this.dumpState("## execute ##");

        String word = this.evalStack.get(0);

        if(Functor.isFunctor(word))
        {
            this.functor = this.evalStack.remove(0);
        }
        else
        {
            //ERROR
            System.err.println(word+" is not a functor.");
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
            System.err.println(io.getMessage());
        }
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
            System.err.println(io.getMessage());
        }

    }//end writeFile

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
            System.err.println(io.getMessage());
        }
    }//end closeWriterFile

    public final void openReaderFile(final String file)
    {
        try
        {
            this.functorReader = new BufferedReader(new FileReader(file));
        }
        catch(IOException io)
        {
            System.err.println(io.getMessage());
        }//end try
    }//end openReaderFile

    public final void writeFileToClause()
    {
        dumpState("## writeFileToClause");

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
                System.err.println(io.getMessage());
         }//end try

        this.dumpState("### after writeFileToClause");
    }//end writeFileToClause

    public final void closeReaderFile()
    {
        try
        {
            this.functorReader.close();
        }
        catch(IOException io)
        {
            System.err.println(io.getMessage());
        }
    }//end closeReaderFile


    public final void define()
    {
        this.dumpState("## define ##");

        //check for terminating condition
        if(clauseStack.isEmpty() && evalStack.size() == 1)
        {
            if(evalStack.get(0).equalsIgnoreCase("."))
            {
                this.defineFlag = false; //back to eval mode
                this.closeWriterFile();
                System.out.println("Functor defined."); //put functor name on p-stack??
            }//end if
        }
        else
        {
            this.writeEvalToFile();
        }//end if

    }//end define

    public final void evaluate()
    {
        if(this.defineFlag)
        {
            this.define();
            return;
        }//end if

        if(this.functor == null) return;

        this.dumpState("## evaluate ##");

        if(Functor.isInternalFunctor(this.functor))
        {
            this.evaluateFunctor();
        }
        else
        {
            this.moveEvalToClauseStack();

            //move clause stack to frame stack - save/create a stack frame.
            if(! this.clauseStack.isEmpty())
                this.moveClauseToFrameStack();

            this.loadFunctor();

        }//end if

        this.functor = null; //reset functor word

        this.dumpState("## after evaluate ##");

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

            System.out.print(this.prompt);

            String   line   = FunCL.readline();
            String[] tokens = FunCL.tokenizeLine(line);

            this.clauseStack.append(tokens);

            while(this.evalFlag)
            {

                this.process();
                this.execute();
                this.evaluate();

            }//end while

            this.dumpState("## post-eval loop ##");
        }//end while

    }//end interpret

    //header information
    public final static String TITLE     = "FunCL: The Functor Clause Language interpreter. ";
    public final static String VERSION   = "Version Alpha 0.411";
    public final static String COPYRIGHT = "Copyright (c) March 2007 ";
    public final static String AUTHOR    = "William Gilreath (wgilreath@gmail.com)  ";
    public final static String LICENSE   = "Released under the terms of the GNU General Public License (GPL).";

    public final static void printHeader()
    {
        System.out.println();
        System.out.print(TITLE);
        System.out.println(VERSION);
        System.out.print(COPYRIGHT);
        System.out.println(AUTHOR);
        System.out.println(LICENSE);
        System.out.println();

        System.out.println();
        System.out.println("Start FunCL interpreter.");
        System.out.println();
    }//end printHeader

    public final static void printFooter()
    {
        System.out.println();
        System.out.println("Close FunCL interpreter.");
        System.out.println();
    }//end printFooter

    public static void main(String[] args)
    {
        FunCL funcl = new FunCL();

        funcl.initialization();
        funcl.interpret();
        funcl.finalization();

        System.exit(0);  //System.exit(funcl.exitCode); ??

    }//end main

}//end class FunCL
