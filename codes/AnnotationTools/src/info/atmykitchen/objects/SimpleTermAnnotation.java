/* 
 * Copyright (C) 2016 Behrang QasemiZadeh <zadeh at phil.hhu.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.atmykitchen.objects;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class SimpleTermAnnotation implements Comparable<SimpleTermAnnotation> {

    private final String annClass;
    private final String termString;

    public SimpleTermAnnotation(String annClass, String termString) {
        this.annClass = annClass;
        this.termString = termString;
    }

    public String getAnnClass() {
        return annClass;
    }

    public String getTermString() {
        return termString;
    }

    /**
     * to use for sorting
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleTermAnnotation) {
            SimpleTermAnnotation oS = (SimpleTermAnnotation) o;
            if (this.annClass.equals(oS.annClass) && this.termString.equals(oS.termString)) {
                return true;
            }
        }
        return false;

    }

    @Override
    public int compareTo(SimpleTermAnnotation t) {

        int cmpStr = this.termString.compareTo(t.termString);
        int cmpStr2 = this.annClass.compareTo(t.annClass);
        if (cmpStr == 0 && cmpStr2 == 0) {
            return 0;
        } else if (cmpStr != 0) {
            return cmpStr;
        } else {
            return cmpStr2;
        }
    }

    @Override
    public String toString() {
        return this.termString+"\t"+this.annClass;
    }
    
    

}
