if(now - start < 500) {
      myDrive.tankDrive(0.7, -0.7);
    }
    else if (now - start < 1000) {
      myDrive.tankDrive(-0.7, 0.7);
    }
    else if (now - start < 2000) {
      myDrive.tankDrive(0.7, -0.7);
    }
    else if (now - start < 3000) {
      myDrive.tankDrive(-0.7, 0.7);
    }
    else if (now - start < 3500) {
      myDrive.tankDrive(0.4, 0.4);
    }
    else if (now - start < 4500) {
      myDrive.tankDrive(0.7, -0.7);
    }
    else if (now - start < 5500) {
      myDrive.tankDrive(-0.7, 0.7);
    }
    else if (now - start < 6000) {
      myDrive.tankDrive(-0.4, -0.4);
    }
    else myDrive.tankDrive( 0, 0 );
