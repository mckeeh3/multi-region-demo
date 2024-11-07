package io.akka.demo.gatling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GatlingConfigTest {

  private GatlingConfig config;
  private File tempFile;

  @BeforeEach
  void setUp() throws IOException {
    // Create a temporary file with test data
    tempFile = Files.createTempFile("gatling", ".conf").toFile();
    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write("testString=testValue\n");
      writer.write("testInt=123\n");
      writer.write("testLong=456789\n");
      writer.write("testDurationMs=100ms\n");
      writer.write("testDurationS=10s\n");
      writer.write("testDurationM=5m\n");
      writer.write("testDurationH=2h\n");
      writer.write("testDurationD=1d\n");
    }

    // Create a GatlingConfig instance with the temporary file
    config = GatlingConfig.load(tempFile);
  }

  @AfterEach
  void tearDown() {
    // Delete the temporary file
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  void testGetString() {
    assertEquals("testValue", config.getString("testString"));
  }

  @Test
  void testGetInt() {
    assertEquals(123, config.getInt("testInt"));
  }

  @Test
  void testGetLong() {
    assertEquals(456789L, config.getLong("testLong"));
  }

  @Test
  void testGetDurationMs() {
    assertEquals(Duration.ofMillis(100), config.getDuration("testDurationMs"));
  }

  @Test
  void testGetDurationS() {
    assertEquals(Duration.ofSeconds(10), config.getDuration("testDurationS"));
  }

  @Test
  void testGetDurationM() {
    assertEquals(Duration.ofMinutes(5), config.getDuration("testDurationM"));
  }

  @Test
  void testGetDurationH() {
    assertEquals(Duration.ofHours(2), config.getDuration("testDurationH"));
  }

  @Test
  void testGetDurationD() {
    assertEquals(Duration.ofDays(1), config.getDuration("testDurationD"));
  }

  @Test
  void testParseDurationInvalidFormat() {
    assertThrows(IllegalArgumentException.class, () -> config.getDuration("invalid"));
  }

  @Test
  void testParseDurationNull() {
    assertThrows(IllegalArgumentException.class, () -> config.getDuration(null));
  }

  @Test
  void testGetStringKeyNotFound() {
    assertThrows(IllegalArgumentException.class, () -> config.getString("nonExistentKey"));
  }

  @Test
  void testGetIntKeyNotFound() {
    assertThrows(IllegalArgumentException.class, () -> config.getInt("nonExistentKey"));
  }

  @Test
  void testGetLongKeyNotFound() {
    assertThrows(IllegalArgumentException.class, () -> config.getLong("nonExistentKey"));
  }

  @Test
  void testGetDurationKeyNotFound() {
    assertThrows(IllegalArgumentException.class, () -> config.getDuration("nonExistentKey"));
  }
}
