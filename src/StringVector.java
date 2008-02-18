/*
 * @(#)StringVector.java     1.00   2008-01-28
 *
 * Title: StringVector - Vector of String.
 *
 * Description: Dynamic array/vector of String type.
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

public final class StringVector extends Object implements Cloneable
{
    private final static int DELTA = 16;

    private String[] element = null;
    private int count = -1;

    public StringVector()
    {
        this.element = new String[DELTA];
        this.count = 0;
    } //end StringVector

    public void clear()
    {
        this.removeAllElements();
    } //end clear

    private synchronized void removeAllElements()  //clear
    {
        //this.element = null; //??
        for (int i = 0; i < count; i++)
        {
            element[i] = null;
        } //end for
        count = 0;
    } //end removeAllElements

    public final Object clone()
    {
        try
        {
            StringVector clone = (StringVector)super.clone();
            clone.element = (String[])this.element.clone();
            return clone;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new InternalError(ex.toString());
        } //end try
    } //end clone

    public final StringVector copy()
    {
        return (StringVector)this.clone();
    } //end copy

    private final void ensureCapacity(int capacity)
    {
        if (element.length >= capacity)
        {
            return;
        } //end if

        int newCapacity = element.length * 2; //multiple, 16,32,64,128
        String[] newArray = new String[Math.max(newCapacity, capacity)];

        System.arraycopy(element, 0, newArray, 0, count);
        element = newArray;
    } //end ensureCapacity

    public final int size()
    {
        return count;
    } //end size

    public final boolean isEmpty()
    {
        return (count == 0);
    } //end isEmpty

    public final boolean has(String str)
    {
        boolean result = false;

        for(int x=0;x<this.count;x++)
        {
            if(this.element[x].equals(str))
            {
                return true;
            }//end if
        }//end for

        return result;
    }//end has

    public final String get(int index)
    {
        if (index >= count)
        {
            throw new ArrayIndexOutOfBoundsException("StringVector.get(): "+index + " >= " + count);
        } //end if

        return element[index];
    } //end get

    public final String head()
    {
        return this.get(0);
    } //end head

    public final String tail()
    {
        return this.get(count - 1);
    } //end tail

    public final String last(int index) //0= count-1, 1=count-2, 2=count-3
    {
        return this.get(count - 1 - index);
    } //end last

    public final void add(int index, String str)
    {
        if (index > count)
        {
            throw new ArrayIndexOutOfBoundsException("StringVector.add(): "+index + " > " + count);
        } //end if

        if (count == element.length)
        {
            ensureCapacity(count + 1);
        } //end if

        System.arraycopy(element, index, element, index + 1, count - index);
        count++;
        element[index] = str;
    } //end add

    public final void push(String str)
    {
        this.add(0, str);
    }//end push

    public final void add(String str)
    {
        if (count == element.length)
        {
            ensureCapacity(count + 1);
        } //end if

        element[count++] = str;
    } //end add

    public final void append(String[] str)//append array of strings
    {
        for(int x=0;x<str.length;x++)
        {
            this.add(str[x]); //add at 0=>size-1, 1=>size, 2=>size+1, ... ,n=>size+n-1, n+1=>size+n
        }//end for

    }//end append

    public final void prepend(String[] str)
    {
        for(int x=0;x<str.length;x++)
        {
            this.add(x, str[x]); //add at 0=>0, 1=>1, 2=>2, ... n-1=>n-1, n=>n
        }//end for
    }//end prepend

    public final String[] toArray() //deep copy of an array
    {
        String[] newArray = new String[count];
        System.arraycopy(element, 0, newArray, 0, count);
        return newArray;
    } //end toArray

    public final String[] arrayOf() //shallow copy of an array
    {
        return this.element;
    }//end arrayOf

    public final String pop()
    {
        return this.remove(0);
    }//end pop

    public final String remove(int index)
    {
        if (index >= count)
        {
            throw new ArrayIndexOutOfBoundsException("StringVector.remove(): "+index + " >= " + count);
        } //end if

        String temp = (String) element[index];

        count--;
        if (index < count)
        {
            System.arraycopy(element, index + 1, element, index, count - index);
        } //end if

        element[count] = null;
        return temp;
    } //end remove

    public final String toString()
    {
        StringBuffer str = new StringBuffer();

        str.append("[ ");
        for(int x=0;x<this.count;x++)
        {
            str.append(this.element[x]);
            str.append(" ");
        } //end for

        str.append(" ]");
        return str.toString();
    } //end toString

} //end class StringVector
