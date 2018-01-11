package gargoyle.xenox.campaign

import gargoyle.xenox.campaign.level1.TestLevelInfo
import gargoyle.xenox.info.CampaignInfo
import gargoyle.xenox.info.LevelInfo

class TestCampaignInfo : CampaignInfo {
    override val name: String = "Test"
    override val levels: List<LevelInfo> = listOf(TestLevelInfo)
}
