package au.com.agiledigital.idea_search;

public class Options {

  public boolean isDefault = false;
  public boolean isUser = false;
  public boolean isStatus = false;

  /**
   * Sets the flag for default value
   *
   * @param isDefault Is a default value
   * @return Options object with the applied flag
   */
  public Options withDefault(boolean isDefault) {
    this.isDefault = isDefault;

    return this;
  }

  /**
   * Sets the flag for a user value
   *
   * @param user Is a user value
   * @return Options object with the applied flag
   */
  public Options withUser(boolean user) {
    isUser = user;

    return this;
  }

  /**
   * Sets the flag for a status value
   *
   * @param user Is a status value
   * @return Options object with the applied flag
   */
  public Options withStatus(boolean status) {
    this.isStatus = status;

    return this;
  }

  @Override
  public String toString() {
    return (
      "Options{" +
        "isDefault=" +
        isDefault +
        ", isUser=" +
        isUser +
        ", isStatus=" +
        isStatus +
        '}'
    );
  }
}
