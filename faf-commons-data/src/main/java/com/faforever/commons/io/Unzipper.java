package com.faforever.commons.io;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public final class Unzipper {
  /**
   * Total amount of written bytes after which zip bomb protection goes active
   */
  private final static int ZIP_BOMB_PROTECTION_WRITTEN_BYTES_THRESHOLD = 1_000_000;
  /**
   * Assume zip bomb if [read files from zip file] / [written bytes into stream] > [factor]
   */
  private final static int ZIP_BOMB_PROTECTION_FACTOR = 100;

  private final ArchiveInputStream archiveInputStream;
  private final boolean closeStream;

  private ByteCountListener byteCountListener;
  private int byteCountInterval;
  private int bufferSize;
  private long bytesTotal;
  private Path targetDirectory;

  private Unzipper(ArchiveInputStream archiveInputStream, boolean closeStream) {
    this.archiveInputStream = archiveInputStream;
    this.closeStream = closeStream;
    // 4K
    bufferSize = 0x1000;
    byteCountInterval = 40;
  }


  public static Unzipper from(Path zipFile, String archiveType) throws IOException, ArchiveException {
    ArchiveInputStream archiveInputStream = new ArchiveStreamFactory().createArchiveInputStream(archiveType,
      new BufferedInputStream(Files.newInputStream(zipFile)));

    return new Unzipper(archiveInputStream, true)
      .totalBytes(Files.size(zipFile));
  }

  public static Unzipper from(ArchiveInputStream archiveInputStream) {
    return new Unzipper(archiveInputStream, false);
  }

  public Unzipper to(Path targetDirectory) {
    this.targetDirectory = targetDirectory;
    return this;
  }

  public Unzipper byteCountInterval(int byteCountInterval) {
    this.byteCountInterval = byteCountInterval;
    return this;
  }

  public Unzipper listener(ByteCountListener byteCountListener) {
    this.byteCountListener = byteCountListener;
    return this;
  }

  public Unzipper bufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    return this;
  }

  public Unzipper totalBytes(long totalBytes) {
    this.bytesTotal = totalBytes;
    return this;
  }

  public void unzip() throws IOException {
    long bytesDone = 0;

    ArchiveEntry archiveEntry;
    while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
      Path targetFile = targetDirectory.resolve(archiveEntry.getName());
      if (archiveEntry.isDirectory()) {
        log.trace("Creating directory {}", targetFile);
        Files.createDirectories(targetFile);
        continue;
      }

      Path parentDirectory = targetFile.getParent();
      if (Files.notExists(parentDirectory)) {
        log.trace("Creating directory {}", parentDirectory);
        Files.createDirectories(parentDirectory);
      }

      long compressedSize = archiveEntry.getSize();
      if (compressedSize != -1) {
        bytesDone += compressedSize;
      }

      log.trace("Writing file {}", targetFile);
      try (OutputStream outputStream = Files.newOutputStream(targetFile, CREATE, TRUNCATE_EXISTING, WRITE)) {
        final long currentBytesWritten = bytesDone;
        ByteCopier
          .from(archiveInputStream)
          .to(outputStream)
          .bufferSize(bufferSize)
          .byteCountInterval(byteCountInterval)
          .totalBytes(bytesTotal)
          .listener((written, total) -> updateBytesCounted(currentBytesWritten + written, total))
          .copy();

        if (byteCountListener != null) {
          byteCountListener.updateBytesWritten(archiveInputStream.getBytesRead(), bytesTotal);
        }

      } finally {
        if (closeStream) {
          archiveInputStream.close();
        }
      }
    }
  }

  private void updateBytesCounted(long written, long total) {
    zipBombCheck(archiveInputStream.getBytesRead(), written);

    if (byteCountListener == null)
      return;

    byteCountListener.updateBytesWritten(written, total);
  }

  public void zipBombCheck(long inpuBytesRead, long outputBytesWritten) {
    if (outputBytesWritten > ZIP_BOMB_PROTECTION_WRITTEN_BYTES_THRESHOLD
      && outputBytesWritten / inpuBytesRead > ZIP_BOMB_PROTECTION_FACTOR) {
      throw new ZipBombException("Zip bomb detected. Aborting unzip process");
    }
  }
}
