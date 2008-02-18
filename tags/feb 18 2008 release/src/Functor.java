/*
 * @(#)Functor.java          1.00   2008-01-28
 *
 * Title: Funtor - mathematical abstraction of a function.
 *
 * Description: short paragraph description.
 *
 * Author: William F. Gilreath (wgilreath@gmail.com)
 *
 * Copyright (c) 2008  All Rights Reserved.
 *
 * License: This software is subject to the terms of the
 * GNU General Public License  (GPL)  available  at  the
 * following link: http://www.gnu.org/copyleft/gpl.html.
 *
 * You	must accept the terms of the GNU General  Public
 * License license  agreement to use this software.
 *
 */

//add DUMP - dumps state of FunCL VM without logging it

package funcl;

import java.io.File;
import java.math.BigDecimal;

public final class Functor
{
    private Functor() {}

  
    //functor definition constants FNV hash of functor name
    public final static int _PROMPT = -1778443337 ;  //interal command functor ":PROMPT"

    public final static int ADD     = 1234974974  ;
    public final static int DEC     = 378875119   ;
    public final static int DEF     = 378875114   ;
    public final static int _DOT_   = 84696369    ; //period or '.'
    public final static int DUP     = 647316940   ;
    public final static int ECHO    = 713277698   ;
    public final static int ECHOP   = -278342058  ;
    public final static int EQ      = 1819748223  ;
    public final static int EXIT    = 26822469    ;
    public final static int FALSE   = -1842761174 ;
    public final static int FLUSHB  = -1563992815 ;
    public final static int FLUSHC  = -1563992816 ;
    public final static int FLUSHE  = -1563992810 ;
    public final static int FLUSHP  = -1563992829 ;
    public final static int FLUSHX  = -1563992821 ;
    public final static int INC     = 529034919   ;
    public final static int MUL     = 712897237   ;
    public final static int NE      = 1668749446  ;
    public final static int NOT     = 544973840   ;
    public final static int NOP     = 544973844   ;
    public final static int POP     = 1081563622  ;
    public final static int POPX    = 1642235242  ;
    public final static int PUSH    = 1758001241  ;
    public final static int PUSHX   = 1298068579  ;
    public final static int SAY     = 443572614   ;
    public final static int SIZE    = 603889522   ;
    public final static int TRUE    = -1241336767 ;
    public final static int WHEN    = 1889140833  ;

 public final static boolean isInternalFunctor(final String functor)
 {
     final int functorHash = FunCL.fnvhash(functor.toLowerCase());

     switch(functorHash)
     {
         case Functor._DOT_   :
         case Functor._PROMPT :
         case Functor.DUP     :
         case Functor.ADD     :
         case Functor.DEC     :
         case Functor.DEF     :
         case Functor.ECHO    :
         case Functor.ECHOP   :
         case Functor.EQ      :
         case Functor.EXIT    :
         case Functor.FALSE   :
         case Functor.FLUSHB  :
         case Functor.FLUSHC  :
         case Functor.FLUSHE  :
         case Functor.FLUSHP  :
         case Functor.FLUSHX  :
         case Functor.INC     :
         case Functor.MUL     :
         case Functor.NE      :
         case Functor.NOP     :
         case Functor.NOT     :
         case Functor.POP     :
         case Functor.POPX    :
         case Functor.PUSH    :
         case Functor.PUSHX   :
         case Functor.SAY     :
         case Functor.SIZE    :
         case Functor.TRUE    :
         case Functor.WHEN    :
                                return true;

         default              : return false;

     }//end switch

 }//end isInternalFunctor

    public final static boolean isExternalFunctor(final String functor)
    {
        //check for external functor: functor + ".func" (or .fcl??)
        File functorFile = new File(functor+FunCLConst.FUNCL_EXTENSION);
        return functorFile.exists();
    }//end isExternalFunctor

    public final static boolean isFunctor(final String functor)
    {
        return Functor.isInternalFunctor(functor) || Functor.isExternalFunctor(functor);
    }//end isFunctor

