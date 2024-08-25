package jmg.cli;

import jmg.core.config.AbstractConfig;
import jmg.core.config.Constants;
import jmg.sdk.jMGenerator;
import jmg.sdk.util.SDKResultUtil;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Console {

    private static AbstractConfig config = new AbstractConfig();
    private static final List<String> SERVER_TYPES = Arrays.asList(
            Constants.SERVER_TOMCAT,
            Constants.SERVER_SPRING_MVC,
            Constants.SERVER_JETTY,
            Constants.SERVER_RESIN,
            Constants.SERVER_WEBLOGIC,
            Constants.SERVER_WEBSPHERE,
            Constants.SERVER_UNDERTOW,
            Constants.SERVER_GLASSFISH,
            Constants.SERVER_JBOSS);
    private static final List<String> TOOL_TYPES = Arrays.asList(
            Constants.TOOL_GODZILLA,
            Constants.TOOL_BEHINDER,
            Constants.TOOL_ANTSWORD,
            Constants.TOOL_SUO5,
            Constants.TOOL_NEOREGEORG);

    private static final List<String> SHELL_TYPES = Arrays.asList(
            Constants.SHELL_LISTENER,
            Constants.SHELL_FILTER,
            Constants.SHELL_INTERCEPTOR);

    private static final List<String> FORMAT_TYPES = Arrays.asList(
            Constants.FORMAT_BASE64,
            Constants.FORMAT_BCEL,
            Constants.FORMAT_BIGINTEGER,
            Constants.FORMAT_CLASS,
            Constants.FORMAT_JAR,
            Constants.FORMAT_JAR_AGENT,
            Constants.FORMAT_JSP);

    private static final List<String> GADGET_TYPES = Arrays.asList(
            Constants.GADGET_NONE,
            Constants.GADGET_JDK_TRANSLET,
            Constants.GADGET_XALAN_TRANSLET,
            Constants.GADGET_FJ_GROOVY,
            Constants.GADGET_SNAKEYAML);


    public void init() {
        System.out.println(String.format("Welcome to jMG %s !", Constants.JMG_VERSION));
        config = new AbstractConfig() {{
            // 设置工具类型
            setToolType(Constants.TOOL_GODZILLA);
            // 设置中间件 or 框架
            setServerType(Constants.SERVER_TOMCAT);
            // 设置内存马类型
            setShellType(Constants.SHELL_LISTENER);
            // 设置输出格式为 BASE64
            setOutputFormat(Constants.FORMAT_BASE64);
            // 设置漏洞利用封装，默认不启用
            setGadgetType(Constants.GADGET_NONE);
            // 初始化基础配置
            build();
        }};

    }

    public void run() throws Throwable {
        List<String> commands = Arrays.asList("help", "list", "use", "set", "show", "generate", "info", "exit");
        Terminal terminal = TerminalBuilder.builder().build();
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new StringsCompleter(commands))
                .build();

        String prompt = "jmg > ";
        String line;
        while (true) {
            line = lineReader.readLine(prompt);
            String[] parts = line.trim().split("\\s+");
            String command = parts[0];
            String argument1 = parts.length > 1 ? parts[1] : null;
            String argument2 = parts.length > 2 ? parts[2] : null;
            switch (command) {
                case "help":
                    help();
                    break;
                case "list":
                    list(argument1);
                    break;
                case "use":
                    use(argument1, argument2);
                    break;
                case "set":
                    set(argument1, argument2);
                    break;
                case "get":
                    get(argument1);
                    break;
                case "generate":
                    generate();
                    break;
                case "info":
                    info();
                    break;
                case "exit":
                    System.out.println("Bye!");
                    return;
                default:
                    System.out.println("Unknown command: " + line);
            }
        }
    }

    public static void help() {
        String[][] helpMessages = {
                {"help", "help message", "帮助信息"},
                {"list [type]", "list toolTypes/serverTypes/formatTypes/shellTypes", "支持的工具类型/中间件|框架/组件类型/输出格式"},
                {"use <type> <name>", "choose toolType/serverType/formatType/shellType", "选择工具类型/中间件|框架/组件类型/输出格式"},
                {"set <key> <value>", "set pass/key/headerName/headerValue/urlPattern/...", "设置密码/密钥/请求头名称/请求头值/请求路径[/*]/..."},
                {"get <type>", "get <type> or <key>", "查看配置"},
                {"generate", "generate payload", "生成载荷"},
                {"info", "connect info", "连接信息"},
                {"exit", "exit jmg", "退出"}
        };

        for (String[] message : helpMessages) {
            System.out.println(String.format("%-20s %-50s %-40s", message[0], message[1], message[2]));
        }
    }


    public static void list(String argument1) {
        if ("serverTypes".equalsIgnoreCase(argument1)) {
            System.out.println("Servers: " + SERVER_TYPES);
        } else if ("toolTypes".equalsIgnoreCase(argument1)) {
            System.out.println("Tools: " + TOOL_TYPES);
        } else if ("shellTypes".equalsIgnoreCase(argument1)) {
            System.out.println("Shells: " + SHELL_TYPES);
        } else if ("formatTypes".equalsIgnoreCase(argument1)) {
            System.out.println("Formats: " + FORMAT_TYPES);
        } else if ("gadgetTypes".equalsIgnoreCase(argument1)) {
            System.out.println("Gadgets: " + GADGET_TYPES);
        } else {
            System.out.println("Unknown type: " + argument1);
        }
    }

    public static void get(String argument1) {
        try {
            String methodName = "get" + argument1.substring(0, 1).toUpperCase() + argument1.substring(1);
            Method method = config.getClass().getMethod(methodName);
            System.out.println(argument1 + " : " + method.invoke(config));
        } catch (NoSuchMethodException e) {
            System.out.println("Unknown type: " + argument1);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Error getting value for type: " + argument1);
        }
    }

    public static void use(String argument1, String argument2) {
        if ("serverType".equalsIgnoreCase(argument1)) {
            if (!SERVER_TYPES.contains(argument2)) {
                System.out.println("Unsupported server type: " + argument2);
                return;
            }
            set("serverType", argument2);
        } else if ("toolType".equalsIgnoreCase(argument1)) {
            if (!TOOL_TYPES.contains(argument2)) {
                System.out.println("Unsupported tool type: " + argument2);
                return;
            }
            set("toolType", argument2);
        } else if ("shellType".equalsIgnoreCase(argument1)) {
            if (!SHELL_TYPES.contains(argument2)) {
                System.out.println("Unsupported shell type: " + argument2);
                return;
            }
            set("shellType", argument2);
        } else if ("formatType".equalsIgnoreCase(argument1)) {
            if (!FORMAT_TYPES.contains(argument2)) {
                System.out.println("Unsupported format type: " + argument2);
                return;
            }
            set("outputFormat", argument2);
        } else if ("gadgetType".equalsIgnoreCase(argument1)) {
            if (!GADGET_TYPES.contains(argument2)) {
                System.out.println("Unsupported gadget type: " + argument2);
                return;
            }
            set("gadgetType", argument2);
        } else {
            System.out.println("Unknown type: " + argument1);
        }
    }

    public static void set(String argument1, String argument2) {
        try {
            String methodName = "set" + argument1.substring(0, 1).toUpperCase() + argument1.substring(1);
            Method method = config.getClass().getMethod(methodName, String.class);
            method.invoke(config, argument2);
            System.out.println(argument1 + " : " + argument2);
        } catch (NoSuchMethodException e) {
            System.out.println("Unknown key: " + argument1);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Error setting value for key: " + argument1);
        }
    }

    public static void generate() throws Throwable {
        // 更新配置
        config.build();
        jMGenerator generator = new jMGenerator(config);
        generator.genPayload();
        generator.printPayload();
    }

    public static void info() throws Throwable {
        // 连接信息
        SDKResultUtil.printBasicInfo(config);
        SDKResultUtil.printDebugInfo(config);
    }
}