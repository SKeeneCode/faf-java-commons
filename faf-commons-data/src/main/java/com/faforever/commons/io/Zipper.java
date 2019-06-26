package com.faforever.commons.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

@Slf4j
public final class Zipper {

  private static final char PATH_SEPARATOR = File.separatorChar;
  private static final Path NEUTRAL_PATH = Paths.get("");

  private final Path directoryToZip;
  private final boolean zipContent;
  private final String archiveType;

  private ByteCountListener byteCountListener;
  private int byteCountInterval;
  private int bufferSize;
  private long bytesTotal;
  private long bytesDone;
  private ArchiveOutputStream archiveOutputStream;
  private boolean closeStream;

  /**
   * @param zipContent {@code true} if the contents of the directory should be zipped, {@code false} if the specified
   */
  private Zipper(Path directoryToZip, boolean zipContent, String archiveType) {
    this.directoryToZip = directoryToZip;
    this.archiveType = archiveType;
    this.zipContent = zipContent;
    // 4K
    bufferSize = 0x1000;
    byteCountInterval = 40;
  }

  public static Zipper of(Path path) {
    return of(path, ArchiveStreamFactory.ZIP);
  }

  public static Zipper of(Path path, String archiveType) {
    return new Zipper(path, false, archiveType);
  }

  public static Zipper contentOf(Path path) {
    return contentOf(path, ArchiveStreamFactory.ZIP);
  }

  public static Zipper contentOf(Path path, String archiveType) {
    return new Zipper(path, true, archiveType);
  }

  public Zipper to(ArchiveOutputStream archiveOutputStream) {
    this.archiveOutputStream = archiveOutputStream;
    this.closeStream = false;
    return this;
  }

  public Zipper to(Path path) throws IOException, ArchiveException {
    this.archiveOutputStream = new ArchiveStreamFactory().createArchiveOutputStream(archiveType, new BufferedOutputStream(Files.newOutputStream(path)));
    this.closeStream = true;
    return this;
  }

  public Zipper byteCountInterval(int byteCountInterval) {
    this.byteCountInterval = byteCountInterval;
    return this;
  }

  public Zipper listener(ByteCountListener byteCountListener) {
    this.byteCountListener = byteCountListener;
    return this;
  }

  public void zip() throws IOException {
    Objects.requireNonNull(archiveOutputStream, "archiveOutputStream must not be null");
    Objects.requireNonNull(directoryToZip, "directoryToZip must not be null");

    bytesTotal = calculateTotalBytes();
    bytesDone = 0;

    Files.walkFileTree(directoryToZip, new SimpleFileVisitor<Path>() {
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path relativePath = zipContent
          ? directoryToZip.relativize(dir)
          : directoryToZip.getParent().relativize(dir);

        if (relativePath.equals(NEUTRAL_PATH)) {
          return FileVisitResult.CONTINUE;
        }

        ArchiveEntry archiveEntry = archiveOutputStream.createArchiveEntry(dir.toFile(), relativePath.toString().replace(PATH_SEPARATOR, '/') + '/');
        archiveOutputStream.putArchiveEntry(archiveEntry);
        archiveOutputStream.closeArchiveEntry();
        return FileVisitResult.CONTINUE;
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        log.trace("Zipping file {}", file.toAbsolutePath());

        Path relativePath = zipContent
          ? directoryToZip.relativize(file)
          : directoryToZip.getParent().relativize(file);

        ArchiveEntry archiveEntry = archiveOutputStream.createArchiveEntry(file.toFile(), relativePath.toString().replace(PATH_SEPARATOR, '/'));
        archiveOutputStream.putArchiveEntry(archiveEntry);

        try (InputStream inputStream = Files.newInputStream(file)) {
          final long currentBytesDone = bytesDone;
          ByteCopier
            .from(inputStream)
            .to(archiveOutputStream)
            .bufferSize(bufferSize)
            .byteCountInterval(byteCountInterval)
            .totalBytes(bytesTotal)
            .listener((written, total) -> updateBytesCounted(currentBytesDone + written, total))
            .copy();
        }
        archiveOutputStream.closeArchiveEntry();
        return FileVisitResult.CONTINUE;
      }
    });

    if (closeStream) {
      archiveOutputStream.close();
    }
  }

  private void updateBytesCounted(long written, long total) {
    if (byteCountListener == null)
      return;

    byteCountListener.updateBytesWritten(written, total);
  }

  private long calculateTotalBytes() throws IOException {
    final long[] bytesTotal = {0};
    Files.walkFileTree(directoryToZip, new SimpleFileVisitor<Path>() {
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        bytesTotal[0] += Files.size(file);
        return FileVisitResult.CONTINUE;
      }
    });
    return bytesTotal[0];
  }
}
