package com.spotify.docker;
import com.spotify.docker.client.AnsiProgressHandler;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ProgressMessage;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static com.google.common.base.Strings.isNullOrEmpty;

public class Utils {
  public static String[] parseImageName(String imageName) throws MojoExecutionException {
    if (isNullOrEmpty(imageName)) {
      throw new MojoExecutionException("You must specify an \"imageName\" in your " + "docker-maven-client\'s plugin configuration");
    }
    final int lastSlashIndex = imageName.lastIndexOf('/');
    final int lastColonIndex = imageName.lastIndexOf(':');
    String repo = imageName;
    String tag = null;
    if (lastColonIndex > lastSlashIndex) {
      repo = imageName.substring(0, lastColonIndex);
      tag = imageName.substring(lastColonIndex + 1);
      if (tag.isEmpty()) {
        tag = null;
      }
    }
    return new String[] { repo, tag };
  }

  public static void pushImage(DockerClient docker, String imageName, Log log, final DockerBuildInformation buildInfo) throws MojoExecutionException, DockerException, IOException, InterruptedException {
    log.info("Pushing " + imageName);
    final AnsiProgressHandler ansiProgressHandler = new AnsiProgressHandler();
    final DigestExtractingProgressHandler handler = new DigestExtractingProgressHandler(ansiProgressHandler);
    docker.push(imageName, handler);
    if (buildInfo != null) {
      final String imageNameWithoutTag = parseImageName(imageName)[0];
      buildInfo.setDigest(imageNameWithoutTag + "@" + handler.digest());
    }
  }

  public static void pushImageTag(DockerClient docker, String imageName, List<String> imageTags, Log log) throws MojoExecutionException, DockerException, IOException, InterruptedException {
    if (imageTags.isEmpty()) {
      throw new MojoExecutionException("You have used option \"pushImageTag\" but have" + " not specified an \"imageTag\" in your" + " docker-maven-client\'s plugin configuration");
    }
    for (final String imageTag : imageTags) {
      final String imageNameWithTag = imageName + ":" + imageTag;
      log.info("Pushing " + imageName + ":" + imageTag);
      docker.push(imageNameWithTag, new AnsiProgressHandler());
    }
  }

  public static void writeImageInfoFile(final DockerBuildInformation buildInfo, final String tagInfoFile) throws IOException {
    final Path imageInfoPath = Paths.get(tagInfoFile);
    if (imageInfoPath.getParent() != null) {
      Files.createDirectories(imageInfoPath.getParent());
    }
    Files.write(imageInfoPath, buildInfo.toJsonBytes());
  }

  private static class DigestExtractingProgressHandler implements ProgressHandler {
    private final ProgressHandler delegate;

    private String digest;

    DigestExtractingProgressHandler(final ProgressHandler delegate) {
      this.delegate = delegate;
    }

    @Override public void progress(final ProgressMessage message) throws DockerException {
      if (message.digest() != null) {
        digest = message.digest();
      }
      delegate.progress(message);
    }

    public String digest() {
      return digest;
    }
  }
}