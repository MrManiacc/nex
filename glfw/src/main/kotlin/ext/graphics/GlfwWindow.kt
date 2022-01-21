package ext.graphics

import engine.core.ext.Input
import engine.core.ext.Window
import engine.core.event.WindowCreatedEvent
import engine.core.event.WindowDestroyedEvent
import engine.core.project.Project
import engine.core.project.context.Inject
import engine.core.project.context.Registry
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.awt.SystemColor.window
import java.util.*

@Listener
class GlfwWindow : Window {
    override var width: Int = 1920
    override var height: Int = 1080
    override var vsync: Boolean = false
    override var fullscreen: Boolean = false
    override var title: String = "GlfwWindow"
    override var center: Boolean = true
    private var handle: Long = 0
    private val inputs: MutableList<Input> = arrayListOf()
    @Inject lateinit var project: Project
    @Inject lateinit var bus: MBassador<Any>

    override fun init() {
        inputs.addAll(project.extensionsFor(Input::class))
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()
        if (!GLFW.glfwInit()) throw IllegalStateException("Unable to create nexus.plugins.glfw")
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, if (fullscreen) 1 else 0)
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL)
        if (handle == MemoryUtil.NULL) throw  RuntimeException("Failed to create the GLFW window");
        initInput()
        finalizeWindow()
        bus.publish(WindowCreatedEvent(handle))
    }

    override fun dispose() {
        bus.publish(WindowDestroyedEvent(handle))
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null))?.free();
    }


    private fun finalizeWindow() {
        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        // Enable v-sync
        GLFW.glfwSwapInterval(if (vsync) 1 else 0)
        if (center) center()
        // Make the window visible
        GLFW.glfwShowWindow(handle)
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        clearBuffer()
        renderBuffer()

    }

    private fun center() {
        if (fullscreen) return
        // Get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*
            GLFW.glfwSetWindowSize(handle, this.width, this.height)
            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(handle, pWidth, pHeight)
            // Center the window
            val monitor = getMonitorForWindow()
            val area = getMonitorArea(monitor)

            val mode = GLFW.glfwGetVideoMode(monitor) ?: return
            GLFW.glfwSetWindowPos(
                handle,
                area[0] + (mode.width() - pWidth[0]) / 2,
                area[1] + (mode.height() - pHeight[0]) / 2
            )
        }
    }

    private fun getMonitorForWindow(): Long {
        val primary = GLFW.glfwGetPrimaryMonitor()
        val monitors = GLFW.glfwGetMonitors() ?: return primary
        val constraints = getConstraints(handle)
        for (i in 0 until monitors.capacity()) {
            val monHandle = monitors[i]
            val area = getMonitorArea(monHandle)
            val cx = (constraints[2] / 2) + constraints[0]
            val cy = (constraints[3] / 2) + constraints[1]
            if ((area[0] < cx && area[2] > cx) && (area[1] < cy && area[3] > cy)) {
                return monHandle
            }
        }
        return primary
    }

    private fun getMonitorArea(monitor: Long): IntArray {
        val x = IntArray(1)
        val y = IntArray(1)
        val width = IntArray(1)
        val height = IntArray(1)
        GLFW.glfwGetMonitorWorkarea(monitor, x, y, width, height)
        return intArrayOf(x[0], y[0], width[0], height[0])
    }

    private fun getConstraints(handle: Long): IntArray {
        val x = IntArray(1)
        val y = IntArray(1)
        val width = IntArray(1)
        val height = IntArray(1)
        GLFW.glfwGetWindowPos(handle, x, y)
        GLFW.glfwGetWindowSize(handle, width, height)
        return intArrayOf(x[0], y[0], width[0], height[0])
    }

    private fun initInput() {
        GLFW.glfwSetScrollCallback(handle) { _, xOffset, yOffset ->
            inputs.forEach {
                it.onMouseScrollEvent(xOffset, yOffset)
            }
        }

        GLFW.glfwSetCursorPosCallback(handle) { _, x, y ->
            inputs.forEach {
                it.onMouseMoveEvent(x, y)
            }

        }

        GLFW.glfwSetKeyCallback(handle) { _, key, scancode, action, mods ->
            inputs.forEach {
                it.onKeyEvent(key, scancode, action, mods)
            }
        }
        GLFW.glfwSetMouseButtonCallback(handle) { _, button, action, mods ->
            inputs.forEach {
                it.onMouseClickEvent(button, action, mods)
            }
        }

        GLFW.glfwSetWindowCloseCallback(handle) { id ->
            inputs.forEach {
                it.onWindowClose(id)
            }
        }
    }


    override fun clearBuffer() {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT or GL11.GL_COLOR_BUFFER_BIT)
        GL11.glClearColor(0.223f, 0.322f, 0.4232f, 1.0f)
    }

    override fun renderBuffer() {
        GLFW.glfwSwapBuffers(handle)
        GLFW.glfwPollEvents()
    }

    override fun isOpen(): Boolean = !GLFW.glfwWindowShouldClose(handle)

    override fun toString(): String {
        return "GlfwWindow(width=$width, height=$height, vsync=$vsync, fullscreen=$fullscreen, title='$title')"
    }
}
