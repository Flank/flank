 val isWindows = System.getProperty("os.name").startsWith("Windows")
 val isMacOS = System.getProperty("os.name").indexOf("mac") >= 0
 val isLinux = !isWindows && !isMacOS