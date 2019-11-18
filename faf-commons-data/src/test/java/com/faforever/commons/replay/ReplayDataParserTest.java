package com.faforever.commons.replay;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.LittleEndianDataInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ReplayDataParserTest {

  @TempDir
  public Path temporaryFolder;

  @Test
  public void parse() throws Exception {
    Path replayFile = temporaryFolder.resolve("tmp.fafreplay");
    Files.copy(getClass().getResourceAsStream("/replay/test.fafreplay"), replayFile);

    ReplayData data = new ReplayDataParser(replayFile).parse();

    List<GameOption> gameOptions = data.getGameOptions();
    assertThat(gameOptions.size(), is(28));
    assertThat(gameOptions.get(0), is(new GameOption("CheatMult", "2.0")));
    assertThat(gameOptions.get(1), is(new GameOption("Timeouts", "3")));
    assertThat(gameOptions.get(10), is(new GameOption("ScenarioFile", "/maps/scmp_009/scmp_009_scenario.lua")));
    assertThat(gameOptions.get(12), is(new GameOption("ClanTags", ImmutableMap.builder()
      .put("Geosearchef", "")
      .put("Tokyto", "e")
      .put("Jitsu", "JEW")
      .put("kubkolienka", "")
      .put("sandwormsurfer", "GDI")
      .put("MazorNoob", "SNF")
      .put("Nojoke", "TEA")
      .put("hepko", "e")
      .build()
    )));

    List<ChatMessage> chatMessages = data.getChatMessages();
    assertThat(chatMessages.get(0), is(new ChatMessage(Duration.ofSeconds(30), "kubkolienka", "all", "how does this game play?:D")));
  }

  @Test
  void testReadString() throws Exception {
    ReplayDataParser replayDataParser = new ReplayDataParser(Paths.get("."));

    String unicodeString = "Oh, helloäöüthere!";

    byte[] stringBytes = (unicodeString + "\0").getBytes(StandardCharsets.UTF_8);
    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(stringBytes);
    LittleEndianDataInputStream dataInputStream = new LittleEndianDataInputStream(byteInputStream);

    String result = replayDataParser.readString(dataInputStream);

    assertThat(result, is(unicodeString));
  }
}
