public static double[] rotateRight(int now, int begintime, int stoptime, double speed) {
  double[] values = new double[2];
  if (now > begintime) {
    if (now < stoptime) {
      if (-encoder1.getDistance() == encoder2.getDistance()) {
          values = [speed, -speed];
      } else if (-encoder1.getDistance() > encoder2.getDistance()) {
          values = [speed * 0.9, -speed * 1.1];
      } else if (-encoder1.getDistance() < encoder2.getDistance()) {
          values = [speed * 1.1, -speed * 0.9];
      }
    } else {
      values = [0.0, 0.0];
    }
  }
}



double[] values = rotateRight(now, 0, 1250, /*[TBD]*/);
myDrive.tankDrive(values[0], values[1]);
// Please test TBD for different values... We want the value that makes the robot rotate 90 degrees after 1.25 sec.
