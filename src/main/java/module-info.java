module Xenox {
    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires org.jetbrains.annotations;

    exports gargoyle.xenox to kotlin.reflect;
}
