package ooo.sansk.learnopengl;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL33.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL33.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL33.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL33.GL_FALSE;
import static org.lwjgl.opengl.GL33.GL_FLOAT;
import static org.lwjgl.opengl.GL33.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL33.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL33.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL33.GL_TRIANGLES;
import static org.lwjgl.opengl.GL33.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL33.glAttachShader;
import static org.lwjgl.opengl.GL33.glBindBuffer;
import static org.lwjgl.opengl.GL33.glBindVertexArray;
import static org.lwjgl.opengl.GL33.glBufferData;
import static org.lwjgl.opengl.GL33.glClear;
import static org.lwjgl.opengl.GL33.glClearColor;
import static org.lwjgl.opengl.GL33.glCompileShader;
import static org.lwjgl.opengl.GL33.glCreateProgram;
import static org.lwjgl.opengl.GL33.glCreateShader;
import static org.lwjgl.opengl.GL33.glDeleteBuffers;
import static org.lwjgl.opengl.GL33.glDeleteProgram;
import static org.lwjgl.opengl.GL33.glDeleteShader;
import static org.lwjgl.opengl.GL33.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL33.glDrawArrays;
import static org.lwjgl.opengl.GL33.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL33.glGenBuffers;
import static org.lwjgl.opengl.GL33.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL33.glGetProgrami;
import static org.lwjgl.opengl.GL33.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL33.glGetShaderi;
import static org.lwjgl.opengl.GL33.glLinkProgram;
import static org.lwjgl.opengl.GL33.glShaderSource;
import static org.lwjgl.opengl.GL33.glUseProgram;
import static org.lwjgl.opengl.GL33.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLApplication {
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try(final var errorCallback = GLFWErrorCallback.createPrint(System.err)) {
            errorCallback.set();

            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            // Configure GLFW
            glfwDefaultWindowHints(); // optional, the current window hints are already the default
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

            // Create the window
            long window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }

            glfwSetFramebufferSizeCallback(window, (callbackWindow, width, height) -> {
                glViewport(0, 0, width, height);
            });

            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);

            GL.createCapabilities();

            final var vertexShader = createVertexShader();
            final var fragmentShader = createFragmentShader();
            final var shaderProgram = createShaderProgram(new int[]{vertexShader}, new int[]{fragmentShader});

            float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f
            };

            final var vertexArrayObject = glGenVertexArrays();
            final var vertexBufferObject = glGenBuffers();

            glBindVertexArray(vertexArrayObject);

            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            while (!glfwWindowShouldClose(window)) {
                processInput(window);

                glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT);

                glUseProgram(shaderProgram);
                glBindVertexArray(vertexArrayObject);
                glDrawArrays(GL_TRIANGLES, 0, 3);

                glfwSwapBuffers(window); // swap the color buffers
                glfwPollEvents();
            }

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

            glDeleteVertexArrays(vertexArrayObject);
            glDeleteBuffers(vertexBufferObject);
            glDeleteProgram(shaderProgram);

            // Terminate GLFW and free the error callback
            glfwTerminate();
        }
    }

    private void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }

    private int createVertexShader() {
        //language=GLSL
        final var vertexShaderSource = """
                #version 330 core
                layout (location = 0) in vec3 aPos;
                
                void main()
                {
                    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0f);
                }
                """;

        final var vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        final var compileResult = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (compileResult == GL_FALSE) {
            final var error = glGetShaderInfoLog(vertexShader);
            throw new RuntimeException("Vertex shader compilation failed: " + error);
        }

        return vertexShader;
    }

    private int createFragmentShader() {
        //language=GLSL
        final var fragementShaderSource = """
                #version 330 core
                out vec4 FragColor;
                
                void main()
                {
                    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
                }
                """;

        final var fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragementShaderSource);
        glCompileShader(fragmentShader);

        final var compileResult = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (compileResult == GL_FALSE) {
            final var error = glGetShaderInfoLog(fragmentShader);
            throw new RuntimeException("Fragment shader compilation failed: " + error);
        }

        return fragmentShader;
    }

    private int createShaderProgram(int[] vertexShaders, int[] fragmentShaders) {
        final var program = glCreateProgram();

        for (final var vertexShader : vertexShaders) {
            glAttachShader(program, vertexShader);
        }
        for (final var fragmentShader : fragmentShaders) {
            glAttachShader(program, fragmentShader);
        }

        glLinkProgram(program);

        for (final var vertexShader : vertexShaders) {
            glDeleteShader(vertexShader);
        }
        for (final var fragmentShader : fragmentShaders) {
            glDeleteShader(fragmentShader);
        }

        final var linkResult = glGetProgrami(program, GL_LINK_STATUS);
        if (linkResult == GL_FALSE) {
            final var error = glGetProgramInfoLog(program);
            throw new RuntimeException("Program linking failed: " + error);
        }

        return program;
    }
}
