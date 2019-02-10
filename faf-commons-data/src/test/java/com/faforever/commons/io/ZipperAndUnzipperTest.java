package com.faforever.commons.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipperAndUnzipperTest {

  @TempDir
  Path folderToZip;
  @TempDir
  Path tempFolder;
  @TempDir
  Path targetFolder;

  @Test
  void testZip() throws Exception {

    Path file1 = Files.createFile(folderToZip.resolve("file1"));
    Files.createFile(folderToZip.resolve("file2"));

    Path folder1 = Files.createDirectory(folderToZip.resolve("folder1"));
    Files.createFile(folder1.resolve("file1"));
    Path folder11 = Files.createDirectory(folder1.resolve("folder11"));
    Files.createFile(folder11.resolve("file1"));

    Path folder2 = Files.createDirectory(folderToZip.resolve("folder2"));
    Files.createFile(folder2.resolve("file1"));

    Files.createDirectory(folderToZip.resolve("folder3"));

    byte[] file1Contents = new byte[1024];
    new Random().nextBytes(file1Contents);

    Files.write(file1, file1Contents);

    Path zipFile = targetFolder.resolve("target.zip");
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
      Zipper.contentOf(folderToZip)
        .to(zipOutputStream)
        .zip();
    }

    try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
      Unzipper.from(zipInputStream)
        .to(targetFolder)
        .unzip();
    }

    Path targetDirectory = targetFolder;

    assertTrue(Files.exists(targetDirectory.resolve("file1")));
    assertTrue(Files.exists(targetDirectory.resolve("file2")));
    assertTrue(Files.exists(targetDirectory.resolve("folder1")));
    assertTrue(Files.exists(targetDirectory.resolve("folder1").resolve("file1")));
    assertTrue(Files.exists(targetDirectory.resolve("folder2")));
    assertTrue(Files.exists(targetDirectory.resolve("folder2").resolve("file1")));
    assertTrue(Files.exists(targetDirectory.resolve("folder3")));

    assertArrayEquals(file1Contents, Files.readAllBytes(targetDirectory.resolve("file1")));
  }
}
