package src.backend;

public class ProgressBar {
    public static final int DEFAULT_LENGTH = 50;

    private String message;
    private int length, lineLength;

    public ProgressBar(String message, int length) {
        this.message = message;
        this.length = length;
        if (this.message == null)
            this.message = "Progress: ";
        if (length < 2)
            throw new IllegalArgumentException("length must be >=2");
        lineLength = this.message.length() + 4 + this.length;
    }

    public void update(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("progress must be in [0,1]");
        if (progress == 1) {
            complete();
            return;
        }
        int bars = (int) Math.floor(progress * length);
        String print = message + "||" + "=".repeat(bars) + ">" + " ".repeat(length - bars -1) + "||";
        System.out.print("\r" + print);
    }
    public void complete() {
        System.out.print("\r" + " ".repeat(lineLength) + "\r");
    }
}
