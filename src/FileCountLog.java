package src;

import java.util.HashMap;
import java.util.Map.Entry;

public class FileCountLog {
    private HashMap<String, Integer> table;

    public FileCountLog(String in) {
        table = new HashMap<>();
        if (in == null) return;
        for (String item: in.split("\n")) {
                String[] components = item.split("\s");
                table.put(components[0], Integer.valueOf(components[1]));
            }
        }

        public String toString() {
            String out = "";
            for (Entry<String, Integer> entry: table.entrySet())
                out += entry.getKey() + " " + entry.getValue() + "\n";
            return out;
        }

        public void changeFileCount(String fileName, String IV, String change) {
            String fileNameHash = Encryption.hashName(fileName, IV);
            switch (change) {
                case "+1":
                    if (!table.containsKey(fileNameHash))
                        table.put(fileNameHash, 0);
                    table.put(fileNameHash, table.get(fileNameHash) + 1);
                    break;
                case "-1":
                    table.put(fileNameHash, table.get(fileNameHash) - 1);
                    if (table.get(fileNameHash) <= 0) {
                        table.remove(fileNameHash);
                        FileHandler.delete(FileHandler.ENCRYPTED_VAULT_EXT + fileNameHash);
                    }
                    break;
                case "delete":
                    table.remove(fileNameHash);
                    FileHandler.delete(FileHandler.ENCRYPTED_VAULT_EXT + fileNameHash);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
}