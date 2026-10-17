#!/usr/bin/env python3
"""Create minimal dependency stubs; compile the actual production pathfinding classes."""
from pathlib import Path
import sys
root = Path(sys.argv[1])
stubs = {
    "com/minecolonies/api/util/Log.java": """package com.minecolonies.api.util;
public final class Log {
    public static Logger getLogger() { return new Logger(); }
    public static final class Logger {
        public void info(String format, Object... args) {}
        public void error(String msg, Throwable t) { throw new AssertionError(msg, t); }
    }
}
""",
    "com/minecolonies/core/MineColonies.java": """package com.minecolonies.core;
public final class MineColonies {
    private static final Config CONFIG = new Config();
    public static Config getConfig() { return CONFIG; }
    public static final class Config {
        public Server getServer() { return new Server(); }
    }
    public static final class Server {
        public final Setting pathfindingThreads = new Setting();
    }
    public static final class Setting {
        public int get() { return Integer.getInteger("pathfinding.override", 0); }
    }
}
""",
    "com/minecolonies/core/entity/pathfinding/pathjobs/AbstractPathJob.java": """package com.minecolonies.core.entity.pathfinding.pathjobs;
import java.util.concurrent.ThreadPoolExecutor;
public class AbstractPathJob {
    public Result getResult() { return new Result(); }
    public static class Result {
        public void startJob(ThreadPoolExecutor executor) {}
    }
}
""",
    "org/jetbrains/annotations/NotNull.java": """package org.jetbrains.annotations;
import java.lang.annotation.*;
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.CLASS)
public @interface NotNull {}
"""
}
for rel, contents in stubs.items():
    target = root / rel
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(contents)
