package gargoyle.xenox.util.applet

import java.net.URL
import javax.swing.JPanel

open class JApplet : JPanel(), Applet {
    private lateinit var stub: AppletStub
    protected val codeBase: URL?
        get() = stub.codeBase
    protected val documentBase: URL?
        get() = stub.documentBase

    fun setStub(stub: AppletStub) {
        this.stub = stub
    }
}
