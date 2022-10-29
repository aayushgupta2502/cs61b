package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class StagingArea implements Serializable {
    /** Hashmap of files to be added. */
    private HashMap<String, String> stagedForAddition;
    /** Files to be removed. */
    private HashSet<String> stagedForRemoval;

    StagingArea() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new HashSet<>();
    }

    /** Adds a file to the stagedForAddition array.
     * @param file F
     * @param blobHash B */
    public void add(String file, String blobHash) {
        stagedForAddition.put(file, blobHash);
    }

    /** Adds a file to the stagedForRemoval array.
     * @param s S */
    public void addToRemove(String s) {
        stagedForRemoval.add(s);
    }

    public void clear() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
    }

    public HashMap<String, String> getAdded() {
        return stagedForAddition;
    }

    public HashSet<String> getRemoved() {
        return stagedForRemoval;
    }


}
