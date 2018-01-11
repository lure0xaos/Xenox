package gargoyle.xenox.game.scr.select

import gargoyle.xenox.Xenox
import gargoyle.xenox.game.sys.JScreen
import gargoyle.xenox.info.CampaignInfo
import gargoyle.xenox.util.i18n.get
import java.awt.BorderLayout
import java.awt.Component
import java.awt.GridLayout
import java.util.*
import javax.swing.*
import javax.swing.plaf.basic.BasicComboBoxRenderer

class JSelect(parent: JScreen) : JDialog(
    SwingUtilities.getWindowAncestor(parent), parent.messages[STR_SELECT],
    ModalityType.APPLICATION_MODAL
) {
    val cmbSelect: JComboBox<CampaignInfo>

    init {
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        layout = BorderLayout()
        cmbSelect = JComboBox(DefaultComboBoxModel(getCampaigns())).apply {
            renderer = CampaignInfoRenderer()
        }
        add(JPanel(GridLayout(0, 1)).apply { add(cmbSelect) }, BorderLayout.CENTER)
        add(JPanel(GridLayout(1, 2)).apply {
            add(JButton(parent.messages[Xenox.STR_OK]).apply {
                addActionListener { doHide() }
            })
        }, BorderLayout.SOUTH)
    }

    fun getCampaigns(): Array<CampaignInfo> =
        mutableListOf<CampaignInfo>().apply { ServiceLoader.load(CampaignInfo::class.java).forEach { this += it } }
            .toTypedArray()

    var campaign: CampaignInfo
        get() = cmbSelect.selectedItem as CampaignInfo
        set(value) {
            cmbSelect.selectedItem = value
        }

    fun doHide() {
        isVisible = false
    }

    companion object {
        const val STR_SELECT: String = "select"
    }

    class CampaignInfoRenderer : BasicComboBoxRenderer(), ListCellRenderer<Any> {
        override fun getListCellRendererComponent(
            list: JList<out Any>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component =
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
                if (value is CampaignInfo?) if (this is JLabel) text = value?.name ?: ""
            }
    }
}
