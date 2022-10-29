package gitlet;

import java.io.File;
import java.io.Serializable;;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;
import java.util.TreeMap;


public class SomeObject2 implements Serializable {
    /** Represents the current working directory path. */
    private static File _cwd = new File(System.getProperty("user.dir"));
    /** Represents the staging area object in which stages will be tracked. */
    private StagingArea _stage;
    /** Represents the main gitlet folder. */
    private static File _gitlet = Utils.join(_cwd, ".gitlet");
    /** Represents the branches directory. */
    private static File _branchesDir = Utils.join(_gitlet, "branches");
    /** Represents the commits directory. */
    private static File _commitsDir = Utils.join(_gitlet, "commits");
    /** Represents the blobs directory. */
    private static File _blobsDir = Utils.join(_gitlet, "blobs");
    /** Represents the staging directory. */
    private static File _stagingDir = Utils.join(_gitlet, "staging");
    /** File to record stages. */
    private File stage;
    /** A pointer to the current commit object. */
    private Commit _HEAD;
    /** A HashMap to track branches. */
    private HashMap<String, String> trackBranches;
    /** A HashSet to track commits by their SHA1 hash. */
    private HashSet<String> trackCommits;
    /** A pointer to the current branch. */
    private String _MASTER;

    public SomeObject2() {
        stage = Utils.join(_stagingDir, "stage.txt");

        if (stage.exists()) {
            _stage = Utils.readObject(stage, StagingArea.class);
        }

        trackBranches = new HashMap<>();
        trackCommits = new HashSet<>();
    }

    public void init() {
        if (_gitlet.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists" + " in the current directory.");
            System.exit(0);
        } else {
            _gitlet.mkdir();
            _commitsDir.mkdir();
            _branchesDir.mkdir();
            _blobsDir.mkdir();
            _stagingDir.mkdir();
            _stage = new StagingArea();
            Utils.writeObject(stage, _stage);

            TreeMap<String, String> initial = new TreeMap<>();
            Commit first = new Commit("initial commit", null, initial);
            saveCommit(first);

            _HEAD = first;

            trackCommits.add(first.getHash());
            trackBranches.put("master", first.getHash());
            _MASTER = "master";

        }
    }

    public void saveCommit(Commit c) {
        String hash = c.getHash();
        File f = Utils.join(_commitsDir, hash + ".txt");
        Utils.writeObject(f, c);
    }

    public void add(String filename) {
        File nf = new File(filename);
        if (!nf.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            byte[] byteBlob = Utils.readContents(nf);
            String blobHash = Utils.sha1(byteBlob);

            String x = _HEAD.getBlobs().get(filename);

            if (x != null && x.equals(blobHash)) {
                if (_stage.getAdded().containsKey(filename)) {
                    _stage.getAdded().remove(filename);
                }
                if (_stage.getRemoved().contains(filename)) {
                    _stage.getRemoved().remove(filename);
                }
                Utils.writeObject(stage, _stage);
                return;

            }
            Utils.writeContents(Utils.join(_blobsDir,
                    blobHash + ".txt"), byteBlob);
            _stage.add(filename, blobHash);
            _stage.getRemoved().remove(filename);
            Utils.writeObject(stage, _stage);

        }
    }


