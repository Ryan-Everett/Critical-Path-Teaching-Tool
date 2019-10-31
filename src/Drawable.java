import java.awt.*;

/**
 * Interface for all Drawable objects
 * Contains abstract methods, which are needed for the DrawableList
 */
public interface Drawable {
    void draw(Graphics g);

    Drawable getNextDrawable();

    void setNextDrawable(Drawable d);

    int getObjectPriority();

    boolean priorityGreaterThanOrEqual(Drawable d);

    void setSelected(boolean selected);

    boolean checkIfInside(Vector v);

    void delete();

    void setDisplayingAnswers(boolean b);
}