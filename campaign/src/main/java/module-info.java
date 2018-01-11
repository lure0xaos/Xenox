module Xenox.campaign {
    requires kotlin.stdlib;
    requires Xenox.services;

    exports gargoyle.xenox.campaign;
    provides gargoyle.xenox.info.CampaignInfo with gargoyle.xenox.campaign.TestCampaignInfo;

}
