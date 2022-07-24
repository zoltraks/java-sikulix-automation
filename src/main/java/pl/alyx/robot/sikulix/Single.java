package pl.alyx.robot.sikulix;

/**
 * Example of enum singleton pattern.
 * Idea taken from Joshua Bloch, author of Effective Java.
 */
public enum Single {

    INSTANCE;

    Single() { }

    private Integer number = 0;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer increment() {
        Integer result;
        synchronized (this) {
            this.number += 1;
            result = this.number;
        }
        return result;
    }

}

