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

import java.util.Comparator;

/**
 *
 * @author bq
 */
public class Annotation implements  Comparable<Annotation>{

    private int startOffsetSentence;
    private int sentenceNumber;
    private String type;
    private String content;

    public Annotation(int startOffsetSentence, int sentenceNumber, String type, String content) {
        this.startOffsetSentence = startOffsetSentence;
        this.sentenceNumber = sentenceNumber;
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public int getStartOffsetSentence() {
        return startOffsetSentence;
    }

    public String getType() {
        return type;
    }

    public static Comparator<Annotation> sentnceOrderComp() {
        return new Comparator<Annotation>() {
            @Override
            public int compare(Annotation a, Annotation b) {
                return Integer.compare(a.startOffsetSentence, b.startOffsetSentence);
            }
        };
    }

    public void updateSentenceNumber(int sentenceNumber) {
        this.sentenceNumber = sentenceNumber;
    }
    
    
    public boolean hasSameBoundaryAs(Annotation b) {
        if (this.sentenceNumber == b.sentenceNumber & 
                this.startOffsetSentence == b.startOffsetSentence 
                & this.content.length() == b.content.length()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEqual(Annotation b) {
        if (b.type.equals(this.type) && hasSameBoundaryAs(b)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public int compareTo(Annotation t) {
        if (this.isEqual(t)) {
            return 0;
        } else if (this.hasSameBoundaryAs(t)) {
            return this.type.compareTo(t.type);
        } else {
            return Annotation.sentnceOrderComp().compare(this, t);
        }
    }

    
    

}
