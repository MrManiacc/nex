package ext.gui

import engine.core.event.WindowCreatedEvent
import engine.core.event.WindowDestroyedEvent
import engine.core.ext.Updatable
import engine.core.project.Project
import engine.core.project.context.Inject
import imgui.*
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import net.engio.mbassy.listener.Handler
import org.lwjgl.glfw.GLFW
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths


/**
 * This is used to initialize/render the imgui backend
 */
class Editor : Updatable {

    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()
    @Inject lateinit var project: Project
    private val fontAtlas = ImFontAtlas(0)
    private val renderables: MutableList<Renderable> = ArrayList()

    /**
     * This is used to initalize our internal imgui state
     */
    @Handler fun onWindowCreated(event: WindowCreatedEvent) {
        ImGui.createContext()
        configureImGui()
        imGuiGlfw.init(event.handle, true);
        imGuiGl3.init("#version 150");
    }

    private fun configureImGui() {
        val io = ImGui.getIO()
        io.iniFilename = null
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
        io.configViewportsNoTaskBarIcon = true
        initFonts(io);
    }

    /**
     * Example of fonts configuration
     * For more information read: https://github.com/ocornut/imgui/blob/33cdbe97b8fd233c6c12ca216e76398c2e89b0d8/docs/FONTS.md
     */
    private fun initFonts(io: ImGuiIO) {
//        io.fonts.addFontDefault() // Add default font for latin glyphs
        // You can use the ImFontGlyphRangesBuilder helper to create glyph ranges based on text input.
        // For example: for a game where your script is known, if you can feed your entire script to it (using addText) and only build the characters the game needs.
        // Here we are using it just to combine all required glyphs in one place
        val rangesBuilder = ImFontGlyphRangesBuilder() // Glyphs ranges provide
        rangesBuilder.addRanges(io.fonts.glyphRangesDefault)
        rangesBuilder.addRanges(io.fonts.glyphRangesCyrillic)
        rangesBuilder.addRanges(io.fonts.glyphRangesJapanese)

        // Font config for additional fonts
        // This is a natively allocated struct so don't forget to call destroy after atlas is built
        val fontConfig = ImFontConfig()
//        fontConfig.mergeMode = true // Enable merge mode to merge cyrillic, japanese and icons with default font
        val glyphRanges = rangesBuilder.buildRanges()
        val bytes = javaClass.getResource("/fonts/SourceCodePro-Regular.ttf").readBytes()
        io.fonts.addFontFromMemoryTTF(bytes,
            17f,
            fontConfig,
            glyphRanges)
        io.fonts.build()
        fontConfig.destroy()
    }

    /**
     * Shuts down our imgui instance
     */
    @Handler fun onWindowDestroyed(event: WindowDestroyedEvent) {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    override fun begin() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    /**
     * Calls once per frame
     */
    override fun update(dt: Float) {
        ImGui.text("Extra");
    }

    override fun end() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }
}