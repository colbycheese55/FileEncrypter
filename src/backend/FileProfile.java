package src.backend;
public class FileProfile {
    private String name, key, initialVector;

    public FileProfile(String name, String key, String initialVector) {
        this.name = name;
        this.key = key;
        this.initialVector = initialVector;
    }
    public FileProfile(String in) {
        String[] components = in.split("\\|"); // regex for divide by |
        this.name = components[0];
        this.key = components[1];
        this.initialVector = components[2];
    }

    public String getName() {return name;}
    public String getKey() {return key;}
    public String getIV() {return initialVector;}
    public String toString() {return name + "|" + key + "|" + initialVector;}
}
