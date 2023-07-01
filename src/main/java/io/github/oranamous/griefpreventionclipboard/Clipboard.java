package io.github.oranamous.griefpreventionclipboard;

import java.util.ArrayList;

public class Clipboard {
    public final ArrayList<String> builders;
    public final ArrayList<String> containers;
    public final ArrayList<String> accessors;
    public final ArrayList<String> managers;

    Clipboard() {
        this.builders = new ArrayList<>();
        this.containers = new ArrayList<>();
        this.accessors = new ArrayList<>();
        this.managers = new ArrayList<>();
    }
}
