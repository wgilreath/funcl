import java.io.*;

/*
 * @(#)FNVHash.java   1.00   2008-01-28
 *
 * Title: FNVHash - Compute FNV hash code for String as 32-bit primitive int.
 *
 * Description: Compute hash for a string using FNV hash algorithm. Returns
 * Java code for constant static declaration as a primitive int for the string.
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

public final class FNVHash
{
    private final static int INIT = 0x811c9dc5;
    private int              hash = 0;

    public FNVHash(){}

  public final void clear()
  {
      this.hash = 0;
  }//end constructor

  public final void init(String s)
  {
    byte[] buf = null;
    try
    {
      buf = s.getBytes("UTF-8");
    }
    catch (Exception e)
    {
      buf = s.getBytes();
    }//end try

    init(buf, 0, buf.length);
  }//end init

  private final void init(byte[] buf, int offset, int len)
  {
    hash = fnv(buf, offset, len, INIT);
  }//end init

  public final int getHash(String str)
  {
  	this.init(str);
  	int result = this.hash;
  	this.hash = 0; //clear
  	return result;
  }//end getHash

  private final int fnv(byte[] buf, int offset, int len, int seed)
  {
    for (int i = offset; i < offset + len; i++)
    {
      seed += (seed << 1) + (seed << 4) + (seed << 7) + (seed << 8) + (seed << 24);
      seed ^= buf[i];
    }//end for

    return seed;

  }//end fnv

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

  public final static void main(String[] arg)
  {

  	    FNVHash hash = new FNVHash();

        if(arg.length == 0) System.exit(1);

        System.out.println();
        java.util.Arrays.sort(arg);

        for(int x=0;x<arg.length;x++)
        {
            //System.out.println("FNVHash: " + arg[x].toLowerCase() + " = " + hash.getHash(arg[x].toLowerCase()));
            //System.out.println();
            //System.out.println("public final static int " + arg[x].toUpperCase() + " = " + hash.getHash(arg[x]) + " ;");
            System.out.println("public final static int " + arg[x].toUpperCase() + " = " + fnvhash(arg[x]) + " ;");

        }//end for

        System.out.println();

     	System.exit(0);

  }//end main


}//end class FNVHash