    // [+|-](digit)+ [ [.] (digit)+ ]
    public final static boolean isNum(final String str) //implement FSM for number
    {
        final int START  = 0;
        final int SIGN   = 1;
        final int FDIGIT = 2;
        final int DOT    = 3;
        final int LDIGIT = 4;

        int     state  = START;
        boolean result = true;

        for(int x=0;x<str.length();x++)
        {
            char chr = str.charAt(x);

            switch(state)
            {
                case DOT:
                    switch(chr)
                    {
                         case '+':
                         case '-': return false;

                         case '0':
                         case '1':
                         case '2':
                         case '3':
                         case '4':
                         case '5':
                         case '6':
                         case '7':
                         case '8':
                         case '9': state = LDIGIT; continue;

                         case '.': return false;

                         default : return false;
                    }//end switch DOT

                case FDIGIT:
                    switch(chr)
                    {
                        case '+':
                        case '-': return false;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': state = FDIGIT; continue;

                        case '.': state = DOT;    continue;

                        default:  return false;
                    }//end switch FDIGIT

                case LDIGIT:
                    switch(chr)
                    {
                        case '+':
                        case '-': return false;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': state = LDIGIT; continue;

                        case '.': return false;

                        default : return false;
                    }//end switch LDIGIT

                case SIGN:
                    switch(chr)
                    {
                        case '+':
                        case '-':  return false;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': state = FDIGIT; continue;

                        case '.': return false;

                        default : return false;
                    }//end switch SIGN

                case START:
                    switch(chr)
                    {
                        case '+':
                        case '-': state = SIGN; continue;

                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': state = FDIGIT; continue;

                        case '.': return false;

                        default : return false;
                    }//end switch START

             }//end switch

        }//end for

        return result;

    }//end isNum

    public final static void DEF(FunCL funcl)
    {
        //check param stack
        if(funcl.paramStack.size() == 1)
        {
            //check if file exists, else error unless clobber? 12-16-2007
            funcl.openWriterFile(funcl.paramStack.remove(0)+FunCLConst.FUNCL_EXTENSION);
            funcl.writeEvalToFile();

            funcl.defineFlag = true;
        }
        else
        {
            FunCL.err("\n\r");
            FunCL.errln("Functor Error: DEF define functor requires 1-parameter.");
            FunCL.err("\n\r");
        }//end if

    }//end DEF

    public final static void DUP(FunCL funcl)
    {
       if(funcl.paramStack.isEmpty())
       {
           FunCL.errln("Functor Error: DUP functor param stack is empty.");
       }
       else
       {
           String top = funcl.paramStack.get(0);
           funcl.paramStack.add(0, top);
       }//end if
    }//end DUP

    public final static void NOP(FunCL funcl)
    {
        ; //nop
    }//end NOP

    public final static void _PROMPT(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: :PROMPT functor - param stack is empty so cannot change prompt.");
            return;
        }//end if

        String prompt = funcl.paramStack.remove(0);

        prompt = FunCL.trimLit(prompt); //remove literal

