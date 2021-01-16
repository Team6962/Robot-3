
/*
0.25 sec pauses inbetween actions
0.7 default motor value for each action

2 sec move forward
0.25 wait
2 sec move backward
0.25 wait
1.25 sec turn to next branch
0.25 wait
x4


What speed does 





*/

// Dance:


int prevTime = 0; // Prev goup end time in milliseconds
for (int i = 0; i < 4; i++) {
  int offset = (i * 6000) + prevTime;                                          // For looping time variable
  
  double[] values = moveStraight(now, 0 + offset, 2000 + offset, 0.7);         // 2 sec move forward
  myDrive.tankDrive(values[0], values[1]);
  double[] values = pause(now, 2000 + offset, 2250 + offset);                  // 0.25 sec pause
  myDrive.tankDrive(values[0], values[1]);
  double[] values = moveStraight(now, 2250 + offset, 4250 + offset, -0.7);     // 2 sec move backwards
  myDrive.tankDrive(values[0], values[1]);
  double[] values = pause(now, 4250 + offset, 4500 + offset);                  // 0.25 sec pause
  myDrive.tankDrive(values[0], values[1]);
  double[] values = rotateRight(now, 4500 + offset, 5750 + offset, 0.7);       // 90 degree rotate right (1.25 sec)
  myDrive.tankDrive(values[0], values[1]);
  double[] values = pause(now, 5750 + offset, 6000 + offset);                  // 0.25 sec pause
}




// Moving Functions:

public static double[] pause(int now, int begintime, int stoptime) {
  double[] values = new double[2];
  if (now > begintime) {
    if (now < stoptime) {
      values = [0.0, 0.0];
    }
  }
  return values;
}

public static double[] moveStraight(int now, int begintime, int stoptime, double speed) {
  double[] values = new double[2];
  if (now > begintime) {
    if (now < stoptime) {
      if (encoder1.getDistance() == encoder2.getDistance()) {
          values = [speed, speed];
      } else if (encoder1.getDistance() > encoder2.getDistance()) {
          values = [speed * 0.9, speed * 1.1];
      } else if (encoder1.getDistance() < encoder2.getDistance()) {
          values = [speed * 1.1, speed * 0.9];
      }
    } else {
      values = [0.0, 0.0];
    }
  }
  return values;
}

public static double[] rotateLeft(int now, int begintime, int stoptime, double speed) {
  double[] values = new double[2];
  if (now > begintime) {
    if (now < stoptime) {
      if (encoder1.getDistance() == -encoder2.getDistance()) {
          values = [-speed, speed];
      } else if (encoder1.getDistance() > -encoder2.getDistance()) {
          values = [-speed * 0.9, speed * 1.1];
      } else if (encoder1.getDistance() < -encoder2.getDistance()) {
          values = [-speed * 1.1, speed * 0.9];
      }
    } else {
      values = [0.0, 0.0];
    }
  }
}

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

