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
 * @author bq
 */
public class TypeAgreement {
 
    String classTrm;
    double poType; // observed agreement for this type
    double peType; // agreement by chance for this type

    public TypeAgreement(String classTrm, double poType, double peType) {
        this.classTrm = classTrm;
        this.poType = poType;
        this.peType = peType;
    }
    
    
    
    
}
