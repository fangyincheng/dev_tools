package com.fyc.dev_tools

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.setEmptyState
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import com.intellij.ui.jcef.JBCefBrowser
import com.jgoodies.common.base.Strings
import javax.swing.JButton
import javax.swing.JComponent

class WebBrowserToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val editWindow = EditWindow(toolWindow)
        val addContent = ContentFactory.getInstance().createContent(editWindow.getContent(), "ADD", true)
        toolWindow.contentManager.addContent(addContent, 0)

        toolWindow.contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun contentAdded(e: ContentManagerEvent) {
            }

            override fun contentRemoved(e: ContentManagerEvent) {
                val displayName = e.content.displayName
                Config.getInstance().urls.remove(displayName)
                Config.getInstance().sorts.remove(displayName)
            }
        })

        for ((index, name) in Config.getInstance().sorts.withIndex()) {
            run {
                val webBrowserWindow = WebBrowserWindow(toolWindow, Config.getInstance().urls.get(name).toString())
                val content =
                    ContentFactory.getInstance()
                        .createContent(webBrowserWindow.getContent(), name, false)
                toolWindow.contentManager.addContent(content, index + 1)
            }
        }
    }

    override fun shouldBeAvailable(project: Project) = true

    class WebBrowserWindow(toolWindow: ToolWindow, url: String) {
        var url: String = url

        fun getContent(): JComponent {
            val jbCefBrowser = JBCefBrowser(url);
            return jbCefBrowser.component
        }
    }

    class EditWindow(toolWindow: ToolWindow) {
        var toolWindow: ToolWindow = toolWindow

        fun getContent(): JComponent {
            val jbPanel = JBPanel<JBPanel<*>>()
            val name = JBTextField(10)
            name.setEmptyState("请输入名字")
            val edit = JBTextField(30)
            edit.setEmptyState("请输入地址")
            val button = JButton("确定")
            button.addActionListener {
                if (Strings.isBlank(name.text) || Strings.isBlank(edit.text)) {
                    Messages.showInfoMessage("请输入完整的名字和地址", "缺少参数")
                    return@addActionListener
                }
                val webBrowserWindow = WebBrowserWindow(toolWindow, edit.text)
                val content =
                    ContentFactory.getInstance().createContent(webBrowserWindow.getContent(), name.text, false)
                toolWindow.contentManager.addContent(content)
                toolWindow.contentManager.setSelectedContent(content)

                Config.getInstance().urls.put(
                    name.text, edit.text
                )
                Config.getInstance().sorts.add(name.text)
            }

            jbPanel.add(name)
            jbPanel.add(edit)
            jbPanel.add(button)
            return jbPanel
        }
    }
}