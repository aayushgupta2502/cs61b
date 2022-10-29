package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Aayush Gupta
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    private static File repo = Utils.join(Utils.join(System.getProperty
                    ("user.dir"), ".gitlet"), "repo.txt");
    public static void main(String... args) throws IOException {
        checkArgs(args);
        if (args[0].equals("init")) {
            validateNumArgs(1, args);
            SomeObject2 bloop = new SomeObject2();
            bloop.init();
            Utils.writeObject(repo, bloop);
        } else {
            if (!(new File(".gitlet")).exists()) {
                System.out.println("Not in an initialized Gitlet directory");
                System.exit(0);
            }
            SomeObject2 bloop = Utils.readObject(repo, SomeObject2.class);
            switch (args[0]) {
            case "init":
                validateNumArgs(1, args); bloop.init();
                repo.createNewFile();
                break;
            case "add":
                validateNumArgs(2, args); bloop.add(args[1]);
                break;
            case "commit":
                validateNumArgs(2, args); bloop.commit(args[1]);
                break;
            case "checkout":
                checkoutHelp(bloop, args);
                break;
            case "log":
                validateNumArgs(1, args); bloop.log();
                break;
            case "rm":
                validateNumArgs(2, args); bloop.rm(args[1]);
                break;
            case "global-log":
                validateNumArgs(1, args); bloop.globalLog();
                break;
            case "find":
                validateNumArgs(2, args); bloop.find(args[1]);
                break;
            case "status":
                validateNumArgs(1, args); bloop.status();
                break;
            case "branch":
                validateNumArgs(2, args); bloop.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(2, args); bloop.rmbranch(args[1]);
                break;
            case "reset":
                validateNumArgs(2, args); bloop.reset(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
            }
            Utils.writeObject(repo, bloop);
        }
    }

    public static void validateNumArgs(int required, String... args) {
        if (required != args.length) {
            System.out.println("Incorrect operands");
            System.exit(0);
        }
    }

    public static void checkArgs(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

    public static void checkoutHelp(SomeObject2 bloop, String... args) {
        if (args.length == 3) {
            bloop.checkout1(args[2]);
        } else if (args.length == 4) {
            bloop.checkout2(args[1], args[3]);
        } else if (args.length == 2) {
            bloop.checkout3(args[1]);
        } else {
            System.out.println("Incorrect arguments.");
            System.exit(0);
        }
    }


}
