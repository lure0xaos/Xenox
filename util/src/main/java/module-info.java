module Xenox.util {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires kotlin.stdlib.jdk8;
    requires kotlin.reflect;

    requires java.desktop;
    requires java.prefs;
    requires java.logging;

    exports gargoyle.xenox.util.applet;
    exports gargoyle.xenox.util.config;
    exports gargoyle.xenox.util.i18n;
    exports gargoyle.xenox.util.ini;
    exports gargoyle.xenox.util.log;
    exports gargoyle.xenox.util.prefs;
    exports gargoyle.xenox.util.audio;
}
