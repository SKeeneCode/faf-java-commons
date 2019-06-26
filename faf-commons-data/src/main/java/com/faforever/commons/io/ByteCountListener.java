package com.faforever.commons.io;

@FunctionalInterface
public interface ByteCountListener {

  void updateBytesWritten(long written, long total);
}
