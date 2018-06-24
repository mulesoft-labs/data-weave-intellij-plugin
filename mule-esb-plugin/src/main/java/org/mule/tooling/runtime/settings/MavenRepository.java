package org.mule.tooling.runtime.settings;

public class MavenRepository {

  private String name;
  private String url;
  private String username;
  private String password;

  public MavenRepository(String name, String url, String username, String password) {
    this.name = name;
    this.url = url;
    this.username = username;
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