    public void commit(String message) {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (_stage.getAdded().isEmpty() && _stage.getRemoved().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit nc = _HEAD;
        TreeMap<String, String> newBlobs = nc.getBlobs();

        for (String f : _stage.getAdded().keySet()) {
            String blobHash = _stage.getAdded().get(f);
            newBlobs.put(f, blobHash);
        }

        for (String f : _stage.getRemoved()) {
            newBlobs.remove(f);
        }

        _stage.clear();
        Utils.writeObject(stage, _stage);

        Commit newCommit = new Commit(message, nc, newBlobs);
        _HEAD = newCommit;
        trackCommits.add(newCommit.getHash());
        trackBranches.put(_MASTER, newCommit.getHash());
        saveCommit(newCommit);

    }

    public void checkout1(String filename) {
        Commit c = findPrevious();
        checkoutHelper(c, filename);
    }

    public void checkout2(String id, String filename) {
        if (!Utils.plainFilenamesIn(_commitsDir).contains(id + ".txt")) {
            System.out.println("No such commit exists.");
            System.exit(0);
        } else {
            File commitFile = Utils.join(_commitsDir, id + ".txt");
            Commit c = Utils.readObject(commitFile, Commit.class);

            checkoutHelper(c, filename);
        }

    }

    public void checkout3(String branch) {
        if (!trackBranches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branch.equals(_MASTER)) {
            System.out.println("No need to checkout the current branch.");
        }

        String newHash = trackBranches.get(branch);
        Commit nc = Utils.readObject(Utils.join(_commitsDir,
                newHash + ".txt"), Commit.class);

        TreeMap<String, String> newBlobs = nc.getBlobs();

        for (String file : Utils.plainFilenamesIn(_cwd)) {
            if (!_HEAD.getBlobs().containsKey(file)
                    && newBlobs.containsKey(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String file : Utils.plainFilenamesIn(_cwd)) {
            if (!newBlobs.containsKey(file)
                    && _HEAD.getBlobs().containsKey(file)) {
                Utils.restrictedDelete(file);
            }
        }

        for (String file : newBlobs.keySet()) {
            String hash = newBlobs.get(file);
            String temp = Utils.readContentsAsString
                    (Utils.join(_blobsDir, hash + ".txt"));
            Utils.writeContents(new File(file), temp);
        }
        _stage.clear();
        Utils.writeObject(stage, _stage);
        _HEAD = nc;
        _MASTER = branch;
    }

    public void checkoutHelper(Commit c, String filename) {
        TreeMap<String, String> files = c.getBlobs();
        if (!files.containsKey(filename)) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            File temp = Utils.join(_cwd, filename);
            String blobHash = c.getBlobs().get(filename);
            File nf = Utils.join(_blobsDir, blobHash + ".txt");
            String contents = Utils.readContentsAsString(nf);
            Utils.writeContents(temp, contents);
        }
    }

    public Commit findPrevious() {
        return _HEAD;
    }


    public void log() {
        Commit c = _HEAD;
        while (c != null) {
            System.out.println("===");
            System.out.println("commit "  + c.getHash());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println();

            c = c.getParent();

        }

    }

    public void rm(String filename) {

        if (_stage.getAdded().containsKey(filename)) {
            _stage.getAdded().remove(filename);
            Utils.writeObject(stage, _stage);
            return;
        }

        Commit c = _HEAD;

        if (c.getBlobs().containsKey(filename)) {
            Utils.restrictedDelete(filename);
            _stage.addToRemove(filename);
            _stage.getAdded().remove(filename);
            Utils.writeObject(stage, _stage);
        } else if (_stage.getAdded().containsKey(filename)) {
            _stage.getAdded().remove(filename);
            Utils.writeObject(stage, _stage);
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    public void globalLog() {
        for (String hash: Utils.plainFilenamesIn(_commitsDir)) {
            File newFile = Utils.join(_commitsDir, hash);
            Commit c = Utils.readObject(newFile, Commit.class);
            System.out.println("===");
            System.out.println("commit " + c.getHash());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println();
        }

    }

    public void find(String commitMessage) {
        boolean found = false;
        for (String hash : Utils.plainFilenamesIn(_commitsDir)) {
            File newFile = Utils.join(_commitsDir, hash);
            Commit c = Utils.readObject(newFile, Commit.class);
            if (c.getMessage().equals(commitMessage)) {
                found = true;
                System.out.println(c.getHash());
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
        }

    }

    public void status() {
        System.out.println("=== Branches ===");
        ArrayList<String> branches = new ArrayList<>(trackBranches.keySet());
        Collections.sort(branches);
        for (String branch : branches) {
            if (branch.equals(_MASTER)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        ArrayList<String> staged = new ArrayList<>(_stage.getAdded().keySet());
        Collections.sort(staged);
        for (String filename : staged) {
            System.out.println(filename);
        }
        System.out.println();

        ArrayList<String> removed = new ArrayList<>(_stage.getRemoved());
        Collections.sort(removed);

        System.out.println("=== Removed Files ===");
        for (String f : removed) {
            System.out.println(f);

        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();

    }

    public void branch(String branch) {
        if (trackBranches.containsKey(branch)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        } else {
            trackBranches.put(branch, _HEAD.getHash());
        }

    }

    public void rmbranch(String branch) {
        if (_MASTER.equals(branch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        } else if (!trackBranches.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else {
            trackBranches.remove(branch);
        }
    }

    public void reset(String commitID) {
        File commit = Utils.join(_commitsDir, commitID + ".txt");
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Commit nc = Utils.readObject(commit, Commit.class);
        Commit c = _HEAD;

        TreeMap<String, String> newBlobs = nc.getBlobs();
        TreeMap<String, String> currentBlobs = c.getBlobs();

        for (String file : Utils.plainFilenamesIn(_cwd)) {
            if (!currentBlobs.containsKey(file) && newBlobs.containsKey(file)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first");
                System.exit(0);
            }
        }

        for (String file : newBlobs.keySet()) {
            String blobHash = newBlobs.get(file);
            if (!blobHash.equals(c.getBlobs().get(file))) {
                File blobFile = Utils.join(_blobsDir, blobHash + ".txt");
                String blob = Utils.readContentsAsString
                        (Utils.join(_blobsDir, blobHash + ".txt"));
                File nf = Utils.join(_cwd, blobHash + ".txt");
                Utils.writeContents(nf, blob);

            }
        }

        for (String file : Utils.plainFilenamesIn(_cwd)) {
            if (!newBlobs.containsKey(file) && currentBlobs.containsKey(file)) {
                Utils.restrictedDelete(file);
            }
        }

        _stage.clear();
        Utils.writeObject(stage, _stage);
        _HEAD = nc;
        trackBranches.put(_MASTER, c.getHash());
    }

}