        funcl.prompt  = prompt;

    }//end _PROMPT

    public final static void FLUSHB(FunCL funcl)
    {
        funcl.boolStack.clear();
    }//end FLUSHB

    public final static void FLUSHC(FunCL funcl)
    {
        funcl.clauseStack.clear();
    }//end FLUSHC

    public final static void FLUSHE(FunCL funcl)
    {
        funcl.evalStack.clear();
    }//end FLUSHE

    public final static void FLUSHP(FunCL funcl)
    {
        funcl.paramStack.clear();
    }//end FLUSHP

    public final static void FLUSHX(FunCL funcl)
    {
        funcl.exprStack.clear();
    }//end FLUSHX

    public final static void POP(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            return;
        }
        else
        {
            funcl.paramStack.remove(0);
        }//end if

    }//end POP

    public final static String TRUE_STR  = "true";
    public final static String FALSE_STR = "false";

    public final static void TRUE_(FunCL funcl)
    {
        funcl.boolStack.add(0, TRUE_STR);
    }//end TRUE_

    public final static void FALSE_(FunCL funcl)
    {
        funcl.boolStack.add(0, FALSE_STR);
    }//end FALSE_


    public final static void PUSHX(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: PUSHX functor param stack is empty.");
        }
        else
        {
            String top = funcl.paramStack.pop();
            funcl.exprStack.push(top);
        }//end if

    }//end PUSHX

    public final static void POPX(FunCL funcl)
    {
        if(funcl.exprStack.isEmpty())
        {
            FunCL.errln("Functor Error: POPX functor param stack is empty.");
        }
        else
        {
            String top = funcl.exprStack.pop();
            funcl.paramStack.add(0, top);
        }//end if

    }//end POPX

    public final static void EQ(FunCL funcl)
    {
        if(funcl.paramStack.size() >= 2)
        {
            int hash0 = FunCL.fnvhash(funcl.paramStack.remove(0));
            int hash1 = FunCL.fnvhash(funcl.paramStack.remove(0));

            if(hash0 == hash1)
            {
                funcl.boolStack.push(Functor.TRUE_STR);
            }
            else
            {
                funcl.boolStack.push(Functor.FALSE_STR);
            }//end if
        }
        else
        {
            FunCL.errln("Functor Error: EQ equal functor requires 2-parameters.");
        }//end if
    }//end EQ

    public final static void NE(FunCL funcl)
    {
        if(funcl.paramStack.size() >= 2)
        {
            int hash0 = FunCL.fnvhash(funcl.paramStack.remove(0));
            int hash1 = FunCL.fnvhash(funcl.paramStack.remove(0));

            if(hash0 != hash1)
            {
                funcl.boolStack.push(Functor.TRUE_STR);
            }
            else
            {
                funcl.boolStack.push(Functor.FALSE_STR);
            }//end if
        }
        else
        {
            FunCL.errln("Functor Error: NE not equal functor requires 2-parameters.");
        }//end if
    }//end NE

    public final static void ECHO(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            FunCL.putln("");
        }
        else
        {
            FunCL.putln(funcl.paramStack.remove(0));
        }//end if
    }//end ECHO

    public final static void SAY(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            FunCL.putln("");
        }
        else
        {
            FunCL.putln(funcl.paramStack.get(0));
        }//end if
    }//end SAY

    public final static void EXIT(FunCL funcl)
    {
        funcl.exitFlag = true;
    }//end EXIT

    public final static void ECHOP(FunCL funcl)
    {
        if(funcl.paramStack.isEmpty())
        {
            FunCL.putln("");
            return;
        }//end if

        final int size = funcl.paramStack.size();

        for(int x=0;x<size;x++)
        {
            FunCL.putln(funcl.paramStack.remove(0));
        }//end for

    }//end ECHOP

    public final static void SIZE(FunCL funcl)
    {
        final int size = funcl.paramStack.size();

        funcl.paramStack.push(Integer.toString(size));

    }//end SIZEP

    public final static void MUL(FunCL funcl)
    {
        BigDecimal m0;
        BigDecimal m1;
        BigDecimal r;

        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: MUL multiply functor requires at least 1-parameter.");
            return;
        }//end if

        if(funcl.paramStack.size() == 1)
        {
            if(!Functor.isNum(funcl.paramStack.get(0)))
            {
                FunCL.errln("Functor Error: MUL multiply functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
            }//end if
            return; //multiply single number => return number on param stack
        }//end if

        if(Functor.isNum(funcl.paramStack.get(0)))
        {
            m0 = new BigDecimal(funcl.paramStack.remove(0));
            if(Functor.isNum(funcl.paramStack.get(0)))
            {
                  m1 = new BigDecimal(funcl.paramStack.remove(0));
                  r  = m0.multiply(m1);
                  funcl.paramStack.push(r.toString());
            }
            else
            {
                  FunCL.errln("Functor Error: MUL multiply functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
            }//end if
        }
        else
        {
            FunCL.errln("Functor Error: MUL multiply functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
        }//end if

    }//end MUL

    public final static void ADD(FunCL funcl)
    {
        BigDecimal m0;
        BigDecimal m1;
        BigDecimal r;

        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: ADD addition functor: param stack is empty!");
            return;
        }//end if

        if(funcl.paramStack.size() == 1)
        {
            if(!Functor.isNum(funcl.paramStack.get(0)))
            {
                FunCL.errln("Functor Error: ADD addition functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
            }//end if
            return; //multiply single number => return number on param stack
        }//end if

        if(Functor.isNum(funcl.paramStack.get(0)))
        {
            m0 = new BigDecimal(funcl.paramStack.remove(0));
            if(Functor.isNum(funcl.paramStack.get(0)))
            {
                  m1 = new BigDecimal(funcl.paramStack.remove(0));
                  r  = m0.add(m1);
                  funcl.paramStack.push(r.toString());
            }
            else
            {
                  FunCL.errln("Functor Error: ADD addition functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
            }//end if
        }
        else
        {
            FunCL.errln("Functor Error: ADD addition functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
        }//end if

    }//end ADD

    private final static BigDecimal ONE_BIG_DEC = new BigDecimal("1");

    public final static void INC(FunCL funcl)
    {
        BigDecimal m0;
        BigDecimal r;

        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: INC increment functor: param stack is empty!");
            return;
        }//end if

        if(funcl.paramStack.size() == 1)
        {
            if(!Functor.isNum(funcl.paramStack.get(0)))
            {
                FunCL.errln("Functor Error: INC increment functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
                return;
            }//end if
         }//end if

        if(Functor.isNum(funcl.paramStack.get(0)))
        {
            m0 = new BigDecimal(funcl.paramStack.remove(0));
            r  = m0.add(Functor.ONE_BIG_DEC);
            funcl.paramStack.add(0, r.toString());
        }
        else
        {
            FunCL.errln("Functor Error: INC increment functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
        }//end if

    }//end INC

    public final static void DEC(FunCL funcl)
    {
        BigDecimal m0;
        BigDecimal r;

        if(funcl.paramStack.isEmpty())
        {
            FunCL.errln("Functor Error: DEC decrement functor: param stack is empty!");
            return;
        }//end if

        if(funcl.paramStack.size() == 1)
        {
            if(!Functor.isNum(funcl.paramStack.get(0)))
            {
                FunCL.errln("Functor Error: DEC decrement functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
                return;
            }//end if
         }//end if

        if(Functor.isNum(funcl.paramStack.get(0)))
        {
            m0 = new BigDecimal(funcl.paramStack.remove(0));
            r  = m0.subtract(Functor.ONE_BIG_DEC);
            funcl.paramStack.add(0, r.toString());
        }
        else
        {
            FunCL.putln("Functor Error: DEC decrement functor parameter: "+funcl.paramStack.get(0)+" is not a number!");
        }//end if

    }//end DEC

    public final static void WHEN(FunCL funcl)
    {
        if(funcl.boolStack.isEmpty())
        {
            FunCL.errln("Functor Error: WHEN conditional functor no Boolean on the bool stack.");
        }//end if

        String boolVal = funcl.boolStack.pop();

        if(boolVal == Functor.FALSE_STR)
        {
            funcl.evalStack.clear();
        }//end if

    }//end WHEN

    public final static void NOT(FunCL funcl)
    {
        if(funcl.boolStack.isEmpty())
        {
            FunCL.errln("Functor Error: NOT logical functor no Boolean on the bool stack.");
        }//end if

        String boolVal = funcl.boolStack.pop();

        if(boolVal == Functor.FALSE_STR)
        {
            funcl.boolStack.push(Functor.TRUE_STR);
        }
        else
        {
            funcl.boolStack.push(Functor.FALSE_STR);
        }//end if

    }//end WHEN

/*
        public final static void MATHXXX(FunCL funcl)
        {
            BigDecimal m0;
            BigDecimal m1;
            BigDecimal r;

            if(funcl.paramStack.isEmpty())
            {
                FunCL.errln("Error in xxx: param stack is empty!");
                return;
            }//end if

            if(funcl.paramStack.size() == 1)
            {
                if(!Functor.isNum(funcl.paramStack.get(0)))
                {
                    FunCL.errln("Error in xxx: "+funcl.paramStack.get(0)+" is not a number!");
                }//end if

                return; //multiply single number => return number on param stack
            }//end if

            if(Functor.isNum(funcl.paramStack.get(0)))
            {
                m0 = new BigDecimal(funcl.paramStack.remove(0));
                if(Functor.isNum(funcl.paramStack.get(0)))
                {
                      m1 = new BigDecimal(funcl.paramStack.remove(0));
                      r  = m0.xxx(m1);
                      funcl.paramStack.push(r.toString());
                }
                else
                {
                      FunCL.errln("Error in xxx: "+funcl.paramStack.get(0)+" is not a number!");
                }//end if
            }
            else
            {
                FunCL.errln("Error in xxx: "+funcl.paramStack.get(0)+" is not a number!");
            }//end if

        }//end MATHXXX
//*/

/*
        public final static void XXX(FunCL funcl)
        {

        }//end XXX
//*/

}//end class Functor
