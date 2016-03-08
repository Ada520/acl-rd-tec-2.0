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
package info.atmykitchen.common;

import java.util.TreeMap;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 * @param <T>
 */
public class MapCounter<T> extends TreeMap<T, Integer> {

    //TreeMap<T, Integer> counterMap 
    public MapCounter() {
        super();
    }

    public void add(T t) {
        if (this.containsKey(t)) {
            Integer get = this.get(t);
            this.put(t, ++get);
        } else {
            this.put(t, 1);
        }
    }

    public int getCount(T t) {
        if (this.containsKey(t)) {
            return this.get(t);
        } else {
            return 0;
        }
    }

    public void add(TreeMap<T, Integer> tm) {

        for (T t : tm.keySet()) {
            if (this.containsKey(t)) {
                Integer get = this.get(t) + tm.get(t);
                this.put(t, get);
            } else {
                this.put(t, 1);
            }

        }
    }
}
