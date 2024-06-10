package com.fyc.dev_tools


import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "Config",
    storages = [Storage("dev_tools.xml")]
)
class Config : PersistentStateComponent<Config> {

    var urls: HashMap<String, String> = HashMap()
    var sorts: ArrayList<String> = ArrayList()

    override fun getState(): Config {
        return this
    }

    override fun loadState(state: Config) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): Config {
            return ServiceManager.getService(Config::class.java)
        }
    }
}