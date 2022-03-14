package me.zhanshi123.vipsystem.custom;

import me.zhanshi123.vipsystem.Main;
import me.zhanshi123.vipsystem.api.VipSystemAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomFunction {
    private String name;
    private String description;
    private String[] args;
    private long duration;
    private String waitTillOnline;
    private List<String> onStart;
    private List<String> onEnd;
    private File script;
    private List<String> scripts = new ArrayList<>();
    private Map<String, String[]> functions = new HashMap<>();
    private String durationArgName;

    public CustomFunction(String name) {
        this.name = name;
        CustomFunction customFunction = Main.getCustomManager().getCustomFunction(name);
        if (customFunction == null) {
            Main.getInstance().getLogger().warning("Custom function named " + name + " is not loaded, plz check it out!");
            return;
        }
        this.description = customFunction.getDescription();
        this.args = customFunction.getArgs();
        this.duration = customFunction.getDuration();
        this.waitTillOnline = customFunction.getWaitTillOnline();
        this.onStart = customFunction.getOnStart();
        this.onEnd = customFunction.getOnEnd();
        this.script = customFunction.getScript();
        this.functions = customFunction.getFunctions();
        this.durationArgName = customFunction.getDurationArgName();
        preCompile();
    }

    public void preCompile() {
        Main.getInstance().debug("Precompile custom function " + name + " 's script, file:" + script.getAbsolutePath());
        Main.getScriptManager().getCompiledScript(script);
    }

    public CustomFunction(String name, String description, String[] args, String durationArgName, String waitTillOnline, List<String> onStart, List<String> onEnd, File script) {
        this.name = name;
        this.description = description;
        this.args = args;
        this.duration = 0;
        if (durationArgName == null) {
            durationArgName = "60000";
        }
        if (durationArgName.contains("{") && durationArgName.contains("}")) {
            this.durationArgName = durationArgName.replace("{", "").replace("}", "");
            this.duration = VipSystemAPI.getInstance().getTimeMillis(this.durationArgName);
        }
        this.waitTillOnline = waitTillOnline;
        this.onStart = onStart;
        this.onEnd = onEnd;
        this.script = script;
        scripts.addAll(onEnd);
        scripts.addAll(onStart);
        scripts = scripts.stream()
                .filter(str -> str.startsWith("[Script]"))
                .map(str -> str.replace("[Script]", "").trim())
                .collect(Collectors.toList());
        if (scripts.size() != 0) {
            try {
                preCompile();
                scripts.forEach(function -> {
                    if (function.contains("(") && function.contains(")")) {
                        String functionName = function.substring(0, function.indexOf("("));
                        String arguments = function.substring(function.indexOf("(")).replace("(", "").replace(")", "");
                        String[] argArray;
                        if (arguments.contains(",")) {
                            argArray = arguments.split(",");
                            for (int i = 0; i < argArray.length; i++) {
                                argArray[i] = argArray[i].trim();
                            }
                        } else {
                            argArray = new String[]{arguments};
                        }
                        functions.put(functionName, argArray);
                    }
                });
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("Script resolve error! File path: " + script.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArgs() {
        return args;
    }

    public String getFormattedArgs() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            stringBuilder.append("[");
            stringBuilder.append(args[i]);
            stringBuilder.append("] ");
        }
        String tmp = stringBuilder.toString();
        tmp = tmp.substring(0, tmp.length() - 1);
        return tmp;
    }

    public long getDuration() {
        return duration;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public List<String> getOnStart() {
        return onStart;
    }

    public void setOnStart(List<String> onStart) {
        this.onStart = onStart;
    }

    public List<String> getOnEnd() {
        return onEnd;
    }

    public void setOnEnd(List<String> onEnd) {
        this.onEnd = onEnd;
    }

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public String[] getSortedArguments(String functionName) {
        return functions.get(functionName);
    }

    public Object executeFunction(String functionName, String... args) {
        return Main.getScriptManager().invokeCustomFunction(this, functionName, args);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String[]> getFunctions() {
        return functions;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getWaitTillOnline() {
        return waitTillOnline;
    }

    public void setWaitTillOnline(String waitTillOnline) {
        this.waitTillOnline = waitTillOnline;
    }

    public String getDurationArgName() {
        return durationArgName;
    }
}
