package game;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyJar {
    public static void main(String[] args) throws Exception {
        Path src = Path.of("d:\\cursor---21--game\\21点游戏.jar");
        Path dst = Path.of("d:\\cursor---21--game\\bin\\21点游戏.jar");
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copied JAR to bin directory");
    }
}
