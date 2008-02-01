/*
 * @(#)ObjectVector.java   1.00   2008-01-28
 *
 * Title: ObjectVector - Vector of Object.
 *
 * Description: Dynamic array/vector of Object type.
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

public final class ObjectVector extends Object
{
    private final static int DELTA = 16;

    private Object[] element;
    private int      count;

    public ObjectVector()
    {
        this.element   = new Object[DELTA];
        this.count     = 0;
    }//end ObjectVector

    private final void ensureCapacity(final int capacity)
    {
        if (element.length >= capacity)
        {
            return;
        }//end if

        int      newCapacity = element.length + DELTA;
        Object[] newArray    = new Object[newCapacity];

        System.arraycopy(element, 0, newArray, 0, count);
        element = newArray;
    }//end ensureCapacity

    private final void resize(final int capacity)
    {
        if (element.length > capacity)
        {
            return;
        }//end if

        Object[] newArray    = new Object[capacity];

        System.arraycopy(element, 0, newArray, 0, count);
        element = newArray;
    }//end resize

    public final int size()
    {
        return count;
    }//end size

    public final boolean isEmpty()
    {
        return (count == 0);
    }//end isEmpty

    public final Object get(final int index)
    {
        if (index >= count)
        {
            throw new ArrayIndexOutOfBoundsException("ObjectVector.get(): "+this.getClass().getName()+"."+new Exception().getStackTrace()[0].getMethodName()+" "+index + " >= " + count+" index exceeds capacity.");
        }//end if

        return element[index];
    }//end get

    public final Object head()
    {
        return this.get(0);
    }//end head

    public final Object tail()
    {
        return this.get(count-1);
    }//end tail

   public final Object last(final int index)  //0= count-1, 1=count-2, 2=count-3
   {
       return this.get(count - 1 - index);
   }//end last

   public final void add(final int index, final Object obj)
   {
        if (index > count)
        {
            throw new ArrayIndexOutOfBoundsException("ObjectVector.add(): "+this.getClass().getName()+"."+new Exception().getStackTrace()[0].getMethodName()+" "+index + " >= " + count+" index exceeds capacity.");
        }//end if

        if (count == element.length)
        {
            ensureCapacity(count + 1);
        }//end if

        System.arraycopy(element, index, element, index + 1, count - index);
        count++;
        element[index] = obj;

    }//end add

    public final void add(final Object obj)
    {
        if (count == element.length)
        {
            ensureCapacity(count + 1);
        }//end if

        element[count++] = obj;
    }//end add

    public final Object[] toArray()
    {
        Object[] newArray = new Object[count];
        System.arraycopy(element, 0, newArray, 0, count);

        return newArray;
    }//end toArray

    public final Object remove(final int index)
    {
        if (index >= count)
        {
            throw new ArrayIndexOutOfBoundsException("ObjectVector.remove(): "+this.getClass().getName()+"."+new Exception().getStackTrace()[0].getMethodName()+" "+index + " >= " + count+" index exceeds capacity.");
        }//end if

        Object temp = element[index];

        count--;
        if (index < count)
        {
            System.arraycopy(element, index + 1, element, index,count - index);
        }//end if

        element[count] = null;
        return temp;
    }//end remove

    public final String toString()
    {
        StringBuffer str = new StringBuffer();

        str.append("< ");
        for(int x=0;x<this.count-1;x++)
        {
            str.append(element[x]);
            str.append(", ");
        }//end for
        str.append(element[this.count-1]);
        str.append(" >");
        return str.toString();

    }//end toString

}//end class ObjectVector
