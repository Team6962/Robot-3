if(now - start < 500) {
      encoder1.reset();
      encoder2.reset();
      myDrive.tankDrive(0.7, -0.7);
    }
    else if (now - start < 1500) {
      myDrive.tankDrive(-0.7, 0.7);
    }
    else if(now - start < 2000) {
      myDrive.tankDrive(0.7, -0.7);
    }
    else if (now - start < 3000) {
      myDrive.tankDrive(-0.7, -0.7);
    }
    else if (now - start < 4500) {
      myDrive.tankDrive(0.3, 0.7);
    }
    else if (now - start < 6000) {
      myDrive.tankDrive(0.7, 0.3);
    }
    else if (now - start < 7000) {
      if (encoder1.getDistance() == encoder2.getDistance()) {
        myDrive.tankDrive(0, 0);
      }
      else (encoder1.getDistance() != encoder2.getDistance()) {
        while(encoder1.getDistance() != encoder2.getDistance()) {
          if(encoder1.getDistance() < encoder2.getDistance()) {
            myDrive.tankDrive(0.01, 0);
          }
          else if (encoder1.getDistance() > encoder2.getDistance()) {
            myDrive.tankDrive(0, 0.01);
          }
          else {
            myDrive.tankDrive(0,0);
          }
        }
      }
    }
    else myDrive.tankDrive(0, 0);
