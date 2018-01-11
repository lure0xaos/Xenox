module Xenox {
    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires kotlin.stdlib.jdk8;

    requires transitive java.desktop;
    requires transitive java.prefs;

    requires Xenox.util;

    exports gargoyle.xenox;

    requires transitive Xenox.services;
    requires Xenox.campaign;
    uses gargoyle.xenox.info.CampaignInfo;
}
