package src.backend;

public interface ProgressBar {

    public void start();
    public void update(double val);
    public void complete();
}