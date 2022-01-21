package engine.core.plugin.extensions.manifest

import engine.core.plugin.extensions.Extension
import engine.core.plugin.extensions.ExtensionType
import engine.core.plugin.extensions.Extensions
import org.dom4j.Element
import org.dom4j.Node
import org.dom4j.io.SAXReader
import java.io.File
import java.net.URL


object ManifestReader {


    /**
     * Reads text of file and used the from(String) method
     */
    fun read(manifest: File): Manifest {
        return read(manifest.toURI().toURL())
    }

    /**
     * Reads text of url and used the from(String) method
     */
    fun read(manifest: URL): Manifest {
        val reader = SAXReader()
        val document = reader.read(manifest)
        val module = document.rootElement
        val nodes = module.nodeIterator()
        var id: String = "undefined"
        var name: String = "undefined"
        var version: String = "1.0.0"
        var extensions: Extensions? = null
        nodes.forEach {
            when (it.name) {
                "id" -> id = it.text
                "name" -> name = it.text
                "version" -> version = it.text
                "extensions" -> extensions = parseExtension(it as Element)
            }
        }
        return Manifest(id, name, version, extensions!!)
    }

    /**
     * Parses the extension
     */
    private fun parseExtension(ext: Element): Extensions {
        val ns = ext.attribute("namespace").value
        val extensionList = ArrayList<Extension>()
        val exts = ext.selectNodes("//*")
        exts.forEach {
            val node = it as Element
            if (node.attribute("interface") != null) {
                val extension =
                    Extension(node.name,
                        ExtensionType.Interface, parseAttributes(node))
                extensionList.add(extension)
            } else if (node.attribute("implementation") != null) {
                val extension =
                    Extension(node.name, ExtensionType.Implementation, parseAttributes(node))
                extensionList.add(extension)
            }
        }
        return Extensions(ns, extensionList)
    }


    private fun parseAttributes(node: Element): Map<String, String> {
        val map = HashMap<String, String>()
        node.attributes().forEach {
            map[it.name] = it.value
        }
        return map
    }


}