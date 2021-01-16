public static void rotateRight(int now, int begintime, int stoptime, double speed) {
  if (now > begintime) {
    if (now < stoptime) {
      if (-encoder1.getDistance() == encoder2.getDistance()) {
          myDrive.tankDrive(speed, -speed);
      } else if (-encoder1.getDistance() > encoder2.getDistance()) {
          myDrive.tankDrive(speed * 0.9, -speed * 1.1);
      } else if (-encoder1.getDistance() < encoder2.getDistance()) {
          myDrive.tankDrive(speed * 1.1, -speed * 0.9);
      }
    } else {
      myDrive.tankDrive(0, 0);
    }
  }
}



rotateRight(now, 0, 1250, /*[TBD]*/);
// Please test TBD for different values... We want the value that makes the robot rotate 90 degrees.
