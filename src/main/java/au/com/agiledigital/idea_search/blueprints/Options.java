package au.com.agiledigital.idea_search.blueprints;

public class Options {

  public boolean isDefault = false;
  public boolean isUser = false;
  public boolean isStatus = false;
  public boolean isTechnology = false;

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
    this.isUser = user;

    return this;
  }

  /**
   * Sets the flag for a status value
   *
   * @param status Is a status value
   * @return Options object with the applied flag
   */
  public Options withStatus(boolean status) {
    this.isStatus = status;

    return this;
  }

  /**
   *  Sets the flag for a technology value
   *
   * @param technology Is a technology value
   * @return Options object with the applied flag
   */
  public Options withTechnology(boolean technology) {
    this.isTechnology = technology;

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
      ", isTechnology=" +
      isTechnology +
      '}'
    );
  }
}
