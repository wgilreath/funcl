/*
 * @(#)FunCLConst.java       1.01   2008-02-16
 *
 * Title: FunCL - Functor Clause Language.
 *
 * Description: FunCL interpreter constants centralized in one class.
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

public class FunCLConst 
{
    private FunCLConst(){}

    //interpreter defaults
    public final static String DEFAULT_PROMPT  = ":>";
    public final static String FUNCL_EXTENSION = ".fncl"; 

    //header information
    public final static String TITLE     = "FunCL: The Functor Clause Language interpreter. ";
    public final static String VERSION   = "Version Alpha 0.500";
    public final static String COPYRIGHT = "Copyright (c) February 2008 ";
    public final static String AUTHOR    = "William Gilreath (wgilreath@gmail.com)  ";
    public final static String LICENSE   = "Released under the terms of the GNU General Public License (GPL).";

    //begin-finish messages
    public final static String FUNCL_MESSAGE_START = "Start FunCL interpreter.";
    public final static String FUNCL_MESSAGE_CLOSE = "Close FunCL interpreter.";
    
    //command-line options
    public final static String OPTION_DUMP   = "-dump";
    public final static String OPTION_LOGERR = "-logerr";
    public final static String OPTION_TRACE  = "-trace";
    
    //external log file names
    public final static String FILE_ERROR_LOG = "error.log";
    public final static String FILE_TRACE_LOG = "trace.log";
    
    //state.log for FunCL VM dump for each evaluation
    
    
    
}//end class FunCLConst
