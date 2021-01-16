  @Override
  public void autonomousPeriodic() {

    long now = System.currentTimeMillis();
    if (now - start < 2000) {
      myDrive.tankDrive(-0.9, 0.9);
    } else if (now - start < 4000) {
      myDrive.tankDrive(0.7, -0.7);
    } else if (now - start < 6000) {
      myDrive.tankDrive(0.6, 0.6);
    } else if (now - start < 8000) {
      myDrive.tankDrive(-0.8, -0.8);
    } else {
      myDrive.tankDrive(0, 0);
    }

  }
