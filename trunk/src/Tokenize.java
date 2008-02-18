/*
 * @(#)Tokenize.java         1.11   2008-02-16
 *
 * Title: FunCL String Tokenizer.
 *
 * Description: Extract FunCL lexemes or tokens in context.
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

package funcl;

public final class Tokenize
{
    private StringBuffer strtok = null;
    private StringVector tokens = null;

    private String str   = null;
    private int    index = -1;
    private int    chr   = -1;

	public Tokenize(final String string)
	{
            this.strtok = new StringBuffer();
            this.tokens = new StringVector();
            this.str    = string;
            this.index  = 0;
	}//end constructor

	private final boolean hasNextChar()
	{
		return index < this.str.length();
	}//end hasNextChar

        private final boolean isNextCharDigit()
        {
            if(index+1 >= this.str.length()) return false;
            return Character.isDigit(this.str.charAt(index));
        }//end isNextCharDigit

	private final char getNextChar()
	{
                char ch;
                if(this.chr == -1)
                {
                    this.getChar();
                }

                ch = (char) this.chr;
                this.chr = -1;
                return ch;
	}//end getNextChar

	private final void getChar()
	{
                if(index < this.str.length())
                {
                    this.chr = this.str.charAt(index);
                    index++;
                }//end if
	}//end getChar

	private final void unGetChar(char ch)
	{
		this.chr = ch;
	}//end unGetChar

	private final void makeToken()
	{
                if(strtok.length() > 0)
                {
                    String stok = strtok.toString();
                    strtok.delete(0, strtok.length());
                    tokens.add(stok);
                }//end if
	}//end makeToken

	private final String[] tok()
	{
		String[] result = null;
                char     chr    = 0;
                while(this.hasNextChar())
                {

                    chr = this.getNextChar();

                    if (Character.isDigit(chr))
                    {
                        this.strtok.append(chr);
                        this.doNum();
                    }
                    else
                    if (Character.isLetter(chr))
                    {
                        this.strtok.append(chr);
                        this.doId();
                    }
                    else
                    {
                        switch(chr)
                        {
                            case '+' :
                            case '-' :  this.strtok.append(chr);
                                        this.doNum();
                                        break;

                            case ' ' :
                            case '\t':
                            case '\n':
                            case '\r':
                            case '\f':  this.doDelim();
                                        break;

                            case '.' :  this.strtok.append(chr);
                                        this.makeToken();
                                        break;

                            case '"' :  this.strtok.append(chr);
                                        this.doLit2Quote();
                                        break;

                            case '\'':  this.strtok.append(chr);
                                        this.doLit1Quote();
                                        break;

                            default  :  this.strtok.append(chr);
                        }//end switch
                    }//end if
                }//end while

                chr = this.getNextChar();
                if(chr == '.')
                {
                   this.makeToken();
                   this.strtok.append(chr);
                   this.makeToken();
                }//end if

                return this.tokens.toArray();

	}//end tokenize

        private final void doDelim()
        {
            this.makeToken();
        }//end doDelim

        private final void doId()
        {
            char ch = 0;

            while(this.hasNextChar())
            {
                ch = this.getNextChar();

                if(Character.isLetterOrDigit(ch))
                {
                    this.strtok.append(ch);
                }
                else
                if(ch == '\t' || ch == ' ' || ch == '\n' //delimiters
                || ch == '\r' || ch == '\f'|| ch == '.'
                || ch == '\'' || ch == '"' )
                {
                    this.makeToken();
                    this.unGetChar(ch);
                    return;
                }
                else
                {
                    this.strtok.append(ch);
                }//end if

            }//end while
            this.makeToken();
        }//end doId

        private final void doLit2Quote()
        {
            char ch = 0;
            while(this.hasNextChar())
            {
                ch = this.getNextChar();
                this.strtok.append(ch);
                if(ch == '"')
                    break;
            }//end while
            this.makeToken();
        }//end doLit2Quote

        private final void doLit1Quote()
        {
            char ch = 0;
            while(this.hasNextChar())
            {
                ch = this.getNextChar();
                this.strtok.append(ch);
                if(ch == '\'')
                    break;
            }//end while
            this.makeToken();
        }//end doLit1Quote

        private final void doNum()
        {
            char ch = 0;
            boolean dotFlag = false;

            while(this.hasNextChar())
            {
                ch = this.getNextChar();
                if(Character.isDigit(ch))
                {
                    this.strtok.append(ch);
                }
                else
                if(ch == '.')
                {
                    if(dotFlag)
                    {
                        this.makeToken();
                        this.unGetChar(ch);
                        return;
                    }
                    else
                    {
                        if(this.isNextCharDigit())
                        {
                            this.strtok.append(ch);
                            dotFlag = true;
                        }
                        else //no char beyond dot, dot is separate token
                        {
                            this.makeToken();
                            this.unGetChar(ch);
                            return;
                        }
                    }//end if
                }
                else //ch != '.'
                {
                    this.makeToken();
                    this.unGetChar(ch);
                    return;
                }//end if

            }//end while

            this.makeToken();

        }//end doNum

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

        public final static String[] tokenize(final String tokstr)
        {
            return new Tokenize(tokstr).tok();
        }//end tokenize

}//end class Tokenize
