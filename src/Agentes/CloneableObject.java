package Agentes;

public class CloneableObject implements Cloneable {
    public CloneableObject clone() {
        try {
            return (CloneableObject)super.clone();
        } catch (CloneNotSupportedException err) {
            return null;
        }
    }
}