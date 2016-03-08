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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Get all the files in a folder and its sub-folders
 * @author Behrang
 */
public class GetFiles {

    private List<String> setOfFiles;

    public GetFiles() {
        setOfFiles = new ArrayList<String>();
    }

    public void getCorpusFiles(String parentDir) {
        FilenameFilter filter1 = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        };

        File dir = new File(parentDir);
        if (dir.isDirectory()) {
            File[] dirs = dir.listFiles(filter1);
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i].isFile()) {
                   // if (dir.getAbsolutePath().endsWith("pdf")) {

                    setOfFiles.add(dirs[i].getAbsolutePath());

                    //   }
                } else if (dirs[i].isDirectory()) {
                    getCorpusFiles(dirs[i].toString());
                }
            }
        } else {
            System.err.println("here: " + dir.getAbsolutePath());
        }

    }

    public List<String> getFiles() {
        return setOfFiles;
    }

}
