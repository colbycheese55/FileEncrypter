package src.backend;

import java.util.*;

public class ShareLog {
    HashMap<Integer, ArrayList<SharedFile>> shares;

    public ShareLog(String in) {
        shares = new HashMap<>();
        if (in == null) return;
        String[] lines = in.split("\n");
        for (String line: lines) {
            SharedFile sharedFile = new SharedFile(line);
            if (shares.containsKey(sharedFile.usernameHash))
                shares.get(sharedFile.usernameHash).add(sharedFile);
            else {
                ArrayList<SharedFile> list = new ArrayList<>();
                list.add(sharedFile);
                shares.put(sharedFile.usernameHash, list);
        }}}

    public void addEntry(FileProfile fileProfile, String receivingUsername, String OTP) {
        String encFileProfile = Encryption.encrypt(fileProfile.toString(), OTP);
        SharedFile sharedFile = new SharedFile(receivingUsername, encFileProfile);
        if (shares.containsKey(sharedFile.usernameHash))
                shares.get(sharedFile.usernameHash).add(sharedFile);
        else {
            ArrayList<SharedFile> list = new ArrayList<>();
            list.add(sharedFile);
            shares.put(sharedFile.usernameHash, list);
        }
    }

    public ArrayList<String> getEntriesForUser(String username) {
        ArrayList<String> result = new ArrayList<>();
        if (!shares.containsKey(username.hashCode())) 
            return result;
        for (SharedFile entry: shares.get(username.hashCode()))
            result.add(entry.encFileProfile);
        return result;
    }

    public void removeOldEntries(ArrayList<String> remainingEntries, String username) {
        ArrayList<SharedFile> remainingShares = new ArrayList<>();
        for (int i = 0; i < remainingEntries.size(); i++)
            remainingShares.add(new SharedFile(username, remainingEntries.get(i)));
        shares.put(username.hashCode(), remainingShares);
    }

    public String toString() {
        String result = "";
        for (ArrayList<SharedFile> list: shares.values()) {
            for (SharedFile entry: list.toArray(new SharedFile[0]))
                result += entry.toString() + "\n";
        }
        return result;
    }



    private static class SharedFile {
        int usernameHash;
        String encFileProfile;

        public SharedFile(String in) {
            String[] parts = in.split("\\|");
            usernameHash = Integer.parseInt(parts[0]);
            encFileProfile = parts[1];
        }
        public SharedFile(String username, String encFileProfiles) {
            this.usernameHash = username.hashCode();
            this.encFileProfile = encFileProfiles;
        }

        public String toString() {
            return usernameHash + "|" + encFileProfile;
        }
    }
}
