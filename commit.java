package gitlet;
import java.util.Date;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

public class Commit implements Serializable {
    /** commit message entered by the user. */
    private String _message;

    /** timestamp of when commit was created, default if initial commit. */
    private String _timestamp;

    /** ID of parent commit. */
    private Commit _parent;

    /** files and their SHA1 hash being committed. */
    private TreeMap<String, String> _blobs;

    /** SHA1 hash of this commit. */
    private String _hashCode;

    public Commit(String msg, Commit p, TreeMap<String, String> blobs) {
        this._message = msg;
        Date date;
        if (p == null) {
            date = new Date(0);
        } else {
            date = new Date();
        }
        Format a = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        this._timestamp = a.format(date);
        this._parent = p;
        _blobs = blobs;
        _hashCode = Utils.sha1(Utils.serialize(this));
    }


    /** Returns the commit message for this commit. */
    public String getMessage() {
        return this._message;
    }

    /** Returns the timestamp for this commit. */
    public String getTimestamp() {
        return this._timestamp;
    }

    /** Returns the parents SHA1 hash. */
    public Commit getParent() {
        return this._parent;
    }

    /** Returns the SHA1 hash for this commit. */
    public String getHash() {
        return _hashCode;
    }

    public TreeMap<String, String> getBlobs() {
        return _blobs;
    }


}

