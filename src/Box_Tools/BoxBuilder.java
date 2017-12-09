package Box_Tools;

import java.util.ArrayList;

public class BoxBuilder {
    // The BoxBuilder's purpose to instantiate Box objects. BoxBuilder is implemented in a way
    //  that allows multiple Box objects to be created from only one instantiation of BoxBuilder.

    // Class Variables
    private Box box;

    // BoxBuilder() instantiates the BoxBuilder
    public BoxBuilder(){}

    // buildBox(String, ArrayList<String>) creates a Box object and sets the Box's Head field as the given subject
    //  and the Box's Body field as the given list of descriptions. It then returns the Box.
    public Box buildBox(String subject, ArrayList<String> descriptions) {

        this.box = new Box();
        this.box.setHead(subject);
        this.box.addManyElementsToBody(descriptions);

        return this.box;
    }
}
