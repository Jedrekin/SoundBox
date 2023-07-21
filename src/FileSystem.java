import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystemNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FileSystem {
    private static final Comparator<File> FILE_COMPARATOR = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
    private final File directory;
    int index;
    private FileSystem(File file) {
        directory = file;
        index = 0;
    }

    public List<File> getDirectoryContent() {
        List<File> fileList = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        fileList.sort(FILE_COMPARATOR);
        return fileList;
    }
    public File getIndexedFile() {
        File[] files = directory.listFiles();
        return files != null ? files[index] : null;
    }
    public String getIndexedFileName() {
        List<File> fileList = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        return fileList.get(index).getName().replaceAll(".wav", " ");
    }
    public int getDirectoryFileCount() {
        return Objects.requireNonNull(directory.listFiles()).length;
    }
    public static FileSystem loadDirectory(File file) throws FileNotFoundException {
        if ( !file.exists() ) throw new FileNotFoundException();
        if ( !file.isDirectory() ) throw new FileSystemNotFoundException();
        return new FileSystem(file);
    }
}
